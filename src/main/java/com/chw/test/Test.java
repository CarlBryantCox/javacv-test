package com.chw.test;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgproc.MORPH_CLOSE;

public class Test {

    public static void main(String[] args) {
        test1();
    }

    private static void test2(){
        Mat src = Helper.openImg("pic/test_06.png");
        Mat resize = Helper.getResize(src.clone(), 3);
        Helper.show(resize,"resize");
        Mat gaussianBlur = Helper.getMorphologyEx(resize,MORPH_CLOSE);
        Helper.show(gaussianBlur,"gaussianBlur");
    }


    private static void test1(){
        Mat src = Helper.openImg("pic/test_11.png");
        Config config = new Config(4, 3, 0, 14, 9, 0.20, 0.20, true);
        List<MatRect> modelList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int y=22+(i)*48;
            for (int j = 0; j < 4; j++) {
                int x = 87+(j)*66;
                modelList.add(new MatRect(new Rect(x,y,44,26)));
            }
        }
        Question question = new Question(src.clone(),config,modelList);
        for (int i = 0; i < 5; i++) {
            String number = String.valueOf(i+51);
            List<Option> optionList = Arrays.asList(new Option(number,"A"),
                    new Option(number,"B"), new Option(number,"C"),
                    new Option(number,"D"));
            Answer answer = new Answer(number,optionList,question.getMaskScore());
            question.getAnswerList().add(answer);
        }
        question.findAnswer();
    }
}
