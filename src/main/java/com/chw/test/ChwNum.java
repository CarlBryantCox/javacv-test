package com.chw.test;

import java.util.ArrayList;
import java.util.List;

public class ChwNum {

    private Integer gap;

    private Integer total;

    private Integer avg;

    private List<Integer> numList;

    public ChwNum(Integer gap) {
        this.avg = 0;
        this.numList = new ArrayList<>();
        this.gap =gap;
        this.total = 0;
    }

    public boolean addNum(Integer num){
        if(numList.isEmpty()){
            total=num;
            avg=num;
            numList.add(num);
            return true;
        }
        if(Math.abs(num-avg)<gap){
            numList.add(num);
            total=total+num;
            avg=total/numList.size();
            return true;
        }
        return false;
    }

    public Integer getAvg() {
        return avg;
    }

    public List<Integer> getNumList() {
        return numList;
    }

    @Override
    public String toString() {
        return "ChwNum{" +
                "gap=" + gap +
                ", total=" + total +
                ", avg=" + avg +
                ", numList=" + numList +
                '}';
    }
}
