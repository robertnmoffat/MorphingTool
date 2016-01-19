package com.example.a00916129.imageopener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dingus on 18/01/2016.
 */
public class FrameGenerator {
    private int nameCount=0;

    public FrameGenerator(){}

    public void generateFrames(Bitmap leftImage, Bitmap rightImage, ArrayList<SelectionLine> lineList, int frameCount){

    }

    public void saveFrame(Bitmap frame){
        String filename = "frame"+nameCount++;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            frame.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getFrameCount(){
        return nameCount;
    }

    public Bitmap getFrame(int frameNumber){
        return BitmapFactory.decodeFile("frame"+frameNumber);
    }
}
