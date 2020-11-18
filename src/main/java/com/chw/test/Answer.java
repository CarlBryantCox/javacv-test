package com.chw.test;

public class Answer {

    private String number;

    private String answer;

    private int score;

    public Answer(String number, String answer) {
        this.number = number;
        this.answer = answer;
    }

    public Answer() {
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "number='" + number + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
