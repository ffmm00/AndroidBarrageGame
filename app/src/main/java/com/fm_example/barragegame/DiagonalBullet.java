package com.fm_example.barragegame;

public class DiagonalBullet extends BulletObject {

    public DiagonalBullet(int left, int top, int width, int height, int xSpeed, int ySpeed) {
        super(left, top, width, height, xSpeed, ySpeed);
    }

    public void move() {
        super.move(mSpeedX, mSpeedY);
    }
}
