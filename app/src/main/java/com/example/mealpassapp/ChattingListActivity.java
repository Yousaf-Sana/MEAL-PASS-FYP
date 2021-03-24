package com.example.mealpassapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mealpassapp.adapters.ChattingListAdapter;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.ChatModelClass;
import com.example.mealpassapp.model.UsersModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChattingListActivity extends BaseActivity {
DatabaseReference databaseReference;
ListView listView;
List<ChatModelClass> orderList;
List<String> check;
TextView noManual;
SharedPreferences sharedPreferences;
String userId, userType;
String receiverBusiness , receiverName, receiverPic;
Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        showProgressDialog();
        orderList = new ArrayList<>();
        check = new ArrayList<>();
        check.add("jhdbh");

        listView = findViewById(R.id.manualList);
        noManual = findViewById(R.id.noManual);

        toolbar = findViewById(R.id.chatList);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("userLoginData",MODE_PRIVATE);
        userId = sharedPreferences.getString("ID","null");
        userType = sharedPreferences.getString("type","null");

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatting").child("Chat"+userId);

        loadDataFromFirebase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if(isNetworkAvailable()) {
                    ChatModelClass chat = orderList.get(pos);
                    if(userId.equals(chat.getSenderId())){
                        getReceiverRecord(chat.getRecieverId());
                    }else if(userId.equals(chat.getRecieverId())){
                        Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                        intent.putExtra("id", chat.getSenderId());
                        intent.putExtra("firstName", chat.getSenderName());
                        intent.putExtra("userPic", chat.getSenderPic());
                        startActivity(intent);
                        finish();
                    }
                }else {
                   // Snackbar.make(view, "Network not available !!!", Snackbar.LENGTH_LONG).setAction("Ok", null).show();

                    final Snackbar snackbar =   Snackbar.make(view, "Network not available !!!", Snackbar.LENGTH_LONG)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });
                    snackbar.show();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ChatModelClass chat = orderList.get(pos);
                deleteRecord(chat);
                return true;
            }
        });
    }

    private void getReceiverRecord(final String receiverId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UsersModel user = snapshot.getValue(UsersModel.class);
                    if(receiverId.equals(user.getId())){
                        receiverName = user.getName();
                        receiverBusiness = user.getAccount();
                        receiverPic = user.getImageUri();
                    }
                }
                Intent intent = new Intent(getApplicationContext() , ChattingActivity.class);
                intent.putExtra("id",receiverId);
                intent.putExtra("firstName" , receiverName);
                intent.putExtra("userPic",receiverPic);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
    }

    private void deleteRecord(final ChatModelClass chat) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation dialog");
        builder.setMessage("Are you sure to delete ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chatting").child("Chat"+userId).child(chat.getId());
                dbRef.removeValue();
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

    public void loadDataFromFirebase() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                check.clear();
                noManual.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatModelClass chat = snapshot.getValue(ChatModelClass.class);
                    if(userId.equals(chat.getSenderId())){
                        if(!check.contains(chat.getRecieverId())){
                            orderList.add(chat);
                            check.add(chat.getRecieverId());
                        }else {
                            continue;
                        }
                    }else if(userId.equals(chat.getRecieverId())){
                        if(!check.contains(chat.getSenderId())){
                            orderList.add(chat);
                            check.add(chat.getSenderId());
                        }else {
                            continue;
                        }
                    }
                }
                if(orderList.size() > 0){
                    Collections.reverse(orderList);
                    ChattingListAdapter adapter = new ChattingListAdapter(ChattingListActivity.this , orderList);
                    listView.setAdapter(adapter);
                }else {
                    noManual.setText("No Chats!!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNet = cm.getActiveNetworkInfo();
        return activeNet != null && activeNet.isConnected();
    }
}