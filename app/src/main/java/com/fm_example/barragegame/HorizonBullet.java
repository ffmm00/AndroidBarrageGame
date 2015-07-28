package com.fm_example.barragegame;


public class HorizonBullet extends BulletObject {
    private int xSpeed;

    public HorizonBullet(int left, int top, int width, int height, int speed) {
        super(left, top, width, height, speed);
    }

    public void move() {
        super.move(xSpeed, 0);
    }
}
