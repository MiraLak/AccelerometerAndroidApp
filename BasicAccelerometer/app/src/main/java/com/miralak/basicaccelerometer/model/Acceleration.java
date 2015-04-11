package com.miralak.basicaccelerometer.model;


public class Acceleration {

    private long date;
    private double x;
    private double y;
    private double z;

    public Acceleration(float x_value, float y_value, float z_value, long timestamp) {
        x= x_value;
        y=y_value;
        z=z_value;
        date = timestamp;
    }

    public long getDate() {
        return date;
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
