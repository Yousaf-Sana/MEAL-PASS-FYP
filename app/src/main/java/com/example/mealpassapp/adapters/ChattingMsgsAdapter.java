package com.example.mealpassapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mealpassapp.ChattingActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.UserActivity;
import com.example.mealpassapp.model.ChatModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import org.ocpsoft.prettytime.PrettyTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChattingMsgsAdapter extends ArrayAdapter<ChatModelClass>{
    private Activity context;
    private List<ChatModelClass> msgsList;
    private Uri ImageUri;
    double latitude;
    SharedPreferences sharedPreferences;
    Uri uri;
    int counter = 0;

    public ChattingMsgsAdapter(Activity context , List<ChatModelClass> msgsList){
        super(context , R.layout.user_chat_activity, msgsList);
        this.context = context;
        this.msgsList = msgsList;
    }
    @Override
    public View getView(final int position, final View convertView , ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        final View listViewItem = inflater.inflate(R.layout.user_chat_activity, null , true);

        TextView others = (TextView) listViewItem.findViewById(R.id.tvOtherMsg);
        TextView mine = (TextView) listViewItem.findViewById(R.id.tvMyMsg);
        ImageView myFile = (ImageView) listViewItem.findViewById(R.id.myFilePic);
        ImageView otherFile = (ImageView) listViewItem.findViewById(R.id.othersFilePic);
        RelativeLayout otherLayout = (RelativeLayout) listViewItem.findViewById(R.id.othersLayout);
        RelativeLayout myLayout = (RelativeLayout) listViewItem.findViewById(R.id.myLayout);
        RelativeLayout otherLayoutText = (RelativeLayout) listViewItem.findViewById(R.id.othersLayoutText);
        RelativeLayout myLayoutText = (RelativeLayout) listViewItem.findViewById(R.id.myLayoutText);
        TextView otherDate = (TextView) listViewItem.findViewById(R.id.tvOtherDate);
        TextView myDate = (TextView) listViewItem.findViewById(R.id.tvMyDate);
        TextView otherDateText = (TextView) listViewItem.findViewById(R.id.tvOtherMsgDate);
        TextView myDateText = (TextView) listViewItem.findViewById(R.id.tvMyMsgDate);
        RelativeLayout layout = (RelativeLayout) listViewItem.findViewById(R.id.chatLayout);

        uri = Uri.parse("android.resource://com.example.mealpassapp/" +R.drawable.file);

        sharedPreferences = context.getSharedPreferences("userLoginData", Context.MODE_PRIVATE);

        String userId = sharedPreferences.getString("ID","null");

        PrettyTime p = new PrettyTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        final ChatModelClass list = msgsList.get(position);

        if(userId.equals(list.getSenderId())){
            otherLayoutText.setVisibility(View.GONE);
            otherLayout.setVisibility(View.GONE);

            if(list.getFileType().equals("text")){
                myLayout.setVisibility(View.GONE);
                mine.setText(list.getMessage());

                String date = list.getDate()+" "+list.getTime();
                try {
                    Date d = sdf.parse(date);
                    myDateText.setText(p.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else if(!list.getFileType().equals("pdf") && !list.getFileType().equals("docx")){
                myLayoutText.setVisibility(View.GONE);
                Picasso.with(context).load(list.getPicUrl()).fit().centerCrop().placeholder(R.drawable.loading).into(myFile);

                String date = list.getDate()+" "+list.getTime();
                try {
                    Date d = sdf.parse(date);
                    myDate.setText(p.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if(list.getFileType().equals("pdf")){
                myLayoutText.setVisibility(View.GONE);
                Picasso.with(context).load(uri).placeholder(R.drawable.pdf_file_icon).fit().centerCrop().into(myFile);

                String date = list.getDate()+" "+list.getTime();
                try {
                    Date d = sdf.parse(date);
                    myDate.setText(p.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                myLayoutText.setVisibility(View.GONE);
                Picasso.with(context).load(uri).placeholder(R.drawable.docx_file_icon).fit().centerCrop().into(myFile);
                String date = list.getDate()+" "+list.getTime();
                try {
                    Date d = sdf.parse(date);
                    myDate.setText(p.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }else {
            myLayoutText.setVisibility(View.GONE);
            myLayout.setVisibility(View.GONE);

            if(list.getFileType().equals("text")){
                otherLayout.setVisibility(View.GONE);
                others.setText(list.getMessage());

                String date = list.getDate()+" "+list.getTime();
                try {
                    Date d = sdf.parse(date);
                    otherDateText.setText(p.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if(!list.getFileType().equals("pdf") && !list.getFileType().equals("docx")){
                otherLayoutText.setVisibility(View.GONE);
                Picasso.with(context).load(list.getPicUrl()).fit().centerCrop().placeholder(R.drawable.loading).into(otherFile);

                String date = list.getDate()+" "+list.getTime();
                try {
                    Date d = sdf.parse(date);
                    otherDate.setText(p.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else {
                otherLayoutText.setVisibility(View.GONE);
                Picasso.with(context).load(uri).placeholder(R.drawable.file).fit().centerCrop().into(otherFile);

                String date = list.getDate()+" "+list.getTime();
                try {
                    Date d = sdf.parse(date);
                    otherDate.setText(p.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if(ChattingActivity.isActionMode) {

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listViewItem.setBackgroundColor(Color.parseColor("#80B7EC"));

                    if (ChattingActivity.userSelection.contains(msgsList.get(position))) {
                        ChattingActivity.userSelection.remove(msgsList.get(position));
                    }else {
                        ChattingActivity.userSelection.add(msgsList.get(position));
                    }
                    ChattingActivity.actionMode.setTitle(ChattingActivity.userSelection.size()+" selected..");
                }
            });
        }else {
            listViewItem.setBackgroundColor(Color.parseColor("#00000000"));
        }
        return listViewItem;
    }
}
