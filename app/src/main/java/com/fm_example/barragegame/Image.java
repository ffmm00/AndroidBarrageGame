package com.fm_example.barragegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Image extends ItemObject {

    private Bitmap mBitmap;

    public Image(int left, int top, int width, int height, Bitmap bitmap) {
        super(left, top, width, height);
        mBitmap = bitmap;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, getLeft(), getTop(), null);
    }

}
