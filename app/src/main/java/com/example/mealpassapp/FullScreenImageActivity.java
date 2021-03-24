package com.example.mealpassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageView = findViewById(R.id.fullImage);
        Intent intent = getIntent();
        String s = intent.getStringExtra("URI");
        Uri uri = Uri.parse(s);

        Picasso.with(FullScreenImageActivity.this).load(uri).fit().centerCrop().into(imageView);
        //imageView.setImageURI(uri);
    }
}
