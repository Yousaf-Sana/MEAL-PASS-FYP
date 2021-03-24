package com.example.mealpassapp.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.mealpassapp.R;
import com.example.mealpassapp.model.ChatModelClass;
import com.example.mealpassapp.model.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ChattingListAdapter extends ArrayAdapter<ChatModelClass> {
    private Activity context;
    private List<ChatModelClass> muploadList;
    SharedPreferences sharedPreferences;
    String userId,userType, receiverPhone , receiverName, receiverPic;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/");
    DatabaseReference dbRef;
    int count = 0;

    public ChattingListAdapter(Activity context , List<ChatModelClass> muploadList){
        super(context , R.layout.user_list_layout, muploadList);
        this.context = context;
        this.muploadList = muploadList;

        sharedPreferences = context.getSharedPreferences("userLoginData",MODE_PRIVATE);
        userId = sharedPreferences.getString("ID","null");
        userType = sharedPreferences.getString("type","null");

        dbRef = FirebaseDatabase.getInstance().getReference("Chatting").child("Chat"+userId);

    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.user_list_layout, null , true);
        TextView name = (TextView) listViewItem.findViewById(R.id.tvDealerName);
        TextView phone = (TextView) listViewItem.findViewById(R.id.tvNumber);
        TextView counter = (TextView) listViewItem.findViewById(R.id.tvCounter);
        ImageView image = (ImageView) listViewItem.findViewById(R.id.dealersProfilePic);

        ChatModelClass chat = muploadList.get(position);

        if(userId.equals(chat.getSenderId())){
            loadData(chat.getRecieverId(),name,phone,image);
        }else if(userId.equals(chat.getRecieverId())){
            loadData(chat.getSenderId(),name,phone,image);
        }

        loadCounter(counter, chat);

        return listViewItem;
    }

    private void loadCounter(final TextView counter, final ChatModelClass chat) {

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatModelClass obj = snapshot.getValue(ChatModelClass.class);
                    if(userId.equals(obj.getRecieverId()) && obj.getFlag().equals("unread")){
                        count = count+1;
                    }
                }
                if(count > 0){
                    counter.setVisibility(View.VISIBLE);
                    counter.setText(""+count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});

    }

    private void loadData(final String receiverId, final TextView name, final TextView phone, final ImageView image) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UsersModel user = snapshot.getValue(UsersModel.class);
                        if (receiverId.equals(user.getId())) {
                            receiverName = user.getName();
                            receiverPhone = user.getPhone();
                            receiverPic = user.getImageUri();
                        }
                    }
                    name.setText(receiverName);
                    phone.setText(receiverPhone);
                    Picasso.with(context).load(receiverPic).fit().centerCrop().placeholder(R.drawable.loading).into(image);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
    }
}