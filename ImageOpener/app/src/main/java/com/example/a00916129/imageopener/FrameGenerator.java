package com.example.a00916129.imageopener;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dingus on 18/01/2016.
 */
public class FrameGenerator{
    private static int frameCount=0;
    private Bitmap[] frames;
    private static int[] pixelArray;
    private static Context context;

    private static String directoryPath;

    public FrameGenerator(Context context){
        this.context = context;
    }

    public void generateFrames(Bitmap leftImage, Bitmap rightImage, ArrayList<SelectionLine> lineList, int frameCount){
        frames = new Bitmap[frameCount];
        //frames[0]=leftImage;

        int outOfBoundCount = 0;
        int timesRun=0;

        double weightSum=0.0;
        double xDeltaSum=0.0;
        double yDeltaSum=0.0;

        Bitmap bitmap = leftImage;
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        Bitmap newFrame = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        pixelArray = new int[x * y];
        int[] newPixelArray = new int[x * y];
        bitmap.getPixels(pixelArray, 0, x, 0, 0, x, y);

        for(int i=0; i<pixelArray.length; i++) {
            //for(int i=0; i<100; i++) {
            int currPixelValue = pixelArray[i];
            Point curPixel = new Point();
            curPixel.set(i%x, (i/x));
            //curPixel.set((int)(x*0.75), (int)(y*0.5));

            //System.out.println(pixelArray[i]);
            for(int j=0; j<lineList.size(); j++){
                if(!lineList.get(j).isLeftLine()) {
                    timesRun++;
                    //Grab current line
                    SelectionLine curLine = lineList.get(j);

                    //Translate the initial point from the first line to the second line
                    Point newPoint = translatePoint(curLine, curPixel);

                    //Grab corresponding line
                    SelectionLine twinLine = curLine.getTwinLine();

                    double weight = calculateWeight(curLine, curPixel);
                    xDeltaSum+= (newPoint.x-curPixel.x)*weight;
                    yDeltaSum+= (newPoint.y-curPixel.y)*weight;
                    weightSum+= weight;

                    //if(i<1000){System.out.println("From:"+curPixel.x+","+curPixel.y+" to:"+newPoint.x+","+newPoint.y);}

                    //Round to nearest pixel point
                    int arrayPosition = (int)Math.round(newPoint.y*x+newPoint.x);
                    //if in bounds
                    if(arrayPosition>=0&&arrayPosition<newPixelArray.length) {
                        //newPixelArray[arrayPosition]=currPixelValue;
                        currPixelValue = pixelArray[arrayPosition];
                        newPixelArray[i] = currPixelValue;

                    }else {
                        outOfBoundCount++;
                    }
                }
            }

            double finalX = curPixel.x+(xDeltaSum/weightSum);
            double finalY = curPixel.y+(yDeltaSum/weightSum);

//            //Round to nearest pixel point
//            int arrayPosition = (int)Math.round(finalY*x+finalX);
//            //if in bounds
//            if(arrayPosition>=0&&arrayPosition<newPixelArray.length) {
//                //newPixelArray[arrayPosition]=currPixelValue;
//                currPixelValue = pixelArray[arrayPosition];
//                newPixelArray[i] = currPixelValue;
//
//            }else {
//                outOfBoundCount++;
//            }
        }



        System.out.println(outOfBoundCount+" out of "+pixelArray.length+" Times run:"+timesRun);
        newFrame.setPixels(newPixelArray, 0, x, 0, 0, x, y);
        //newFrame.setPixels(pixelArray, 0, x, 0, 0, x, y);
        frames[0]=newFrame;
        saveFrame(newFrame);
//        for(int i=0; i<lineList.size(); i++){
//            if(lineList.get(i).isLeftLine()){
//
//            }
//        }
    }

