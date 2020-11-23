package com.chw.test;

public class Config {

    private Integer optionCount;

    private Integer multiple;

    private Integer thresh;

    private Integer width;

    private Integer height;

    private Boolean autoGetWidthAndHeight;

    private Double widthScope;

    private Double heightScope;

    private Integer widthGap;

    private Integer heightGap;

    private Boolean open;

    private Boolean tryFix;

    public Config() {
        this(5,1,0,0,0,0.15,0.15,false,true);
    }

    public Config(Integer optionCount,Integer thresh, Integer width, Integer height) {
        this(optionCount,1,thresh,width,height,0.15,0.15,false,true);
    }

    public Config(Integer optionCount,Integer thresh, Integer width, Integer height,Boolean open,Boolean tryFix) {
        this(optionCount,1,thresh,width,height,0.15,0.15,open,tryFix);
    }

    public Config(Integer optionCount,Integer thresh, Integer width, Integer height, Double widthScope, Double heightScope) {
         // call W
        this(optionCount,1,thresh,width,height,widthScope,heightScope);
    }

    public Config(Integer optionCount,Integer multiple,Integer thresh, Integer width, Integer height, Double widthScope, Double heightScope) {
        // W
        this(optionCount,multiple,thresh,width,height,widthScope,heightScope,false,true);
    }

    public Config(Integer optionCount,Integer thresh, Integer width, Integer height, Integer widthGap, Integer heightGap) {
        this(optionCount,1,thresh,width,height,0.15,0.15,widthGap,heightGap);
    }

    public Config(Integer optionCount,Integer multiple,Integer thresh, Integer width, Integer height, Integer widthGap, Integer heightGap) {
        this(optionCount,multiple,thresh,width,height,0.15,0.15,widthGap,heightGap);
    }

    public Config(Integer optionCount,Integer multiple, Integer thresh, Integer width, Integer height,
                  Double widthScope, Double heightScope, Integer widthGap, Integer heightGap) {
        this(optionCount, multiple,thresh, width, height, widthScope, heightScope,widthGap,heightGap,false,true);
    }

    public Config(Integer optionCount,Integer multiple, Integer thresh, Integer width, Integer height,
                  Double widthScope, Double heightScope,Boolean open,Boolean tryFix) {
        this(optionCount, multiple,thresh, width, height, widthScope, heightScope,0,0,open,tryFix);
    }

    public Config(Integer optionCount, Integer multiple, Integer thresh, Integer width, Integer height,
                  Double widthScope, Double heightScope, Integer widthGap, Integer heightGap, Boolean open,Boolean tryFix) {
        this.optionCount = optionCount;
        this.multiple = multiple;
        this.thresh = thresh;
        this.width = width*multiple;
        this.height = height*multiple;
        this.autoGetWidthAndHeight=false;
        this.widthScope = widthScope;
        this.heightScope = heightScope;
        this.widthGap = widthGap*multiple;
        this.heightGap = heightGap*multiple;
        this.open = open;
        this.tryFix = tryFix;
    }

    public Boolean getTryFix() {
        return tryFix;
    }

    public Integer getWidthGap() {
        return widthGap;
    }

    public Integer getHeightGap() {
        return heightGap;
    }

    public Integer getOptionCount() {
        return optionCount;
    }

    public Boolean getOpen() {
        return open;
    }

    public Integer getMultiple() {
        return multiple;
    }

    public Integer getThresh() {
        return thresh;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Double getWidthScope() {
        return widthScope;
    }

    public Double getHeightScope() {
        return heightScope;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getAutoGetWidthAndHeight() {
        return autoGetWidthAndHeight;
    }

    public void setAutoGetWidthAndHeight(Boolean autoGetWidthAndHeight) {
        this.autoGetWidthAndHeight = autoGetWidthAndHeight;
    }
}
