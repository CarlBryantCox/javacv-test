package com.chw.test;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

public class MatRect {

    private Mat mat;

    private Rect rect;

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
}
