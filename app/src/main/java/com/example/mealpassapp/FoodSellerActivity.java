package com.example.mealpassapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mealpassapp.helpers.Connectivity;
import com.example.mealpassapp.model.PayModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodSellerActivity extends AppCompatActivity {
    Button additem , checkOrder, profile, inbox, delivery, payment;
    SharedPreferences sharedPref , sharedPrefLogin;
    SharedPreferences.Editor editor , editor2;
    public static String Name , CNIC , Phone , URI, userId="", token, flag;
    public static double lati, longi;
    AlertDialog alertDialog;
    String payType="", payNumber="";
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_seller);

        additem = findViewById(R.id.btnAddItem);
        checkOrder = findViewById(R.id.btnCheck);
        delivery = findViewById(R.id.btnDelivery);
        payment = findViewById(R.id.btnPayment);
        profile = findViewById(R.id.btnProfile);
        inbox = findViewById(R.id.btnInbox);

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("StatusFood" , true);
        editor.apply();

        sharedPrefLogin = getSharedPreferences("userLoginData", Context.MODE_PRIVATE);

        Name = sharedPrefLogin.getString("fullName","null");
        CNIC =  sharedPrefLogin.getString("account","null");
        URI = sharedPrefLogin.getString("URI","null");
        Phone = sharedPrefLogin.getString("phone","null");
        userId = sharedPrefLogin.getString("ID","null");
        lati = Double.parseDouble(sharedPrefLogin.getString("latitude","0"));
        longi = Double.parseDouble(sharedPrefLogin.getString("longitude","0"));
        token = sharedPrefLogin.getString("token","null");
        flag = sharedPrefLogin.getString("flag","00");
        editor2 = sharedPrefLogin.edit();

        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , AddFoodItemActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(FoodSellerActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext(), CheckOrderActivity.class);
                    intent.putExtra("key", "pending");
                    startActivity(intent);
                }else {
                    Toast.makeText(FoodSellerActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , CheckDeliveryPointsActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(FoodSellerActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    setPayment();
                }else {
                    Toast.makeText(FoodSellerActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FoodSellerActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnectedFast(getApplicationContext()) && Connectivity.isConnectedFast(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext() , ChattingListActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(FoodSellerActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("UserPaymentDetails/").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PayModelClass pay = snapshot.getValue(PayModelClass.class);
                        payType = String.valueOf(pay.getFileType());
                        payNumber = String.valueOf(pay.getImageUri());
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
    public void onBackPressed() {
            super.onBackPressed();
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu_seller, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.history){
            Intent intent = new Intent(getApplicationContext(), CheckOrderActivity.class);
            intent.putExtra("key", "history");
            startActivity(intent);
        }
        if (id == R.id.logout) {
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
    private void setPayment(){

        try {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UserPaymentDetails/").child(userId);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Payment Details");
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.set_payment_layout, null);
            builder.setView(dialogView);
            TextView textView = dialogView.findViewById(R.id.currentNumber);
            final Spinner spinner = dialogView.findViewById(R.id.chooseNumType);
            final EditText number = dialogView.findViewById(R.id.payNumber);
            Button submit = dialogView.findViewById(R.id.btnSubPay);

            textView.setText("Existing: " + payNumber + "  " + payType);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        String type = spinner.getSelectedItem().toString();
                        String contact = number.getText().toString();

                        if (type.equals("Select")) {
                            Toast.makeText(FoodSellerActivity.this, "Select payment method type please !!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(contact)) {
                            Toast.makeText(FoodSellerActivity.this, "Enter number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (type.equals("easypaisa") && contact.length() < 11) {
                            Toast.makeText(getApplicationContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (type.equals("jazzcash") && contact.length() < 11) {
                            Toast.makeText(getApplicationContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String cDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        PayModelClass pay = new PayModelClass(userId, contact, userId, type, cDate);
                        dbRef.child(userId).setValue(pay);

                        Toast.makeText(FoodSellerActivity.this, "payment method set", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
