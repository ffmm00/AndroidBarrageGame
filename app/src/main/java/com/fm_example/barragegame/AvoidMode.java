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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AvoidMode extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int SAFE_AREA = 73;

    private int mBullet;

    private int mWidth;
    private int mHeight;
    private int mSafeArea;

    private int mBulletSecondSaveOne;
    private int mBulletSecondSaveTwo;
    private int mFirstMove = 20;

    private SurfaceHolder mHolder;

    private boolean mIsClear = false;
    private boolean mIsFailed = false;

    private Canvas mCanvas = null;
    private Paint mPaint = null;
    private Bitmap mBitmapPlayer;
    private PlayerChara mPlayer;
    private BossFirst mBarrier;

    private boolean mIsAttached;
    private Thread mThread;

    private long mStartTime;

    private boolean mIsGameOne = false;
    private boolean mIsGameOneRestart = false;
    private boolean mIsGameTwo = false;
    private boolean mIsGameTwoRestart = false;
    private boolean mIsGameThree = false;
    private boolean mIsGameThreeRestart = false;
    private boolean mIsGameFour = false;
    private boolean mIsGameFive = false;

    private int mIsBarrierMove = 2;

    private SoundPool mSoundPool;

    private MediaPlayer mStageBgm;
    private int mGetItem;
    private int mGetYellow;
    private int mGetRed;


    private Bitmap mBitmapBullet;
    private Bitmap mBitmapItemOne;
    private Bitmap mBitmapBarrier;

    private List<PointItem> mItemListYellow = new ArrayList<PointItem>();
    private List<BulletObject> mBulletList = new ArrayList<BulletObject>();

    public AvoidMode(Context context) {
        super(context);

        mSoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        mGetYellow = mSoundPool.load(context, R.raw.getyellow, 1);
        mGetRed = mSoundPool.load(context, R.raw.getred, 1);

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
        mBitmapItemOne = BitmapFactory.decodeResource(rsc, R.drawable.item_1);
        mBitmapBullet = BitmapFactory.decodeResource(rsc, R.drawable.item_2);
        mBitmapBarrier = BitmapFactory.decodeResource(rsc, R.drawable.barrier);


        mBitmapItemOne = Bitmap.createScaledBitmap(mBitmapItemOne, mWidth / 20,
                mHeight / 34, false);

        mBitmapBullet = Bitmap.createScaledBitmap(mBitmapBullet, mWidth / 22,
                mHeight / 36, false);

        mBitmapPlayer = Bitmap.createScaledBitmap(mBitmapPlayer, mWidth / 10,
                mHeight / 15, false);

        mBitmapBarrier = Bitmap.createScaledBitmap(mBitmapBarrier, mWidth / 5,
                mHeight / 13, false);

        mStageBgm = MediaPlayer.create(getContext(), R.raw.practice);

        this.mStageBgm.setLooping(true);
        mStageBgm.start();

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

        int mSecond = (int) ((((System.currentTimeMillis() - mStartTime)) / 100) % 60) + 1;

        //String a = "" + mSecond;
        // Log.d("Test", a);

        if (mSecond == 1) {
            mBulletSecondSaveOne = 0;
            mBulletSecondSaveTwo = 0;
        }

        if (!mIsGameOneRestart) {
            mPlayer.setLocate(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()));
            newItemOne();
            barrier();
            mIsGameOneRestart = true;
        }

        if (!mIsGameOne) {
            if (mSecond >= mFirstMove) {
                mPlayer.move(AvoidModeMove.role, AvoidModeMove.pitch);
                mFirstMove = -20;
            }

            if (mIsBarrierMove == 1) {
                mBarrier.move(0, heightAdjust(5));
                if (mBarrier.getButton() > mHeight)
                    mIsBarrierMove++;
            }
            if (mIsBarrierMove == 2) {
                mBarrier.move(0, heightAdjust(-5));
                if (mBarrier.getTop() < mBitmapPlayer.getHeight())
                    mIsBarrierMove = 1;
            }

            if (mSecond - mBulletSecondSaveOne == 7) {
                stageTwoShot_1();
                mBulletSecondSaveOne = mSecond;
            }

            if (mSecond - mBulletSecondSaveTwo == 7) {
                stageTwoShot_2();
                mBulletSecondSaveTwo = mSecond;
            }
            if (mItemListYellow.isEmpty()) {
                mFirstMove = 20;
                mBarrier.setLocate(-mBitmapBarrier.getWidth(), -mBitmapBarrier.getHeight());
                mStartTime = System.currentTimeMillis();
                mSecond = 0;
                mIsGameOne = true;
            }
        }


        //2ステージ
        if (!mIsGameTwoRestart && mItemListYellow.isEmpty()) {
            mPlayer.setLocate(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()));
            newItemOne();
            mIsGameTwoRestart = true;
        }

        if (!mIsGameTwo && mIsGameOne) {
            if (mSecond >= mFirstMove) {
                mPlayer.move(AvoidModeMove.role, AvoidModeMove.pitch);
                mFirstMove = -20;
            }

            if (mSecond - mBulletSecondSaveOne == 10) {
                stageOneShot_1();
                mBulletSecondSaveOne = mSecond;
            }

            if (mSecond - mBulletSecondSaveTwo == 15) {
                stageOneShot_2();
                mBulletSecondSaveTwo = mSecond;
            }
        }

        //ステージ３
        if (!mIsGameThreeRestart && mItemListYellow.isEmpty()) {
            mIsGameTwo = true;
            mIsGameThreeRestart = true;
        }

        bulletDelete();

        charaTouchedBulletDelete();

        try {

            mCanvas = getHolder().lockCanvas();
            if (mCanvas != null) {
                mCanvas.drawColor(Color.LTGRAY);

                if (!((mIsClear) || (mIsFailed))) {
                    mPaint.setColor(Color.DKGRAY);
                    for (PointItem pointitem : mItemListYellow) {
                        mCanvas.drawBitmap(mBitmapItemOne, pointitem.getLeft(), pointitem.getTop(), null);
                    }
                    for (BulletObject bulletObject : mBulletList) {
                        mCanvas.drawBitmap(mBitmapBullet, bulletObject.getLeft(), bulletObject.getTop(), null);
                    }
                    for (BulletObject bulletObject : mBulletList) {
                        if (bulletObject != null) {
                            bulletObject.move(bulletObject.mSpeedX, bulletObject.mSpeedY);
                        }
                    }
                }

                if (mFirstMove == -20) {
                    String msg = "ＧＯ";
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(heightAdjust(120));
                    mCanvas.drawText(msg, mWidth / 2 - mBitmapPlayer.getHeight(), mHeight - (2 * mBitmapPlayer.getHeight()), mPaint);
                }
                if (mFirstMove == 20) {
                    String msg = "READY";
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(heightAdjust(120));
                    mCanvas.drawText(msg, mWidth / 2 - mBitmapPlayer.getHeight(), mHeight - (2 * mBitmapPlayer.getHeight()), mPaint);
                }

                if (!mIsGameFive) {
                    String msg = "黄色い玉を獲得せよ";
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(heightAdjust(120));
                    mCanvas.drawText(msg, mBitmapPlayer.getWidth() * 2, mBitmapPlayer.getWidth(), mPaint);
                } else {
                    String msg = "ゲームクリア";
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(heightAdjust(120));
                    mCanvas.drawText(msg, mBitmapPlayer.getWidth() * 3, mBitmapPlayer.getWidth(), mPaint);
                }


                mPlayer.draw(mCanvas);
                mBarrier.draw(mCanvas);

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
        if (mBitmapBarrier != null) {
            mBitmapBarrier.recycle();
            mBitmapBarrier = null;
        }

        mStageBgm.stop();
        mIsAttached = false;
        while (mThread.isAlive()) ;
    }

    private void newPlayer() {

        mPlayer = new PlayerChara(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()), mBitmapPlayer.getWidth(), mBitmapPlayer.getHeight(),
                mBitmapPlayer);
        mIsFailed = false;
        mStartTime = System.currentTimeMillis();
    }

    private void newItemOne() {
        PointItem pointItem;

        int top = mBitmapPlayer.getHeight() * 2 - mBitmapItemOne.getWidth();
        for (int left = mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth(); left < mWidth - mBitmapPlayer.getWidth(); left += mBitmapPlayer.getWidth() * 3) {
            pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
            mItemListYellow.add(pointItem);
        }
    }

    private void stageOneShot_1() {
        BulletObject horizonalBullet;

        int xSpeed = widthAdjust(7);
        int left = -mBitmapBullet.getWidth();

        for (int top = mBitmapPlayer.getHeight() * 3; top <= mHeight - mBitmapPlayer.getHeight() * 3; top += mBitmapPlayer.getHeight() + mBitmapBullet.getWidth()) {
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, 0);
            mBulletList.add(horizonalBullet);
        }
    }

    private void stageOneShot_2() {
        BulletObject horizonalBullet;

        int xSpeed = widthAdjust(7);
        int left = mWidth + mBitmapBullet.getWidth();

        for (int top = mBitmapPlayer.getHeight() * 3; top <= mHeight - mBitmapPlayer.getHeight() * 3; top += mBitmapPlayer.getHeight() + mBitmapBullet.getWidth()) {
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), -xSpeed, 0);
            mBulletList.add(horizonalBullet);
        }
    }

    private void barrier() {
        mBarrier = new BossFirst(mWidth / 2 - mBitmapBarrier.getWidth() / 2, mBitmapPlayer.getHeight() * 3, mBitmapBarrier.getWidth(),
                mBitmapBarrier.getHeight(), mBitmapBarrier);
    }

    private void stageTwoShot_1() {
        BulletObject horizonalBullet;

        int xSpeed = widthAdjust(7);
        int left = -mBitmapBullet.getWidth();

        for (int top = mBitmapPlayer.getHeight() * 3; top <= mHeight - mBitmapPlayer.getHeight() * 3; top += mBitmapPlayer.getHeight() + mBitmapBullet.getWidth() / 2) {
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, 0);
            mBulletList.add(horizonalBullet);
        }
    }

    private void stageTwoShot_2() {
        BulletObject horizonalBullet;

        int xSpeed = widthAdjust(7);
        int left = mWidth + mBitmapBullet.getWidth();

        for (int top = mBitmapPlayer.getHeight() * 3 + mBitmapBullet.getWidth() + mBitmapBullet.getWidth() / 2; top <= mHeight - mBitmapPlayer.getHeight() * 3; top += mBitmapPlayer.getHeight() + mBitmapBullet.getWidth() / 2) {
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), -xSpeed, 0);
            mBulletList.add(horizonalBullet);
        }
    }


    private void charaTouchedBulletDelete() {
        Iterator<PointItem> itemYellow = mItemListYellow.iterator();
        while (itemYellow.hasNext()) {
            PointItem pointitem = itemYellow.next();
            if ((mPlayer.getLeft() + mSafeArea + heightAdjust(11) < pointitem.getRight()) &&
                    (mPlayer.getTop() + mSafeArea + heightAdjust(11) < pointitem.getButton()) &&
                    (mPlayer.getRight() - mSafeArea > pointitem.getLeft()) &&
                    (mPlayer.getButton() - mSafeArea > pointitem.getTop())) {
                mSoundPool.play(mGetYellow, 2.0F, 2.0F, 0, 0, 1.0F);
                itemYellow.remove();
            }
        }

        Iterator<BulletObject> bullet = mBulletList.iterator();
        while (bullet.hasNext()) {
            BulletObject bulletObject = bullet.next();
            if ((mPlayer.getLeft() + mSafeArea + heightAdjust(8) < bulletObject.getRight()) &&
                    (mPlayer.getTop() + mSafeArea + heightAdjust(8) < bulletObject.getButton()) &&
                    (mPlayer.getRight() - mSafeArea > bulletObject.getLeft()) &&
                    (mPlayer.getButton() - mSafeArea > bulletObject.getTop())) {
                if (!mItemListYellow.isEmpty() && !mIsGameOne) {
                    mItemListYellow.clear();
                    mIsGameOneRestart = false;
                }
                if (!mItemListYellow.isEmpty() && !mIsGameTwo) {
                    mItemListYellow.clear();
                    mIsGameTwoRestart = false;
                }
                mSoundPool.play(mGetRed, 2.0F, 2.0F, 0, 0, 1.0F);
                bullet.remove();
            }
        }
    }

    private void bulletDelete() {
        Iterator<BulletObject> bullet = mBulletList.iterator();
        while (bullet.hasNext()) {
            BulletObject bulletObject = bullet.next();
            if (bulletObject.getLeft() < -mBitmapBullet.getWidth() * 4 ||
                    bulletObject.getRight() > (mWidth + mBitmapBullet.getWidth() * 4) ||
                    bulletObject.getLeft() < mBarrier.getRight() - mBitmapBullet.getWidth() &&
                            bulletObject.getRight() > mBarrier.getLeft() + mBitmapBullet.getWidth() &&
                            bulletObject.getTop() < mBarrier.getButton() &&
                            bulletObject.getButton() > mBarrier.getTop()) {
                bullet.remove();
            }
        }
    }


}
