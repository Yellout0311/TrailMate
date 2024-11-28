package com.example.trailrunner.ui.home_like;

public class Track {
    int imageResourceId;
    String mountain;
    String distance;
    String level;

    public Track(int imageResourceId, String mountain, String diatance, String level) {
        this.imageResourceId = imageResourceId;
        this.mountain = mountain;
        this.distance = diatance;
        this.level = level;
    }//생성자 추가

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getMountain() {
        return mountain;
    }

    public void setMountain(String mountain) {
        this.mountain = mountain;
    }

    public String getDistance() {
        return distance +"km";
    }

    public void setDistance(String distance) {
        this.distance = this.distance;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    //get, set 메서드 추가
}
