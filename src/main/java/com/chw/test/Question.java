package com.chw.test;

import org.bytedeco.opencv.opencv_core.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Question {

    private Config config;

    private Integer maskScore;

    private Mat src;

    private MatRectMap matRectMap;

    // 识别到的填涂区域
    private List<MatRect> matRectList;

    // 模板提供的填涂区域
    private List<MatRect> modelList;

    private List<Answer> answerList;

    private Mat threshPic;

    public Question(Mat src,Config config) {
        this(src,config,null,null);
    }

    public Question(Mat src, Config config,List<MatRect> modelList) {
        this(src, config,modelList,null);
    }

    public Question(Mat src,Config config,MatRectMap matRectMap) {
        this(src,config,null,matRectMap);
    }

    public Question(Mat src, Config config, List<MatRect> modelList, MatRectMap matRectMap) {
        this.config=config;
        this.modelList = modelList;
        this.matRectMap = matRectMap;
        this.src=Helper.getResize(src,config.getMultiple());
        this.setThreshPic();
        if(matRectMap==null){
            this.setMatRectList();
        }else {
            this.matRectList = new ArrayList<>();
        }
        this.setMaskScore();
        System.out.println("maskScore="+maskScore);
    }

    /**
     * 设置填涂的满分值
     */
    private void setMaskScore(){
        if(!matRectList.isEmpty()){
            this.maskScore=countNonZero(getMask(matRectList.get(0).getMat()));
            return;
        }
        if(modelList!=null && !modelList.isEmpty()){
            this.maskScore=countNonZero(getMask(modelList.get(0).getMat()));
            return;
        }
        if(matRectMap!=null && !matRectMap.getRowList().isEmpty()){
            MatRectRow matRectRow = matRectMap.getRowList().get(0);
            if(!matRectRow.getMatRectList().isEmpty()){
                this.maskScore=countNonZero(getMask(matRectRow.getMatRectList().get(0).getMat()));
                return;
            }
        }
        this.maskScore=0;
    }

    /**
     * 识别填涂的答案（多途径寻找）
     */
    public List<Answer> findAnswer(){
        if(matRectList.size()!=(config.getOptionCount()*answerList.size())){
            if(!config.getTryFix() || !tryFullMatRectList()){
                if(modelList==null){
                    return findAnswerByMap();
                }else {
                    matRectList = modelList;
                    if(matRectList.size()!=(config.getOptionCount()*answerList.size())){
                        System.out.println("--------模板异常------------");
                        return findAnswerByMap();
                    }
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

    /**
     * 识别指定的区域
     */
    public List<Answer> findAnswerByMap(){
        if(matRectMap==null){
            System.out.println("--------识别失败--------");
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

    /**
     * 尝试利用识别到的信息补充模板
     */
    private boolean tryFullMatRectList(){
        System.out.println("--------开始尝试补充模板-------");
        if(matRectList.isEmpty()){
            return false;
        }
        int xGap = (int) (config.getWidth()*config.getWidthScope()*2);
        int yGap = (int) (config.getHeight()*config.getHeightScope()*2);
        ChwNumList xList = new ChwNumList(xGap);
        ChwNumList yList = new ChwNumList(yGap);
        System.out.println(matRectList);
        for (MatRect matRect : matRectList) {
            Rect rect = matRect.getRect();
            xList.addNum(rect.x());
            yList.addNum(rect.y());
        }
        if(!checkNumList(xList,config.getWidthGap(),xGap,config.getOptionCount())
                || !checkNumList(yList,config.getHeightGap(),yGap,answerList.size())){
            System.out.println("----尝试补充模板失败-----------");
            return false;
        }
        matRectList = new ArrayList<>();
        for (ChwNum yNum : yList.getChwNumList()) {
            for (ChwNum xNum : xList.getChwNumList()) {
                matRectList.add(new MatRect(new Rect(xNum.getAvg(),yNum.getAvg(),config.getWidth(),config.getHeight())));
            }
        }
        System.out.println("----尝试补充模板成功-----------");
        return true;
    }

    /**
     * 判断识别到的信息能否 得到 完整的模板
     */
    private boolean checkNumList(ChwNumList chwNumList,int bigGap,int gap,int count){
        System.out.println("inputList------");
        System.out.println(chwNumList);
        List<ChwNum> numList = chwNumList.getChwNumList();
        if(numList.size()==count){
            numList.sort(Comparator.comparing(ChwNum::getAvg));
            return true;
        }
        return fullNumList(chwNumList,bigGap,gap,count);
    }

    /**
     * 尝试补充 识别到的信息
     */
    private boolean fullNumList(ChwNumList chwNumList,int bigGap,int gap,int count){
        List<ChwNum> numList = chwNumList.getChwNumList();
        double round;
        if(bigGap==0){
            numList.sort(Comparator.comparing(ChwNum::getAvg));
            ChwNumList gapList = new ChwNumList(gap);
            gapList.addNum(numList.get(numList.size()-1).getAvg()-numList.get(0).getAvg());
            for (int i = 1; i < numList.size(); i++) {
                gapList.addNum(numList.get(i).getAvg()-numList.get(i-1).getAvg());
            }
            round = (double) gapList.getMax() / gapList.getMin();
            System.out.println("-----王道-------");
        }else {
            round = (double)(chwNumList.getMax()-chwNumList.getMin()) / bigGap;
            System.out.println("-----侠道-------");
        }
        if(round<(count-1+0.1) && round>(count-1-0.1)){
            chwNumList.getChwNumList().clear();
            int v = (int) ((double) (chwNumList.getMax() - chwNumList.getMin()) / (count - 1));
            for (int i = 0; i < count; i++) {
                chwNumList.addNum(chwNumList.getMin()+i*v);
            }
            System.out.println("-----outputList----");
            System.out.println(chwNumList);
            return true;
        }
        return false;
    }

    /**
     * 计算掩码下 非零点的数量（即计算答题卡被涂黑的区域大小）
     */
    private int calculateScore(Mat mat){
        Mat mask =getMask(mat);
        Mat bit = new Mat();
        bitwise_and(threshPic,mask,bit);
        return countNonZero(bit);
    }

    /**
     * 获取掩码
     */
    private Mat getMask(Mat mat){
        Mat mask = Mat.zeros(threshPic.size(), CV_8UC1).asMat();
        MatVector vector = new MatVector(mat);
        Scalar slr = new Scalar(255);
        drawContours(mask,vector,-1,slr,CV_FILLED,LINE_8,new Mat(),8,new Point());
        return mask;
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
        //System.out.println(matRectList);
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
