package com.example.mealpassapp.registration;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mealpassapp.FoodSellerActivity;
import com.example.mealpassapp.LoginScreenActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.UserActivity;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.UsersModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity {
    EditText edtName, edtPassword , edtCnic;
    Spinner accountType;
    Context context;
    Button btnReg;
    ImageView imgSignup;
    DatabaseReference databaseReference;
    StorageReference mRef;
    double lati , longi;
    LocationManager locationManager;
    LocationListener locationListener;
    String id ,  name , cnic , Password , type , phoneNumber , fbURI;
    private StorageTask mUploadTask;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int STORAGE_PERMISSION_CODE = 100;
    String token;
    public Uri mImageUri = Uri.EMPTY, resultUri = Uri.EMPTY;
    private boolean mGranted;
    ProgressBar progressBar;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
        mRef = FirebaseStorage.getInstance().getReference("UsersData/");

        SharedPreferences e = getSharedPreferences("token",MODE_PRIVATE);
        token = e.getString("id","null");
        final SharedPreferences.Editor tokenEditor = e.edit();

        Toast.makeText(getApplicationContext() , token , Toast.LENGTH_LONG).show();

        mImageUri = Uri.parse("android.resource://com.example.mealpassapp/" +R.drawable.admin);
        resultUri = Uri.parse("android.resource://com.example.mealpassapp/" +R.drawable.admin);;

        SharedPreferences sharedPreferences = getSharedPreferences("phonenum",MODE_PRIVATE);
        final SharedPreferences.Editor editorPhone = sharedPreferences.edit();
        phoneNumber = sharedPreferences.getString("phone","0333");

        context = RegisterActivity.this;
        accountType = findViewById(R.id.type);
        edtName = findViewById(R.id.edtname);
        edtPassword = findViewById(R.id.edtpass);
        edtCnic = findViewById(R.id.edtAccount);
        btnReg = findViewById(R.id.btnreg);
        imgSignup = findViewById(R.id.signupImg);
        progressBar = findViewById(R.id.progressBar);

        imgSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mGranted) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            return;
                        }
                    }
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
            }
        });

//        final List<String> typeList;
//        typeList = new ArrayList<>();
//        typeList.add("Account Type");
//        typeList.add("User");
//        typeList.add("Food Seller");
//
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String >(RegisterActivity.this,android.R.layout.simple_list_item_1,typeList);
//        accountType.setAdapter(adapter);
//
//        accountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                type = typeList.get(position);
//                if(type.equals("Food Seller")){
//
//                }else if(type.equals("User")){
//
//                }else if(type.equals("Account Type")){
//
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) { }
//        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = edtName.getText().toString();
                cnic = edtCnic.getText().toString();
                Password = edtPassword.getText().toString();
                type = accountType.getSelectedItem().toString();

                if(!RegularExpressions.validateUserName(name)){
                    edtName.setError("Invalid Name");
                    edtName.requestFocus();
                    return;
                }
                if(name.length()>20){
                    edtName.setError("Name length not > 20");
                    edtName.requestFocus();
                    return;
                }
                if(name.length()<3){
                    edtName.setError("Atleast 3 chars");
                    edtName.requestFocus();
                    return;
                }
                if(!RegularExpressions.validateBusinessName(cnic)){
                    edtCnic.setError("Invalid Name");
                    edtCnic.requestFocus();
                    return;
                }
                if(cnic.length()>25){
                    edtCnic.setError("Account title length not > 25");
                    edtCnic.requestFocus();
                    return;
                }
                if(edtCnic.length()<3){
                    edtCnic.setError("Atleast 3 chars");
                    edtCnic.requestFocus();
                    return;
                }
                if(!RegularExpressions.validatePassword(Password)){
                    edtPassword.setError("Invalid password");
                    edtPassword.requestFocus();
                    return;
                }

                if (type.equals("Account Type")) {
                    Toast.makeText(getApplicationContext(),"Select Account Type",Toast.LENGTH_LONG).show();
                    return;
                }

                editorPhone.clear();
                editorPhone.apply();

                tokenEditor.clear();
                tokenEditor.apply();

                showProgressDialog();
                getLocation();

                edtPassword.setText("");
                edtName.setText("");
                edtCnic.setText("");
            }
        });
    }

    private void getLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    lati = location.getLatitude();
                    longi = location.getLongitude();
                    addData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }
            @Override
            public void onProviderEnabled(String s) { }
            @Override
            public void onProviderDisabled(String s) { }};

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3000, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3000, locationListener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                mGranted = true;
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //getData() return the Uri of selected imagerf
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                resultUri  = result.getUri();
                Picasso.with(this).load(resultUri).fit().centerCrop().into(imgSignup);
            }
            else
            {
                if(resultCode== CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error=result.getError();
                }
            }
        }
    }

    private  String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton() ;
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void addData() {

        if(type.equals("Food Seller")){
            hideProgressDialog();
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Select");
            builder.setMessage("Do you want to provide delivery service ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    flag = 1;
                    showProgressDialog();
                    dialog.dismiss();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    flag = 0;
                    showProgressDialog();
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        id = databaseReference.push().getKey();

            final StorageReference fileref =mRef.child(System.currentTimeMillis() + "." + getExtension(mImageUri));

            mUploadTask =   fileref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() { }}, 500);

                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try {
                                fbURI = uri.toString();
                                UsersModel users = new UsersModel(id,uri.toString(), name,cnic,phoneNumber, Password,type, lati, longi, token,flag);
                                users.setImageUri(uri.toString());
                                databaseReference.child(id).setValue(users);

                                char t = type.charAt(0);
                                Toast.makeText(getApplicationContext(), "Account created Successfully " , Toast.LENGTH_LONG).show();

                                hideProgressDialog();

                                switch (t) {
                                    case 'U':
                                        Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                                        addUserdataToSharedPref();
                                        startActivity(intent);
                                        finish();
                                        break;
                                    case 'F':
                                        Intent intent2 = new Intent(RegisterActivity.this, FoodSellerActivity.class);
                                        addUserdataToSharedPref();
                                        startActivity(intent2);
                                        finish();
                                        break;
                                    default:
                                }

                            } catch (Exception ex ){
                                Toast.makeText(getApplicationContext()  , "Error : " + ex.toString() , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {

                    Toast.makeText(getApplicationContext(),"Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
                    edtName.requestFocus();
                    hideProgressDialog();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
    }
    public void addUserdataToSharedPref(){
        sharedPref = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("fullName", name);
        editor.putString("account",cnic);
        editor.putString("URI", fbURI);
        editor.putString("ID", id);
        editor.putString("latitude", String.valueOf(lati));
        editor.putString("longitude", String.valueOf(longi));
        editor.putString("phone",phoneNumber);
        editor.putString("password",Password);
        editor.putString("type",type);
        editor.putString("flag",flag+"");
        editor.putString("token",token);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}