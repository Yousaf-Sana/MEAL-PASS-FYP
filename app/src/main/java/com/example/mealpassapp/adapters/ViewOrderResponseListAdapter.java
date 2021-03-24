package com.example.mealpassapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mealpassapp.FoodAllCommentsActivity;
import com.example.mealpassapp.R;
import com.example.mealpassapp.ViewOrderResponseActivity;
import com.example.mealpassapp.model.OrderStatusModelClass;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ViewOrderResponseListAdapter extends ArrayAdapter<OrderStatusModelClass> {
    private Context context;
    private List<OrderStatusModelClass> statusList;

    public ViewOrderResponseListAdapter(Context context, List<OrderStatusModelClass> list) {

        super(context, R.layout.order_response_list, list);
        this.context = context;
        this.statusList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View status_list = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        status_list = inflater.inflate(R.layout.order_response_list,null);

        ImageView picture = (ImageView) status_list.findViewById(R.id.orderDealerPic);
        ImageView rating = (ImageView) status_list.findViewById(R.id.rate_items);
        TextView status = (TextView) status_list.findViewById(R.id.tvstatus);
        TextView item = (TextView) status_list.findViewById(R.id.tv_item);
        TextView dealerbusiness = (TextView) status_list.findViewById(R.id.tvdealer);
        TextView total = (TextView) status_list.findViewById(R.id.tvtotal);
        TextView tvprice = (TextView) status_list.findViewById(R.id.textviewPrice);
        TextView tvquan = (TextView) status_list.findViewById(R.id.textVquan);
        TextView date = (TextView) status_list.findViewById(R.id.statDate);

        int price = 0 , quantity= 0;

        final OrderStatusModelClass selectedItemStatus = statusList.get(position);

        String uri = selectedItemStatus.getItemPic();

        if(selectedItemStatus.getStatus().equals("Accepted")){
            price = selectedItemStatus.getPrice();
            quantity = selectedItemStatus.getQuantity();
            int totalbill = price * quantity;
            float dis = selectedItemStatus.getItemDiscount()/100*totalbill;
            float tot = totalbill - dis;
            String t = String.valueOf(tot);
            total.setText("Rs."+t);
        }else {
            total.setText("N/A");
        }
        tvprice.setText(selectedItemStatus.getPrice()+"");
        tvquan.setText(selectedItemStatus.getQuantity()+"");
        status.setText(selectedItemStatus.getStatus());
        item.setText(selectedItemStatus.getItem_name());
        dealerbusiness.setText(selectedItemStatus.getDealername());
        date.setText(selectedItemStatus.getDateTime());
        Picasso.with(context).load(uri).placeholder(R.drawable.loading).into(picture);

        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = selectedItemStatus.getStatus();
                if(status.equals("Rejected")){
                    Toast.makeText(context, "Does not rate rejected orders!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, FoodAllCommentsActivity.class);
                ViewOrderResponseActivity.itemId = selectedItemStatus.getItemId();
                ViewOrderResponseActivity.orderId = selectedItemStatus.getOrderId();
                intent.putExtra("decision", "addRatings");
                context.startActivity(intent);

            }
        });
        return status_list;
    }
}
