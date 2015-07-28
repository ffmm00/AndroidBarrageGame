package com.fm_example.barragegame;


public class PointItem extends ItemObject {
    private int speed;

    public PointItem(int left, int top, int width, int height, int speed) {
        super(left, top, width, height);
        setSpeed(speed);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void move() {
        super.move(0, speed);
    }
}
