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

    private Double trust;

    public Answer(String questionNumber,List<Option> scanOptions) {
        this(questionNumber,scanOptions,0.4);
    }

    public Answer(String questionNumber, List<Option> scanOptions,Double basePercent) {
        this.questionNumber = questionNumber;
        this.scanOptions = scanOptions;
        this.basePercent = basePercent;
    }

    public Answer copy(){
        return new Answer(questionNumber,scanOptions,basePercent);
    }

    private int vernier;

    public List<Option> findChooseOptions(Integer maskScore) {
        this.maskScore=maskScore;
        return findChooseOptions();
    }

    private List<Option> findChooseOptions() {
        List<Option> collect = scanOptions.stream().filter(o -> o.getScore() > maskScore*basePercent).collect(Collectors.toList());
        if(collect.isEmpty()){
            scanOptions.sort(Comparator.comparing(Option::getScore));
            Option min = scanOptions.get(0);
            Option max = scanOptions.get(scanOptions.size()-1);
            if(max.getScore()==0){
                this.trust=1.0;
            }else {
                this.trust=(double)(max.getScore()-min.getScore())/max.getScore();
            }
            vernier = min.getScore();
            boolean get = false;
            boolean check = false;
            for (int i = 1; i < scanOptions.size(); i++) {
                Option current = scanOptions.get(i);
                if(current.getScore()>(vernier*2)){
                    vernier = current.getScore();
                    get = true;
                    check=false;
                }
                int j = i + 1;
                if(get && (j >= scanOptions.size() || current.getScore()>(scanOptions.get(j).getScore()*0.8))){
                    check=true;
                }
            }
            int score = (int) (maskScore * 0.25);
            vernier = vernier > score ? vernier : score;
            if(check){
                chooseOptions = scanOptions.stream().filter(o -> o.getScore()>=vernier).collect(Collectors.toList());
            }else {
                chooseOptions=collect;
            }
            return chooseOptions;
        }
        Option max = collect.stream().max(Comparator.comparing(Option::getScore)).orElse(new Option());
        Option min = scanOptions.stream().min(Comparator.comparing(Option::getScore)).orElse(new Option());
        if(max.getScore()==0){
            this.trust=1.0;
        }else {
            this.trust=(double)(max.getScore()-min.getScore())/max.getScore();
        }
        chooseOptions = collect.stream().filter(o -> o.getScore()>(max.getScore()*0.8)).collect(Collectors.toList());
        return chooseOptions;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public List<Option> getScanOptions() {
        return scanOptions;
    }

    public List<Option> getChooseOptions() {
        if(chooseOptions==null){
            return findChooseOptions();
        }
        return chooseOptions;
    }

    public Double getTrust() {
        return trust;
    }
}
