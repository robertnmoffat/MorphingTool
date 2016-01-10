package com.example.a00916129.imageopener;

/**
 * Created by a00916129 on 1/6/2016.
 */
public class SelectionLine {
    private int x1,x2,y1,y2;
    private SelectionLine twinLine;

    public SelectionLine(){

    }

    public SelectionLine(int x1, int y1, int x2, int y2){
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
    }
}
