package com.example.mealpassapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mealpassapp.model.FoodOrderModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class CartItemDetailsActivity extends AppCompatActivity {
ImageView imageView, chatting;
TextView name, price, discount, tvPoints;
Button submit, reviews;
String itemName, itemPrice, ItDiscount, ItPoints, ItPic, sellerId, sellerName, sellerToken, sellerPic;
public static String itemId;
int points;
boolean isDeliver, delivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        imageView = findViewById(R.id.itemPic);
        chatting = findViewById(R.id.imgChat);
        name = findViewById(R.id.tvItemname);
        price = findViewById(R.id.tvItemprice);
        discount = findViewById(R.id.tvItemScheme);
        tvPoints = findViewById(R.id.points);
        submit = findViewById(R.id.submitOrder);
        reviews = findViewById(R.id.btnReviews);

        itemName = CartItemsActivity.itemName;
        sellerName = CartItemsActivity.sellerName;
        sellerPic = CartItemsActivity.sellerPic;
        itemPrice = CartItemsActivity.price+"";
        itemId = CartItemsActivity.itemId;
        ItDiscount = CartItemsActivity.discount+"";
        ItPoints = CartItemsActivity.points+"";
        points = Integer.parseInt(CartItemsActivity.points+"");
        ItPic = CartItemsActivity.pic;
        sellerId = CartItemsActivity.sellerId;
        sellerToken = CartItemsActivity.token;
        delivery = CartItemsActivity.delivery;

        Picasso.with(this).load(ItPic).fit().centerCrop().placeholder(R.drawable.loading).into(imageView);
        name.setText(itemName);
        price.setText("Rs."+itemPrice);
        discount.setText(ItDiscount+" % off");
        tvPoints.setText("Loyalty Points : "+ItPoints);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFunction();
            }
        });
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FoodAllCommentsActivity.class);
                intent.putExtra("decision", "viewRatings");
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
        });
        chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("id", sellerId);
                intent.putExtra("fcm", sellerToken);
                intent.putExtra("firstName",sellerName);
                intent.putExtra("userPic",sellerPic);
                startActivity(intent);
            }
        });
    }
    private void BottomSheetFunction() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CartItemDetailsActivity.this , R.style.BtoonSheetDialogTheme);
        View Bottomsheetview = LayoutInflater.from(this)
                .inflate(R.layout.layout_botton_sheet , (LinearLayout) findViewById(R.id.bottom_sheet_container));

        final TextView itemNameP, dealerNameP, itemPriceP, discount, increaseP, decreaseP, quantityP, ddd;
        final ImageView itemPicP;
        final Button submit;
        final CheckBox checkBox;
        final String status = "pending";

        itemNameP = Bottomsheetview.findViewById(R.id.itemNamePop);
        dealerNameP = Bottomsheetview.findViewById(R.id.dealerNamePop);
        itemPriceP = Bottomsheetview.findViewById(R.id.itemPricePop);
        discount = Bottomsheetview.findViewById(R.id.itemSchemePop);
        increaseP = Bottomsheetview.findViewById(R.id.increasePop);
        decreaseP = Bottomsheetview.findViewById(R.id.decreasePop);
        quantityP = Bottomsheetview.findViewById(R.id.quantityPop);
        submit = Bottomsheetview.findViewById(R.id.submitOrderPop);
        checkBox = Bottomsheetview.findViewById(R.id.isDeliver);
        itemPicP = Bottomsheetview.findViewById(R.id.itemPicPop);
        ddd = Bottomsheetview.findViewById(R.id.ddd);

        Picasso.with(this).load(ItPic).placeholder(R.drawable.loading).fit().centerCrop().into(itemPicP);
        itemNameP.setText(itemName);
        dealerNameP.setText(sellerName);
        itemPriceP.setText("Rs."+itemPrice);
        discount.setText(ItDiscount);

        if(delivery){
            ddd.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
        }

        increaseP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q = Integer.parseInt(quantityP.getText().toString());
                q = q+1;
                if(q>0){
                    decreaseP.setEnabled(true);
                }
                if(q<=200){
                    quantityP.setText(""+q);
                }else {
                    Toast.makeText(CartItemDetailsActivity.this, "Not greater than 200", Toast.LENGTH_SHORT).show();
                }
            }
        });
        decreaseP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q = Integer.parseInt(quantityP.getText().toString());
                if(q>0){
                    q = q-1;
                }
                quantityP.setText(""+q);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isDeliver = true;
                }else {
                    isDeliver = false;
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int quan = Integer.parseInt(quantityP.getText().toString());

                if(quan<1){
                    decreaseP.setEnabled(false);
                }

                    final int itemQuantity = Integer.parseInt(quantityP.getText().toString());

                    AlertDialog.Builder confirmationBuilder = new AlertDialog.Builder(CartItemDetailsActivity.this);
                    confirmationBuilder.setTitle("Confirmation");
                    confirmationBuilder.setMessage("Are you sure to want place order ?  " + itemQuantity).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("FoodOrder/");

                            int price = Integer.parseInt(itemPrice);
                            float discount = Float.parseFloat(ItDiscount);

                            String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                            String id = dbRef.push().getKey();
                            FoodOrderModel order = new FoodOrderModel(id,itemName, itemId, ItPic, points, sellerId, sellerName, price,discount, quan, UserActivity.userId,
                                    UserActivity.userName, UserActivity.URI, date, status, UserActivity.token, isDeliver);

                            dbRef.child(id).setValue(order);


                            new MyFirebaseInstanceService().sendMessageSingle(CartItemDetailsActivity.this,sellerToken,"Alert", "New Item order", null);


                            Toast.makeText(getApplicationContext(), "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = confirmationBuilder.create();
                    dialog.show();

            }
        });
        Bottomsheetview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(Bottomsheetview);
        bottomSheetDialog.show();
    }

}
