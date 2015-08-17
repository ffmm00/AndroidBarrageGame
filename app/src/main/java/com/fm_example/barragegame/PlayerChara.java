package com.fm_example.barragegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerChara extends ItemObject {

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
