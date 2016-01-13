package com.example.a00916129.imageopener;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private static int RESULT_LOAD_LEFT_IMAGE = 1;
    private static int RESULT_LOAD_RIGHT_IMAGE = 2;
    private static String leftImagePath, rightImagePath;
    private static int frameAmount=1;
    private static SeekBar seekBar;
    private static TextView framesTextView;
    private static Bitmap leftBitmap=null, rightBitmap=null;
    private static Bitmap[] warpFrames;
    private Drawer drawer;

    private MyView leftView=null, rightView=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        framesTextView = (TextView) findViewById(R.id.framesTextView);

        if(leftImagePath!=null){
            MyView leftView = (MyView) findViewById(R.id.imageView1);
            leftView.setDrawable(true);
            leftView.setChild(rightView);
            leftView.setImageBitmap(BitmapFactory.decodeFile(leftImagePath));
        }
        if(rightImagePath!=null){
            MyView rightView = (MyView) findViewById(R.id.imageView2);
            rightView.setDrawable(false);
            leftView.setChild(rightView);
            rightView.setImageBitmap(BitmapFactory.decodeFile(rightImagePath));
        }

        //Play button at bottom
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FrameViewActivity.class);
                intent.putExtra("LeftImageFilename", leftImagePath);
                intent.putExtra("RightImageFilename", rightImagePath);
                startActivity(intent);
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frameAmount = progress + 1;
                framesTextView.setText("Frames:" + frameAmount);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        //drawer = new Drawer(this);
        //setContentView(drawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Drawer.class);
            startActivity(intent);
        }
        if (id == R.id.action_load_left){
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_LEFT_IMAGE);
        }
        if (id == R.id.action_load_right){
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_RIGHT_IMAGE);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_LEFT_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            leftImagePath = cursor.getString(columnIndex);
            System.out.println(leftImagePath);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            leftBitmap = BitmapFactory.decodeFile(leftImagePath);
            imageView.setImageBitmap(leftBitmap);
        }

        if (requestCode == RESULT_LOAD_RIGHT_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            rightImagePath = cursor.getString(columnIndex);
            System.out.println(rightImagePath);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            rightBitmap = BitmapFactory.decodeFile(rightImagePath);
            imageView.setImageBitmap(rightBitmap);
        }

    }










}
