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
import android.util.Log;
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
    private static final int HORIZONAL_INTERVAL = 1600;
    private static final int HORIZONAL_INTERVAL_RIGHT = 2200;
    private static final int BOSS_BULLET_INTERVAL = 1800;
    private static final int PLAYER_BULLET_INTERVAL = 110;
    private static final int BULLETS = 4;

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
    private BossChara mBoss;
    private Bitmap mBitmapBoss;
    private Bitmap mBitmapPlayerBullet;

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


    private List<BulletObject> mBulletList = new ArrayList<BulletObject>();
    private List<StraightShoot> mPlayerBulletList = new ArrayList<StraightShoot>();

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
        mBitmapBoss = BitmapFactory.decodeResource(rsc, R.drawable.boss);
        mBitmapBullet = BitmapFactory.decodeResource(rsc, R.drawable.minibullet);
        mBitmapPlayerBullet = BitmapFactory.decodeResource(rsc, R.drawable.playerbullet);

        mRand = new Random();

        // BulletDeleteZone();

        newPlayer();
        newBoss();

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

        mSecond = (int) ((System.currentTimeMillis() - mStartTime));
        // String a = "" + mSecond;
        //Log.d("Test", a);


        for (int i = HORIZONAL_INTERVAL + 93; i <= (HORIZONAL_INTERVAL + 109); i++) {
            if (mSecond % i == 0)
                newHorizonalBullet();
        }

        for (int i = HORIZONAL_INTERVAL_RIGHT + 93; i <= (HORIZONAL_INTERVAL_RIGHT + 109); i++) {
            if (mSecond % i == 0)
                newHorizonalBulletRight();
        }

        for (int i = PLAYER_BULLET_INTERVAL + 93; i <= (PLAYER_BULLET_INTERVAL + 107); i++) {
            if (mSecond % i == 0) {
                newPlayerBullet();
            }
        }

        for (int i = BOSS_BULLET_INTERVAL + 93; i <= (BOSS_BULLET_INTERVAL + 109); i++) {
            if (mSecond % i == 0) {
                newBossBulletRight();
                newBossBulletLeft();
            }
        }


        if (mBoss.getLeft() >= 30) {
            mBoss.move(2);
        }

        if (mBoss.getRight() == mWidth - 30)
            mBoss.setLocate(30, mBoss.getTop());


        //String a = "" + mBoss.getLeft();
        // Log.d("Test", a);

        bulletDelete();
        bossTouchedBulletDelete();

        try {
            for (BulletObject bulletObject : mBulletList) {
                if (bulletObject != null) {
                    bulletObject.move(bulletObject.mSpeedX, bulletObject.mSpeedY);
                }
            }
            for (StraightShoot playershoot : mPlayerBulletList) {
                if (playershoot != null) {
                    playershoot.move(playershoot.mSpeedX, playershoot.mSpeedY);
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
            if (!mIsClear) {
                //弾に当たる
                for (BulletObject bulletObject : mBulletList) {
                    if (mPlayer.shotCheck(bulletObject)) {
                        mIsFailed = true;
                    }
                }

                //ボスに当たる
                if (mPlayer.touchBoss(mBoss))
                    mIsFailed = true;

                //ボスに弾を当てる
                for (StraightShoot straightShoot : mPlayerBulletList) {
                    if (mBoss.shotCheck(straightShoot)) {
                        mIsClear = true;
                    }
                }
            }


            if (!((mIsClear) || (mIsFailed))) {
                mPaint.setColor(Color.DKGRAY);
                for (BulletObject bulletObject : mBulletList) {
                    mCanvas.drawBitmap(mBitmapBullet, bulletObject.getLeft(), bulletObject.getTop(), null);
                }


                for (StraightShoot playershoot : mPlayerBulletList) {
                    mCanvas.drawBitmap(mBitmapPlayerBullet, playershoot.getLeft(), playershoot.getTop(), null);
                }
            }

            mPlayer.draw(mCanvas);
            mBoss.draw(mCanvas);

            getHolder().unlockCanvasAndPost(mCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String gameEnd() {
        mEndTime = System.currentTimeMillis();
        return ("スコア");
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
        mStartTime = System.currentTimeMillis();
    }

    private void newBoss() {
        mBoss = new BossChara(30, mBitmapBoss.getHeight(), mBitmapBoss.getWidth(), mBitmapBoss.getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.boss));
    }


    private void newHorizonalBullet() {
        BulletObject horizonalBullet;

        for (int i = 0; i < BULLETS; i++) {
            int left = -mBitmapBullet.getWidth();
            int top = mRand.nextInt(mHeight);

            int xSpeed = mRand.nextInt(6) + 4;
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, 0);
            mBulletList.add(horizonalBullet);
        }

    }

    private void newHorizonalBulletRight() {
        BulletObject horizonalBullet;

        for (int i = 0; i < BULLETS + 1; i++) {
            int left = mWidth + mBitmapBullet.getWidth();
            int top = mRand.nextInt(mHeight);

            int xSpeed = mRand.nextInt(6) + 4;
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), -xSpeed, 0);
            mBulletList.add(horizonalBullet);

        }
    }

    private void newBossBulletLeft() {
        BulletObject bossBulletLeft;

        int left = mBoss.getLeft() + 39;
        int top = mBoss.getButton();
        int ySpeed = 4;
        bossBulletLeft = new StraightShoot(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), 0, ySpeed);
        mBulletList.add(bossBulletLeft);
    }

    private void newBossBulletRight() {
        BulletObject bossBulletRight;

        int left = mBoss.getRight() - 82;
        int top = mBoss.getButton();
        int ySpeed = 4;
        bossBulletRight = new StraightShoot(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), 0, ySpeed);
        mBulletList.add(bossBulletRight);
    }

    private void newPlayerBullet() {
        StraightShoot straightShoot;
        int left = mPlayer.getLeft();
        int top = mPlayer.getTop() - 8;
        int ySpeed = 15;
        straightShoot = new StraightShoot(left, top, mBitmapPlayerBullet.getWidth(), mBitmapPlayerBullet.getHeight(), 0, -ySpeed);
        mPlayerBulletList.add(straightShoot);
    }


    private void bulletDelete() {
        Iterator<BulletObject> bullet = mBulletList.iterator();
        while (bullet.hasNext()) {
            BulletObject bulletObject = bullet.next();
            if (bulletObject.getLeft() == -mBitmapBullet.getWidth() * 3 ||
                    bulletObject.getRight() == mWidth + mBitmapBullet.getWidth() * 3 ||
                    bulletObject.getButton() == mHeight + mBitmapBullet.getHeight()) {
                bullet.remove();
            }
        }
    }

    private void bossTouchedBulletDelete() {
        Iterator<StraightShoot> bullet = mPlayerBulletList.iterator();
        while (bullet.hasNext()) {
            StraightShoot straightShoot = bullet.next();
            if ((mBoss.getLeft() < straightShoot.getRight()) &&
                    (mBoss.getTop() < straightShoot.getButton()) &&
                    (mBoss.getRight() > straightShoot.getLeft()) &&
                    (mBoss.getButton() > straightShoot.getTop())) {
                bullet.remove();
            }
        }
    }

}
