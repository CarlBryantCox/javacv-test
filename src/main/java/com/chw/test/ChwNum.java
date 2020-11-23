package com.chw.test;

import java.util.ArrayList;
import java.util.List;

public class ChwNum {

    private Integer gap;

    private Integer avg;

    private List<Integer> numList;

    private List<Integer> outList;

    public ChwNum(Integer gap) {
        this.avg = 0;
        this.numList = new ArrayList<>();
        this.gap =gap;
    }

    public boolean addNum(Integer num){
        if(numList.isEmpty()){
            numList.add(num);
            avg=num;
            outList=new ArrayList<>();
            return true;
        }
        if(Math.abs(num-avg)<gap){
            numList.add(num);
            setOutList();
            return true;
        }
        return false;
    }

    private void setOutList(){
        List<Integer> list = new ArrayList<>(numList.size());
        List<Integer> oList = new ArrayList<>();
        int total = 0;
        int max =0;
        int min = Integer.MAX_VALUE;
        for (Integer integer : numList) {
            if(Math.abs(integer-avg)<gap){
                list.add(integer);
                total=total+integer;
                if(integer>max){
                    max=integer;
                }
                if(integer<min){
                    min=integer;
                }
            }else {
                oList.add(integer);
            }
        }
        if(!oList.isEmpty()){
            outList=oList;
            numList=list;
        }
        if(list.size()>2){
            avg=(int)Math.round((double)(total-max-min)/(list.size()-2));
        }else {
            avg=(int)Math.round((double)total/list.size());
        }
    }

    public Integer getAvg() {
        return avg;
    }

    public List<Integer> getNumList() {
        return numList;
    }

    public List<Integer> getOutList() {
        return outList;
    }

    @Override
    public String toString() {
        return "ChwNum{" +
                "gap=" + gap +
                ", avg=" + avg +
                ", numList=" + numList +
                '}';
    }
}
