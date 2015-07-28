package com.fm_example.barragegame;

public class DiagonalBullet extends BulletObject {
    private int xSpeed;
    private int ySpeed;

    public DiagonalBullet(int left, int top, int width, int height, int speed) {
        super(left, top, width, height, speed);
    }

    public void move() {
        super.move(xSpeed, ySpeed);
    }
}
