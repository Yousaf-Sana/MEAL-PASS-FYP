package com.example.mealpassapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealpassapp.adapters.CommentsListAdapter;
import com.example.mealpassapp.model.ItemCommentsModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodAllCommentsActivity extends AppCompatActivity {
TextView nocomments, tvRating;
List<ItemCommentsModelClass> commentsList;
ListView listViewComments;
DatabaseReference dbRefComment;
EditText edtComment;
SeekBar rating;
Button post;
String itemID="", customerId, customerName , customerPic, orderId="", decision="";
ProgressBar progressBar;
SharedPreferences sharedPrefLogin;
boolean flag = false, check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholesale_product_all_comments);

        dbRefComment = FirebaseDatabase.getInstance().getReference("ItemComments/");
        commentsList = new ArrayList<>();
        sharedPrefLogin = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        decision = intent.getStringExtra("decision");

        customerId =  sharedPrefLogin.getString("ID","null");
        customerName =  sharedPrefLogin.getString("fullName","null");
        customerPic = sharedPrefLogin.getString("URI","null");

        nocomments = findViewById(R.id.noComments);
        listViewComments = findViewById(R.id.lvcomments);
        progressBar = findViewById(R.id.progress);
        edtComment = findViewById(R.id.edtComment);
        rating = findViewById(R.id.seekbarRating);
        tvRating = findViewById(R.id.tvrating);
        post = findViewById(R.id.btnPost);

        if(decision.equals("viewRatings")){
            itemID = intent.getStringExtra("itemId");
            edtComment.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            rating.setVisibility(View.GONE);
            tvRating.setVisibility(View.GONE);
            loadRatingData();
        }else if(decision.equals("addRatings")){
            itemID = ViewOrderResponseActivity.itemId;
            orderId = ViewOrderResponseActivity.orderId;
            Toast.makeText(this, "item id : "+itemID, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "order id : "+orderId, Toast.LENGTH_SHORT).show();
            loadRatingData2();
        }

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userComment = edtComment.getText().toString().trim();
                int ratings = rating.getProgress();
                if(TextUtils.isEmpty(userComment)){
                    edtComment.setError("Type something");
                    edtComment.requestFocus();
                    return;
                }
                if(ratings<1){
                    Toast.makeText(FoodAllCommentsActivity.this, "Please provide rating", Toast.LENGTH_SHORT).show();
                    return;
                }
                String id = dbRefComment.push().getKey();
                ItemCommentsModelClass comment = new ItemCommentsModelClass(id, itemID, userComment, ratings, customerId, customerName, customerPic, orderId);
                dbRefComment.child(id).setValue(comment);
                Toast.makeText(FoodAllCommentsActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                edtComment.setText("");
            }
        });
    }
    public void loadRatingData() {
        dbRefComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                nocomments.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ItemCommentsModelClass comments = snapshot.getValue(ItemCommentsModelClass.class);
                    if(itemID.equals(comments.getItemId())) {
                        commentsList.add(comments);
                        flag = true;
                    }
                }
                if(flag) {
                    Collections.reverse(commentsList);
                    CommentsListAdapter adapter = new CommentsListAdapter(FoodAllCommentsActivity.this, commentsList);
                    listViewComments.setAdapter(adapter);
                }else {
                    nocomments.setText("No reviews");
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadRatingData2() {
        dbRefComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                nocomments.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ItemCommentsModelClass comments = snapshot.getValue(ItemCommentsModelClass.class);
                    if(itemID.equals(comments.getItemId())) {
                        commentsList.add(comments);
                        flag = true;
                    }
                }
                if(flag) {
                    Collections.reverse(commentsList);
                    CommentsListAdapter adapter = new CommentsListAdapter(FoodAllCommentsActivity.this, commentsList);
                    listViewComments.setAdapter(adapter);
                }else {
                    nocomments.setText("No reviews");
                }
                progressBar.setVisibility(View.INVISIBLE);

                for (ItemCommentsModelClass rate : commentsList) {
                    if (orderId.equals(rate.getOrderId())) {
                        check = true;
                        break;
                    }
                }
                if (check) {
                    edtComment.setEnabled(false);
                    post.setVisibility(View.GONE);
                    rating.setEnabled(false);
                    edtComment.setHint("You're already rated this");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
