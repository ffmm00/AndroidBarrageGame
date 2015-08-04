package com.fm_example.barragegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class BossChara extends ItemObject {
    private static final int LIFE = 40;
    private int mLifeCount = 0;
    private Bitmap mBitmap;

    public BossChara(int left, int top, int width, int height, Bitmap bitmap) {
        super(left, top, width, height);
        mBitmap = bitmap;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, getLeft(), getTop(), null);
    }

    public void move(int xSpeed) {
        super.move(xSpeed, 0);
    }

    public boolean shotCheck(StraightShoot playershoot) {
        if ((this.getLeft() < playershoot.getRight()) &&
                (this.getTop() < playershoot.getButton()) && (
                this.getRight() > playershoot.getLeft()) &&
                this.getButton() > playershoot.getTop()) {
            mLifeCount++;
        }
        if (LIFE == mLifeCount)
            return true;

        return false;
    }


}
