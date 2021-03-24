package com.example.mealpassapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mealpassapp.FullScreenImageActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.model.FoodOrderModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CheckOrdersListAdapter extends ArrayAdapter<FoodOrderModel> implements Filterable {
    private Activity context;
    private List<FoodOrderModel> ordersList, searchList;
    private String check;

    public CheckOrdersListAdapter(Activity context , List<FoodOrderModel> ordersList, String check){
        super(context , R.layout.order_list , ordersList);
        this.context = context;
        this.ordersList = ordersList;
        this.check = check;

        searchList = new ArrayList<>();
        searchList.addAll(ordersList);
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        final View listViewItem = inflater.inflate(R.layout.order_list , null , true);

        TextView customerName = (TextView) listViewItem.findViewById(R.id.tvCustomerName);
        TextView itemname = (TextView) listViewItem.findViewById(R.id.tvitem);
        TextView textViewquantity = (TextView) listViewItem.findViewById(R.id.tv3);
        TextView price = (TextView) listViewItem.findViewById(R.id.tvprice);
        TextView dateTime = (TextView) listViewItem.findViewById(R.id.datetime);
        TextView delivery = (TextView) listViewItem.findViewById(R.id.deliver);
        ImageView profilePic = (ImageView) listViewItem.findViewById(R.id.customerPic);

        final FoodOrderModel order = ordersList.get(position);

        if(check.equals("history")){
            delivery.setVisibility(View.VISIBLE);
            delivery.setText(order.getFlag());
        }
        boolean deliver = order.isDeliver();
        if(deliver){
            delivery.setVisibility(View.VISIBLE);
            delivery.setText("With Delivery");
        }
        if(order.getFlag().equals("pending2")){
            customerName.setText(order.getUserName());
            itemname.setText(order.getFoodName());
            textViewquantity.setText(order.getQuantity()+"");
            price.setText("Points Purchased");
            dateTime.setText(order.getDate());
            Picasso.with(context).load(order.getUserPic()).fit().centerCrop().placeholder(R.drawable.loading).into(profilePic);
        }else {
            customerName.setText(order.getUserName());
            itemname.setText(order.getFoodName());
            textViewquantity.setText(order.getQuantity()+"");
            price.setText("Rs "+order.getFoodPrice());
            dateTime.setText(order.getDate());
            Picasso.with(context).load(order.getUserPic()).fit().centerCrop().placeholder(R.drawable.loading).into(profilePic);
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , FullScreenImageActivity.class);
                intent.putExtra("URI" , order.getUserPic());
                context.startActivity(intent);
            }
        });

        return listViewItem;
    }


    @Override
    public Filter getFilter() {
        return Dataresult;
    }
    private Filter Dataresult = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FoodOrderModel> FilterList = new ArrayList<>();
            if(constraint == null && constraint.length()==0){
                FilterList.addAll(searchList);
            }else {
                String characters = constraint.toString().toLowerCase().trim();
                for(FoodOrderModel order : searchList){
                    if(order.getFoodName().toLowerCase().contains(characters)){
                        FilterList.add(order);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = FilterList;
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ordersList.clear();
            ordersList.addAll((Collection<? extends FoodOrderModel>) results.values);
            notifyDataSetChanged();
        }
    };
}
