package com.fm_example.barragegame;

import android.graphics.Canvas;
import android.graphics.Color;

import java.util.LinkedList;

public class FpsMgr {

    private LinkedList<FpsTask> _taskList = new LinkedList<FpsTask>();

    FpsMgr() {
        _taskList.add(new Fps());
    }

    public boolean onUpdate() {
        for (int i = 0; i < _taskList.size(); i++) {
            if (_taskList.get(i).onUpdate() == false) {
                _taskList.remove(i);
                i--;
            }
        }
        return true;
    }

    public void onDraw(Canvas c) {
        for (int i = 0; i < _taskList.size(); i++) {
            _taskList.get(i).onDraw(c);
        }
    }

}