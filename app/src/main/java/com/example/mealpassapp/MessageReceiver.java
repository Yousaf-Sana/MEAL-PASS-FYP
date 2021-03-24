package com.example.mealpassapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.mealpassapp.helpers.App;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MessageReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ONE_ID = "channel_one_id";
    public static final String CHANNEL_TWO_ID = "channel_two_id";

    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");
        ctx = context;
        callNotification(context, message, title);
    }

    private void callNotification(Context context, String message, String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext() , App.CHANNEL_ONE_ID);
        builder.setSmallIcon(R.drawable.meal_pass).setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        Intent intent = new Intent(context, ViewOrderResponseActivity.class);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
}
