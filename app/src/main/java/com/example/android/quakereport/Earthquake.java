package com.example.android.quakereport;

public class Earthquake {
    private double magnitude;
    private String location;
    private long timeInMilliSeconds;
    private String url;

    public Earthquake(double magnitude, String location, long timeInMilliSeconds,String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.timeInMilliSeconds = timeInMilliSeconds;
        this.url=url;
    }

    public String getUrlString() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTimeInMilliSeconds() {
        return timeInMilliSeconds;
    }

    public void setTimeInMilliSeconds(long timeInMilliSeconds) {
        this.timeInMilliSeconds = timeInMilliSeconds;
    }
}
