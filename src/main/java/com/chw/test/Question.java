package com.chw.test;

import org.bytedeco.opencv.opencv_core.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Question {

    private Config config;

    private Integer maskScore;

    private Mat src;

    private MatRectMap matRectMap;

    private List<MatRect> matRectList;

    private List<MatRect> modelList;

    private List<Answer> answerList;

    private Mat threshPic;

    public Question(Mat src,Config config) {
        this(src,config,null);
    }

    public Question(Mat src,Config config,List<MatRect> modelList) {
        this.config=config;
        this.modelList = modelList;
        this.src=Helper.getResize(src,config.getMultiple());
        this.setThreshPic();
        this.setMatRectList();
        this.maskScore=this.calculateScore(matRectList.get(0).getMat());
    }

    public List<Answer> findAnswer(){
        if(matRectList.size()!=(config.getOptionCount()*answerList.size())){
            if(modelList==null){
                return findAnswerByMap();
            }else {
                matRectList = modelList;
                if(matRectList.size()!=(config.getOptionCount()*answerList.size())){
                    return findAnswerByMap();
                }
            }
        }
        int index = 0;
        for (Answer answer : answerList) {
            List<Option> optionList = answer.getScanOptions();
            for (int j = 0; j < config.getOptionCount(); j++) {
                optionList.get(j).setScore(this.calculateScore(matRectList.get(index).getMat()));
                index++;
            }
            List<Option> chooseOptions = answer.findChooseOptions();
            System.out.println(chooseOptions);
        }
        return answerList;
    }

    public List<Answer> findAnswerByMap(){
        if(matRectMap==null){
            return answerList;
        }
        List<MatRectRow> rowList = matRectMap.getRowList();
        if(rowList.size()!=answerList.size()){
            return answerList;
        }
        for (int i = 0; i < answerList.size(); i++) {
            Answer answer = answerList.get(i);
            List<Option> optionList = answer.getScanOptions();
            List<MatRect> matRectList = rowList.get(i).getMatRectList();
            if(optionList.size()!=matRectList.size()){
                continue;
            }
            for (int j = 0; j < matRectList.size(); j++) {
                optionList.get(j).setScore(this.calculateScore(matRectList.get(j).getMat()));
            }
            List<Option> chooseOptions = answer.findChooseOptions();
            System.out.println(chooseOptions);
        }
        return answerList;
    }

    private int calculateScore(Mat mat){
        Mat mask = Mat.zeros(threshPic.size(), CV_8UC1).asMat();
        MatVector vector = new MatVector(mat);
        Scalar slr = new Scalar(255);
        drawContours(mask,vector,-1,slr,CV_FILLED,LINE_8,new Mat(),8,new Point());
        Mat bit = new Mat();
        bitwise_and(threshPic,mask,bit);
        return countNonZero(bit);
    }

    /**
     * 二值化
     */
    private void setThreshPic(){
        threshPic = new Mat(src.size());
        //当像素值超过了阈值（或者小于阈值，根据type来决定），所赋予的值
        int max_val=255;
        // 阈值
        int thresh = config.getThresh();
        /*
         * THRESH_BINARY 超过阈值部分取max_val（最大值），否则取0
         * THRESH_BINARY_INV THRESH_BINARY的反转
         * THRESH_TRUNC 大于阈值部分设为阈值，否则不变
         * THRESH_TOZERO 大于阈值部分不改变，否则设为0
         * THRESH_TOZERO_INV THRESH_TOZERO的反转
         */

        if(thresh>0){
            threshold(src,threshPic,thresh,max_val,THRESH_BINARY_INV);
        }else {
            // 当 type = CV_THRESH_OTSU 时, thresh 的值会被忽略 算法会自动计算合适的阈值
            threshold(src,threshPic,thresh,max_val,THRESH_BINARY_INV | THRESH_OTSU);
            //adaptiveThreshold(src,threshPic,max_val,ADAPTIVE_THRESH_GAUSSIAN_C,THRESH_BINARY_INV,3,5);
        }
        if(config.getOpen()){
            threshPic=Helper.getMorphologyEx(threshPic,MORPH_OPEN);
        }
        Helper.show(threshPic,"threshPic");
    }

    /**
     * 找到题目的轮廓及外接矩形
     */
    private void setMatRectList(){
        MatVector mvt = new MatVector();
        findContours(threshPic,mvt,new Mat(),RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);
        matRectList = filterContour(mvt);
        matRectList.sort(MatRect::compareTo);
    }

    /**
     * 过滤找到的轮廓
     * @param mvt 找到的轮廓
     * @return matRectList
     */
    private List<MatRect> filterContour(MatVector mvt){
        List<MatRect> rectList = new ArrayList<>();
        for (int i = 0; i < mvt.size(); i++) {
            Mat mat = mvt.get(i);
            Rect rect = boundingRect(mat);
            if(rect.height()>config.getHeight()*(1-config.getHeightScope())
                    && rect.height()<config.getHeight()*(1+config.getHeightScope())
                    && rect.width()>config.getWidth()*(1-config.getWidthScope())
                    && rect.width()<config.getWidth()*(1+config.getWidthScope())){
                rectList.add(new MatRect(mat,rect));
            }
        }
        return rectList;
    }

    public Integer getMaskScore() {
        return maskScore;
    }

    public List<Answer> getAnswerList() {
        if(answerList==null){
            answerList=new ArrayList<>();
        }
        return answerList;
    }
}
