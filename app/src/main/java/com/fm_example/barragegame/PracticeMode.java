package com.fm_example.barragegame;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.annotation.StringRes;
import android.view.MotionEvent;
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

    private boolean mIsGameOne = false;
    private boolean mIsGameTwo = false;
    private boolean mIsGameThree = false;
    private boolean mIsGameFour = false;
    private boolean mIsGameFive = false;

    private SoundPool mSoundPool;
    private Image mButton;

    private MediaPlayer mStageBgm;
    private int mGetItem;
    private int mGetYellow;
    private int mGetRed;
    private int mWarp;
    private int mWarpCount;

    private Path mGameEnd;
    private Region mRegionGameEnd;

    private Region mRegionWholeScreen;


    private Bitmap mBitmapButton;
    private Bitmap mBitmapBullet;
    private Bitmap mBitmapItemOne;
    private Bitmap mBitmapItemTwo;

    private List<PointItem> mItemListYellow = new ArrayList<PointItem>();
    private List<PointItem> mItemListRed = new ArrayList<PointItem>();


    public PracticeMode(Context context) {
        super(context);

        mSoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        mGetYellow = mSoundPool.load(context, R.raw.getyellow, 1);
        mGetRed = mSoundPool.load(context, R.raw.getred, 1);
        mWarp = mSoundPool.load(context, R.raw.warp, 1);

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
        mBitmapItemTwo = BitmapFactory.decodeResource(rsc, R.drawable.item_2);
        mBitmapButton = BitmapFactory.decodeResource(rsc, R.drawable.button_xxxhdpi);

        mBitmapItemOne = Bitmap.createScaledBitmap(mBitmapItemOne, mWidth / 17,
                mHeight / 28, false);

        mBitmapItemTwo = Bitmap.createScaledBitmap(mBitmapItemTwo, mWidth / 22,
                mHeight / 36, false);

        mBitmapPlayer = Bitmap.createScaledBitmap(mBitmapPlayer, mWidth / 10,
                mHeight / 15, false);

        mBitmapButton = Bitmap.createScaledBitmap(mBitmapButton, mWidth / 3,
                mHeight / 20, false);

        mStageBgm = MediaPlayer.create(getContext(), R.raw.practice);

        this.mStageBgm.setLooping(true);
        mStageBgm.start();

        newPlayer();

        newButton();

        gameEndScreen();


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


        if (!mIsGameOne && mItemListYellow.isEmpty()) {
            mItemListRed.clear();
            mPlayer.setLocate(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()));
            newItemOne();
            mIsGameOne = true;

        }

        if (!mIsGameTwo && mItemListYellow.isEmpty()) {
            mItemListRed.clear();
            mPlayer.setLocate(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()));
            newItemTwo();
            mIsGameTwo = true;
            if (mWarpCount == 0) {
                mSoundPool.play(mWarp, 2.0F, 2.0F, 0, 0, 1.0F);
                mWarpCount--;
            }
        }

        if (!mIsGameThree && mItemListYellow.isEmpty()) {
            mItemListRed.clear();
            mPlayer.setLocate(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()));
            newItemThree();
            mIsGameThree = true;
            if (mWarpCount < 0) {
                mSoundPool.play(mWarp, 2.0F, 2.0F, 0, 0, 1.0F);
                mWarpCount = 5;
            }
        }

        if (!mIsGameFour && mItemListYellow.isEmpty()) {
            mItemListRed.clear();
            mPlayer.setLocate(mWidth / 2, mBitmapPlayer.getHeight());
            newItemFour();
            mIsGameFour = true;
        }

        if (!mIsGameFive && mItemListYellow.isEmpty()) {
            mItemListRed.clear();
            mIsGameFive = true;
        }

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
                    for (PointItem pointitem : mItemListRed) {
                        mCanvas.drawBitmap(mBitmapItemTwo, pointitem.getLeft(), pointitem.getTop(), null);
                    }
                }


                if (!mIsGameFive) {
                    String msg = "黄色い玉を獲得せよ";
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(heightAdjust(120));
                    mCanvas.drawText(msg, mBitmapPlayer.getWidth() * 2, mBitmapPlayer.getWidth(), mPaint);
                } else {
                    mSoundPool.play(mWarp, 2.0F, 2.0F, 0, 0, 1.0F);
                    mBitmapPlayer.eraseColor(Color.TRANSPARENT);
                    mButton.draw(mCanvas);
                    String msg = "ゲームクリア";
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(heightAdjust(100));
                    mCanvas.drawText(msg, mWidth / 3, mHeight / 2 - mBitmapPlayer.getWidth(), mPaint);
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

    private void gameEndScreen() {
        mRegionWholeScreen = new Region(0, 0, mWidth, mHeight);
        mGameEnd = new Path();
        mGameEnd.addRect(mWidth / 3, mHeight / 2,
                mWidth / 2 - widthAdjust(160) + mBitmapButton.getWidth(), mHeight / 2 + mBitmapButton.getHeight(), Path.Direction.CW);
        mRegionGameEnd = new Region();
        mRegionGameEnd.setPath(mGameEnd, mRegionWholeScreen);

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsGameThree) {
                    if (mRegionGameEnd.contains((int) event.getX(), (int) event.getY())) {
                        mStageBgm.stop();
                        Intent i = new Intent(getContext(), MainActivity.class);
                        getContext().startActivity(i);
                    }
                }
                break;
            default:
                break;
        }
        return true;
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

    private void newButton() {
        mButton = new Image(mWidth / 3, mHeight / 2,
                mBitmapButton.getWidth(), mBitmapButton.getHeight(),
                mBitmapButton);
    }

    private void newItemOne() {
        PointItem pointItem;

        int left = mBitmapItemTwo.getWidth();
        for (int top = 0; top <= mHeight; top += mBitmapItemTwo.getHeight()) {
            pointItem = new PointItem(left, top, mBitmapItemTwo.getWidth(), mBitmapItemTwo.getHeight(), 0);
            mItemListRed.add(pointItem);
        }

        left = mWidth - mBitmapItemTwo.getWidth() * 2;
        for (int top = 0; top <= mHeight; top += mBitmapItemTwo.getHeight()) {
            pointItem = new PointItem(left, top, mBitmapItemTwo.getWidth(), mBitmapItemTwo.getHeight(), 0);
            mItemListRed.add(pointItem);
        }

        int top = mHeight - mBitmapPlayer.getHeight() * 3;
        for (left = mWidth / 3; left < mWidth - mBitmapItemTwo.getHeight() * 2; left += mBitmapItemTwo.getHeight()) {
            pointItem = new PointItem(left, top, mBitmapItemTwo.getWidth(), mBitmapItemTwo.getHeight(), 0);
            mItemListRed.add(pointItem);
        }

        top = mHeight - mBitmapPlayer.getHeight() * 6;
        for (left = mWidth * 2 / 3; left > mBitmapItemTwo.getHeight() * 2; left -= mBitmapItemTwo.getHeight()) {
            pointItem = new PointItem(left, top, mBitmapItemTwo.getWidth(), mBitmapItemTwo.getHeight(), 0);
            mItemListRed.add(pointItem);
        }

        top = mHeight - mBitmapPlayer.getHeight() * 9;
        for (left = mWidth / 3; left < mWidth - mBitmapItemTwo.getHeight() * 2; left += mBitmapItemTwo.getHeight()) {
            pointItem = new PointItem(left, top, mBitmapItemTwo.getWidth(), mBitmapItemTwo.getHeight(), 0);
            mItemListRed.add(pointItem);
        }

        top = mBitmapPlayer.getHeight() * 3;
        for (left = mWidth * 2 / 3; left > mBitmapItemTwo.getHeight() * 2; left -= mBitmapItemTwo.getHeight()) {
            pointItem = new PointItem(left, top, mBitmapItemTwo.getWidth(), mBitmapItemTwo.getHeight(), 0);
            mItemListRed.add(pointItem);
        }

        top = mBitmapPlayer.getHeight() * 2 - mBitmapItemOne.getWidth();
        for (left = mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth(); left < mWidth - mBitmapPlayer.getWidth(); left += mBitmapPlayer.getWidth() * 3) {
            pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
            mItemListYellow.add(pointItem);
        }
    }

    private void newItemTwo() {
        PointItem pointItem;


        for (int top = mBitmapPlayer.getHeight() * 2; top <= mHeight * 2 / 3; top += mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth() / 2)
            for (int left = mBitmapPlayer.getWidth(); left < mWidth - mBitmapPlayer.getWidth(); left += mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth() / 2) {
                pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
                mItemListRed.add(pointItem);
            }

        for (int top = mBitmapPlayer.getHeight() * 2 + (mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth() / 2) / 2; top <= mHeight * 2 / 3; top += mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth() / 2)
            for (int left = mBitmapPlayer.getWidth() + (mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth() / 2) / 2; left < mWidth - mBitmapPlayer.getWidth(); left += mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth() / 2) {
                pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
                mItemListYellow.add(pointItem);
            }

    }

    private void newItemThree() {
        PointItem pointItem;

        for (int top = mBitmapPlayer.getHeight() * 3; top <= mHeight * 2 / 3; top += mBitmapItemOne.getWidth() * 4)
            for (int left = 0; left < mWidth; left += mBitmapPlayer.getWidth() + mSafeArea) {
                pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
                mItemListRed.add(pointItem);
            }

        for (int top = mBitmapPlayer.getHeight() * 3 + (mBitmapItemOne.getWidth() * 3) / 2; top <= mHeight * 2 / 3; top += mBitmapItemOne.getWidth() * 4)
            for (int left = (mBitmapPlayer.getWidth() + mSafeArea) / 2; left < mWidth; left += mBitmapPlayer.getWidth() + mSafeArea) {
                pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
                mItemListRed.add(pointItem);
            }

        int top = mBitmapPlayer.getHeight() * 2 - mBitmapItemOne.getWidth();
        for (int left = mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth(); left < mWidth - mBitmapPlayer.getWidth(); left += mBitmapPlayer.getWidth() * 3) {
            pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
            mItemListYellow.add(pointItem);
        }


    }

    private void newItemFour() {
        PointItem pointItem;

        for (int top = mBitmapPlayer.getHeight() * 3; top <= mHeight * 2 / 3; top += mBitmapItemOne.getWidth() * 4)
            for (int left = 0; left < mWidth; left += mBitmapPlayer.getWidth() + mSafeArea) {
                pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
                mItemListRed.add(pointItem);
            }

        for (int top = mBitmapPlayer.getHeight() * 3 + (mBitmapItemOne.getWidth() * 3) / 2; top <= mHeight * 2 / 3; top += mBitmapItemOne.getWidth() * 4)
            for (int left = (mBitmapPlayer.getWidth() + mSafeArea) / 2; left < mWidth; left += mBitmapPlayer.getWidth() + mSafeArea) {
                pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
                mItemListRed.add(pointItem);
            }

        int top = mHeight - (2 * mBitmapPlayer.getHeight());
        for (int left = mBitmapPlayer.getWidth() + mBitmapItemOne.getWidth(); left < mWidth - mBitmapPlayer.getWidth(); left += mBitmapPlayer.getWidth() * 3) {
            pointItem = new PointItem(left, top, mBitmapItemOne.getWidth(), mBitmapItemOne.getHeight(), 0);
            mItemListYellow.add(pointItem);
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
                mGetItem++;
            }
        }

        Iterator<PointItem> itemRed = mItemListRed.iterator();
        while (itemRed.hasNext()) {
            PointItem pointitem = itemRed.next();
            if ((mPlayer.getLeft() + mSafeArea + heightAdjust(11) < pointitem.getRight()) &&
                    (mPlayer.getTop() + mSafeArea + heightAdjust(11) < pointitem.getButton()) &&
                    (mPlayer.getRight() - mSafeArea > pointitem.getLeft()) &&
                    (mPlayer.getButton() - mSafeArea > pointitem.getTop())) {
                if (!mItemListYellow.isEmpty() && !mIsGameTwo) {
                    mItemListYellow.clear();
                    mIsGameOne = false;
                }
                if (!mItemListYellow.isEmpty() && !mIsGameThree) {
                    mItemListYellow.clear();
                    mIsGameTwo = false;
                }
                if (!mItemListYellow.isEmpty() && !mIsGameFour) {
                    mItemListYellow.clear();
                    mIsGameThree = false;
                }
                if (!mItemListYellow.isEmpty() && !mIsGameFive) {
                    mItemListYellow.clear();
                    mIsGameFour = false;
                }
                mSoundPool.play(mGetRed, 2.0F, 2.0F, 0, 0, 1.0F);
                itemRed.remove();
            }
        }
    }

}
