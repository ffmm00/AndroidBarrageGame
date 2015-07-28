package com.fm_example.barragegame;

public class BulletObject extends ItemObject {
    private int speed;

    public BulletObject(int left, int top, int width, int height, int speed) {
        super(left, top, width, height);
        setSpeed(speed);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
