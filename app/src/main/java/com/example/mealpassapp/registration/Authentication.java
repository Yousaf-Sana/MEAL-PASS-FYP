package com.example.mealpassapp.registration;

import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.chaos.view.PinView;
import com.example.mealpassapp.FoodSellerActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.UserActivity;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.UsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Authentication extends BaseActivity {
    DatabaseReference databaseReference;
    List<UsersModel> usersList;
    LinearLayout layout1, layout2, layout3;
    boolean flag = false;
    int flag2;
    private Button sendCodeButton;
    private Button verifyCodeButton;
    private Button button3;
    private EditText edtNumber;
    private PinView verifyCodeET;
    private TextView phonenumberText;
    private TextView resendCode;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String Name = "", CNIC = "", Phone = ""  , ID="" ,IMAGE_URI="" , Password="" , type="", token = "";
    double Latitude = 0 , Longitude = 0;
    char t;
    boolean resend = false;

    FirebaseAuth mAuth;
    String verificationId , phoneNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean mVerificationInProgress = false;
    TextView mCountdown;
    private long mTimeLeftInMillis = 60000;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
        usersList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout3 = (LinearLayout) findViewById(R.id.layout3);

        sendCodeButton = (Button) findViewById(R.id.submit1);
        verifyCodeButton = (Button) findViewById(R.id.submit2);
        button3 = (Button) findViewById(R.id.submit3);

        edtNumber = (EditText) findViewById(R.id.phonenumber);
        verifyCodeET = (PinView) findViewById(R.id.pinView);
        phonenumberText = (TextView) findViewById(R.id.phonenumberText);
        resendCode = findViewById(R.id.tvResend);
        mCountdown = findViewById(R.id.countdown);
        mCountdown.setVisibility(View.INVISIBLE);

//        stepView = findViewById(R.id.step_view);
//        stepView.setStepsNumber(3);
//        stepView.go(0, true);
        layout1.setVisibility(View.VISIBLE);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phoneNumber = edtNumber.getText().toString();
                if(!RegularExpressions.validateMobileNumber(phoneNumber)){
                    edtNumber.setError("Invalid number");
                    edtNumber.requestFocus();
                    return;
                }
                phonenumberText.setText(phoneNumber);

                SharedPreferences.Editor editor = getSharedPreferences("phonenum", MODE_PRIVATE).edit();
                editor.putString("phone" , phoneNumber);
                editor.apply();

                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);

                sendVerificationCode(phoneNumber);

            }
        });

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String verificationCode = verifyCodeET.getText().toString();
                if(verificationCode.isEmpty()){
                    final Snackbar snackbar = Snackbar.make(view,"Enter verification code",Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }else {
                    showProgressDialog();
                    verifyCodeET.setText("");
                    verifyCode(verificationCode);
                }
            }
        });
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    showProgressDialog();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(Authentication.this , RegisterActivity.class);
                                    intent.putExtra("Number" , phoneNumber);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    },3000);

                }catch (Exception e){
                    Log.d(e.toString() ,"error");
                    e.printStackTrace();
                }
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId , code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    for(UsersModel fbAdapter : usersList){
                        if(phoneNumber.equals(fbAdapter.getPhone())){
                            Toast.makeText(Authentication.this, "Account existed", Toast.LENGTH_SHORT).show();
                            type = fbAdapter.getType();
                            t = type.charAt(0);
                            Name = fbAdapter.getName();
                            CNIC = fbAdapter.getAccount();
                            Phone = fbAdapter.getPhone();
                            IMAGE_URI = fbAdapter.getImageUri();
                            Latitude = fbAdapter.getLatitude();
                            Longitude = fbAdapter.getLongtitude();
                            Password = fbAdapter.getPassword();
                            token = fbAdapter.getToken();
                            flag2 = fbAdapter.getFlag();

                            switch (t) {
                                case 'U':
                                    Intent intent = new Intent(Authentication.this, UserActivity.class);
                                    addUserdataToSharedPref();
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 'F':
                                    Intent intent2 = new Intent(Authentication.this, FoodSellerActivity.class);
                                    addUserdataToSharedPref();
                                    startActivity(intent2);
                                    finish();
                                    break;
                                default:
                            }
                        }
                    }
                    hideProgressDialog();

                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Successfull",Toast.LENGTH_SHORT).show();

                } else {
                    hideProgressDialog();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext()," Not Successfull",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92"+number,        // Phone number to verify
                60 ,                 // Timeout duration
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,// Unit of timeout
                mCallbacks);        // OnVerificationStateChangedCallbacks
        startTimer();
    }
    private void resendVerificationCode() {
        SharedPreferences sharedPreferences = getSharedPreferences("phonenum", MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString("phone","03111234567");


        if(mResendToken !=null){
            if(resend){
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+92"+phoneNumber,        // Phone number to verify
                        60,              // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,       // Activity (for callback binding)
                        mCallbacks,         // OnVerificationStateChangedCallbacks
                        mResendToken);             // ForceResendingToken from callbacks
                Toast.makeText(getApplicationContext(), "code is resent", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "wait for remaining time finish", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "Send code first", Toast.LENGTH_SHORT).show();
        }
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            mResendToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            showProgressDialog();

            mVerificationInProgress = false;
            signInWithPhoneAuthCredential(phoneAuthCredential);

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            mVerificationInProgress = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    usersList.clear();
                    for (DataSnapshot farmerSnapshot : dataSnapshot.getChildren()) {
                        UsersModel users = farmerSnapshot.getValue(UsersModel.class);
                        usersList.add(users);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void addUserdataToSharedPref(){
        sharedPref = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("fullName", Name);
        editor.putString("account",CNIC);
        editor.putString("URI", IMAGE_URI);
        editor.putString("ID", ID);
        editor.putString("latitude", String.valueOf(Latitude));
        editor.putString("longitude", String.valueOf(Longitude));
        editor.putString("phone",Phone);
        editor.putString("password",Password);
        editor.putString("type",type);
        editor.putString("token",token);
        editor.putString("flag",flag2+"");
        editor.apply();
    }
    private void startTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mCountdown.setVisibility(View.INVISIBLE);
                resend = true;

            }
        }.start();
    }
    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        mCountdown.setVisibility(View.VISIBLE);
        mCountdown.setText(timeLeftFormatted+" time left");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
