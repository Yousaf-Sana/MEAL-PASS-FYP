package com.example.mealpassapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealpassapp.adapters.CartItemListAdapter;
import com.example.mealpassapp.adapters.ViewFoodItemListAdapter;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.FoodModel;
import com.example.mealpassapp.model.FoodModel2;
import com.example.mealpassapp.model.ItemCommentsModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CartItemsActivity extends BaseActivity implements Comparator<FoodModel2> {
RecyclerView recyclerView ;
DatabaseReference databaseReference;
CartItemListAdapter imageAdapter;
TextView tvNoItems, tvItems;
List<FoodModel> list;
List<FoodModel2> finalList;
List<ItemCommentsModelClass> ratingList;
Toolbar toolbar;
SharedPreferences sharedPrefLogin;
String userId="", userName, userBusiness, userPic;
public int counter=0;
public static String sellerName,sellerPic, sellerId, token, pic, itemId, itemName;
public static float discount;
public static int points, price;
public static boolean delivery = false;
public int counter2=0;
float totalRating=0;

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

        finalList = new ArrayList<>();
        list = new ArrayList<>();
        ratingList = new ArrayList<>();

        showProgressDialog();
        tvNoItems = findViewById(R.id.tvNoitems2);
        tvItems = findViewById(R.id.textIt);
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

        sharedPrefLogin = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);

        userId = sharedPrefLogin.getString("ID","null");
        userName =  sharedPrefLogin.getString("fullName","null");
        userBusiness = sharedPrefLogin.getString("account","null");
        userPic = sharedPrefLogin.getString("URI","null");

        databaseReference = FirebaseDatabase.getInstance().getReference("AddToCartItems").child(userId);

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
                    list.add(item);
                }
                if(list.size()>0){
                    for(FoodModel obj : list){
                        countItemRating(obj);
                    }
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
                Collections.sort(finalList, new CartItemsActivity());
                imageAdapter = new CartItemListAdapter(CartItemsActivity.this, finalList);
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
    public int compare(FoodModel2 obj1, FoodModel2 obj2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Integer.compare(obj2.getRating(),obj1.getRating());
        }
        return 0;
    }
}