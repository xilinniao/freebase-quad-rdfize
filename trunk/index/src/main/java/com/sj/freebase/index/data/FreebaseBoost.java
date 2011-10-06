package com.sj.freebase.index.data;

public class FreebaseBoost {
    private double boost = 1.0;
    private String value = "";


    public FreebaseBoost (double boost, String value) {
        this.setBoost(boost);
        this.setValue(value);
    }


    public void setBoost(double boost) {
        this.boost = boost;
    }


    public double getBoost() {
        return boost;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getValue() {
        if (this.value == null) {
            this.value = "";
        }
        return value;
    }
}
