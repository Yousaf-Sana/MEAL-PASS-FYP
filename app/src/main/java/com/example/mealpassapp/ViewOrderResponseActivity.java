package com.example.mealpassapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mealpassapp.adapters.ViewOrderResponseListAdapter;
import com.example.mealpassapp.helpers.BaseActivity;
import com.example.mealpassapp.model.OrderStatusModelClass;
import com.example.mealpassapp.model.PayModelClass;
import com.example.mealpassapp.model.UsersModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewOrderResponseActivity extends BaseActivity {
ListView listView;
TextView textView;
DatabaseReference databaseReference, dbRefPayment;
List<OrderStatusModelClass> list;
String userId;
public static String itemId, orderId;
String dealerpayType="", dealerpayNumber="", dealerName="", dealerPic="", dealerContact="", dealerbusiness="";
Context context = ViewOrderResponseActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_response);

        showProgressDialog();

        listView = findViewById(R.id.listItem);
        textView = findViewById(R.id.noStatus);

        databaseReference = FirebaseDatabase.getInstance().getReference("FoodOrderStatus/");
        list = new ArrayList<>();
        userId = UserActivity.userId;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final OrderStatusModelClass status = list.get(position);
                if(status.getStatus().equals("Accepted")){
                    getUserPayDetails(status.getDealerId());
                }else if(status.getStatus().equals("Accepted2")){
                    Toast.makeText(getApplicationContext(), "Purchased from loyalty points", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Deal is rejected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderStatusModelClass status = snapshot.getValue(OrderStatusModelClass.class);
                    if(userId.equals(status.getCustId())){
                        list.add(status);
                    }
                }
                if(list.size()>0){
                    ViewOrderResponseListAdapter adapter = new ViewOrderResponseListAdapter(ViewOrderResponseActivity.this, list);
                    listView.setAdapter(adapter);
                }else {
                    textView.setText("No Response !!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserPayDetails(final String foodSellerId) {
        dbRefPayment = FirebaseDatabase.getInstance().getReference("UserPaymentDetails/").child(foodSellerId);
        dbRefPayment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PayModelClass pay = snapshot.getValue(PayModelClass.class);
                    dealerpayType = String.valueOf(pay.getFileType());
                    dealerpayNumber = String.valueOf(pay.getImageUri());
                }
                getUssrDetails(foodSellerId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
    }
    private void getUssrDetails(final String dealerId) {
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users/");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UsersModel user = snapshot.getValue(UsersModel.class);
                    if (dealerId.equals(user.getId())){
                        dealerbusiness = user.getAccount();
                        dealerContact = user.getPhone();
                        dealerPic = user.getImageUri();
                        dealerName = user.getName();
                    }
                }
                BottomSheetFunction();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
    }

    private void BottomSheetFunction() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewOrderResponseActivity.this , R.style.BtoonSheetDialogTheme);
        View dialog = LayoutInflater.from(ViewOrderResponseActivity.this)
                .inflate(R.layout.fragment_bottom_sheet , (LinearLayout) bottomSheetDialog.findViewById(R.id.bottom_sheet_container));

        ImageView userPic, payMethod;
        final TextView userName, userbusiness, userContact, payThrough, payNumber;
        Button pay;

        pay = dialog.findViewById(R.id.paySubmit);
        userPic = dialog.findViewById(R.id.payDealerPic);
        userbusiness = dialog.findViewById(R.id.paybusiness);
        userContact = dialog.findViewById(R.id.payContact);
        userName = dialog.findViewById(R.id.payName);
        payThrough = dialog.findViewById(R.id.payThrough);
        payNumber = dialog.findViewById(R.id.userPayNumber);
        payMethod = dialog.findViewById(R.id.payType);

        Picasso.with(context).load(dealerPic).fit().centerCrop().placeholder(R.drawable.loading).into(userPic);
        userbusiness.setText(dealerbusiness);
        userContact.setText(dealerContact);
        userName.setText(dealerName);
        payNumber.setText(dealerpayNumber);

        if(dealerpayType.equals("easypaisa")){
            Picasso.with(context).load(R.drawable.easypaisa_icon).fit().centerCrop().placeholder(R.drawable.easypaisa_icon).into(payMethod);
            payThrough.setText("Pay through easypaisa");
        }else if(dealerpayType.equals("jazzcash")){
            Picasso.with(context).load(R.drawable.jazzcash_icon).fit().centerCrop().placeholder(R.drawable.jazzcash_icon).into(payMethod);
            payThrough.setText("Pay through jazzcash");
        }else if(dealerpayType.equals("UBL Bank Account")){
            Picasso.with(context).load(R.drawable.ubl_icon).fit().centerCrop().placeholder(R.drawable.ubl_icon).into(payMethod);
            payThrough.setText("Pay through UBL App");
        } else {
            Picasso.with(context).load(R.drawable.loading).fit().centerCrop().placeholder(R.drawable.loading).into(payMethod);
            payThrough.setText("Pay method not set yet");
        }

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dealerpayType.equals("easypaisa")){
                    try {
                        Intent broadcasIntent = new Intent(context, MessageReceiver.class);
                        broadcasIntent.setAction("com.example.farmersapp.SEND_MESSAGE");
                        broadcasIntent.putExtra("message", dealerpayNumber);
                        broadcasIntent.putExtra("title", "User payment number");
                        context.sendBroadcast(broadcasIntent);

                        Intent intent = context.getPackageManager().getLaunchIntentForPackage("pk.com.telenor.phoenix");
                        context.startActivity(intent);

                    }catch (Exception ex){
                        Intent broadcasIntent = new Intent(context, MessageReceiver.class);
                        broadcasIntent.setAction("com.example.farmersapp.SEND_MESSAGE");
                        broadcasIntent.putExtra("message", dealerpayNumber);
                        broadcasIntent.putExtra("title", "User payment number");
                        context.sendBroadcast(broadcasIntent);

                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pk.com.telenor.phoenix"));
                        startActivity(intent2);
                    }
                }else if(dealerpayType.equals("jazzcash")){
                    try {
                        Intent broadcasIntent = new Intent(context, MessageReceiver.class);
                        broadcasIntent.setAction("com.example.farmersapp.SEND_MESSAGE");
                        broadcasIntent.putExtra("message", dealerpayNumber);
                        broadcasIntent.putExtra("title", "User payment number");
                        context.sendBroadcast(broadcasIntent);

                        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.techlogix.mobilinkcustomer");
                        context.startActivity(intent);

                    }catch (Exception ex){
                        Intent broadcasIntent = new Intent(context, MessageReceiver.class);
                        broadcasIntent.setAction("com.example.farmersapp.SEND_MESSAGE");
                        broadcasIntent.putExtra("message", dealerpayNumber);
                        broadcasIntent.putExtra("title", "User payment number");
                        context.sendBroadcast(broadcasIntent);

                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techlogix.mobilinkcustomer"));
                        startActivity(intent2);
                    }
                }else if(dealerpayType.equals("UBL Bank Account")){
                    try {
                        Intent broadcasIntent = new Intent(context, MessageReceiver.class);
                        broadcasIntent.setAction("com.example.farmersapp.SEND_MESSAGE");
                        broadcasIntent.putExtra("message", dealerpayNumber);
                        broadcasIntent.putExtra("title", "User UBL account number");
                        context.sendBroadcast(broadcasIntent);

                        Intent intent = context.getPackageManager().getLaunchIntentForPackage("app.com.brd");
                        context.startActivity(intent);

                    }catch (Exception ex){
                        Intent broadcasIntent = new Intent(context, MessageReceiver.class);
                        broadcasIntent.setAction("com.example.farmersapp.SEND_MESSAGE");
                        broadcasIntent.putExtra("message", dealerpayNumber);
                        broadcasIntent.putExtra("title", "User UBL account number");
                        context.sendBroadcast(broadcasIntent);

                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.com.brd"));
                        startActivity(intent2);
                    }
                }
                else {
                    Toast.makeText(context, "No payment method found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomSheetDialog.setContentView(dialog);
        bottomSheetDialog.show();
    }

}
