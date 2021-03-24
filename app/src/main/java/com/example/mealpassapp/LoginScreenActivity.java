package com.example.mealpassapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mealpassapp.helpers.Connectivity;
import com.example.mealpassapp.model.UsersModel;
import com.example.mealpassapp.registration.Authentication;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginScreenActivity extends AppCompatActivity {
Button login;
EditText phoneNumber , password,reenterpassword;
TextView signUp, forgotPassword;
DatabaseReference databaseReference;
List<UsersModel> usersList;
SharedPreferences sharedPref;
SharedPreferences.Editor editor;
String Name = "", account = "", Phone = "", ID="", IMAGE_URI="", Password="", type="", token="";
double Latitude = 0 , Longitude = 0;
int delivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
        usersList = new ArrayList<>();

        login = (Button) findViewById(R.id.buttonlogin);
        forgotPassword = findViewById(R.id.forgotTextView);
        signUp = findViewById(R.id.tvSignup);
        phoneNumber = (EditText) findViewById(R.id.edtNumber);
        password = (EditText) findViewById(R.id.editTextpassword);
        reenterpassword= (EditText) findViewById((R.id.editTextpassword));

        SharedPreferences sharedPref = getSharedPreferences("token",MODE_PRIVATE);
        token = sharedPref.getString("id","null");
        Toast.makeText(this, "token : "+token, Toast.LENGTH_SHORT).show();

        loadCredentials();

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Connectivity.isConnected(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , Authentication.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(LoginScreenActivity.this, "Check your network!!", Toast.LENGTH_SHORT).show();
            }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Connectivity.isConnected(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , Authentication.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(LoginScreenActivity.this, "Check your network!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String phone = phoneNumber.getText().toString();
                String pass_word = password.getText().toString();

                if (!Connectivity.isConnected(getApplicationContext())) {
                    final Snackbar snackbar = Snackbar.make(v, "Not connected to internet", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    return;
                }
                if(!Connectivity.isConnectedFastLogin(getApplicationContext())){
                    final Snackbar snackbar = Snackbar.make(v, "Check your connection, too slow", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    phoneNumber.setError("Enter number");
                    return;
                }
                if (TextUtils.isEmpty(pass_word)) {
                    password.setError("Enter password");
                    return;
                }

                boolean flag = false;
                boolean flag2 = false;
                char t = 'a';

                try {
                    for (UsersModel fbAdapter : usersList) {
                        if (phone.equals(fbAdapter.getPhone())) {
                            flag = true;
                            if (pass_word.equals(fbAdapter.getPassword())) {
                                type = fbAdapter.getType();
                                t = type.charAt(0);
                                flag2 = true;

                                ID = fbAdapter.getId();
                                Name = fbAdapter.getName();
                                account = fbAdapter.getAccount();
                                Phone = fbAdapter.getPhone();
                                IMAGE_URI = fbAdapter.getImageUri();
                                Latitude = fbAdapter.getLatitude();
                                Longitude = fbAdapter.getLongtitude();
                                Password = fbAdapter.getPassword();
                                account = fbAdapter.getAccount();
                                token = fbAdapter.getToken();
                                delivery =  fbAdapter.getFlag();
                                break;
                            }
                        }
                    }
                    if (flag && flag2) {
                        updateTokenDetails(ID);
                        switch (t) {
                            case 'U':
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                                db.child(ID).child("token").setValue(token);

                                Intent intent = new Intent(LoginScreenActivity.this, UserActivity.class);
                                addUserdataToSharedPref(token);
                                startActivity(intent);
                                password.setText("");
                                phoneNumber.setText("");
                                finish();
                                break;
                            case 'F':
                                DatabaseReference db2 = FirebaseDatabase.getInstance().getReference("Users");
                                db2.child(ID).child("token").setValue(token);

                                Intent intent2 = new Intent(LoginScreenActivity.this, FoodSellerActivity.class);
                                addUserdataToSharedPref(token);
                                startActivity(intent2);
                                password.setText("");
                                phoneNumber.setText("");
                                finish();
                                break;
                            default:
                                Toast.makeText(LoginScreenActivity.this, "Login does not exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        final Snackbar snackbar = Snackbar.make(v, "Error : Check your details", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private void loadCredentials() {
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
            public void onCancelled(DatabaseError databaseError) { }});

    }

    public void addUserdataToSharedPref(String token){
        sharedPref = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("fullName", Name);
        editor.putString("account",account);
        editor.putString("URI", IMAGE_URI);
        editor.putString("ID", ID);
        editor.putString("latitude", String.valueOf(Latitude));
        editor.putString("longitude", String.valueOf(Longitude));
        editor.putString("phone",Phone);
        editor.putString("password",Password);
        editor.putString("type",type);
        editor.putString("token",token);
        editor.putString("flag",delivery+"");

        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateTokenDetails(final String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
        databaseReference.child(userId).child("token").setValue(token);
    }

}