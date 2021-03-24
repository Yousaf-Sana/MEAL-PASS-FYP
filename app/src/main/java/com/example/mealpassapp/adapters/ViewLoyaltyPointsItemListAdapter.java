package com.example.mealpassapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealpassapp.R;
import com.example.mealpassapp.UserActivity;
import com.example.mealpassapp.MyFirebaseInstanceService;
import com.example.mealpassapp.model.FoodModel;
import com.example.mealpassapp.model.FoodOrderModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class ViewLoyaltyPointsItemListAdapter extends RecyclerView.Adapter<ViewLoyaltyPointsItemListAdapter.ImageViewHolder> implements Filterable {
    private Context mcontext ;
    private List<FoodModel> muploadList;
    private List<FoodModel> muploadListsearch;
    int position;

    public ViewLoyaltyPointsItemListAdapter(Context context , List<FoodModel> uploadList ) {
        mcontext = context ;
        muploadList = uploadList ;

        muploadListsearch = new ArrayList<>();
        muploadListsearch.addAll(muploadList);

        position = 0;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.items_list2, parent , false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

     this.position = position;

     final FoodModel item = muploadList.get(position);

     //holder.ratingBar.setRating(item.getDiscount());
     holder.textViewItemName.setText(item.getFoodTitle());
     holder.tvPoints.setText("Points : "+item.getPoints());
     holder.dealerName.setText(item.getSellName());
     Picasso.with(mcontext).load(item.getFoodPic()).placeholder(R.drawable.loading).into(holder.itemPic);
     Picasso.with(mcontext).load(item.getSellerPic()).placeholder(R.drawable.loading).fit().centerCrop().into(holder.dealerPic);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure to buy this item from loyalty points?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("FoodOrder/");
                        if(UserActivity.points>item.getPoints()){
                            String id = dbRef.push().getKey();

                            String date = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                            final String status = "pending2";

                            FoodOrderModel order = new FoodOrderModel(id,item.getFoodTitle(), item.getId(), item.getFoodPic(), item.getPoints(), item.getSellerId(),
                                    item.getSellName(), item.getPrice(),item.getDiscount(), 1, UserActivity.userId,
                                    UserActivity.userName, UserActivity.URI, date, status, UserActivity.token, item.isDelivery());
                            dbRef.child(id).setValue(order);

                            Toast.makeText(mcontext, "Order placed successfully", Toast.LENGTH_SHORT).show();
                            new MyFirebaseInstanceService().sendMessageSingle(mcontext,item.getSellerToken(),"Alert", "New Item order", null);

                            int remainPoints = UserActivity.points - item.getPoints();
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                            db.child(UserActivity.userId).child("flag").setValue(remainPoints);
                        }else {
                            Toast.makeText(mcontext, "Insufficient points!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return muploadList.size();
    }

    public static  class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewItemName;
        public TextView tvPoints;
        public ImageView itemPic;
        public ImageView dealerPic;
        public TextView dealerName;
        public RatingBar ratingBar;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewItemName = itemView.findViewById(R.id.tvItemName2);
            tvPoints = itemView.findViewById(R.id.tvPoints);
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
           List<FoodModel> FilterList = new ArrayList<>();
           if(constraint == null && constraint.length()==0){
               FilterList.addAll(muploadListsearch);
           }else {
               String characters = constraint.toString().toLowerCase().trim();
               for(FoodModel upload : muploadListsearch){
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
            muploadList.addAll((Collection<? extends FoodModel>) results.values);
            notifyDataSetChanged();
        }
    };

}
