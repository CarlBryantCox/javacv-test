package com.chw.test;

import java.util.ArrayList;
import java.util.List;

public class MatRectRow {

    private List<MatRect> matRectList;

    public List<MatRect> getMatRectList() {
        if(matRectList==null){
            matRectList=new ArrayList<>();
        }
        return matRectList;
    }
}
