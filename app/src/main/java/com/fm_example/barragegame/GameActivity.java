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
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.media.MediaPlayer;

public class GameActivity extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private static final int NO_OUTSIDE = 60;
    private static final int BULLETS = 5;

    private static final int FIRST_BOSS_LIFE = 280;
    private static final int PLAYER_LIFE = 40;
    private static final int BASE_TIME = 110;

    private int mWidth;
    private int mHeight;
    private int mPlayerDamage;
    private int mBossDamage;

    private int mPlayerBulletSecondSave;
    private int mHorizonalBulletRightSecondSave;
    private int mHorizonalBulletLeftSecondSave;
    private int mBossBulletSecondSave;
    private int mBossBulletSecondVerTwoSave;

    private SurfaceHolder mHolder;

    private boolean mIsClear = false;
    private boolean mIsFailed = false;
    private boolean mIsBossPowerUp = false;

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

    private Path mGameEnd;
    private Region mRegionGameEnd;
    private Path mPlayerDelete;
    private Region mRegionPlayerDelete;
    private Path mBossDelete;
    private Region mRegionBossDelete;

    private Region mRegionWholeScreen;

    private SoundPool mSoundPool;

    private MediaPlayer mStageOne;
    private int mDamage;
    private int mClear;
    private int mFail;


    private Bitmap mBitmapBullet;

    private List<BulletObject> mBulletList = new ArrayList<BulletObject>();
    private List<StraightShoot> mPlayerBulletList = new ArrayList<StraightShoot>();

    private Random mRand;


    public GameActivity(Context context) {
        super(context);

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mDamage = mSoundPool.load(context, R.raw.damagesound, 1);
        mFail = mSoundPool.load(context, R.raw.failedsound, 1);
        mClear = mSoundPool.load(context, R.raw.clearedsound, 1);

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
        mStageOne = MediaPlayer.create(getContext(), R.raw.stagefirst);

        this.mStageOne.setLooping(true);
        mStageOne.start();

        mRand = new Random();

        newPlayer();
        newBoss();
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

        int mSecond = (int) ((((System.currentTimeMillis() - mStartTime)) / 100) % 60);
        //String a = "" + mSecond;
        //Log.d("Test", a);

        if (mSecond == 0) {
            mPlayerBulletSecondSave = 0;
            mHorizonalBulletLeftSecondSave = 0;
            mHorizonalBulletRightSecondSave = 0;
            mBossBulletSecondSave = 0;
            mBossBulletSecondVerTwoSave = 0;
        }

        if (mSecond - mPlayerBulletSecondSave == 2) {
            newPlayerBullet();
            mPlayerBulletSecondSave = mSecond;
        }


        if (mSecond - mHorizonalBulletLeftSecondSave == 14) {
            newHorizonalBullet();
            mHorizonalBulletLeftSecondSave = mSecond;
        }

        if (mSecond - mHorizonalBulletRightSecondSave == 17) {
            newHorizonalBulletRight();
            mHorizonalBulletRightSecondSave = mSecond;
        }

        if (mSecond - mBossBulletSecondSave == 11) {
            newBossBulletLeft();
            newBossBulletRight();
            mBossBulletSecondSave = mSecond;
        }


        if (mIsBossPowerUp) {
            mBoss.move(3);
            if (mSecond - mBossBulletSecondVerTwoSave == 11) {
                newBossDiagnalBulletLeft();
                newBossDiagnalBulletRight();
                newBossBulletCenter();
                mBossBulletSecondVerTwoSave = mSecond;
            }


        }

        if (mBoss.getLeft() >= 30) {
            mBoss.move(2);
        }
        if (mBoss.getRight() >= mWidth - 30)
            mBoss.setLocate(30, mBoss.getTop());


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


            //衝突チェック
            if (!mIsClear) {
                //弾に当たる
                for (BulletObject bulletObject : mBulletList) {

                    int temp = mPlayerDamage;
                    mPlayerDamage = mPlayer.shotCheck(bulletObject);
                    if (temp < mPlayerDamage) {
                        mSoundPool.play(mDamage, 1.0F, 1.0F, 0, 0, 1.0F);
                        temp = mPlayerDamage;
                    }

                    if (mPlayer.shotCheck(bulletObject) >= PLAYER_LIFE) {
                        mIsFailed = true;
                    }
                }
            }

            //ボスに当たる
            if (mPlayer.touchBoss(mBoss) >= PLAYER_LIFE)
                mIsFailed = true;

            //ボスに弾を当てる
            for (StraightShoot straightShoot : mPlayerBulletList) {
                mBossDamage = mBoss.shotCheck(straightShoot);
                if (mBoss.shotCheck(straightShoot) >= FIRST_BOSS_LIFE / 2)
                    mIsBossPowerUp = true;
                if (mBoss.shotCheck(straightShoot) >= FIRST_BOSS_LIFE) {
                    mIsClear = true;
                }
            }

            if (mIsClear) {
                mSoundPool.play(mClear, 2.0F, 2.0F, 0, 0, 1.0F);
                String msg = "ゲームクリア";
                mPaint.setColor(Color.BLACK);
                mPaint.setTextSize(50);
                mCanvas.drawText(msg, mWidth / 2 - 100, mHeight / 2 - 50, mPaint);
            }

            if (mIsFailed) {
                mSoundPool.play(mFail, 2.0F, 2.0F, 0, 0, 1.0F);
                String msg = "攻略失敗";
                mPaint.setTextSize(50);
                mPaint.setColor(Color.BLACK);
                mCanvas.drawText(msg, mWidth / 2 - 100, mHeight / 2 - 50, mPaint);
            }

            if (mIsClear || mIsFailed) {
                String msg = gameScore();
                mPaint.setTextSize(50);
                mPaint.setColor(Color.BLACK);
                mCanvas.drawText(msg, mWidth / 2 - 120, mHeight / 2 + 10, mPaint);
                String endmsg = "タイトルに戻る";
                mCanvas.drawText(endmsg, mWidth / 2 - 120, mHeight / 2 + 140, mPaint);

                mPaint.setColor(Color.DKGRAY);
                mCanvas.drawPath(mGameEnd, mPaint);

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
        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }

    }


    private void gameEndScreen() {
        mRegionWholeScreen = new Region(0, 0, mWidth, mHeight);
        mGameEnd = new Path();
        mGameEnd.addRect(mWidth / 2 - 120, mHeight / 2 + 160, mWidth * 2 / 3, mHeight / 2 + 270, Path.Direction.CW);
        mRegionGameEnd = new Region();
        mRegionGameEnd.setPath(mGameEnd, mRegionWholeScreen);
    }


    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsClear || mIsFailed) {
                    if (mRegionGameEnd.contains((int) event.getX(), (int) event.getY())) {
                        mStageOne.stop();
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


    private String gameScore() {
        int mScore = (int) (((System.currentTimeMillis() - mStartTime)) / 1000) % 60;
        mScore = BASE_TIME - mScore;
        mScore *= 200;
        mScore -= mPlayerDamage * 200;
        mScore += mBossDamage * 150;
        if (mIsFailed)
            mScore /= 5;
        if (mIsClear)
            mScore += 10000;
        if (mScore < 0)
            mScore = 0;
        return ("スコア：" + mScore);
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
        mBoss = new BossFirst(30, mBitmapBoss.getHeight(), mBitmapBoss.getWidth(), mBitmapBoss.getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.boss));
    }


    private void newHorizonalBullet() {
        BulletObject horizonalBullet;

        for (int i = 0; i < BULLETS; i++) {
            int left = -mBitmapBullet.getWidth();
            int top = mRand.nextInt(mHeight);

            int xSpeed = mRand.nextInt(5) + 4;
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, 0);
            mBulletList.add(horizonalBullet);
        }

    }

    private void newHorizonalBulletRight() {
        BulletObject horizonalBullet;

        for (int i = 0; i < BULLETS + 1; i++) {
            int left = mWidth + mBitmapBullet.getWidth();
            int top = mRand.nextInt(mHeight);

            int xSpeed = mRand.nextInt(5) + 4;
            horizonalBullet = new HorizonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), -xSpeed, 0);
            mBulletList.add(horizonalBullet);

        }
    }

    private void newBossBulletLeft() {
        BulletObject bossBulletLeft;

        int left = mBoss.getLeft() + 39;
        int top = mBoss.getButton();
        int ySpeed = 5;
        bossBulletLeft = new StraightShoot(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), 0, ySpeed);
        mBulletList.add(bossBulletLeft);
    }

    private void newBossBulletRight() {
        BulletObject bossBulletRight;

        int left = mBoss.getRight() - 82;
        int top = mBoss.getButton();
        int ySpeed = 5;
        bossBulletRight = new StraightShoot(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), 0, ySpeed);
        mBulletList.add(bossBulletRight);
    }

    private void newBossBulletCenter() {
        BulletObject bossBulletRight;

        int left = mBoss.getCenterX();
        int top = mBoss.getButton();
        int ySpeed = 8;
        bossBulletRight = new StraightShoot(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), 0, ySpeed);
        mBulletList.add(bossBulletRight);
    }

    private void newBossDiagnalBulletLeft() {
        BulletObject bossBullet;

        int left = mBoss.getLeft() + 39;
        int top = mBoss.getButton();
        int xSpeed = -2;
        int ySpeed = 6;
        bossBullet = new DiagonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, ySpeed);
        mBulletList.add(bossBullet);
    }

    private void newBossDiagnalBulletRight() {
        BulletObject bossBullet;

        int left = mBoss.getRight() - 82;
        int top = mBoss.getButton();
        int xSpeed = 2;
        int ySpeed = 5;
        bossBullet = new DiagonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, ySpeed);
        mBulletList.add(bossBullet);
    }

    private void newPlayerBullet() {
        StraightShoot straightShoot;
        int left = mPlayer.getLeft() + 13;
        int top = mPlayer.getTop() - 8;
        int ySpeed = 15;
        straightShoot = new StraightShoot(left, top, mBitmapPlayerBullet.getWidth(), mBitmapPlayerBullet.getHeight(), 0, -ySpeed);
        mPlayerBulletList.add(straightShoot);
    }


    private void bulletDelete() {
        Iterator<BulletObject> bullet = mBulletList.iterator();
        while (bullet.hasNext()) {
            BulletObject bulletObject = bullet.next();
            if (bulletObject.getButton() == -mBitmapBullet.getHeight() * 3 ||
                    bulletObject.getLeft() == -mBitmapBullet.getWidth() * 3 ||
                    bulletObject.getRight() == mWidth + mBitmapBullet.getWidth() * 3 ||
                    bulletObject.getTop() == mHeight + mBitmapBullet.getHeight()) {
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
