package com.fm_example.barragegame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class GameActivity extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int PLAYER_POS = 100;
    private static final int NO_OUTSIDE = 60;
    private static final int BULLET_ZONE = 10;
    private static final int BULLETS = 13;

    private int mWidth;
    private int mHeight;
    private int mSecond;

    private SurfaceHolder mHolder;

    private boolean mIsClear = false;
    private boolean mIsFailed = false;

    private boolean mIsAttached;
    private Thread mThread;

    private long mStartTime;
    private long mEndTime;

    private Canvas mCanvas = null;
    private Paint mPaint = null;
    private Bitmap mBitmapPlayer;
    private PlayerChara mPlayer;

    private Path mRightBulletZone;
    private Path mLeftBulletZone;
    private Path mTopBulletZone;
    private Path mBottomBulletZone;

    private Region mRegionRightBulletZone;
    private Region mRegionLeftBulletZone;
    private Region mRegionTopBulletZone;
    private Region mRegionBottomBulletZone;
    private Region mRegionWholeScreen;

    private Bitmap mBitmapBullet;
    private BulletObject mBullet;


    private List<BulletObject> mBulletList = new ArrayList<BulletObject>(15);

    private Random mRand;

    public GameActivity(Context context) {
        super(context);
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
        mBitmapPlayer = BitmapFactory.decodeResource(rsc, R.drawable.player);
        mBitmapBullet = BitmapFactory.decodeResource(rsc, R.drawable.minibullet);

        mRand = new Random();

        // BulletDeleteZone();

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

    private void BulletDeleteZone() {
        mRegionWholeScreen = new Region(0, 0, mWidth, mHeight);

        mRightBulletZone = new Path();
        mRightBulletZone.addRect(mWidth, 0, mWidth - BULLET_ZONE, mHeight, Path.Direction.CW);
        mRegionRightBulletZone = new Region();
        mRegionRightBulletZone.setPath(mRightBulletZone, mRegionWholeScreen);

        mLeftBulletZone = new Path();
        mLeftBulletZone.addRect(0, 0, BULLET_ZONE, mHeight, Path.Direction.CW);
        mRegionRightBulletZone = new Region();
        mRegionRightBulletZone.setPath(mRightBulletZone, mRegionWholeScreen);

        //x1 y1 x2 y2
    }


    public void drawGameBoard() {
        if ((mIsFailed) || (mIsClear)) {
            return;
        }


        mPlayer.move(CharacterMove.role, CharacterMove.pitch);

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

        mSecond = (int) (((System.currentTimeMillis() - mStartTime)) / 1000) % 60;


        if (mSecond % 2 == 0)
            newHorizonalBullet();

        if (mSecond % 5 == 0) {
            newHorizonalBulletRight();
        }


        try {
            for (BulletObject bulletObject : mBulletList) {
                if (bulletObject != null) {
                    bulletObject.move(bulletObject.mSpeedX, bulletObject.mSpeedY);
                }
            }

            mCanvas = getHolder().lockCanvas();
            mCanvas.drawColor(Color.LTGRAY);


            // for (HorizonalBullet horizonalBullet : mHorizonalBulletList) {
            //     if (mRegionRightBulletZone.contains(horizonalBullet.getRight(), horizonalBullet.getButton())) {
            //         horizonalBullet.setLocate(0, horizonalBullet.getTop());
            //     }
            // }


            //衝突チェック
            //  if (!mIsClear) {
            //      for (BulletObject bulletObject : mBulletList) {
            //          if (mPlayer.shotCheck(bulletObject)) {
            //              mIsFailed = true;
            //          }
            //      }
            //  }

            if (!((mIsClear) || (mIsFailed))) {
                mPaint.setColor(Color.DKGRAY);
                for (BulletObject bulletObject : mBulletList) {
                    mCanvas.drawBitmap(mBitmapBullet, bulletObject.getLeft(), bulletObject.getTop(), null);
                }

                mPlayer.draw(mCanvas);
            }


            getHolder().unlockCanvasAndPost(mCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String gameEnd() {
        mEndTime = System.currentTimeMillis();
        long score = mEndTime - mStartTime;
        score = (int) mEndTime * 100;
        return ("スコア" + score);
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
        mIsAttached = false;
        while (mThread.isAlive()) ;
    }

    private void newPlayer() {
        mPlayer = new PlayerChara(mWidth / 2, mHeight / 2, mBitmapPlayer.getWidth(), mBitmapPlayer.getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.player));
        mIsClear = false;
        mIsFailed = false;
        mStartTime = System.currentTimeMillis() * 1000;
    }


    private void newHorizonalBullet() {
        BulletObject horizonalBullet;

        //mBulletList.clear();

        for (int i = 0; i < BULLETS; i++) {
            int left = -mBitmapBullet.getWidth();
            int top = mRand.nextInt(mHeight);

            int xSpeed = mRand.nextInt(6) + 5;
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, 0);
            mBulletList.add(horizonalBullet);

        }
    }

    private void newHorizonalBulletRight() {
        BulletObject horizonalBullet;

        //mBulletList.clear();

        for (int i = 0; i < BULLETS; i++) {
            int left = mWidth + mBitmapBullet.getWidth();
            int top = mRand.nextInt(mHeight);

            int xSpeed = mRand.nextInt(6) + 6;
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), -xSpeed, 0);
            mBulletList.add(horizonalBullet);

        }
    }

    private void BulletDelete() {
        Iterator<BulletObject> bullet = mBulletList.iterator();
        while (bullet.hasNext()) {
            BulletObject bulletObject = bullet.next();
            if (bulletObject.getLeft() == -mBitmapBullet.getWidth() * 2 ||
                    bulletObject.getRight() == mWidth + mBitmapBullet.getWidth() * 2) {
                bullet.remove();
            }
        }
    }


}
