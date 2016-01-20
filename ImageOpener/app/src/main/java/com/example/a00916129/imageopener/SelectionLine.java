package com.example.a00916129.imageopener;

/**
 * Created by a00916129 on 1/6/2016.
 */
public class SelectionLine {
    private int x1,x2,y1,y2,centerx,centery;
    private SelectionLine twinLine;
    private boolean leftLine;

    public SelectionLine(){

    }

    public SelectionLine(int x1, int y1, int x2, int y2, boolean leftLine){
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
        this.leftLine = leftLine;
    }

    public SelectionLine(SelectionLine toCopy){
        this.x1 = toCopy.getX1();
        this.y1 = toCopy.getY1();
        this.x2 = toCopy.getX2();
        this.y2 = toCopy.getY2();
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }


    public void setTwinLine(SelectionLine twinLine) {
        this.twinLine = twinLine;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public SelectionLine getTwinLine() {
        return twinLine;
    }

    public void setCenterx(int centerx) {
        this.centerx = centerx;
    }

    public void setCentery(int centery) {
        this.centery = centery;
    }

    public int getCenterx() {
        return centerx;
    }

    public int getCentery() {
        return centery;
    }

    public boolean isLeftLine() {
        return leftLine;
    }

    public void setLeftLine(boolean leftLine) {
        this.leftLine = leftLine;
    }


}
