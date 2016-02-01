package com.example.a00916129.imageopener;

import android.app.ProgressDialog;
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
import android.os.Handler;
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
    private static int frameAmount=2;
    private static SeekBar seekBar;
    private static TextView framesTextView;
    private static Bitmap leftBitmap=null, rightBitmap=null;
    private static Bitmap[] warpFrames;
    private Drawer drawer;
    private ProgressDialog progress;

    private MyView leftView=null, rightView=null;

    private FrameGenerator frameGenerator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        framesTextView = (TextView) findViewById(R.id.framesTextView);

        leftView = (MyView) findViewById(R.id.imageView1);
        rightView = (MyView) findViewById(R.id.imageView2);

        if(leftImagePath!=null){
            leftView.setImageBitmap(BitmapFactory.decodeFile(leftImagePath));
        }
        if(rightImagePath!=null){
            rightView.setImageBitmap(BitmapFactory.decodeFile(rightImagePath));
        }

        rightView.invalidate();
        leftView.invalidate();

        //Play button at bottom
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress=new ProgressDialog(MainActivity.this);
                progress.setMessage("Generating frames...");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(false);
                progress.setProgress(0);
                progress.show();
                progress.setMax(100);
                progress.setProgress(1);
                frameGenerator.setProgressBar(progress);
                //frameGenerator.saveFrame(leftView.getBitmap());
                //String path = frameGenerator.saveFrame(leftView.getBitmap());

//                final Handler handler = new Handler();
//                final Runnable r = new Runnable() {
//                    public void run() {
//                        frameGenerator.generateFrames(leftView.getBitmap(), rightView.getBitmap(), leftView.getLines(), frameAmount);
//                        handler.postDelayed(this, 1000);
//                    }
//                };
                //frameGenerator.generateFrames(leftView.getBitmap(), rightView.getBitmap(), leftView.getLines(), frameAmount);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        frameGenerator.generateFrames(leftView.getBitmap(), rightView.getBitmap(), leftView.getLines(), frameAmount);
                        startResultActivity();
                    }});

                t.start();

//                while(t.isAlive()) {
//                    progress.incrementProgressBy(1);
//                }

//                try {
//                    t.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }



            }
        });




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frameAmount = progress + 2;
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

    public void startResultActivity(){
        Intent intent = new Intent(MainActivity.this, FrameViewActivity.class);
        //intent.putExtra("FrameGenerator", frameAmount);
        startActivity(intent);
    }

    public void onStart(){
        super.onStart();
        frameGenerator = new FrameGenerator(getApplicationContext());
        leftView.setDrawable(true);
        leftView.setChild(rightView);
        rightView.setDrawable(false);
        leftView.setImagePath(leftImagePath);
        rightView.setImagePath(rightImagePath);
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
        if (id == R.id.action_clear) {
            leftView.clearLines();
        }
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
            //Bitmap tinyScaled = leftBitmap.createScaledBitmap(leftBitmap, 208, 167, false);
           // Bitmap tinyScaled = leftBitmap.createScaledBitmap(leftBitmap, 52, 41, false);
            Bitmap scaledBitmap = leftBitmap.createScaledBitmap(leftBitmap, 832, 669, false);
            imageView.setImageBitmap(scaledBitmap);
            imageView.getLayoutParams().width = imageView.getLayoutParams().width;
            imageView.getLayoutParams().height = imageView.getLayoutParams().height;
        }

        if (requestCode == RESULT_LOAD_RIGHT_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            rightImagePath = cursor.getString(columnIndex);
            System.out.println(rightImagePath);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            rightBitmap = BitmapFactory.decodeFile(rightImagePath);
            //Bitmap tinyScaled = leftBitmap.createScaledBitmap(rightBitmap, 208, 167, false);
            //Bitmap tinyScaled = leftBitmap.createScaledBitmap(rightBitmap, 52, 41, false);
            Bitmap scaledBitmap = rightBitmap.createScaledBitmap(rightBitmap, 832, 669, false);
            imageView.setImageBitmap(scaledBitmap);
        }

    }










}
