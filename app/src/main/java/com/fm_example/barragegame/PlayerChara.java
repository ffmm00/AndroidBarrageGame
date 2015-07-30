package com.fm_example.barragegame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.ImageView;

public class PlayerChara extends ItemObject {
    private static final int SAFE_AREA = 20;
    private Bitmap mBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);

    public PlayerChara(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    public void draw(Canvas canvas) {
        //canvas=new Canvas(mBitmap);
        //canvas.drawBitmap(R.mipmap.ic_launcher);
    }

    public void move(int role, int pitch) {
        super.move(role / 2, -(pitch / 2));
    }

    public boolean shotCheck(BulletObject bulletObject) {
        if ((this.getLeft() + SAFE_AREA < bulletObject.getRight()) &&
                (this.getTop() + SAFE_AREA < bulletObject.getButton()) && (
                this.getRight() - SAFE_AREA > bulletObject.getLeft()) &&
                this.getButton() - SAFE_AREA > bulletObject.getTop()) {
            return true;
        }
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
