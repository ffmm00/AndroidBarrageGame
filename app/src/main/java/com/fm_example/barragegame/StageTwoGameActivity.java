package com.fm_example.barragegame;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StageTwoGameActivity extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int NO_OUTSIDE = 60;

    private int mWidth;
    private int mHeight;
    private int mPlayerDamage;
    private int mBossDamage;

    private SurfaceHolder mHolder;

    private boolean mIsClear = false;
    private boolean mIsFailed = false;

    private boolean mIsAttached;
    private Thread mThread;

    private long mStartTime;

    private Canvas mCanvas = null;
    private Paint mPaint = null;
    private Bitmap mBitmapPlayer;
    private PlayerChara mPlayer;
    private BossFirst mBoss;
    private Bitmap mBitmapBoss;
    private Bitmap mBitmapPlayerBullet;

    private SoundPool mSoundPool;

    private MediaPlayer mStageOne;
    private int mDamage;
    private int mClear;
    private int mFail;

    private Bitmap mBitmapBullet;

    private List<BulletObject> mBulletList = new ArrayList<BulletObject>();
    private List<StraightShoot> mPlayerBulletList = new ArrayList<StraightShoot>();

    private Random mRand;

    public StageTwoGameActivity(Context context) {
        super(context);

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        //mDamage = mSoundPool.load(context, R.raw.damagesound, 1);
        //mFail = mSoundPool.load(context, R.raw.failedsound, 1);
        //mClear = mSoundPool.load(context, R.raw.clearedsound, 1);

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);


        mWidth = getWidth();
        mHeight = getHeight();
        Resources rsc = getResources();

        mIsAttached = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void run() {
        while (mIsAttached) {
            drawGameBoard();
        }
    }

    public void drawGameBoard() {

        mPlayer.move(StageTwoMove.role, StageTwoMove.pitch);

        if (mPlayer.getButton() > mHeight) {
            mPlayer.setLocate(mPlayer.getLeft(), mHeight - NO_OUTSIDE - 125);
        }
        if (mPlayer.getTop() < 0) {
            mPlayer.setLocate(mPlayer.getLeft(), NO_OUTSIDE + 20);
        }
        if (mPlayer.getLeft() < 0) {
            mPlayer.setLocate(NO_OUTSIDE - 10, mPlayer.getTop());
        }
        if (mPlayer.getRight() > mWidth) {
            mPlayer.setLocate(mWidth - (NO_OUTSIDE + 45), mPlayer.getTop());
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mBitmapPlayer != null) {
            mBitmapPlayer.recycle();
            mBitmapPlayer = null;
        }
        if (mBitmapBullet != null) {
            mBitmapBullet.recycle();
            mBitmapBullet = null;
        }
        mStageOne.stop();
        mIsAttached = false;
        while (mThread.isAlive()) ;
    }

    //  private void newPlayer() {
    //      mPlayer = new PlayerChara(mWidth / 2, mHeight / 2, mBitmapPlayer.getWidth(), mBitmapPlayer.getHeight(),
    //              BitmapFactory.decodeResource(getResources(), R.mipmap.aaaaa2));
    //      mIsClear = false;
    //      mIsFailed = false;
    //      mStartTime = System.currentTimeMillis();
    //  }

}
