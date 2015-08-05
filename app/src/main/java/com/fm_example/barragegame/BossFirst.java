package com.fm_example.barragegame;


import android.graphics.Bitmap;

public class BossFirst extends BossChara {

    public BossFirst(int left, int top, int width, int height, Bitmap bitmap) {
        super(left, top, width, height, bitmap);
    }

    public void move(int xSpeed) {
        super.move(xSpeed, 0);
    }
}
