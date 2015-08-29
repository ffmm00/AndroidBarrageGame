package com.fm_example.barragegame;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class BossMoveFour extends Activity implements SensorEventListener {

    private BossModeFour mSurfaceView;
    private SensorManager mSensorManager;
    private Sensor mMagField;
    private Sensor mAccelerometer;

    private static final int MATRIX_SIZE = 16;
    private float[] mgValues = new float[3];
    private float[] acValues = new float[3];

    private Handler mHandler = new Handler();

    public static int pitch = 0;
    public static int role = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mSurfaceView = new BossModeFour(this);
        setContentView(mSurfaceView);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void PostRunnable(final Runnable run) {
        mHandler.post(run);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagField, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagField);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] inR = new float[MATRIX_SIZE];
        float[] outR = new float[MATRIX_SIZE];
        float[] I = new float[MATRIX_SIZE];
        float[] orValues = new float[3];

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                acValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mgValues = event.values.clone();
                break;
        }

        if (mgValues != null && acValues != null) {
            SensorManager.getRotationMatrix(inR, I, acValues, mgValues);

            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            SensorManager.getOrientation(outR, orValues);

            pitch = rad2Deg(orValues[1]);
            role = rad2Deg(orValues[2]);
        }

    }

    private int rad2Deg(float rad) {
        return (int) Math.floor(Math.toDegrees(rad));
    }

}
