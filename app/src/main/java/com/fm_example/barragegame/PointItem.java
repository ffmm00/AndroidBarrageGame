package com.fm_example.barragegame;


public class PointItem extends ItemObject {
    private int mSpeedY;

    public PointItem(int left, int top, int width, int height, int speed) {
        super(left, top, width, height);
        setSpeed(speed);
    }

    public void setSpeed(int speed) {
        this.mSpeedY = speed;
    }

    public void move() {
        super.move(0, mSpeedY);
    }
}
