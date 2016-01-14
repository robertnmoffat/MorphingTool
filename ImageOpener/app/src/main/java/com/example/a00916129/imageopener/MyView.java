package com.example.a00916129.imageopener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by a00916129 on 1/13/2016.
 */
public class MyView extends ImageView {

    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;

    private static ArrayList<SelectionLine> lines = new ArrayList<SelectionLine>();
    private SelectionLine currentLine;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;

    private Paint       mPaint;
    private MaskFilter mEmboss;
    private MaskFilter  mBlur;

    private MyView child=null;
    private boolean drawable=true;
    private SelectionLine dragLine=null;
    private boolean draggingHead=false, draggingTail=false;

    private String imagePath="/storage/emulated/0/DCIM/Camera/IMG_20160109_205304.jpg";

    public MyView(Context context) {
        super(context);
        init(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);

        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setImagePath(String path){
        imagePath = path;
        System.out.println(imagePath);
//        Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath);
//        //double ratioHeight = tempBitmap.getHeight()/tempBitmap.getWidth()*getHeight();
//        tempBitmap = Bitmap.createScaledBitmap(tempBitmap, getWidth(), getHeight(), false);
//        mBitmap  = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap tempBitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        tempBitmap = Bitmap.createScaledBitmap(tempBitmap, getWidth(), getHeight(), false);
        mBitmap  = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFAAAAAA);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        if(drawable) {
            for (int i=0; i<lines.size(); i++) {
                SelectionLine curLine = lines.get(i);
                if(x>curLine.getX1()-20&&x<curLine.getX1()+20
                        &&y>curLine.getY1()-20&&y<curLine.getY1()+20){
                    draggingTail=true;
                    dragLine = curLine;
                    lines.remove(i);
                    return;
                }
                if(x>curLine.getX2()-20&&x<curLine.getX2()+20
                        &&y>curLine.getY2()-20&&y<curLine.getY2()+20){
                    draggingHead=true;
                    dragLine = curLine;
                    lines.remove(i);
                    return;
                }
            }

            currentLine = new SelectionLine();
            currentLine.setX1((int) x);
            currentLine.setY1((int) y);

            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

            System.out.println("Touch start");

        }
    }
    private void touch_move(float x, float y) {
        if(drawable) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                //mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                drawLines();

                mPath.moveTo(currentLine.getX1(), currentLine.getY1());

                mCanvas.drawCircle(currentLine.getX1(), currentLine.getY1(), 20, mPaint);

                mPath.lineTo(mX, mY);
                // commit the path to our offscreen
                mCanvas.drawPath(mPath, mPaint);//draw line at current figure position
                mCanvas.drawCircle(mX, mY, 20, mPaint);

                mPath.reset();//clear the line because this isnt necessarily the final position


            }
        }
    }
    private void touch_up() {
        if(draggingTail){
            dragLine.setX1((int) mX);
            dragLine.setY1((int) mY);
            lines.add(dragLine);
            dragLine=null;
            drawLines();
            return;
        }
        if(draggingHead){
            dragLine.setX2((int) mX);
            dragLine.setY2((int) mY);
            lines.add(dragLine);
            dragLine=null;
            drawLines();
            return;
        }
        if(drawable) {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            mCanvas.drawCircle(mX, mY, 20, mPaint);
            // kill this so we don't double draw

            currentLine.setX2((int) mX);
            currentLine.setY2((int) mY);

            lines.add(currentLine);

            drawLines();

            mPath.reset();
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void drawLines(){
        Bitmap tempBitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        tempBitmap = Bitmap.createScaledBitmap(tempBitmap, getWidth(), getHeight(), false);
        mBitmap  = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);

        for (SelectionLine curLine : lines) {
            addLine(curLine);

            if (child != null) {
                child.addLine(curLine);
                child.postInvalidate();
            }
        }
        postInvalidate();
    }

    public void addLine(SelectionLine line){
        mPath.reset();
        mPath.moveTo(line.getX1(), line.getY1());
        mCanvas.drawCircle(line.getX1(), line.getY1(), 20, mPaint);
        mPath.lineTo(line.getX2(), line.getY2());
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        mCanvas.drawCircle(line.getX2(), line.getY2(), 20, mPaint);
        mPath.reset();
    }

    public MyView getChild() {
        return child;
    }

    public boolean isDrawable() {
        return drawable;
    }

    public void setChild(MyView child) {
        this.child = child;
    }

    public void setDrawable(boolean drawable) {
        this.drawable = drawable;
    }
}
