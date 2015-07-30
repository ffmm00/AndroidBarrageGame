package com.fm_example.barragegame;


public class HorizonalBullet extends BulletObject {

    public HorizonalBullet(int left, int top, int width, int height, int xSpeed, int ySpeed) {
        super(left, top, width, height, xSpeed, 0);
    }

    public void move() {
        super.move(mSpeedX, 0);
    }
}
