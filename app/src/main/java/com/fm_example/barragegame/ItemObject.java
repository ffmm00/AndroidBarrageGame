package com.fm_example.barragegame;

public class ItemObject {

    private int mLeft;
    private int mTop;
    private int mWidth;
    private int mHeight;

    public ItemObject(int left, int top, int width, int height) {
        setLocate(left, top);
        this.mWidth = width;
        this.mHeight = height;
    }

    public void setLocate(int left, int top) {
        this.mLeft = left;
        this.mTop = top;
    }

    public void move(int left, int top) {
        this.mLeft = left + getLeft();
        this.mTop = top + getTop();

    }


    public int getLeft() {
        return mLeft;
    }

    public int getRight() {
        return mLeft + mWidth;
    }

    public int getTop() {
        return mTop;
    }

    public int getButton() {
        return mTop + mHeight;
    }

    public int getCenterX() {
        return (getLeft() + mWidth / 2);
    }

    public int getCenterY() {
        return (getTop() + mHeight / 2);
    }

}
