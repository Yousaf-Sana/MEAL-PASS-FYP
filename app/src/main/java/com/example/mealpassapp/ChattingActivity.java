package com.example.mealpassapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealpassapp.adapters.ChattingMsgsAdapter;
import com.example.mealpassapp.model.ChatModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChattingActivity extends AppCompatActivity {
ImageView select , submit, chatPic, selectedfile;
TextView chatUser;
EditText edtChat;
ProgressBar loading;
String checker = "null";
private static final int PICK_IMAGE_REQUEST = 101;
private static final int STORAGE_PERMISSION_CODE = 100;
boolean mGranted;
Uri fileUri = Uri.EMPTY;
SharedPreferences sharedPrefLogin;
String userName, userPic,userType, userBusiness, receiverId, receiverName, receiverPic, message="", fcmToken;
public static String userId;
private StorageTask mUploadTask;
List<ChatModelClass> msgsList;
public static List<ChatModelClass> userSelection = new ArrayList<>();
DatabaseReference databaseReference , databaseReference2;
StorageReference mRef;
Toolbar toolbar;
ListView listView;
FirebaseStorage mStorage;
MenuItem item;
boolean flag = false;
public static ActionMode actionMode;
public static boolean isActionMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        select = findViewById(R.id.imgSelect);
        selectedfile = findViewById(R.id.selectedFile);
        submit = findViewById(R.id.imgSubmiit);
        edtChat = findViewById(R.id.edtChat);
        listView = findViewById(R.id.listViewDealersM);
        listView.setStackFromBottom(true);
        loading = findViewById(R.id.loadingProgress);

        toolbar = findViewById(R.id.dealerstoolbarM);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chatPic = toolbar.findViewById(R.id.chatProfile);
        chatUser = toolbar.findViewById(R.id.chatUser);

        Intent intent = getIntent();
        receiverId = intent.getStringExtra("id");
        fcmToken = intent.getStringExtra("fcm");
        receiverName = intent.getStringExtra("firstName");
        receiverPic = intent.getStringExtra("userPic");

        chatUser.setText(receiverName);
        Picasso.with(getApplicationContext()).load(receiverPic).fit().centerCrop().placeholder(R.drawable.loading).into(chatPic);

        chatPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , FullScreenImageActivity.class);
                intent.putExtra("URI",receiverPic);
                startActivity(intent);
            }
        });
        mStorage = FirebaseStorage.getInstance();
        msgsList = new ArrayList<>();

        sharedPrefLogin = getSharedPreferences("userLoginData",MODE_PRIVATE);
        userId = sharedPrefLogin.getString("ID","null");
        userName = sharedPrefLogin.getString("fullName","null");
        userBusiness = sharedPrefLogin.getString("account","null");
        userPic = sharedPrefLogin.getString("URI","null");
        userType = sharedPrefLogin.getString("type","null");

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatting").child("Chat"+userId);
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Chatting").child("Chat"+receiverId);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = edtChat.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    edtChat.setError("Type something");
                    edtChat.requestFocus();
                    return;
                }
                String id = databaseReference.push().getKey();
                String cDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String cTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
                ChatModelClass chat = new ChatModelClass(id, userId, userName, userBusiness, userPic, receiverId, "null",
                        message, cDate, cTime, "text message", "text", "unread");
                databaseReference.child(id).setValue(chat);
                databaseReference2.child(id).setValue(chat);
                edtChat.setText("");

                new MyFirebaseInstanceService().sendMessageSingle(ChattingActivity.this, fcmToken, userName + " " + userType, message, null);

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence arr[] = new CharSequence[]{
                        "Images",
                        "PDF Files",
                        "MS Word Files"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);
                builder.setTitle("Select");
                builder.setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            checker = "image";
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
                            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                        }
                        if(i==1) {
                            checker = "pdf";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!mGranted) {
                                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                                        return;
                                    }
                                }
                            }
                            Intent intent = new Intent();
                            intent.setType("application/pdf");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select PDF File"), PICK_IMAGE_REQUEST);
                        }
                        if(i == 2){
                            checker = "docx";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!mGranted) {
                                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                                        return;
                                    }
                                }
                            }
                            Intent intent = new Intent();
                            intent.setType("application/msword");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Word File"), PICK_IMAGE_REQUEST);
                        }
                        Toast.makeText(ChattingActivity.this, checker, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatModelClass chat = msgsList.get(i);
                if(chat.getFileType().equals("pdf") || chat.getFileType().equals("docx")){
                    try {

//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getPicUrl()));
//                    startActivity(intent);
                        Uri uri = Uri.parse(chat.getPicUrl());
                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri)
                                .setTitle("File Download")
                                .setDescription("File is downloading..")
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        downloadManager.enqueue(request);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }else if(chat.getFileType().equals("image")){
                    Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                    intent.putExtra("URI", chat.getPicUrl());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    msgsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModelClass chat = snapshot.getValue(ChatModelClass.class);
                        if (userId.equals(chat.getSenderId()) && receiverId.equals(chat.getRecieverId()) || userId.equals(chat.getRecieverId()) && receiverId.equals(chat.getSenderId())) {
                            msgsList.add(chat);
                            flag = true;
                        }
                        if(chat.getFlag().equals("unread")){

                            databaseReference.child(chat.getId()).child("flag").setValue("read");
                        }
                    }
                    if (flag){
                        ChattingMsgsAdapter adapter = new ChattingMsgsAdapter(ChattingActivity.this, msgsList);
                        listView.setAdapter(adapter);
                        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
                        listView.setMultiChoiceModeListener(multiChoiceModeListener);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
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

        if (requestCode == PICK_IMAGE_REQUEST && data != null) {
            fileUri = data.getData();

            if (!checker.equals("image")) {
                selectedfile.setVisibility(View.VISIBLE);
                Picasso.with(this).load(R.drawable.file).fit().centerCrop().placeholder(R.drawable.file).into(selectedfile);
                addFile();
            }else if (checker.equals("image")) {
                selectedfile.setVisibility(View.VISIBLE);
                Picasso.with(this).load(fileUri).fit().centerCrop().placeholder(R.drawable.loading).into(selectedfile);
                addData();
            }else {
            Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
          }
        }
      }

    private void addFile() {
        loading.setVisibility(View.VISIBLE);
        if(fileUri !=null){
            mRef = FirebaseStorage.getInstance().getReference("DocumentFiles/");
            final StorageReference fileref = mRef.child(System.currentTimeMillis() + "." + checker);

            mUploadTask =   fileref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                String id = databaseReference.push().getKey();
                                String cDate = new SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault()).format(new Date());
                                String cTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
                                ChatModelClass chat = new ChatModelClass(id,userId,userName,userBusiness,userPic,receiverId,uri.toString(),
                                        "",cDate,cTime,fileUri.getLastPathSegment(),checker, "unread");
                                databaseReference.child(id).setValue(chat);
                                databaseReference2.child(id).setValue(chat);
                                new MyFirebaseInstanceService().sendMessageSingle(ChattingActivity.this,fcmToken,userName, fileUri.getLastPathSegment(), null);

                                loading.setVisibility(View.GONE);
                                selectedfile.setVisibility(View.GONE);

                                fileUri = Uri.EMPTY;
                            } catch (Exception ex ){
                                Toast.makeText(getApplicationContext()  , "err" + ex.toString() , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    TextView tv = findViewById(R.id.tvNoDealersM);
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    tv.setText(""+progress);
                }
            });
        }
    }

    private void addData() {

        loading.setVisibility(View.VISIBLE);

        if(fileUri !=null){
            mRef = FirebaseStorage.getInstance().getReference("ImagesFile/");
            final StorageReference fileref = mRef.child(System.currentTimeMillis() + "." + getExtension(fileUri));

            mUploadTask =   fileref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                String id = databaseReference.push().getKey();
                                String cDate = new SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault()).format(new Date());
                                String cTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
                                ChatModelClass chat = new ChatModelClass(id,userId,userName,userBusiness,userPic,receiverId,uri.toString(),
                                        "",cDate,cTime,fileUri.getLastPathSegment(),checker, "unread");
                                databaseReference.child(id).setValue(chat);
                                databaseReference2.child(id).setValue(chat);

                                new MyFirebaseInstanceService().sendMessageSingle(ChattingActivity.this,fcmToken,userName, fileUri.getLastPathSegment(), null);

                                loading.setVisibility(View.GONE);
                                selectedfile.setVisibility(View.GONE);

                                fileUri = Uri.EMPTY;

                            } catch (Exception ex ){
                                Toast.makeText(getApplicationContext()  , "err" + ex.toString() , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    TextView tv = findViewById(R.id.tvNoDealersM);
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    tv.setText(""+progress);
                }
            });
        }
    }

    private  String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton() ;
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu , menu);
        //item = menu.findItem(R.id.navi_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.chat_logout){
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setBackgroundColor(Color.BLUE);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setBackgroundColor(Color.BLUE);

            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.contexual_menu,menu);
            isActionMode=true;
            actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setBackgroundColor(Color.BLUE);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.delete) {
                if (userSelection.size() > 0) {
                    delete();
                    userSelection.clear();
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            actionMode = null;
            userSelection.clear();
            isActionMode=false;
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setBackgroundColor(Color.parseColor("#4EA0E9"));
        }
    };

    private void delete() {
            for(final ChatModelClass chat : userSelection){
                if (chat.getFileType().equals("text")) {
                    Toast.makeText(ChattingActivity.this, "true ", Toast.LENGTH_SHORT).show();
                    databaseReference.child(chat.getId()).removeValue();
                }else {
                    StorageReference imgRef = mStorage.getReferenceFromUrl(chat.getPicUrl());
                    imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child(chat.getId()).removeValue();
                        }
                    });
                }
            }
    }
}