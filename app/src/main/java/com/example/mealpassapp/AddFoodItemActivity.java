package com.example.mealpassapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.FoodModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFoodItemActivity extends BaseActivity {

    private  static  final  int PICK_IMAGE_REQUEST = 1  , PICK_IMAGE_REQUEST_AD = 2;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Button ButtonUpload , showUploads ;
    private EditText edtName , edtPrice ,edtPoints , edtDiscount;
    private Spinner spnCategory;
    private Uri mImageUri = Uri.EMPTY ,resultUri = Uri.EMPTY, adUri = Uri.EMPTY;
    ImageView setImage ;
    ImageView addsPic;
    Context context = AddFoodItemActivity.this;
    private boolean mGranted;
    TextView it_details;
    StorageReference mStorageRef ;
    DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    Toolbar toolbar;
    String itemName, itemCategory;
    int itemPrice, itemPoints;
    Float itemDiscount;
    boolean pic = false, picSelect = false, delivery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);

        ButtonUpload = findViewById(R.id.uploadbtn) ;
        showUploads = findViewById(R.id.showuploadsbtn) ;
        edtName = findViewById(R.id.title);
        edtPrice = findViewById(R.id.price);
        edtPoints = findViewById(R.id.edtPoints);
        edtDiscount = findViewById(R.id.edtDis);
        spnCategory = findViewById(R.id.spnCate);

        if(FoodSellerActivity.flag.equals("1")){
            delivery = true;
        }else if(FoodSellerActivity.flag.equals("0")){
            delivery = false;
        }

        setImage = findViewById(R.id.imageviewset);
        it_details = findViewById(R.id.ttv);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FoodItem/") ;
        mStorageRef = FirebaseStorage.getInstance().getReference("FoodItems/") ;

        toolbar = findViewById(R.id.fixBar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageUri = Uri.EMPTY;
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

        ButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mUploadTask != null && mUploadTask.isInProgress()){
                }
                else {
                        itemName = edtName.getText().toString().trim();
                        String disount = edtDiscount.getText().toString().trim();
                        String price = edtPrice.getText().toString().trim();
                        String points = edtPoints.getText().toString().trim();
                        itemCategory = spnCategory.getSelectedItem().toString();

                    if(itemName.length()>25){
                        edtName.setError("Invalid name");
                        edtName.requestFocus();
                        return;
                    }

                    if(TextUtils.isEmpty(itemName)){
                        edtName.setError("Enter name");
                        edtName.requestFocus();
                        return;
                    }

                    if(TextUtils.isEmpty(price)){
                        edtPrice.setError("Please enter price");
                        edtPrice.requestFocus();
                        return;
                    }
                    int price2 = Integer.parseInt(price);
                    if(price2<1){
                        edtPrice.setError("Price is invalid");
                        edtPrice.requestFocus();
                        return;
                    }

                    if(TextUtils.isEmpty(disount)){
                        edtDiscount.setError("Enter Discount");
                        edtDiscount.requestFocus();
                        return;
                    }
                    if(TextUtils.isEmpty(points)){
                        edtPoints.setError("Enter Points");
                        edtPoints.requestFocus();
                        return;
                    }

                    if(!TextUtils.isEmpty(points)) {
                        int points2 = Integer.parseInt(points);
                        if (points2 < 0) {
                            edtPoints.setError("Points is negative");
                            edtPoints.requestFocus();
                            return;
                        }
                    }else {
                        edtPoints.setError("Enter Loyalty Points");
                        edtPoints.requestFocus();
                        return;
                    }

                    if(!TextUtils.isEmpty(disount)) {
                        int dis = Integer.parseInt(disount);
                        if (dis < 0) {
                            edtPoints.setError("Discount is negative");
                            edtPoints.requestFocus();
                            return;
                        }
                    }else {
                        edtDiscount.setError("Enter Discount");
                        edtDiscount.requestFocus();
                        return;
                    }

                    if(itemCategory.equals("Category")){
                        Toast.makeText(context, "Please select category", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    itemPoints = Integer.parseInt(points);
                    itemDiscount = Float.valueOf(disount);
                    itemPrice = Integer.parseInt(price);
                    if (pic) {
                        uploadFile();
                    }else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Alert !");
                        alertDialog.setMessage("Please select picture");
                        alertDialog.setIcon(R.drawable.ic_launcher_foreground);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                    edtName.setText("");
                    edtDiscount.setText("");
                    edtPrice.setText("");
                    edtPoints.setText("");
                    edtName.requestFocus();
                    Picasso.with(context).load(R.drawable.addimage).fit().centerCrop().into(setImage);

                }
            }
        });
        showUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext() , WholesaleProductRetriever.class);
//                startActivity(intent);
            }
        });
    }

    private  String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton() ;
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadFile() {
        showProgressDialog();
        if(mImageUri !=null && resultUri != null){
            final StorageReference fileref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(mImageUri));
            mUploadTask =   fileref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     Handler handler = new Handler();
                     handler.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                         }
                     }, 500);

                     Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                     task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             try {
                                 String id = mDatabaseRef.push().getKey();
                                 String cDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                                 FoodModel item = new FoodModel(id,itemName,itemPrice, itemPoints, itemDiscount, itemCategory, uri.toString() ,FoodSellerActivity.userId,
                                         FoodSellerActivity.Name,FoodSellerActivity.URI,FoodSellerActivity.token,cDate,delivery,"flag");
                                 mDatabaseRef.child(id).setValue(item);
                                 Toast.makeText(getApplicationContext(), "Item Added Successfully" , Toast.LENGTH_LONG).show();
                                 hideProgressDialog();

                                 mImageUri = Uri.EMPTY;
                                 itemName = "";

                                 mImageUri = Uri.EMPTY;
                                 resultUri = Uri.EMPTY;
                             } catch (Exception ex ){
                             Toast.makeText(getApplicationContext()  , "Error : " + ex.toString() , Toast.LENGTH_LONG).show();
                         }
                         }
                     });
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(Exception e) {
                     Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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

//OnActivityResult() method code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowRotation(true)
                    .setAspectRatio(1,1)
                    .setFixAspectRatio(true)
                    .setAllowCounterRotation(true)
                    .setMultiTouchEnabled(true)
                    .setAutoZoomEnabled(true)
                    .start(this);
        }


        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                resultUri  = result.getUri();
                Picasso.with(this).load(resultUri).into(setImage);
                pic = true;
            }
            else
            {
                if(resultCode== CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error=result.getError();

                }
            }
        }

        if(requestCode == PICK_IMAGE_REQUEST_AD && resultCode == RESULT_OK && data != null && data.getData() != null){
            adUri = data.getData();
            Picasso.with(this).load(adUri).fit().centerCrop().placeholder(R.drawable.loading).into(addsPic);
            picSelect = true;
        }
    }

//onRequestPermissionsResult() code
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
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}