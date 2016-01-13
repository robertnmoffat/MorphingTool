package com.example.a00916129.imageopener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by a00916129 on 1/12/2016.
 */
public class Drawer extends View{
    Paint paint = new Paint();

    public Drawer(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
    }

    public void onDraw(Canvas canvas){
        canvas.drawLine(50,100,600,600,paint);
        
    }
}
