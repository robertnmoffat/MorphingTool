package com.example.a00916129.imageopener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FrameViewActivity extends AppCompatActivity {
    Bitmap[] imageArray;
    String leftImagePath, rightImagePath;
    int selectedFrame=0;

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
        imageView.setImageBitmap(FrameGenerator.loadFrame(selectedFrame));

        ImageButton playButton = (ImageButton) findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Getting array...");
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                selectedFrame++;
                imageView.setImageBitmap(FrameGenerator.loadFrame(selectedFrame));
                //int[] array = FrameGenerator.getPixelArray();
                // System.out.print("Array length: "+array.length);
//                for(int i=0; i<array.lengt; i++) {
//                    System.out.print(array[i]);
//                }
            }
        });

        ImageButton skipRightButton = (ImageButton) findViewById(R.id.buttonRight);
        skipRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Right skip");
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                if(selectedFrame<FrameGenerator.getFrameCount())selectedFrame++;
                imageView.setImageBitmap(FrameGenerator.loadFrame(selectedFrame));
                //int[] array = FrameGenerator.getPixelArray();
                // System.out.print("Array length: "+array.length);
//                for(int i=0; i<array.lengt; i++) {
//                    System.out.print(array[i]);
//                }
            }
        });

        ImageButton skipLeftButton = (ImageButton) findViewById(R.id.buttonLeft);
        skipLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Left skip");
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                if(selectedFrame>0)selectedFrame--;
                imageView.setImageBitmap(FrameGenerator.loadFrame(selectedFrame));
                //int[] array = FrameGenerator.getPixelArray();
                // System.out.print("Array length: "+array.length);
//                for(int i=0; i<array.lengt; i++) {
//                    System.out.print(array[i]);
//                }
            }
        });

    }

}