    public Point translatePoint(SelectionLine curLine, Point curPixel){
        Point pqV,nV,xpV,pxV;

        //set vectors
        pqV = new Point();
        pxV = new Point();
        pqV.set(curLine.getX2() - curLine.getX1(), curLine.getY2() - curLine.getY1());
        pxV.set(curPixel.x - curLine.getX1(), curPixel.y - curLine.getY1());

        //get projections
        double d = getPerpendicularDistance(curLine,curPixel);
        //double d = projectVector(xpV, nV);
//                    double fraction = projectVector(pqV, pxV);
//                    double fractionPercent = fraction / vectorLength(pqV);
        double fraction = projectVector(pqV, pxV);
        double fractionPercent = fraction / vectorLength(pqV);

        //Grab corresponding line
        SelectionLine twinLine = curLine.getTwinLine();

        //Set twin lines variables
        Point twinPqV = new Point();
        Point twinNV = new Point();
        twinPqV.set(twinLine.getX2()-twinLine.getX1(), twinLine.getY2()-twinLine.getY1());
        twinNV.set(twinPqV.y*(-1), twinPqV.x);

        //Find point
        int startX=twinLine.getX1();
        int startY=twinLine.getY1();
        double moveUpX = fractionPercent*twinPqV.x;
        double moveUpY = fractionPercent*twinPqV.y;
        double directionX = twinNV.x/vectorLength(twinNV);
        double directionY = twinNV.y/vectorLength(twinNV);
        double moveOutX = -1*d*directionX;
        double moveOutY = -1*d*directionY;
        double newX = startX+moveUpX+moveOutX;
        double newY = startY+moveUpY+moveOutY;
        //double newX = twinLine.getX1()+fractionPercent*twinPqV.x-d*twinNV.x/vectorLength(twinNV);
        //double newY = twinLine.getY1()+fractionPercent*twinPqV.y-d*twinNV.y/vectorLength(twinNV);
        Point newPoint = new Point();
        newPoint.set((int)newX,(int)newY);
        return newPoint;
    }

    public double calculateWeight(SelectionLine curLine, Point curPixel){
        Point pqV,nV,xpV,pxV;

        //weight parameters
        int b,p;
        p=0;
        b=1;
        double a;
        a=0.01;


        double d = getPerpendicularDistance(curLine, curPixel);
        pqV = new Point(curLine.getX2() - curLine.getX1(), curLine.getY2() - curLine.getY1());

        //return Math.pow((Math.pow(vectorLength(pqV), p)) / (a + d),b);
        return (1/(0.01+Math.abs(d)));
    }

    public double getPerpendicularDistance(SelectionLine curLine, Point curPixel){
        Point nV = new Point();
        Point xpV = new Point();
        Point pqV = new Point();
        pqV.set(curLine.getX2() - curLine.getX1(), curLine.getY2() - curLine.getY1());
        xpV.set(curLine.getX1() - curPixel.x, curLine.getY1() - curPixel.y);
        nV.set(pqV.y * (-1), pqV.x);
       return projectVector(nV, xpV);
    }

    public double projectVector(Point first, Point second){
        return dotProduct(first,second)/vectorLength(first);
    }

    public double vectorLength(Point vec){
        return Math.sqrt(vec.x*vec.x+vec.y*vec.y);
    }

    public double dotProduct(Point first, Point second){
        return first.x*second.x+first.y*second.y;
    }

    public static int[] getPixelArray(){
        return pixelArray;
    }

    public void loadBitmap(){

    }

    public static Bitmap loadFrame(int frameNum){
        String path = directoryPath;
        Bitmap b=null;

            try {
                File f=new File(path, "frame"+frameNum+".jpg");
                b = BitmapFactory.decodeStream(new FileInputStream(f));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

        return b;
    }

    public static String saveFrame(Bitmap frame){
//        String filename = "frame"+nameCount++;
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream(filename);
//            frame.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//            // PNG is a lossless format, the compression factor (100) is ignored
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"frame"+frameCount+".jpg");
        //File path2 = new File("/storage/emulated/0/Pictures/", "frame"+frameCount+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            frame.compress(Bitmap.CompressFormat.PNG, 100, fos);
            //fos = new FileOutputStream(path2);
            //frame.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        directoryPath = directory.getAbsolutePath();
        return directory.getAbsolutePath();
    }


    public int getFrameCount(){
        return frameCount;
    }

    public Bitmap getFrame(int frameNumber){
        return frames[frameNumber];
    }
    
    public void setFrame(int frameNumber){
        if(frameNumber<frames.length){
            //// TODO: 18/01/2016 decide if you need this 
        }
    }
}
