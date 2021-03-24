package com.example.mealpassapp;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealpassapp.adapters.SliderAdapter;
import com.example.mealpassapp.adapters.ViewFoodItemListAdapter;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.FoodModel;
import com.example.mealpassapp.model.FoodModel2;
import com.example.mealpassapp.model.ItemCommentsModelClass;
import com.example.mealpassapp.model.OrderStatusModelClass;
import com.example.mealpassapp.model.UsersModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ViewFoodItemActivity extends BaseActivity implements View.OnLongClickListener, Comparator<FoodModel2> {
RecyclerView recyclerView ;
DatabaseReference databaseReference;
ViewFoodItemListAdapter imageAdapter;
TextView tvNoItems, tvItems;
List<FoodModel> list;
List<FoodModel2> finalList;
public static List<FoodModel2> user_selection = new ArrayList<>();
List<FoodModel> slidesList;
List<ItemCommentsModelClass> ratingList;
Toolbar toolbar;
SharedPreferences sharedPrefLogin;
String userId="", userName, userBusiness, userPic;
public boolean is_in_action_mode;
public int counter=0;
//ViewPager viewPager;
//TabLayout indicator;
private String key="";
boolean flag = false;
public static String sellerName,sellerPic, sellerId, token, pic, itemId, itemName;
public static float discount;
public static int points, price;
public static boolean delivery = false;
public int counter2=0;
float totalRating=0;

//Location myLocation , othersLoc;
//double myLatitude, myLongitude;
//double distance;
//List<UsersModel> usersList;
//List<FoodModel> ItemsList;
//List<String> Items;
//ProgressBar progressBar;
//TextView textView;

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
                        imageAdapter.getFilter().filter(newText);
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
        setContentView(R.layout.activity_view_food_item);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        if(key.equals("all")){
            flag = true;
        }
        slidesList = new ArrayList<>();
        finalList = new ArrayList<>();
        list = new ArrayList<>();
        ratingList = new ArrayList<>();
//        usersList = new ArrayList<>();
//        ItemsList = new ArrayList<>();
//        Items = new ArrayList<>();

        showProgressDialog();
        tvNoItems = findViewById(R.id.tvNoitems2);
        tvItems = findViewById(R.id.textIt);
//        textView = findViewById(R.id.tvR);
//        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView2) ;
        recyclerView.setHasFixedSize(true); ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.itemstoolbar2);
        toolbar.setTitle("Search items");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.searchmenu);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItem/");

        sharedPrefLogin = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);

        userId = sharedPrefLogin.getString("ID","null");
        userName =  sharedPrefLogin.getString("fullName","null");
        userBusiness = sharedPrefLogin.getString("account","null");
        userPic = sharedPrefLogin.getString("URI","null");

