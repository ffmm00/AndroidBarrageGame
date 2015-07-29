package com.fm_example.barragegame;

public class StraightShoot extends BulletObject {

    public StraightShoot(int left, int top, int width, int height, int xSpeed, int ySpeed) {
        super(left, top, width, height, 0, -ySpeed);
    }

    public void move() {
        super.move(0, -mSpeedY);
    }
}
