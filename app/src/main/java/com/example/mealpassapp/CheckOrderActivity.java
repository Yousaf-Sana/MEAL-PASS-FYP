package com.example.mealpassapp;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.mealpassapp.adapters.CheckOrdersListAdapter;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CheckOrderActivity extends BaseActivity {
DatabaseReference databaseReference , dbReforderstatus , dbRefToken, dbOrderdelete;
ListView listView;
TextView tvnoOrder;
SharedPreferences sharedPreferences;
List<FoodOrderModel> orderList;
String dealerID , dealerPhone , dealerPic, key="";
boolean flag = false;
CheckOrdersListAdapter adapter;
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
        setContentView(R.layout.activity_check_order);

        showProgressDialog();

        databaseReference = FirebaseDatabase.getInstance().getReference("FoodOrder/");
        dbReforderstatus = FirebaseDatabase.getInstance().getReference("FoodOrderStatus/");
        listView = findViewById(R.id.orderslist);
        tvnoOrder = findViewById(R.id.tvNoOrder);

        orderList = new ArrayList<>();

        sharedPreferences = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);
        dealerID = sharedPreferences.getString("ID","null");
        dealerPhone = sharedPreferences.getString("phone","null");
        dealerPic = sharedPreferences.getString("URI","null");

        toolbar = findViewById(R.id.orderBar);
        toolbar.setTitle("Orders");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.searchmenu);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        if(key.equals("history")){
            loadOrdersHistory();
        }else if(key.equals("pending")){
            loadPendingOrders();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final FoodOrderModel order = orderList.get(i);
                final String orderId = order.getId();

                    AlertDialog.Builder statusDeleteBuilder = new AlertDialog.Builder(CheckOrderActivity.this);
                    statusDeleteBuilder.setTitle("Delete ?");
                    statusDeleteBuilder.setMessage("Are you sure to accept this order?").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String response = "";
                            if(order.getFlag().equals("pending")){
                                response = "Accepted";
                            }else if(order.getFlag().equals("pending2")){
                                response = "Accepted2";
                            }
                            String id = dbReforderstatus.push().getKey();
                            String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                            OrderStatusModelClass orderstatusaccept = new OrderStatusModelClass(id, orderId, order.getUserId(), "Accepted",order.getFoodId(), order.getFoodName(),
                                    order.getFoodPic(), order.getSellerId(), order.getSellerName(), order.getQuantity(), order.getFoodPrice(), order.getDiscount(), date, "provided");
                            dbReforderstatus.child(id).setValue(orderstatusaccept);
                            Toast.makeText(getApplicationContext(), "Order Accepted", Toast.LENGTH_LONG).show();

                            FoodOrderModel obj = new FoodOrderModel(order.getId(), order.getFoodName(), order.getFoodId(), order.getFoodPic(),order.getPoints(),order.getSellerId(),
                                    order.getSellerName(), order.getFoodPrice(), order.getDiscount(), order.getQuantity(), order.getUserId(), order.getUserName(),
                                    order.getUserPic(), order.getDate(), response, order.getUserToken(), order.isDeliver());
                            databaseReference.child(order.getId()).setValue(obj);

                            String fcmToken = order.getUserToken();

                            new MyFirebaseInstanceService().sendMessageSingle(CheckOrderActivity.this, fcmToken, "Alert", "Food Seller responded to your order", null);

                            int total = order.getFoodPrice() * order.getQuantity();
                            addUpPoints(order.getUserId(), response, total);

                        }
                    }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String id = dbReforderstatus.push().getKey();
                            String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                            OrderStatusModelClass orderstatusaccept = new OrderStatusModelClass(id, orderId, order.getUserId(), "Rejected",order.getFoodId(), order.getFoodName(),
                                    order.getFoodPic(), order.getSellerId(), order.getSellerName(), order.getQuantity(), order.getFoodPrice(), order.getDiscount(), date, "provided");
                            dbReforderstatus.child(id).setValue(orderstatusaccept);
                            Toast.makeText(getApplicationContext(), "Order Rejected", Toast.LENGTH_LONG).show();

                            FoodOrderModel obj = new FoodOrderModel(order.getId(), order.getFoodName(), order.getFoodId(), order.getFoodPic(),order.getPoints(),order.getSellerId(),
                                    order.getSellerName(), order.getFoodPrice(), order.getDiscount(), order.getQuantity(), order.getUserId(), order.getUserName(),
                                    order.getUserPic(), order.getDate(), "Rejected", order.getUserToken(), order.isDeliver());
                            databaseReference.child(order.getId()).setValue(obj);

                            String fcmToken = order.getUserToken();

                            new MyFirebaseInstanceService().sendMessageSingle(CheckOrderActivity.this, fcmToken, "Alert", "Food Seller responded to your order", null);
                        }
                    });
                    AlertDialog dialog2 = statusDeleteBuilder.create();
                    dialog2.show();

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(key.equals("history")){
                    final FoodOrderModel order = orderList.get(position);
                    final String orderId = order.getId();

                    AlertDialog.Builder statusDeleteBuilder = new AlertDialog.Builder(CheckOrderActivity.this);
                    statusDeleteBuilder.setTitle("Delete ?");
                    statusDeleteBuilder.setMessage("Select order to delete").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dbOrderdelete = FirebaseDatabase.getInstance().getReference("FoodOrder/").child(orderId);
                            dbOrderdelete.removeValue();
                            Toast.makeText(getApplicationContext(), "Order Deleted", Toast.LENGTH_LONG).show();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog2 = statusDeleteBuilder.create();
                    dialog2.show();
                }
                return true;
            }
        });
    }

    private void addUpPoints(final String userId, String response, final int total) {
        if(response.equals("Accepted2")){
            return;
        }
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UsersModel user = snapshot.getValue(UsersModel.class);
                    if(userId.equals(user.getId())){
                        int currentPoints = user.getFlag();
                        int newPoints = 0;
                        if(total>=250 && total<=500){
                            newPoints = 1;
                        }else if(total>500 && total<=999){
                            newPoints = 2;
                        }else if(total>1000 && total<=1500){
                            newPoints = 3;
                        }else if(total>1500 && total<=2000){
                            newPoints = 4;
                        }else if(total>2000){
                            newPoints = 5;
                        }
                        int t = currentPoints + newPoints;
                        databaseReference.child(userId).child("flag").setValue(t);
                        Toast.makeText(CheckOrderActivity.this, newPoints+" loyalty points added to user account", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void loadOrdersHistory() {
        final String str1 = "Accepted", str2 = "Rejected", str3 = "Accepted2";
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                tvnoOrder.setText("");
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren() ){
                    FoodOrderModel orders = dataSnapshot1.getValue(FoodOrderModel.class);
                    if(dealerID.equals(orders.getSellerId())){
                        if(str1.equals(orders.getFlag()) || str2.equals(orders.getFlag()) || str3.equals(orders.getFlag())){
                            orderList.add(orders);
                        }
                    }
                }
                if (orderList.size()>0){
                    Collections.reverse(orderList);
                    adapter = new CheckOrdersListAdapter(CheckOrderActivity.this,orderList, "history");
                    listView.setAdapter(adapter);
                }else {
                    tvnoOrder.setText("No orders found");
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

    private void loadPendingOrders() {
        final String str = "pending";
        final String str2 = "pending2";
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
                    adapter = new CheckOrdersListAdapter(CheckOrderActivity.this,orderList, "pending");
                    listView.setAdapter(adapter);
                }else {
                    tvnoOrder.setText("No Orders Found");
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
}