package com.chw.test;

import java.util.ArrayList;
import java.util.List;

public class ChwNumList {

    private List<ChwNum> chwNumList;

    private Integer max;

    private Integer min;

    private Integer gap;

    public ChwNumList(Integer gap) {
        this.chwNumList = new ArrayList<>();
        this.max = 0;
        this.min = -1;
        this.gap = gap;
    }

    public void addNum(Integer num){
        for (ChwNum chwNum : chwNumList) {
            if(chwNum.addNum(num)){
                setMaxMin(chwNum);
                return;
            }
        }
        ChwNum chwNum = new ChwNum(gap);
        chwNum.addNum(num);
        setMaxMin(chwNum);
        chwNumList.add(chwNum);
    }

    private void setMaxMin(ChwNum chwNum){
        if(chwNum.getAvg()>max){
            max=chwNum.getAvg();
        }
        if(chwNum.getAvg()<min || min==-1){
            min=chwNum.getAvg();
        }
    }

    public List<ChwNum> getChwNumList() {
        return chwNumList;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getMin() {
        return min;
    }

    @Override
    public String toString() {
        return "ChwNumList{" +
                "chwNumList=" + chwNumList +
                ", max=" + max +
                ", min=" + min +
                ", gap=" + gap +
                '}';
    }
}