//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        indicator = (TabLayout) findViewById(R.id.indicator);

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                finalList.clear();
                tvNoItems.setText("");
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    FoodModel item = dataSnapshot1.getValue(FoodModel.class);
                    if(!flag){
                        if(key.equals(item.getCategory())){
                            list.add(item);
                        }
                    }else {
                        list.add(item);
                    }

                }
                if(list.size()>0){
                    for(FoodModel obj : list){
                        countItemRating(obj);
                    }
                    //recommendations();
                }else {
                    tvNoItems.setText("No items found");
                    hideProgressDialog();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }});
    }


    private void countItemRating(final FoodModel obj) {

        DatabaseReference dbRefComment = FirebaseDatabase.getInstance().getReference("ItemComments/");
        dbRefComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemCommentsModelClass comment = snapshot.getValue(ItemCommentsModelClass.class);
                    if (obj.getId().equals(comment.getItemId())) {
                        ratingList.add(comment);
                    }
                }
                for (ItemCommentsModelClass obj : ratingList) {
                    totalRating = totalRating + obj.getRating();
                    counter2 = counter2 + 1;
                }

                int rating = (int) (totalRating / counter);
                FoodModel2 item = new FoodModel2(obj.getId(),obj.getFoodTitle(),obj.getPrice(),obj.getPoints(),obj.getDiscount(),
                        obj.getCategory(),obj.getFoodPic(), obj.getSellerId(), obj.getSellName(), obj.getSellerPic(), obj.getSellerToken(),
                        obj.getDate(),obj.isDelivery(),obj.getFlag(),rating);
                finalList.add(item);
                Collections.sort(finalList, new ViewFoodItemActivity());
                imageAdapter = new ViewFoodItemListAdapter(ViewFoodItemActivity.this, finalList);
                recyclerView.setAdapter(imageAdapter);
                hideProgressDialog();
                totalRating = 0;
                counter2 = 0;
                ratingList.clear();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
    }
    @Override
    public boolean onLongClick(View view) {
        try {
            toolbar.getMenu().clear();
            is_in_action_mode = true;
            toolbar.inflateMenu(R.menu.add_to_cart_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
            imageAdapter.notifyDataSetChanged();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void prepareSelection(View v, int position) {

        CheckBox cb= (CheckBox) v;
        if(cb.isChecked()){
            counter=counter+1;
            updateCounter(counter);
            user_selection.add(finalList.get(position));
        }
        else{
            counter=counter-1;
            updateCounter(counter);
            user_selection.remove(finalList.get(position));
        }
    }

    private void updateCounter(int counter) {
        toolbar.setTitle(counter+" items selected");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                updateToolbar();
                break;
            case R.id.add_cart:
                if(user_selection.size()>0){
                    addToCart();
                }else {
                    Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(is_in_action_mode){
            updateToolbar();
        }
        else{
            super.onBackPressed();}
    }

    private void addToCart() {
        DatabaseReference dbRef =  FirebaseDatabase.getInstance().getReference("AddToCartItems").child(userId);
        if(user_selection.size()>0){
            for(FoodModel2 item : user_selection){
                dbRef.child(item.getId()).setValue(item);
            }
        }
        updateToolbar();
        user_selection.clear();
        Toast.makeText(getApplicationContext(), "Items added to cart", Toast.LENGTH_SHORT).show();
    }

    private void updateToolbar() {
        try {
            toolbar.getMenu().clear();
            is_in_action_mode = false;
            counter = 0;
            toolbar.inflateMenu(R.menu.searchmenu);
            toolbar.setTitle("Search items");
            setSupportActionBar(toolbar);
          //  getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            imageAdapter.notifyDataSetChanged();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateToolbar();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public int compare(FoodModel2 obj1, FoodModel2 obj2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Integer.compare(obj2.getRating(),obj1.getRating());
        }
        return 0;
    }

//    private void recommendations() {
//        myLocation = new Location("myLoc");
//        myLocation.setLatitude(myLatitude);
//        myLocation.setLongitude(myLongitude);
//
//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    UsersModel user = snapshot.getValue(UsersModel.class);
//
//                    othersLoc = new Location("OtherLoc");
//                    othersLoc.setLatitude(user.getLatitude());
//                    othersLoc.setLongitude(user.getLongtitude());
//
//                    distance = myLocation.distanceTo(othersLoc)/1000;
//                    if(distance<=5 && !userId.equals(user.getId())){
//                        usersList.add(user);
//                    }
//                }
//                if(usersList.size()>0){
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//                for(UsersModel user : usersList){
//                    getFood(user.getId());
//                }
//                if(Items.size()<1){
//                    progressBar.setVisibility(View.GONE);
//                }
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        viewPager.setAdapter(new SliderAdapter(ViewFoodItemActivity.this, ItemsList));
//                        indicator.setupWithViewPager(viewPager, true);
//                        progressBar.setVisibility(View.GONE);
//
//                        Timer timer = new Timer();
//                        timer.scheduleAtFixedRate(new ViewFoodItemActivity.SliderTimer(), 5000, 6000);
//                    }
//                },5000);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }});
//    }

//    private void getFood(final String userId){
//
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("FoodOrderStatus/");
//        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    OrderStatusModelClass order = snapshot.getValue(OrderStatusModelClass.class);
//                    if(userId.equals(order.getCustId())){
//                        checkRating(order.getItemId());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    private void checkRating(final String itemId){
//        DatabaseReference dbRefComment = FirebaseDatabase.getInstance().getReference("ItemComments/");
//        dbRefComment.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    ItemCommentsModelClass comments = snapshot.getValue(ItemCommentsModelClass.class);
//                    if(itemId.equals(comments.getItemId())){
//                        totalRating = totalRating + comments.getRating();
//                        counter = counter + 1;
//                    }
//                }
//                int rating = (int) (totalRating / counter);
//                if(rating>=4){
//                    getItems(itemId);
//                    textView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }});
//    }

//    private void getItems(final String itemId) {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodItem/") ;
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    FoodModel item = snapshot.getValue(FoodModel.class);
//                    if(itemId.equals(item.getId()) && !ItemsList.contains(item)){
//                        ItemsList.add(item);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }});
//    }

//    private class SliderTimer extends TimerTask {
//        @Override
//        public void run() {
//            ViewFoodItemActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (viewPager.getCurrentItem() < ItemsList.size() - 1) {
//                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
//                    } else {
//                        viewPager.setCurrentItem(0);
//                    }
//                }
//            });
//        }
//    }

}