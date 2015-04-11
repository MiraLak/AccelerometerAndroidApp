package com.miralak.basicaccelerometer.model;


import android.util.Log;

public class Acceleration {

    private long timestamp;
    private double x;
    private double y;
    private double z;

    public Acceleration(float x_value, float y_value, float z_value, long timestamp) {
        x= new Double(""+x_value);
        y= new Double(""+y_value);
        z= new Double(""+z_value);
        this.timestamp = timestamp;

    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

}
