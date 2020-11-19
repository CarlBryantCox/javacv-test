package com.chw.test;

public class Config {

    private Integer optionCount;

    private Integer multiple;

    private Integer thresh;

    private Integer width;

    private Integer height;

    private Double widthScope;

    private Double heightScope;

    private Boolean open;

    public Config() {
        this(5,1,0,35,35,0.1,0.1,false);
    }

    public Config(Integer optionCount,Integer thresh, Integer width, Integer height) {
        this(optionCount,1,thresh,width,height,0.1,0.1,false);
    }

    public Config(Integer optionCount,Integer thresh, Integer width, Integer height, Double widthScope, Double heightScope) {
        this(optionCount,1,thresh,width,height,widthScope,heightScope,false);
    }

    public Config(Integer optionCount,Integer multiple, Integer thresh, Integer width, Integer height, Double widthScope, Double heightScope,Boolean open) {
        this.optionCount = optionCount;
        this.multiple = multiple;
        this.thresh = thresh;
        this.width = width*multiple;
        this.height = height*multiple;
        this.widthScope = widthScope;
        this.heightScope = heightScope;
        this.open=open;
    }

    public Integer getOptionCount() {
        return optionCount;
    }

    public void setOptionCount(Integer optionCount) {
        this.optionCount = optionCount;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Integer getMultiple() {
        return multiple;
    }

    public void setMultiple(Integer multiple) {
        this.multiple = multiple;
    }

    public Integer getThresh() {
        return thresh;
    }

    public void setThresh(Integer thresh) {
        this.thresh = thresh;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getWidthScope() {
        return widthScope;
    }

    public void setWidthScope(Double widthScope) {
        this.widthScope = widthScope;
    }

    public Double getHeightScope() {
        return heightScope;
    }

    public void setHeightScope(Double heightScope) {
        this.heightScope = heightScope;
    }
}
