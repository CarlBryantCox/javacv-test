package com.chw.test;

import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import javax.swing.*;
import java.net.URL;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Helper {

    public static void show(Mat image, String caption){
        CanvasFrame canvas = new CanvasFrame(caption, 1);
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        OpenCVFrameConverter converter = new OpenCVFrameConverter.ToIplImage();
        canvas.showImage(converter.convert(image));
    }

    public static String getAbsolutePath(String relativePath){
        URL resource = Helper.class.getClassLoader().getResource(relativePath);
        if(resource==null){
            throw new RuntimeException("读取路径失败！");
        }
        return resource.getPath().substring(1);
    }

    public static Mat openImg(String relativePath){
        Mat src = imread(Helper.getAbsolutePath(relativePath),IMREAD_GRAYSCALE);
        //show(src,"src");
        if(src.empty()){
            throw new RuntimeException("读取图片失败！");
        }
        return src;
    }

    public static Mat getResize(Mat src,int multiple){
        if(multiple==1){
            return src;
        }
        Mat mat = new Mat();
        Size size = new Size(src.size().width()*multiple,src.size().height()*multiple);
        resize(src,mat,size);
        return mat;
    }

    public static Mat getGaussianBlur(Mat gray){
        // 高斯滤波平滑处理
        Mat blurred = new Mat(gray.size(), CV_8UC1);
        GaussianBlur(gray,blurred,new Size(5, 5), 0);
        return blurred;
    }

    public static Mat getMedianBlur(Mat gray){
        // 中值滤波
        Mat blurred = new Mat(gray.size(), CV_8UC1);
        medianBlur(gray,blurred,3);
        return blurred;
    }

    public static Mat getSharpen(Mat src){
        Mat dest = new Mat();
        // 构造锐化核，初始化所有值为 0
        Mat kernel = new Mat(3, 3, CV_32F, new Scalar(0));
        // Indexer 被用于访问矩阵中的值
        FloatIndexer ki = kernel.createIndexer();
        ki.put(1, 1, 5);
        ki.put(0, 1, -1);
        ki.put(2, 1, -1);
        ki.put(1, 0, -1);
        ki.put(1, 2, -1);
        // g过滤图像
        filter2D(src, dest, src.depth(), kernel);
        return dest;
    }

    /**
     * MORPH_ERODE 函数做腐蚀运算
     * MORPH_OPEN 函数做开运算
     * MORPH_CLOSE 函数做闭运算
     * MORPH_GRADIENT 函数做形态学梯度运算
     * MORPH_TOPHAT 函数做顶帽运算
     * MORPH_BLACKHAT 函数做黑帽运算
     * MORPH_DILATE 函数做膨胀运算
     */
    public static Mat getMorphologyEx(Mat src,int type){
        Mat dest = new Mat();
        Mat element = getStructuringElement(MORPH_RECT,new Size(3, 3));
        morphologyEx(src, dest, type,element);
        return dest;
    }

    public static Mat rectToMat(Rect rect){
        Mat mat = new Mat(4,1,12);
        IntIndexer indexer = mat.createIndexer();
        indexer.put(0,0,0,rect.x());
        indexer.put(0,0,1,rect.y());
        indexer.put(1,0,0,rect.x()+rect.width());
        indexer.put(1,0,1,rect.y());
        indexer.put(2,0,0,rect.x()+rect.width());
        indexer.put(2,0,1,rect.y()+rect.height());
        indexer.put(3,0,0,rect.x());
        indexer.put(3,0,1,rect.y()+rect.height());
        return mat;
    }

}
