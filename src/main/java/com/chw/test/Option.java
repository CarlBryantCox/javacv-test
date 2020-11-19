package com.chw.test;

public class Option {

    private String number;

    private String answer;

    private Integer score;

    public Option(String number, String answer) {
        this.number = number;
        this.answer = answer;
    }

    public Option() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getScore() {
        if(score==null){
            score=0;
        }
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Option{" +
                "number='" + number + '\'' +
                ", answer='" + answer + '\'' +
                ", score=" + score +
                '}';
    }
}
