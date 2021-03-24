package com.example.mealpassapp.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.mealpassapp.R;
import com.example.mealpassapp.model.ItemCommentsModelClass;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CommentsListAdapter extends ArrayAdapter<ItemCommentsModelClass> {
    private Activity context;
    private List<ItemCommentsModelClass> muploadList;

    public CommentsListAdapter(Activity context , List<ItemCommentsModelClass> muploadList){
        super(context , R.layout.comment_list , muploadList);
        this.context = context;
        this.muploadList = muploadList;
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        //View class object is used to get custom layout that we create show data that is fetched from firebase...
        View listViewItem = inflater.inflate(R.layout.comment_list , null , true);
        TextView username = listViewItem.findViewById(R.id.user);
        ImageView picture = listViewItem.findViewById(R.id.pic);
        TextView rating = listViewItem.findViewById(R.id.rating);
        RatingBar ratingBar = listViewItem.findViewById(R.id.ratingBar);
        TextView comment = listViewItem.findViewById(R.id.userComment);

        ItemCommentsModelClass comments = muploadList.get(position);

        Uri uri = Uri.parse(comments.getCustomerPic());
        Picasso.with(context).load(uri).fit().centerCrop().placeholder(R.drawable.loading).into(picture);
        username.setText(comments.getCommenterName());
        rating.setText("Rating : "+comments.getRating()+" out of 5");
        comment.setText("Review : "+comments.getComment());

        ratingBar.setActivated(false);
        ratingBar.setIsIndicator(true);

        ratingBar.setRating(comments.getRating());
        return listViewItem;
    }
}
