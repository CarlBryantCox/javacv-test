package com.chw.test;


import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.*;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class AnswerSheet {

    public static void main(String[] args){

        URL resource = Test.class.getClassLoader().getResource("pic/test_05.png");
        if(resource==null){
            throw new RuntimeException("读取路径失败！");
        }

        String srcPath = resource.getPath().substring(1);

        Mat src = imread(srcPath,IMREAD_GRAYSCALE);
        //show(src,"src");

        if(src.empty()){
            throw new RuntimeException("读取图片失败！");
        }
        Mat gray = src.clone();

        // 高斯滤波平滑处理
        Mat blurred = new Mat(gray.size(), CV_8UC1);
        GaussianBlur(gray,blurred,new Size(5, 5), 0);

        /*
         * canny 边缘检测算法
         * 梯度值>maxVal 则处理为边界
         * minVal<梯度值<maxVal 连有边界视为边界 否则舍弃
         * 梯度值小于minVal 则舍弃
         */
        Mat canny = new Mat(gray.size(), CV_8UC1);
        int minVal=75;
        int maxVal=200;
        Canny(blurred,canny,minVal,maxVal);

        /*
         * 轮廓检测

         * mode:轮廓检索模式
         * RETR_EXTERNAL ：只检索最外面的轮廓
         * RETR_LIST：检索所有的轮廓，并将其保存到一条链表当中
         * RETR_CCOMP：检索所有的轮廓，并将他们组织为两层：顶层是各部分的外部边界，第二层是空洞的边界
         * RETR_TREE：检索所有的轮廓，并重构嵌套轮廓的整个层次

         * method:轮廓逼近方法
         * CHAIN_APPROX_NONE：以Freeman链码的方式输出轮廓，所有其他方法输出多边形（顶点的序列）
         * CHAIN_APPROX_SIMPLE:压缩水平的、垂直的和斜的部分，也就是，函数只保留他们的终点部分
         */
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        findContours(canny,contours,hierarchy,RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);
        System.out.println("contours.size()="+contours.size());

        Mat mat = contours.get(0);
        // 求 周长 true表示闭合
        double arcLength = arcLength(mat, true);
        System.out.println("arcLength="+arcLength);

        /*
         * 轮廓近似
         */
        Mat approx = new Mat();
        approxPolyDP(mat,approx,arcLength*0.02,true);

        MatVector matVector = new MatVector(approx);
        //轮廓颜色 (B,G,R,透明度)
        Scalar scalar = new Scalar(0,0,255,1);
        //drawContours(gray,matVector,-1,scalar);
        int thickness=2;
        //指明画第几个轮廓 若为负数 则画出所有轮廓
        int contourIdx=-1;
        //轮廓结构信息
        int maxLevel=8;
        drawContours(gray,matVector,contourIdx,scalar,thickness,LINE_8,new Mat(),maxLevel,new Point());
        //show(gray,"gray");

        // 打印Mat
        //print(approx);

        /*
         * 执行透视变换
         */
        Mat warp = performPerspectiveWarp(src, approx);
        //show(warp,"warp");

        Mat threshPic = new Mat(warp.size());
        //阈值
        int thresh = 0;
        //当像素值超过了阈值（或者小于阈值，根据type来决定），所赋予的值
        int max_val=255;
        /*
         * THRESH_BINARY 超过阈值部分取max_val（最大值），否则取0
         * THRESH_BINARY_INV THRESH_BINARY的反转
         * THRESH_TRUNC 大于阈值部分设为阈值，否则不变
         * THRESH_TOZERO 大于阈值部分不改变，否则设为0
         * THRESH_TOZERO_INV THRESH_TOZERO的反转
         */
        // 当 type = CV_THRESH_OTSU 时, thresh 的值会被忽略 算法会自动计算合适的阈值
        threshold(warp,threshPic,thresh,max_val,THRESH_BINARY_INV | THRESH_OTSU);
        show(threshPic,"threshPic");

        MatVector mvt = new MatVector();
        findContours(threshPic,mvt,new Mat(),RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);
        System.out.println("mvt.size()="+mvt.size());

        List<MatRect> matRectList = filterContour(mvt);
        matRectList.sort(AnswerSheet::compareMatRect);

        if(matRectList.size()!=25){
            System.out.println("matRectList.size()="+matRectList.size());
            return;
        }

        int index = 0;
        for (int i = 0; i < 5; i++) {
            String number = String.valueOf(i+1);
            List<Answer> answerList = Arrays.asList(new Answer(number,"A"),
                    new Answer(number,"B"), new Answer(number,"C"),
                    new Answer(number,"D"), new Answer(number,"E"));
            for (int j = 0; j < 5; j++) {
                Mat mask = Mat.zeros(threshPic.size(), CV_8UC1).asMat();
                MatVector vector = new MatVector(matRectList.get(index).getMat());
                Scalar slr = new Scalar(255);
                drawContours(mask,vector,contourIdx,slr,CV_FILLED,LINE_8,new Mat(),maxLevel,new Point());
                Mat bit = new Mat();
                bitwise_and(threshPic,mask,bit);
                //show(threshPic,"after_threshPic");
                //show(mask,"mask"+index);
                //show(bit,"bit");
                index++;
                answerList.get(j).setScore(countNonZero(bit));
            }
            Answer answer = answerList.stream().max(Comparator.comparing(Answer::getScore)).orElse(null);
            System.out.println(answer);
        }

    }

    private static int compareMatRect(MatRect o1,MatRect o2){
        Rect r1 = o1.getRect();
        Rect r2 = o2.getRect();
        int i = r1.y() - r2.y();
        if(Math.abs(i)>5){
            return i;
        }
        return r1.x()-r2.x();
    }

    private static List<MatRect> filterContour(MatVector mvt){
        List<MatRect> rectList = new ArrayList<>();
        for (int i = 0; i < mvt.size(); i++) {
            Mat mat = mvt.get(i);
            Rect rect = boundingRect(mat);
            float ar = (float) rect.width()/rect.height();
            if(rect.height()>=20 && rect.width()>=20 && ar>=0.9 && ar<=1.1){
                rectList.add(new MatRect(mat,rect));
            }
        }
        return rectList;
    }

    /**
     * 按顺序找到对应下标0123分别是 左上，右上，右下，左下
     * @param mat 输入轮廓
     * @return 顺序的四个点
     */
    private static Point[] orderPoints(Mat mat){
        IntIndexer intIndexer = mat.createIndexer();
        Point[] list = new Point[4];
        for (int i = 0; i < 4; i++) {
            Point point = new Point(intIndexer.get(i,0,0),intIndexer.get(i,0,1));
            if(list[0]==null || (point.x()+point.y())<(list[0].x()+list[0].y())){
                list[0]=point;
            }
            if(list[1]==null || (point.x()-point.y())>(list[1].x()-list[1].y())){
                list[1]=point;
            }
            if(list[2]==null || (point.x()+point.y())>(list[2].x()+list[2].y())){
                list[2]=point;
            }
            if(list[3]==null || (point.x()-point.y())<(list[3].x()-list[3].y())){
                list[3]=point;
            }
        }
        return list;
    }

    private static void show(Mat image, String caption){
        CanvasFrame canvas = new CanvasFrame(caption, 1);
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        OpenCVFrameConverter converter = new OpenCVFrameConverter.ToIplImage();
        canvas.showImage(converter.convert(image));
//        canvas.waitKey(0);
//        canvas.dispose();
    }

    /**
     * Performs a perspective warp that takes four corners and stretches them to the corners of the image.
     * x1,y1 represents the top left corner, 2 top right, going clockwise.
     * This method does not release/deallocate the input image Mat, call inputMat.release() after this method
     * if you don't plan on using the input more after this method.
     *
     * @param srcMat The image to perform the stretch on
     * @return A stretched image approx.
     */
    private static Mat performPerspectiveWarp(Mat srcMat, Mat approx) {

        Point[] points = orderPoints(approx);

        int widthA = (int) Math.sqrt(Math.pow((points[1].x() - points[0].x()),2) + Math.pow((points[1].y() - points[0].y()),2));
        int widthB = (int) Math.sqrt(Math.pow((points[2].x() - points[3].x()),2) + Math.pow((points[2].y() - points[3].y()),2));
        int maxWidth = Math.max(widthA, widthB);
        int heightA = (int) Math.sqrt(Math.pow((points[3].x() - points[0].x()),2) + Math.pow((points[3].y() - points[0].y()),2));
        int heightB = (int) Math.sqrt(Math.pow((points[2].x() - points[1].x()),2) + Math.pow((points[2].y() - points[1].y()),2));
        int maxHeight = Math.max(heightA, heightB);

        FloatPointer srcCorners = new FloatPointer(
                points[0].x(), points[0].y(),
                points[1].x(), points[1].y(),
                points[2].x(), points[2].y(),
                points[3].x(), points[3].y());

        FloatPointer dstCorners = new FloatPointer(
                0, 0,
                maxWidth-1,0,
                maxWidth-1,maxHeight-1,
                0,maxHeight-1);

        //create matrices with width 2 to hold the x,y values, and 4 rows, to hold the 4 different corners.
        Mat src = new Mat(new Size(2, 4), CV_32F, srcCorners);
        Mat dst = new Mat(new Size(2, 4), CV_32F, dstCorners);

        Mat perspective = getPerspectiveTransform(src, dst);
        Mat result = new Mat();
        warpPerspective(srcMat, result, perspective, new Size(maxWidth, maxHeight));

        src.release();
        dst.release();
        srcCorners.deallocate();
        dstCorners.deallocate();

        return result;
    }
}