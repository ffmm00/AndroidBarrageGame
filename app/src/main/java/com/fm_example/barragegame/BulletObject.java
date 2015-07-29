package com.fm_example.barragegame;

public class BulletObject extends ItemObject {
    protected int mSpeedX;
    protected int mSpeedY;

    public BulletObject(int left, int top, int width, int height, int xSpeed, int ySpeed) {
        super(left, top, width, height);
        setSpeed(xSpeed, ySpeed);
    }

    public void setSpeed(int xSpeed, int ySpeed) {
        this.mSpeedX = xSpeed;
        this.mSpeedY = ySpeed;
    }


}
