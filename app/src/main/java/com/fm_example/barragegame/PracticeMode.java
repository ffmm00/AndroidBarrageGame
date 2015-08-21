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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PracticeMode extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private int mWidth;
    private int mHeight;
    private static final int SAFE_AREA = 73;
    private int mSafeArea;

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
    private BossFirst mBossOne;
    private PointItem mItem;

    private SoundPool mSoundPool;

    private MediaPlayer mStageOne;
    private int mGet;


    private Bitmap mBitmapBullet;
    private Bitmap mBitmapItem;

    private List<PointItem> mItemList = new ArrayList<PointItem>();

    private Random mRand;

    public PracticeMode(Context context) {
        super(context);

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        //mGet = mSoundPool.load(context, R.raw.getitem 1);

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

        mSafeArea = heightAdjust(SAFE_AREA);

        mBitmapPlayer = BitmapFactory.decodeResource(rsc, R.drawable.player_xxxhdpi_b);
        mBitmapItem = BitmapFactory.decodeResource(rsc, R.drawable.item_1);

        mBitmapItem = Bitmap.createScaledBitmap(mBitmapItem, mWidth / 24,
                mHeight / 38, false);

        mBitmapPlayer = Bitmap.createScaledBitmap(mBitmapPlayer, mWidth / 10,
                mHeight / 15, false);

        newPlayer();


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

        if ((mIsFailed) || (mIsClear)) {
            return;
        }

        mPlayer.move(PracticeModeMove.role, PracticeModeMove.pitch);

        if (mPlayer.getButton() > mHeight) {
            mPlayer.setLocate(mPlayer.getLeft(), mHeight - (mBitmapPlayer.getHeight()));
        }
        if (mPlayer.getTop() < 0) {
            mPlayer.setLocate(mPlayer.getLeft(), 0);
        }
        if (mPlayer.getLeft() < 0) {
            mPlayer.setLocate(0, mPlayer.getTop());
        }
        if (mPlayer.getRight() > mWidth) {
            mPlayer.setLocate(mWidth - mBitmapPlayer.getWidth(), mPlayer.getTop());
        }


        int mSecond = (int) ((((System.currentTimeMillis() - mStartTime)) / 100) % 60);

        if (mSecond == 1)
            newItem();

        charaTouchedBulletDelete();

        try {


            mCanvas = getHolder().lockCanvas();
            if (mCanvas != null) {
                mCanvas.drawColor(Color.LTGRAY);

                if (!((mIsClear) || (mIsFailed))) {
                    mPaint.setColor(Color.DKGRAY);
                    for (PointItem pointitem : mItemList) {
                        mCanvas.drawBitmap(mBitmapItem, pointitem.getLeft(), pointitem.getTop(), null);
                    }
                }


                mPlayer.draw(mCanvas);

                getHolder().unlockCanvasAndPost(mCanvas);
            }
        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }

    }


    public int heightAdjust(int a) {
        float temp = mHeight / 2560f;
        temp *= a;
        int adjust = Math.round(temp);

        return adjust;
    }

    public int widthAdjust(int a) {
        float temp = mWidth / 1440f;
        temp *= a;
        int adjust = Math.round(temp);

        return adjust;
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

        mIsAttached = false;
        while (mThread.isAlive()) ;
    }

    private void newPlayer() {

        mPlayer = new PlayerChara(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()), mBitmapPlayer.getWidth(), mBitmapPlayer.getHeight(),
                mBitmapPlayer);
        mIsFailed = false;
        mStartTime = System.currentTimeMillis();
    }

    private void newItem() {
        PointItem pointItem;

        int top = getHeight() / 2;


        for (int left = 0; left < mWidth; left += mBitmapItem.getWidth() + 20) {
            pointItem = new PointItem(left, top, mBitmapItem.getWidth(), mBitmapItem.getHeight(), 0);
            mItemList.add(pointItem);
        }
    }

    private void charaTouchedBulletDelete() {
        Iterator<PointItem> item = mItemList.iterator();
        while (item.hasNext()) {
            PointItem pointitem = item.next();
            if ((mPlayer.getLeft() + mSafeArea < pointitem.getRight()) &&
                    (mPlayer.getTop() + mSafeArea < pointitem.getButton()) &&
                    (mPlayer.getRight() - mSafeArea > pointitem.getLeft()) &&
                    (mPlayer.getButton() - mSafeArea > pointitem.getTop())) {
                item.remove();
            }
        }
    }

}
