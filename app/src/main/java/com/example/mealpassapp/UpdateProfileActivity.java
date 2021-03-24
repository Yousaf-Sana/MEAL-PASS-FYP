package com.example.mealpassapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.UsersModel;
import com.example.mealpassapp.registration.RegularExpressions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class UpdateProfileActivity extends BaseActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int STORAGE_PERMISSION_CODE = 100;
    Button update , delete;
    EditText edtfullname , edtAccount , edtPhone , edtpass;
    String userId,fullname, phone, password , accountName , type, URI, token;
    int flag;
    double lati, longi;
    String newFullname , newPassword  , newAccount;
    TextView profilePerc;
    ImageView  edit , selectPic, editLoc;
    SharedPreferences  sharedPrefLogin;
    CircularImageView profilepic;
    SharedPreferences.Editor editor;
    DatabaseReference databaseReference ;
    StorageReference mRef;
    FirebaseStorage mStorage;
    public Uri imageUri = Uri.EMPTY, resultUri = Uri.EMPTY;
    private StorageTask mUploadTask;
    private boolean mGranted;
    int count = 0;
    double latitude , longitude;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
        mRef = FirebaseStorage.getInstance().getReference("UsersData/");

        mStorage = FirebaseStorage.getInstance();

        profilepic = findViewById(R.id.changeprofilepic);
        edit = findViewById(R.id.imgedit);
        editLoc = findViewById(R.id.imgLoc);
        selectPic = findViewById(R.id.selectpic);
        update = findViewById(R.id.btnUpdate);
        delete = findViewById(R.id.btnDelete);
        edtfullname = findViewById(R.id.fullname);
        edtAccount = findViewById(R.id.account);
        edtPhone = findViewById(R.id.edtPhone);
        edtpass = findViewById(R.id.password);
        profilePerc = findViewById(R.id.profilePercentage);

        edtfullname.setEnabled(false);
        edtAccount.setEnabled(false);
        edtPhone.setEnabled(false);
        edtpass.setEnabled(false);

        init();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if(count%2 != 0) {
                    edtfullname.setEnabled(true);
                    edtfullname.requestFocus();
                    edtAccount.setEnabled(true);
                    edtpass.setEnabled(true);
                }else {
                    edtfullname.setEnabled(false);
                    edtAccount.setEnabled(false);
                    edtpass.setEnabled(false);
                }
            }
        });

        selectPic.setOnClickListener(new View.OnClickListener() {
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
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , FullScreenImageActivity.class);
                intent.putExtra("URI" , URI);
                startActivity(intent);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    newFullname = edtfullname.getText().toString();
                    newAccount = edtAccount.getText().toString();
                    newPassword = edtpass.getText().toString();

                    if(!RegularExpressions.validateUserName(newFullname)){
                            edtfullname.setError("Invalid");
                            edtfullname.requestFocus();
                            return;
                    }
                    if(newFullname.length()>20){
                        edtfullname.setError("Name length not > 20");
                        edtfullname.requestFocus();
                        return;
                    }
                    if(newFullname.length()<3){
                        edtfullname.setError("Atleast 3 chars");
                        edtfullname.requestFocus();
                        return;
                    }
                    if(newAccount.length()>25){
                        edtAccount.setError("length not > 25");
                        edtAccount.requestFocus();
                        return;
                    }
                    if(newAccount.length()<3){
                        edtAccount.setError("Atleast 3 chars");
                        edtAccount.requestFocus();
                        return;
                    }
                    if(!RegularExpressions.validatePassword(newPassword)){
                        edtpass.setError("Invalid password(8 to 15)");
                        edtpass.requestFocus();
                        return;
                    }

                    showProgressDialog();

                    UsersModel c = new UsersModel(userId, URI, newFullname, newAccount, phone, newPassword,type,lati,
                            longi,token,flag);

                    databaseReference.child(userId).setValue(c);

                    Toast.makeText(getApplicationContext(), "Account updated Successfully ", Toast.LENGTH_LONG).show();

                    editor.clear();
                    editor.apply();
                    addUserdataToSharedPref(userId, URI, newFullname, newAccount, phone, newPassword, type, lati, longi, token, flag);

                    hideProgressDialog();

                    edtfullname.setEnabled(false);
                    edtAccount.setEnabled(false);
                    edtpass.setEnabled(false);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        editLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
                builder.setTitle("Confirmation?");
                builder.setMessage("Are you sure to update location?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLocation();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecords();
            }
        });
    }


    private void init() {

        sharedPrefLogin = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);
        editor = sharedPrefLogin.edit();

        fullname = sharedPrefLogin.getString("fullName","null");
        accountName = sharedPrefLogin.getString("account","null");
        URI = sharedPrefLogin.getString("URI","null");
        userId = sharedPrefLogin.getString("ID","null");
        lati = Double.parseDouble(sharedPrefLogin.getString("latitude","11.1111"));
        longi = Double.parseDouble(sharedPrefLogin.getString("longitude","22.2222"));
        phone = sharedPrefLogin.getString("phone","null");
        password = sharedPrefLogin.getString("password","null");
        type = sharedPrefLogin.getString("type","null");
        flag = Integer.parseInt(sharedPrefLogin.getString("flag","null"));
        token = sharedPrefLogin.getString("token","null");


        Picasso.with(this).load(URI).fit().centerCrop().placeholder(R.drawable.loading).into(profilepic);
        edtfullname.setText(fullname);
        edtAccount.setText(accountName);
        edtpass.setText(password);
        edtPhone.setText(phone);
    }

    private void deleteRecords() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmtion ?");
        builder.setMessage("Are you sure to delete your account").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAccount();

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                mGranted = true;
            } else {
                Toast.makeText(getApplicationContext(), "Please Allow permission to read Data from Device", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST  && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                resultUri  = result.getUri();
                updateProfilePic();
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

    private void updateProfilePic() {

            if (imageUri != null) {

                showProgressDialog();

                final StorageReference fileref = mRef.child(System.currentTimeMillis() + "." + getExtension(imageUri));
                mUploadTask = fileref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                try {
                                    UsersModel c = new UsersModel(userId, uri.toString(), fullname, accountName, phone, password,type,lati,
                                            longi,token,flag);
                                    databaseReference.child(userId).setValue(c);

                                    hideProgressDialog();
                                    Toast.makeText(getApplicationContext(), "Profile picture updated ", Toast.LENGTH_LONG).show();

                                    editor.clear();
                                    editor.apply();

                                    addUserdataToSharedPref(userId, uri.toString(), fullname, accountName, phone, password, type, lati, longi, token, flag);

                                    hideProgressDialog();

                                    init();

                                } catch (Exception ex) {
                                    Toast.makeText(getApplicationContext(), "Error : " + ex.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    }
                });
            }
    }

    private void deleteAccount() {
        try {
            StorageReference imgRef = mStorage.getReferenceFromUrl(URI);
            imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                    db.removeValue();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(UpdateProfileActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void addUserdataToSharedPref(String userId, String uri, String newFullname, String newAccount, String phone, String newPassword,
                                        String type, double lati, double longi, String token, int flag) {

        sharedPrefLogin = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);
        editor = sharedPrefLogin.edit();
        editor.putString("fullName", newFullname);
        editor.putString("account",newAccount);
        editor.putString("URI", uri);
        editor.putString("ID", userId);
        editor.putString("latitude", String.valueOf(lati));
        editor.putString("longitude", String.valueOf(longi));
        editor.putString("phone",phone);
        editor.putString("password",newPassword);
        editor.putString("type",type);
        editor.putString("flag",flag+"");
        editor.putString("token",token);
        editor.apply();

    }

    private void getLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    databaseReference.child(userId).child("latitude").setValue(latitude);
                    databaseReference.child(userId).child("longtitude").setValue(longitude);
                    Toast.makeText(UpdateProfileActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10000, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10000, locationListener);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
