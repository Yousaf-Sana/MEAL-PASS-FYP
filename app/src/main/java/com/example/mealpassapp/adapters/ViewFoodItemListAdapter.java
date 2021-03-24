package com.example.mealpassapp.adapters;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mealpassapp.ItemsDetailsActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.ViewFoodItemActivity;
import com.example.mealpassapp.model.FoodModel;
import com.example.mealpassapp.model.FoodModel2;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewFoodItemListAdapter extends RecyclerView.Adapter<ViewFoodItemListAdapter.ImageViewHolder> implements Filterable {
    private Context mcontext ;
    private List<FoodModel2> muploadList;
    private List<FoodModel2> muploadListsearch;
    public static String itemId = "";
    int position;
    ViewFoodItemActivity viewFoodItemActivity;
    int totalRating=0, counter=0;

    public ViewFoodItemListAdapter(Context context , List<FoodModel2> uploadList ) {
        mcontext = context ;
        muploadList = uploadList ;

        muploadListsearch = new ArrayList<>();
        muploadListsearch.addAll(muploadList);

        position = 0;
        viewFoodItemActivity = (ViewFoodItemActivity) context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.items_list, parent , false);
        return (new ImageViewHolder(v,viewFoodItemActivity));
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

        if(viewFoodItemActivity.is_in_action_mode){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }else {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.cardView.setOnLongClickListener(viewFoodItemActivity);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    viewFoodItemActivity.prepareSelection(v, position);
                }catch (Exception ex){
                    Log.d("mytag",ex.getMessage());
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext , ItemsDetailsActivity.class);
                ViewFoodItemActivity.sellerName = item.getSellName();
                ViewFoodItemActivity.sellerId = item.getSellerId();
                ViewFoodItemActivity.sellerPic = item.getSellerPic();
                ViewFoodItemActivity.token = item.getSellerToken();
                ViewFoodItemActivity.pic = item.getFoodPic();
                ViewFoodItemActivity.itemId = item.getId();
                ViewFoodItemActivity.itemName = item.getFoodTitle();
                ViewFoodItemActivity.discount = item.getDiscount();
                ViewFoodItemActivity.points = item.getPoints();
                ViewFoodItemActivity.price = item.getPrice();
                ViewFoodItemActivity.delivery = item.isDelivery();
                mcontext.startActivity(intent);
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
        public CheckBox checkBox;
        public CardView cardView;
        public RatingBar ratingBar;
        ViewFoodItemActivity viewFoodItemActivity;

        public ImageViewHolder(View itemView, ViewFoodItemActivity viewFoodItemActivity) {
            super(itemView);

            textViewItemName = itemView.findViewById(R.id.tvItemName2);
            textViewItemPrice = itemView.findViewById(R.id.tvItemPrice2);
            textViewItemDiscount = itemView.findViewById(R.id.tvItemDis);
            itemPic = itemView.findViewById(R.id.ItemPic);
            dealerPic = itemView.findViewById(R.id.dealerPic);
            dealerName = itemView.findViewById(R.id.dealerName);
            checkBox=itemView.findViewById(R.id.checkbox);
            cardView = itemView.findViewById(R.id.cardView);
            ratingBar = itemView.findViewById(R.id.itemRating2);
            this.viewFoodItemActivity = viewFoodItemActivity;
        }
    }

//    public void deletItems(List<Upload> user_selection) {
//        for(Upload upload : user_selection){
//            Toast.makeText(mcontext, ""+upload.getItemName(), Toast.LENGTH_SHORT).show();
//        }
//        notifyDataSetChanged();
//    }

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
