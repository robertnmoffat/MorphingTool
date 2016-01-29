package com.example.a00916129.imageopener;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by dingus on 18/01/2016.
 */
public class FrameGenerator{
    private static int frameCount;
    private Bitmap[] frames;
    private int[] pixelArray;
    private Context context;
    private final int LEFT = 0;
    private final int RIGHT = 1;

    private static String directoryPath;

    public FrameGenerator(Context context){
        this.context = context;
    }

    /**
     *
     * @param leftImage
     * @param rightImage
     * @param lineList
     * @param frameAmount
     */
    public void generateFrames(Bitmap leftImage, Bitmap rightImage, ArrayList<SelectionLine> lineList, int frameAmount){
        frameCount=0;
        frames = new Bitmap[frameAmount];
        //frames[0]=leftImage;
        Bitmap currentFrame;
        ArrayList<SelectionLine> lineTransitionVectors;
        ArrayList<SelectionLine> translatedLines;

        long startTime = System.currentTimeMillis();

        for(int i=0; i<frameAmount; i++) {
            currentFrame = createFrame(leftImage, lineList, i, frameAmount);
            saveFrame(currentFrame, i);
            frameCount++;
        }


        double timeTaken = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("Morph finished in "+timeTaken+" seconds");
    }

    /**
     *
     * @return
     */
    public Bitmap createFrame(Bitmap sourceImage, ArrayList<SelectionLine> lineList, int selectedFrame, int totalFrames) {
        int timesRun=0, outOfBoundCount=0;
        int x=sourceImage.getWidth();
        int y=sourceImage.getHeight();
        Bitmap newFrame = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        pixelArray = new int[x * y];
        sourceImage.getPixels(pixelArray, 0, x, 0, 0, x, y);
        int[] newPixelArray = new int[x * y];


        for (int i = 0; i < pixelArray.length; i++) {
            double weightSum = 0.0;
            double xDeltaSum = 0.0;
            double yDeltaSum = 0.0;

            int currPixelValue = pixelArray[i];
            Point curPixel = new Point();
            curPixel.set(i % x, (i / x));

            for (int j = 0; j < lineList.size(); j++) {
                if (!lineList.get(j).isLeftLine()) {
                    timesRun++;
                    //Grab current line
                    SelectionLine curLine = lineList.get(j);
                    //shift the current line to its position based on the current frame
                    if(totalFrames>1) {
                        curLine = shiftLine(curLine, selectedFrame, totalFrames - 1);
                    }
                    //Translate the initial point from the first line to the second line
                    Point newPoint = translatePoint(curLine, curPixel);
                    //Grab corresponding line
                    SelectionLine twinLine = curLine.getTwinLine();

                    double weight = calculateWeight(curLine, curPixel);
                    xDeltaSum += (newPoint.x - curPixel.x) * weight;
                    yDeltaSum += (newPoint.y - curPixel.y) * weight;
                    weightSum += weight;
                }
            }

            double finalX = Math.round(curPixel.x + (xDeltaSum / weightSum));
            double finalY = Math.round(curPixel.y + (yDeltaSum / weightSum));

            if(!isValidPoint(new Point((int)finalX, (int)finalY), x, y))continue;

            //Round to nearest pixel point
            int arrayPosition = (int) Math.round(finalY * x + finalX);
            //if in bounds
            if (arrayPosition >= 0 && arrayPosition < newPixelArray.length) {
                currPixelValue = pixelArray[arrayPosition];
                newPixelArray[i] = currPixelValue;
            } else {
                outOfBoundCount++;
            }
        }
        System.out.println("Out of bounds frames:" + outOfBoundCount);
        newFrame.setPixels(newPixelArray, 0, x, 0, 0, x, y);
        frames[frameCount]=newFrame;
        return newFrame;
    }


    /**
     * Shifts a line according to its current frame
     * @param curLine
     * @param position
     * @param total
     * @return
     */
    SelectionLine shiftLine(SelectionLine curLine, int position, int total){
        SelectionLine vectorDifferenceLine = new SelectionLine();
        SelectionLine shiftedLine = new SelectionLine();
        double moveAmount;

        //create a line that is a vector representing the total change between this line to its twin
        vectorDifferenceLine.setX1(curLine.getTwinLine().getX1()-curLine.getX1());
        vectorDifferenceLine.setY1(curLine.getTwinLine().getY1()-curLine.getY1());
        vectorDifferenceLine.setX2(curLine.getTwinLine().getX2()-curLine.getX2());
        vectorDifferenceLine.setY2(curLine.getTwinLine().getY2() - curLine.getY2());

        //Set the points of this line at the current frame based on the transition vector
        shiftedLine.setX1(curLine.getX1()+vectorDifferenceLine.getX1()/total*position);
        shiftedLine.setY1(curLine.getY1()+vectorDifferenceLine.getY1()/total*position);
        shiftedLine.setX2(curLine.getX2()+vectorDifferenceLine.getX2()/total*position);
        shiftedLine.setY2(curLine.getY2()+vectorDifferenceLine.getY2()/total*position);

        //give it the same twin as the original
        shiftedLine.setTwinLine(curLine.getTwinLine());

        return shiftedLine;
    }

    /**
     *Returns whether or not the passed point is within bounds of the image.
     * @param point
     * @param width
     * @param height
     * @return
     */
    public boolean isValidPoint(Point point, int width, int height){
        return(point.x>=0&&point.y>=0&&point.x<width&&point.y<height);
    }

    /**
     *
     * @param curLine
     * @param curPixel
     * @return
     */
    public Point translatePoint(SelectionLine curLine, Point curPixel){
        Point pqV,nV,xpV,pxV;

        //set vectors
        pqV = new Point();
        pxV = new Point();
        pqV.set(curLine.getX2() - curLine.getX1(), curLine.getY2() - curLine.getY1());
        pxV.set(curPixel.x - curLine.getX1(), curPixel.y - curLine.getY1());

        //get projections
        double d = getPerpendicularDistance(curLine,curPixel);
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
        Point newPoint = new Point();
        newPoint.set((int)Math.round(newX),(int)Math.round(newY));
        return newPoint;
    }

    /**
     *
     * @param curLine
     * @param curPixel
     * @return
     */
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

        if(fractionPercent>1)d= vectorLength(qxV);
        if(fractionPercent<0)d= vectorLength(pxV);

        return (1/(0.01+Math.abs(d)));
    }

    /**
     *
     * @param curLine
     * @param curPixel
     * @return
     */
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

    public int[] getPixelArray(){
        return pixelArray;
    }

    public void loadBitmap(){

    }

    public static Bitmap loadFrame(int frameNum){
        String path = directoryPath;
        Bitmap b=null;

            try {
                File f=new File(path, "frame"+frameNum+".jpg");
                System.out.println("Loading file frame"+frameNum+".jpg");
                b = BitmapFactory.decodeStream(new FileInputStream(f));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

        return b;
    }

    public String saveFrame(Bitmap frame, int frameNumber){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"frame"+frameNumber+".jpg");
        System.out.println("Saving file frame"+frameNumber+".jpg");
        //File path2 = new File("/storage/emulated/0/Pictures/", "frame"+frameCount+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            frame.compress(Bitmap.CompressFormat.PNG, 100, fos);
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


    public static int getFrameCount(){
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
