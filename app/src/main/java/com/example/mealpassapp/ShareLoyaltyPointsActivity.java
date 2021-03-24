package com.example.mealpassapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.example.mealpassapp.adapters.UsersListAdapter;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.UsersModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShareLoyaltyPointsActivity extends BaseActivity {
DatabaseReference databaseReference;
ListView listView;
TextView textView;
List<UsersModel> list;
UsersListAdapter adapter;
int points;
String userId, userName;
Toolbar toolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        MenuItem searchmenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchmenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    adapter.getFilter().filter(newText);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_loyalty_points);

        showProgressDialog();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        list = new ArrayList<>();

        listView = findViewById(R.id.usersList);
        textView = findViewById(R.id.tvNoUser);

        points = UserActivity.points;
        userId = UserActivity.userId;
        userName = UserActivity.userName;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.searchmenu);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UsersModel user = list.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(ShareLoyaltyPointsActivity.this);
                builder.setTitle("Sharing Points");
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.share_points_layout, null);
                builder.setView(dialogView);
                final EditText edtPoints = dialogView.findViewById(R.id.edtPoints);
                Button submit = dialogView.findViewById(R.id.btnSub);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String targetId = user.getId();
                        int userPoints = user.getFlag();
                        int p = Integer.parseInt(edtPoints.getText().toString());
                        if(p>UserActivity.points){
                            Toast.makeText(ShareLoyaltyPointsActivity.this, "Entered points > your current points", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int total = userPoints+p;
                        databaseReference.child(targetId).child("flag").setValue(total);
                        int myPoints = points - p;
                        databaseReference.child(userId).child("flag").setValue(myPoints);
                        Toast.makeText(ShareLoyaltyPointsActivity.this, "Points Shared Successfully", Toast.LENGTH_SHORT).show();

                        new MyFirebaseInstanceService().sendMessageSingle(ShareLoyaltyPointsActivity.this,user.getToken(),"Loyalty Points Received", userName+" Shared "+p+" with you", null);

                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String str = "User";
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UsersModel user = snapshot.getValue(UsersModel.class);
                    if(str.equals(user.getType()) && !userId.equals(user.getId())){
                        list.add(user);
                    }

                }
                if(list.size()>0){
                    adapter = new UsersListAdapter(ShareLoyaltyPointsActivity.this, list);
                    listView.setAdapter(adapter);
                }else {
                    textView.setText("No Users!!");
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
            }
        });
    }
}
