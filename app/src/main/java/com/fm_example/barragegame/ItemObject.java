package com.fm_example.barragegame;

public class ItemObject {

    private int left;
    private int top;
    private int width;
    private int height;

    public ItemObject(int left, int top, int width, int height) {
        setLocate(left, top);
    }

    public void setLocate(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public void move(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return left + width;
    }

    public int getTop() {
        return top;
    }

    public int getButton() {
        return top + height;
    }

    public int getCenterX() {
        return (getLeft() + width / 2);
    }

    public int getCenterY() {
        return (getTop() + height / 2);
    }

}
