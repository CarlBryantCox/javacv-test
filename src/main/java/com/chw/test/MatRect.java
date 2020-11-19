package com.chw.test;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

public class MatRect implements Comparable<MatRect>{

    private Mat mat;

    private Rect rect;

    public MatRect(Rect rect) {
        this.rect = rect;
        this.mat = Helper.rectToMat(rect);
    }

    public MatRect(Mat mat, Rect rect) {
        this.mat = mat;
        this.rect = rect;
    }

    public MatRect() {
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    @Override
    public int compareTo(MatRect o) {
        Rect r1 = rect;
        Rect r2 = o.getRect();
        int i = r1.y() - r2.y();
        if(Math.abs(i)>5){
            return i;
        }
        return r1.x()-r2.x();
    }

    @Override
    public String toString() {
        return "MatRect{rect.x()="+rect.x()+"--rect.y()="+rect.y()+"--rect.height()="+rect.height()+"--rect.width()="+rect.width()+"}";
    }
}
