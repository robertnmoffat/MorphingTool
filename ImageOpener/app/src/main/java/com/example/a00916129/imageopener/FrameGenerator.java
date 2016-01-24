package com.example.a00916129.imageopener;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.Selection;
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

        Point testPoint = new Point(10,10);
        double totalWeight=0.0;
        double totalDeltaX=0.0;
        double totalDeltaY=0.0;
        SelectionLine destLine1 = new SelectionLine(5,16,1,20,true);
        SelectionLine sourceLine1 = new SelectionLine(1,40,5,1,true);
        destLine1.setTwinLine(sourceLine1);
        SelectionLine destLine2 = new SelectionLine(5,30,15,35,true);
        SelectionLine sourceLine2 = new SelectionLine(8,1,40,40,true);
        destLine2.setTwinLine(sourceLine2);

        Point translatedPoint = translatePoint(destLine1, testPoint);
        System.out.println("POINT:"+translatedPoint);

        double currentWeight = calculateWeight(destLine1, testPoint);
        double deltaX = (translatedPoint.x-testPoint.x)*currentWeight;
        double deltaY = (translatedPoint.y-testPoint.y)*currentWeight;
        totalWeight += currentWeight;
        totalDeltaX+=deltaX;
        totalDeltaY+=deltaY;

        translatedPoint = translatePoint(destLine2, testPoint);
        System.out.println("POINT:"+translatedPoint);

        currentWeight = calculateWeight(destLine2, testPoint);
        deltaX = (translatedPoint.x-testPoint.x)*currentWeight;
        deltaY = (translatedPoint.y-testPoint.y)*currentWeight;
        totalWeight += currentWeight;
        totalDeltaX+=deltaX;
        totalDeltaY+=deltaY;

        double newX = testPoint.x+(totalDeltaX/totalWeight);
        double newY = testPoint.y+(totalDeltaY/totalWeight);

        System.out.println("X:"+newX+" Y:"+newY);


        Bitmap bitmap = leftImage;
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        Bitmap newFrame = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        System.out.println("Bitmap sizes: "+bitmap.getWidth()+" "+bitmap.getHeight()+" "+newFrame.getWidth()+" "+newFrame.getHeight());
        pixelArray = new int[x * y];
        int[] newPixelArray = new int[x * y];
        bitmap.getPixels(pixelArray, 0, x, 0, 0, x, y);
        double largestWeight=0.0;
        double smallestWeight=1000.0;

        for(int i=0; i<pixelArray.length; i++) {
            double weightSum=0.0;
            double xDeltaSum=0.0;
            double yDeltaSum=0.0;
            //for(int i=0; i<100; i++) {
            int currPixelValue = pixelArray[i];
            Point curPixel = new Point();
            curPixel.set(i%x, (i/x));
            //curPixel.set((int)(x*0.75), (int)(y*0.5));

            //System.out.println(pixelArray[i]);
            for(int j=0; j<lineList.size(); j++){
                if(lineList.get(j).isLeftLine()) {
                    timesRun++;
                    //Grab current line
                    SelectionLine curLine = lineList.get(j);

                    //Translate the initial point from the first line to the second line
                    Point newPoint = translatePoint(curLine, curPixel);

                    //if(!isValidPoint(newPoint, x, y))continue;

                    //Grab corresponding line
                    SelectionLine twinLine = curLine.getTwinLine();

                    double weight = calculateWeight(curLine, curPixel);
                    //weight = weight/100;
                    if(weight<smallestWeight)smallestWeight=weight;
                    if(weight>largestWeight)largestWeight=weight;
                    //if(weight>1)System.out.println(weight);
                    //System.out.println(weight);
                    //double weight = 1;
                    xDeltaSum+= (newPoint.x-curPixel.x)*weight;
                    yDeltaSum+= (newPoint.y-curPixel.y)*weight;
                    weightSum+= weight;

//                    Point newPoint = translatePoint(curLine, curPixel);
//
//                    //if(!isValidPoint(newPoint, x, y))continue;
//
//                    //Grab corresponding line
//                    SelectionLine twinLine = curLine.getTwinLine();
//
//                    double weight = calculateWeight(twinLine, newPoint);
//                    //double weight = 1;
//                    xDeltaSum+= (curPixel.x-newPoint.x)*weight;
//                    yDeltaSum+= (curPixel.y-newPoint.y)*weight;
//                    weightSum+= weight;

                    //if(i<1000){System.out.println("From:"+curPixel.x+","+curPixel.y+" to:"+newPoint.x+","+newPoint.y);}

//                    //Round to nearest pixel point
//                    int arrayPosition = (int)Math.round(newPoint.y*x+newPoint.x);
//                    //if in bounds
//                    if(arrayPosition>=0&&arrayPosition<newPixelArray.length) {
//                        //newPixelArray[arrayPosition]=currPixelValue;
//                        currPixelValue = pixelArray[arrayPosition];
//                        newPixelArray[i] = currPixelValue;
//
//                    }else {
//                        outOfBoundCount++;
//                    }
                }
            }

//            double finalX = curPixel.x+(xDeltaSum/weightSum);
//            double finalY = curPixel.y+(yDeltaSum/weightSum);
            double finalX = Math.round(curPixel.x+(xDeltaSum/weightSum));
            double finalY = Math.round(curPixel.y+(yDeltaSum/weightSum));

            //if(!isValidPoint(new Point((int)Math.round(finalX), (int)Math.round(finalY)),x,y))continue;

            //if(i<1000){System.out.println("From:"+curPixel.x+","+curPixel.y+" to:"+finalX+","+finalY);}
//            if(curPixel.x!=Math.round(finalX)||curPixel.y!=Math.round(finalY)){
//                System.out.println("From:"+curPixel.x+","+curPixel.y+" to:"+finalX+","+finalY);
//            }

            //Round to nearest pixel point
            int arrayPosition = (int)Math.round(finalY*x+finalX);
            //if in bounds
            if(arrayPosition>=0&&arrayPosition<newPixelArray.length) {
                //newPixelArray[arrayPosition]=currPixelValue;
                currPixelValue = pixelArray[arrayPosition];
                newPixelArray[i] = currPixelValue;


            }else {
                outOfBoundCount++;
            }
        }

        System.out.println("Smallest:"+smallestWeight+" Largest:"+largestWeight);

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

    public boolean isValidPoint(Point point, int width, int height){
        return(point.x>=0&&point.y>=0&&point.x<width&&point.y<height);
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
        newPoint.set((int)Math.round(newX),(int)Math.round(newY));
        return newPoint;
    }

    public double calculateWeight(SelectionLine curLine, Point curPixel){
        //weight parameters
        int b,p;
        p=0;
        b=1;
        double a;
        a=0.01;

        Point pqV,nV,xpV,pxV;
        pqV = new Point();
        pxV = new Point();
        Point qxV = new Point();

        pxV.set(curPixel.x - curLine.getX1(), curPixel.y - curLine.getY1());
        qxV.set(curPixel.x - curLine.getX2(), curPixel.y - curLine.getY2());

        double d = getPerpendicularDistance(curLine, curPixel);

        double fractionPercent = getFractionalPercent(curLine, curPixel);

        //System.out.println(fractionPercent);

        if(fractionPercent>1)d= vectorLength(qxV);
        if(fractionPercent<0)d= vectorLength(pxV);

        //if(d<1&&d>-1)System.out.println(d);

        //return Math.pow((Math.pow(vectorLength(pqV), p)) / (a + d),b);
        return (1/(0.01+Math.abs(d)));
    }

    public double getFractionalPercent(SelectionLine curLine, Point curPixel){
        Point pqV,nV,xpV,pxV;
        pqV = new Point();
        pxV = new Point();
        Point qxV = new Point();

        pqV.set(curLine.getX2() - curLine.getX1(), curLine.getY2() - curLine.getY1());
        pxV.set(curPixel.x - curLine.getX1(), curPixel.y - curLine.getY1());
        qxV.set(curPixel.x - curLine.getX2(), curPixel.y - curLine.getY2());

        double fraction = projectVector(pqV, pxV);
        return fraction / vectorLength(pqV);
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
