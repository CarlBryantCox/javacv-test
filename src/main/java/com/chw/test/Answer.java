package com.chw.test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Answer {

    private Double basePercent;

    private Integer maskScore;

    private String questionNumber;

    private List<Option> scanOptions;

    private List<Option> chooseOptions;

    public Answer(String questionNumber,List<Option> scanOptions,Integer maskScore) {
        this(questionNumber,scanOptions,maskScore,0.4);
    }

    public Answer(String questionNumber, List<Option> scanOptions,Integer maskScore,Double basePercent) {
        this.questionNumber = questionNumber;
        this.maskScore=maskScore;
        this.scanOptions = scanOptions;
        this.basePercent = basePercent;
    }

    public List<Option> findChooseOptions() {
        //System.out.println(scanOptions);
        List<Option> collect = scanOptions.stream().filter(o -> o.getScore() > maskScore*basePercent).collect(Collectors.toList());
        if(collect.isEmpty()){
            chooseOptions=collect;
            return chooseOptions;
        }
        Option option = collect.stream().max(Comparator.comparing(Option::getScore)).orElse(null);
        chooseOptions = collect.stream().filter(o -> o.getScore()>(option.getScore()*0.8)).collect(Collectors.toList());
        return chooseOptions;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    public List<Option> getScanOptions() {
        return scanOptions;
    }

    public void setScanOptions(List<Option> scanOptions) {
        this.scanOptions = scanOptions;
    }

    public List<Option> getChooseOptions() {
        if(chooseOptions==null){
            return findChooseOptions();
        }
        return chooseOptions;
    }

    public void setChooseOptions(List<Option> chooseOptions) {
        this.chooseOptions = chooseOptions;
    }
}
