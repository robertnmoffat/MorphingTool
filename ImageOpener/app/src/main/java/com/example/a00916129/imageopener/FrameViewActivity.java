package com.example.a00916129.imageopener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class FrameViewActivity extends AppCompatActivity {
    Bitmap[] imageArray;
    String leftImagePath, rightImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        //FrameGenerator frameGenerator = (FrameGenerator)getIntent().getSerializableExtra("FrameGenerator");
        //Bitmap image = frameGenerator.getFrame(0);


        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        //Bitmap leftBitmap = BitmapFactory.decodeFile(leftImagePath);
        //imageView.setImageBitmap(image);

    }

}
