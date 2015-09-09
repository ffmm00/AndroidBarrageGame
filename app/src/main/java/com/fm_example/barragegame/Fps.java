package com.fm_example.barragegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Fps extends FpsTask {

    private long _startTime = 0;
    private int _cnt = 0;
    private Paint _paint = new Paint();
    private float _fps;
    private final static int N = 60;
    private final static int FONT_SIZE = 50;

    public Fps() {
        _paint.setColor(Color.WHITE);
        _paint.setTextSize(FONT_SIZE);
    }

    @Override
    public boolean onUpdate() {
        if (_cnt == 0) {
            _startTime = System.currentTimeMillis();
        }
        if (_cnt == N) {
            long t = System.currentTimeMillis();
            _fps = 1000.f / ((t - _startTime) / (float) N);
            _cnt = 0;
            _startTime = t;
        }
        _cnt++;
        return true;
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawText(String.format("%.1f", _fps), 100, 100, _paint);
    }

}
