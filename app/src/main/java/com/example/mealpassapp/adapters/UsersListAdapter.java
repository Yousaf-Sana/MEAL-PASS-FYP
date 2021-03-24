package com.example.mealpassapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mealpassapp.FullScreenImageActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.model.UsersModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UsersListAdapter extends ArrayAdapter<UsersModel> implements Filterable {
    private Activity context;
    private List<UsersModel> dealersList ,searchList;
    private Uri ImageUri;
    double latitude;
    double longitude;

    public UsersListAdapter(Activity context , List<UsersModel> dealersList){
        super(context , R.layout.users_listlayout , dealersList);
        this.context = context;
        this.dealersList = dealersList;

        searchList = new ArrayList<>();
        searchList.addAll(dealersList);
    }
    @Override
    public View getView(final int position, final View convertView , ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.users_listlayout , null , true);

        TextView name = (TextView) listViewItem.findViewById(R.id.tvDealerName);
        TextView phone = (TextView) listViewItem.findViewById(R.id.tvNumber);
        ImageView profilePic = (ImageView) listViewItem.findViewById(R.id.dealersProfilePic);

        final UsersModel list = dealersList.get(position);

        String uri = list.getImageUri();
        ImageUri = Uri.parse(uri);
        name.setText(list.getName());
        phone.setText(list.getPhone());
        Picasso.with(context).load(ImageUri).fit().centerCrop().placeholder(R.drawable.loading).into(profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , FullScreenImageActivity.class);
                intent.putExtra("URI" , list.getImageUri());
                context.startActivity(intent);
                context.overridePendingTransition(0,0);
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
            List<UsersModel> FilterList = new ArrayList<>();
            if(constraint == null && constraint.length()==0){
                FilterList.addAll(searchList);
            }else {
                String characters = constraint.toString().toLowerCase().trim();
                for(UsersModel dealers : searchList){
                    if(dealers.getName().toLowerCase().contains(characters)){
                        FilterList.add(dealers);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = FilterList;
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dealersList.clear();
            dealersList.addAll((Collection<? extends UsersModel>) results.values);
            notifyDataSetChanged();
        }
    };
}
