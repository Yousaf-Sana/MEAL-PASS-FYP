package com.example.mealpassapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;

import com.example.mealpassapp.adapters.SliderAdapter;
import com.example.mealpassapp.helpers.Connectivity;
import com.example.mealpassapp.model.FoodModel;
import com.example.mealpassapp.model.ItemCommentsModelClass;
import com.example.mealpassapp.model.OrderStatusModelClass;
import com.example.mealpassapp.model.UsersModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserActivity extends AppCompatActivity {

Button viewFoodItem, viewResponse, profile, sharePoints, buyItemPoints, inbox;
SharedPreferences sharedPref , sharedPrefLogin;
SharedPreferences.Editor editor , editor2;
DatabaseReference databaseReference;
public static String userId="", URI, password="", longi="", lati="", phone="",userName ="", userAccount="", token="", flag;
public static int points;
Button pizza, burger, fries, drinks, tacos, top, shwarma, others;
ViewPager viewPager;
TabLayout indicator;
Integer[] myList = {R.drawable.burger, R.drawable.pizza, R.drawable.paratha, R.drawable.tacos};
List<Integer> list = new ArrayList<Integer>(myList.length);
Location myLocation , othersLoc;
TextView tvPoints;
double myLatitude, myLongitude;
double distance;
List<UsersModel> usersList;
List<String> Items;
List<FoodModel> ItemsList;
ProgressBar progressBar;
public static String sellerName, sellerId, sellerPic, token2, pic, itemId, itemName;
public static float discount;
public static int points2, price;
public static boolean delivery = false;
int totalRating = 0, counter = 0;
TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        usersList = new ArrayList<>();
        Items = new ArrayList<>();
        ItemsList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("StatusUser" , true);
        editor.apply();

        for(int i : myList){
            list.add(i);
        }
        sharedPrefLogin = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);

        password = sharedPrefLogin.getString("password","null");
        phone = sharedPrefLogin.getString("phone","null");
        longi = sharedPrefLogin.getString("longitude","11.1111");
        lati = sharedPrefLogin.getString("latitude","22.2222");
        userName = sharedPrefLogin.getString("fullName","null");
        URI = sharedPrefLogin.getString("URI","null");
        userAccount = sharedPrefLogin.getString("account","null");
        userId = sharedPrefLogin.getString("ID","null");
        token = sharedPrefLogin.getString("token","null");
        flag = sharedPrefLogin.getString("flag","null");

        myLatitude = Double.parseDouble(lati);
        myLongitude = Double.parseDouble(longi);
        editor2 = sharedPrefLogin.edit();

        viewFoodItem = findViewById(R.id.btnAllFood);
        viewResponse = findViewById(R.id.btnOrderResponse);
        profile = findViewById(R.id.btnProfile);
        sharePoints = findViewById(R.id.btnSharePoints);
        buyItemPoints = findViewById(R.id.btnPointsItem);
        inbox = findViewById(R.id.btnInbox);
        pizza = findViewById(R.id.btnPizza);
        fries = findViewById(R.id.btnFries);
        burger = findViewById(R.id.btnBurger);
        textView = findViewById(R.id.tvR);
        top = findViewById(R.id.btnTop);
        tacos = findViewById(R.id.btnTacos);
        drinks = findViewById(R.id.btnDrinks);
        shwarma = findViewById(R.id.btnShwarma);
        others = findViewById(R.id.btnOthers);
        progressBar = findViewById(R.id.progressbar);
        tvPoints = findViewById(R.id.points);

        pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Pizza");
                startActivity(intent);
            }
        });
        burger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Burger");
                startActivity(intent);
            }
        });
        shwarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Shwarma");
                startActivity(intent);
            }
        });
        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Drinks");
                startActivity(intent);
            }
        });
        fries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Fries");
                startActivity(intent);
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Paratha Roll");
                startActivity(intent);
            }
        });
        tacos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Tacos");
                startActivity(intent);
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                intent.putExtra("key","Others");
                startActivity(intent);
            }
        });
        viewFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , ViewFoodItemActivity.class);
                    intent.putExtra("key","all");
                    startActivity(intent);
                }else {
                    Toast.makeText(UserActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , ViewOrderResponseActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(UserActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , UpdateProfileActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(UserActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sharePoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , ShareLoyaltyPointsActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(UserActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buyItemPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , BuyItemsFromPointsActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(UserActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , ChattingListActivity.class));
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (TabLayout) findViewById(R.id.indicator);

        recommendations();
    }

    private void recommendations() {
        myLocation = new Location("myLoc");
        myLocation.setLatitude(myLatitude);
        myLocation.setLongitude(myLongitude);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UsersModel user = snapshot.getValue(UsersModel.class);

                    othersLoc = new Location("OtherLoc");
                    othersLoc.setLatitude(user.getLatitude());
                    othersLoc.setLongitude(user.getLongtitude());

                    distance = myLocation.distanceTo(othersLoc)/1000;
                    Toast.makeText(UserActivity.this, user.getId()+"  dis : "+distance, Toast.LENGTH_SHORT).show();
                    if(distance<=5 && !userId.equals(user.getId())){
                        usersList.add(user);
                    }
                }
                if(usersList.size()>0){
                    progressBar.setVisibility(View.VISIBLE);
                }
                for(UsersModel user : usersList){
                    getFood(user.getId());
                }
                if(Items.size()<1){
                    progressBar.setVisibility(View.GONE);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setAdapter(new SliderAdapter(UserActivity.this, ItemsList));
                        indicator.setupWithViewPager(viewPager, true);
                        progressBar.setVisibility(View.GONE);

                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new UserActivity.SliderTimer(), 5000, 6000);
                    }
                },5000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    private void getFood(final String userId){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("FoodOrderStatus/");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderStatusModelClass order = snapshot.getValue(OrderStatusModelClass.class);
                    if(userId.equals(order.getCustId())){
                        checkRating(order.getItemId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkRating(final String itemId){
        DatabaseReference dbRefComment = FirebaseDatabase.getInstance().getReference("ItemComments/");
        dbRefComment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ItemCommentsModelClass comments = snapshot.getValue(ItemCommentsModelClass.class);
                    if(itemId.equals(comments.getItemId())){
                        totalRating = totalRating + comments.getRating();
                        counter = counter + 1;
                    }
                }
                int rating = (int) (totalRating / counter);
                if(rating>=4){
                    getItems(itemId);
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    private void getItems(final String itemId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodItem/") ;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FoodModel item = snapshot.getValue(FoodModel.class);
                    if(itemId.equals(item.getId()) && !ItemsList.contains(item)){
                        ItemsList.add(item);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            UserActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < ItemsList.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.cart){
            startActivity(new Intent(getApplicationContext(), CartItemsActivity.class));
        }
        if (id == R.id.logout) {
           // startActivity(new Intent(getApplicationContext(), ShowMessage.class));
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Logout?");
            alert.setCancelable(false);
            alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editor.clear();
                    editor.apply();
                    editor2.clear();
                    editor2.apply();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(userId).child("token").setValue("null");
                    Intent intent = new Intent(getApplicationContext() , LoginScreenActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Sign Out", Toast.LENGTH_SHORT).show();
                }
            });
            alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        init();
    }



    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersModel user = dataSnapshot.getValue(UsersModel.class);
                points = user.getFlag();
                tvPoints.setText(points+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}