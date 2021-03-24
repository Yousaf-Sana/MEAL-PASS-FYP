package com.example.mealpassapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.mealpassapp.adapters.CheckAccpetedOrdersListAdapter;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.FoodOrderModel;
import com.example.mealpassapp.model.OrderStatusModelClass;
import com.example.mealpassapp.model.UsersModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckDeliveryPointsActivity extends BaseActivity {
DatabaseReference databaseReference;
ListView listView;
TextView tvnoOrder;
SharedPreferences sharedPreferences;
List<FoodOrderModel> orderList;
String dealerID , dealerPhone , dealerPic;
boolean flag = false;
CheckAccpetedOrdersListAdapter adapter;
Toolbar toolbar;
List<OrderStatusModelClass> statusList;
public static double custLati, custLongi;

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
        setContentView(R.layout.activity_check_order);

        showProgressDialog();

        databaseReference = FirebaseDatabase.getInstance().getReference("FoodOrder/");
        listView = findViewById(R.id.orderslist);
        tvnoOrder = findViewById(R.id.tvNoOrder);

        orderList = new ArrayList<>();
        statusList = new ArrayList<>();

        sharedPreferences = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);
        dealerID = sharedPreferences.getString("ID","null");
        dealerPhone = sharedPreferences.getString("phone","null");
        dealerPic = sharedPreferences.getString("URI","null");

        toolbar = findViewById(R.id.orderBar);
        toolbar.setTitle("Customer Orders");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.searchmenu);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadAcceptedOrders();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final FoodOrderModel order = orderList.get(i);
                String custId = order.getUserId();
                getLoc(custId);
            }
        });
    }

    private void getLoc(final String custId) {
        showProgressDialog();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users/");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UsersModel user = snapshot.getValue(UsersModel.class);
                    if(custId.equals(user.getId())){
                        custLati = user.getLatitude();
                        custLongi = user.getLongtitude();
                        Intent intent = new Intent(getApplicationContext(), DeliveryPointsMapsActivity.class);
                        startActivity(intent);
                        hideProgressDialog();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void loadAcceptedOrders() {
        final String str = "Accepted";
        final String str2 = "Accepted2";
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvnoOrder.setText("");
                orderList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren() ){
                    FoodOrderModel orders = dataSnapshot1.getValue(FoodOrderModel.class);
                    if (dealerID.equals(orders.getSellerId()) && str.equals(orders.getFlag()) || dealerID.equals(orders.getSellerId()) && str2.equals(orders.getFlag())) {
                        orderList.add(orders);
                        flag = true;
                    }
                }
                if (flag){
                    Collections.reverse(orderList);
                    adapter = new CheckAccpetedOrdersListAdapter(CheckDeliveryPointsActivity.this,orderList);
                    listView.setAdapter(adapter);
                }else {
                    tvnoOrder.setText("No Accepted Orders Found");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), FoodSellerActivity.class);
        startActivity(intent);
        finish();
    }
}