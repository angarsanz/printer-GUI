package com.hpscan;

import java.awt.*;

public class normalizedPoint extends Point {

    float x;
    float y;


    public normalizedPoint(){
        super();
    }

    public normalizedPoint(float x, float y) {
        this.x=x;
        this.y=y;
    }


    public double getX() {
        return x;
    }


    public double getY() {
        return  this.y;
    }
}
