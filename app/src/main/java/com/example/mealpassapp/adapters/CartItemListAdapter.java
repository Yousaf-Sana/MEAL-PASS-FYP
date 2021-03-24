package com.example.mealpassapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealpassapp.CartItemDetailsActivity;
import com.example.mealpassapp.CartItemsActivity;
import com.example.mealpassapp.ItemsDetailsActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.UserActivity;
import com.example.mealpassapp.ViewFoodItemActivity;
import com.example.mealpassapp.model.FoodModel2;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CartItemListAdapter extends RecyclerView.Adapter<CartItemListAdapter.ImageViewHolder> implements Filterable {
    private Context mcontext ;
    private List<FoodModel2> muploadList;
    private List<FoodModel2> muploadListsearch;
    public static String itemId = "";
    int position;

    public CartItemListAdapter(Context context , List<FoodModel2> uploadList ) {
        mcontext = context ;
        muploadList = uploadList ;

        muploadListsearch = new ArrayList<>();
        muploadListsearch.addAll(muploadList);

        position = 0;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.items_list, parent , false);
        return (new ImageViewHolder(v));
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

     this.position = position;

     final FoodModel2 item = muploadList.get(position);

     holder.ratingBar.setRating(item.getRating());
     holder.textViewItemName.setText(item.getFoodTitle());
     holder.textViewItemPrice.setText("Rs."+item.getPrice());
     holder.textViewItemDiscount.setText(" "+item.getDiscount()+" % off");
     holder.dealerName.setText(item.getSellName());
     Picasso.with(mcontext).load(item.getFoodPic()).placeholder(R.drawable.loading).into(holder.itemPic);
     Picasso.with(mcontext).load(item.getSellerPic()).placeholder(R.drawable.loading).fit().centerCrop().into(holder.dealerPic);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext , CartItemDetailsActivity.class);
                CartItemsActivity.sellerName = item.getSellName();
                CartItemsActivity.sellerId = item.getSellerId();
                CartItemsActivity.sellerPic = item.getSellerPic();
                CartItemsActivity.token = item.getSellerToken();
                CartItemsActivity.pic = item.getFoodPic();
                CartItemsActivity.itemId = item.getId();
                CartItemsActivity.itemName = item.getFoodTitle();
                CartItemsActivity.discount = item.getDiscount();
                CartItemsActivity.points = item.getPoints();
                CartItemsActivity.price = item.getPrice();
                CartItemsActivity.delivery = item.isDelivery();
                mcontext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure to remove from cart?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id = item.getId();
                        DatabaseReference dbRef =  FirebaseDatabase.getInstance().getReference("AddToCartItems").child(UserActivity.userId).child(id);
                        dbRef.removeValue();
                        Toast.makeText(mcontext, "Succesfully removed", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return muploadList.size();
    }

    public static  class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewItemName;
        public TextView textViewItemPrice;
        public TextView textViewItemDiscount;
        public ImageView itemPic;
        public ImageView dealerPic;
        public TextView dealerName;
        public RatingBar ratingBar;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewItemName = itemView.findViewById(R.id.tvItemName2);
            textViewItemPrice = itemView.findViewById(R.id.tvItemPrice2);
            textViewItemDiscount = itemView.findViewById(R.id.tvItemDis);
            itemPic = itemView.findViewById(R.id.ItemPic);
            dealerPic = itemView.findViewById(R.id.dealerPic);
            dealerName = itemView.findViewById(R.id.dealerName);
            ratingBar = itemView.findViewById(R.id.itemRating2);
        }
    }

    @Override
    public Filter getFilter() {
        return Dataresult;
    }
    private Filter Dataresult = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
           List<FoodModel2> FilterList = new ArrayList<>();
           if(constraint == null && constraint.length()==0){
               FilterList.addAll(muploadListsearch);
           }else {
               String characters = constraint.toString().toLowerCase().trim();
               for(FoodModel2 upload : muploadListsearch){
                   if(upload.getFoodTitle().toLowerCase().contains(characters)){
                       FilterList.add(upload);
                   }
               }
           }
           FilterResults filterResults = new FilterResults();
           filterResults.values = FilterList;
           return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            muploadList.clear();
            muploadList.addAll((Collection<? extends FoodModel2>) results.values);
            notifyDataSetChanged();
        }
    };

}
