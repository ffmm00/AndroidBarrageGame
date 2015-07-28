package com.fm_example.barragegame;

public class StraightShoot extends BulletObject {
    private int ySpeed;

    public StraightShoot(int left, int top, int width, int height, int speed) {
        super(left, top, width, height, speed);
    }

    public void move() {
        super.move(0, -ySpeed);
    }
}
