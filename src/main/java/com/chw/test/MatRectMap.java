package com.chw.test;

import java.util.ArrayList;
import java.util.List;

public class MatRectMap {

    private List<MatRectRow> rowList;

    public List<MatRectRow> getRowList() {
        if(rowList==null){
            rowList=new ArrayList<>();
        }
        return rowList;
    }
}
