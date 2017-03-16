package com.miralak.basicaccelerometer.model;

public enum ActivityType {
    WALKING("Walking"),
    JOGGING("Jogging"),
    STANDING("Standing"),
    SITTING("Sitting"),
    UPSTAIRS("Upstairs"),
    DOWNSTAIRS("Downstairs"),
    JUMPING("Jumping"),
    MOON_WALK("Moonwalk");

    private String label;

    ActivityType(String label) {
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

}
