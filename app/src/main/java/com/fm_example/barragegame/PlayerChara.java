package com.fm_example.barragegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerChara extends ItemObject {
    private static final int SAFE_AREA = 35;
    private static final int LIFE = 30;
    private int mLifeCount = 0;
    private Bitmap mBitmap;

    public PlayerChara(int left, int top, int width, int height, Bitmap bitmap) {
        super(left, top, width, height);
        mBitmap = bitmap;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, getLeft(), getTop(), null);
    }

    public void move(int role, int pitch) {
        super.move(role / 2, -(pitch / 2));
    }

    public boolean shotCheck(BulletObject bulletObject) {
        if ((this.getLeft() + SAFE_AREA < bulletObject.getRight()) &&
                (this.getTop() + SAFE_AREA < bulletObject.getButton()) && (
                this.getRight() - SAFE_AREA > bulletObject.getLeft()) &&
                this.getButton() - SAFE_AREA > bulletObject.getTop()) {
            mLifeCount++;
        }
        if (LIFE == mLifeCount)
            return true;

        return false;
    }


    public boolean itemGetCheck(PointItem pointitem) {
        if ((this.getLeft() < pointitem.getRight()) &&
                (this.getTop() < pointitem.getButton()) && (
                this.getRight() > pointitem.getLeft()) &&
                this.getButton() > pointitem.getTop()) {
            return true;
        }
        return false;
    }

}
