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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BossModeSix extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int SAFE_AREA = 73;
    private int mLifeCount = 0;

    private static final int FIRST_BOSS_LIFE = 410;
    private static final int PLAYER_LIFE = 6;
    private static final int BASE_TIME = 120;

    private int mWidth;
    private int mHeight;
    private int mPlayerDamage;
    private int mBossDamage;
    private int mSafeArea;

    private SurfaceHolder mHolder;

    private boolean mIsClear = false;
    private boolean mIsFailed = false;
    private boolean mIsBossMove = false;

    private int mPlayerBulletSecondSave;
    private int mBulletOneSecondSave;

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
    private Bitmap mBitmapButton;
    private Image mButton;
    private int mLifeDelete = PLAYER_LIFE - 1;
    private int mBullet;

    private Bitmap mBitmapLife_0;
    private Bitmap mBitmapLife_1;
    private Bitmap mBitmapLife_2;
    private Bitmap mBitmapLife_3;
    private Bitmap mBitmapLife_4;
    private Bitmap mBitmapLife_5;

    private Bitmap[] mBitmaplife = {mBitmapLife_0, mBitmapLife_1, mBitmapLife_2, mBitmapLife_3, mBitmapLife_4, mBitmapLife_5};

    private Image mLife_0;
    private Image mLife_1;
    private Image mLife_2;
    private Image mLife_3;
    private Image mLife_4;
    private Image mLife_5;

    private Image[] mLife = {mLife_0, mLife_1, mLife_2, mLife_3, mLife_4, mLife_5};

    private Path mGameEnd;
    private Region mRegionGameEnd;

    private Region mRegionWholeScreen;

    private SoundPool mSoundPool;
    private Bitmap mBackGround;

    private MediaPlayer mStageOne;
    private int mDamage;
    private int mClear;
    private int mFail;
    private boolean BulletTime;


    private Bitmap mBitmapBullet;
    private Path mBossHpZone;
    private Region mRegionBossHpZone;
    private Path mHpGage;
    private Region mRegionHpGage;

    private List<BulletObject> mBulletList = new ArrayList<BulletObject>();
    private List<StraightShoot> mPlayerBulletList = new ArrayList<StraightShoot>();

    private Random mRand;
    private Random mRand_2;

    public BossModeSix(Context context) {
        super(context);

        mSoundPool = new SoundPool(11, AudioManager.STREAM_MUSIC, 0);
        mDamage = mSoundPool.load(context, R.raw.damage_2, 1);
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

        mBitmapPlayer = BitmapFactory.decodeResource(rsc, R.drawable.player_xxxhdpi_b);

        mBitmapBoss = BitmapFactory.decodeResource(rsc, R.drawable.boss_7);
        mBitmapBullet = BitmapFactory.decodeResource(rsc, R.drawable.bossbullet_xxxhdpi);
        mBitmapPlayerBullet = BitmapFactory.decodeResource(rsc, R.drawable.playerbullet_xxxhdpi);
        mBitmapButton = BitmapFactory.decodeResource(rsc, R.drawable.button_xxxhdpi);

        mBackGround = BitmapFactory.decodeResource(rsc, R.drawable.background_10);


        mBitmapPlayer = Bitmap.createScaledBitmap(mBitmapPlayer, mWidth / 10,
                mHeight / 15, false);

        mBitmapBoss = Bitmap.createScaledBitmap(mBitmapBoss, mWidth / 4,
                mHeight / 7, false);

        mBitmapBullet = Bitmap.createScaledBitmap(mBitmapBullet, mWidth / 20,
                mHeight / 33, false);

        mBitmapPlayerBullet = Bitmap.createScaledBitmap(mBitmapPlayerBullet, mWidth / 24,
                mHeight / 38, false);

        mBitmapButton = Bitmap.createScaledBitmap(mBitmapButton, mWidth / 3,
                mHeight / 20, false);

        for (int i = 0; i < PLAYER_LIFE; i++) {
            mBitmaplife[i] = BitmapFactory.decodeResource(rsc, R.drawable.life_xxxhdpi);
            mBitmaplife[i] = Bitmap.createScaledBitmap(mBitmaplife[i], mWidth / 23,
                    mHeight / 32, false);
        }

        mSafeArea = heightAdjust(SAFE_AREA);

        mStageOne = MediaPlayer.create(getContext(), R.raw.bgm_6);

        this.mStageOne.setLooping(true);
        mStageOne.start();

        mRand = new Random();
        mRand_2 = new Random();

        newPlayer();
        newBoss();
        newButton();
        newLife();

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

        mPlayer.move(BossMoveSix.role, BossMoveSix.pitch);

        if (mPlayer.getButton() > mHeight) {
            mPlayer.setLocate(mPlayer.getLeft(), mHeight - mBitmapPlayer.getHeight());
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

        if (mSecond == 1) {
            mPlayerBulletSecondSave = 0;
            mBulletOneSecondSave = 0;
        }

        if (mSecond - mPlayerBulletSecondSave == 2) {
            newPlayerBullet();
            mPlayerBulletSecondSave = mSecond;
        }

        if (mSecond - mBulletOneSecondSave == 5) {
            newBossBulletOne();
            mBulletOneSecondSave = mSecond;
        }


        bulletDelete();
        bossTouchedBulletDelete();
        charaTouchedBulletDelete();

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
            if (mCanvas != null) {
                mCanvas.drawBitmap(mBackGround, 0, 0, mPaint);
                mPaint.setColor(Color.BLACK);
                mCanvas.drawPath(mHpGage, mPaint);


                //衝突チェック
                if (!mIsClear) {
                    //弾に当たる
                    for (BulletObject bulletObject : mBulletList) {

                        int temp = mPlayerDamage;
                        mPlayerDamage = mLifeCount;
                        if (temp < mPlayerDamage) {
                            mSoundPool.play(mDamage, 1.0F, 1.0F, 0, 0, 1.0F);
                            mBitmaplife[mLifeDelete--].eraseColor(Color.TRANSPARENT);
                        }

                        if (mLifeCount >= PLAYER_LIFE) {
                            mIsFailed = true;
                        }
                    }
                }

                //ボスに当たる
                if (touchBoss(mBoss) >= PLAYER_LIFE) {
                    for (int i = 0; i < PLAYER_LIFE; i++)
                        mBitmaplife[i].eraseColor(Color.TRANSPARENT);
                    mIsFailed = true;
                }

                //ボスに弾を当てる
                for (StraightShoot straightShoot : mPlayerBulletList) {
                    mBossDamage = mBoss.shotCheck(straightShoot);
                    if (mBoss.shotCheck(straightShoot) >= FIRST_BOSS_LIFE) {
                        mIsClear = true;
                    }
                }

                mBossHpZone = new Path();
                mBossHpZone.addRect(mBitmapBullet.getHeight(), mBitmapBullet.getHeight(),
                        mBitmapBullet.getHeight() + mBitmapPlayer.getHeight(),
                        mBitmapBullet.getHeight() + heightAdjust((3 * (FIRST_BOSS_LIFE - mBossDamage))), Path.Direction.CW);


                mRegionBossHpZone = new Region();
                mRegionBossHpZone.setPath(mBossHpZone, mRegionWholeScreen);

                mPaint.setARGB(255, 136, 160, 244);
                mCanvas.drawPath(mBossHpZone, mPaint);

                if (mIsClear) {
                    mSoundPool.play(mClear, 2.0F, 2.0F, 0, 0, 1.0F);
                    mPaint.setColor(Color.TRANSPARENT);
                    mCanvas.drawPath(mHpGage, mPaint);
                    String msg = "ゲームクリア";
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextSize(heightAdjust(70));
                    mCanvas.drawText(msg, mWidth / 2 - 100, mBitmapBoss.getHeight() * 3, mPaint);
                }

                if (mIsFailed) {
                    mSoundPool.play(mFail, 2.0F, 2.0F, 0, 0, 1.0F);
                    String msg = "攻略失敗";
                    mPaint.setTextSize(heightAdjust(70));
                    mPaint.setColor(Color.BLACK);
                    mCanvas.drawText(msg, mWidth / 2 - 100, mBitmapBoss.getHeight() * 3, mPaint);
                }

                if (mIsClear || mIsFailed) {
                    mBitmapPlayer.eraseColor(Color.TRANSPARENT);
                    mBitmapBoss.eraseColor(Color.TRANSPARENT);
                    mButton.draw(mCanvas);
                    String msg = gameScore();
                    mPaint.setTextSize(heightAdjust(70));
                    mPaint.setColor(Color.BLACK);
                    mCanvas.drawText(msg, mWidth / 2 - 120, mBitmapBoss.getHeight() * 3 + heightAdjust(70) + 2, mPaint);
                    String endmsg = "タイトルに戻る";
                    mCanvas.drawText(endmsg, mWidth / 2 - 120, mBitmapBoss.getHeight() * 3 + heightAdjust(70) * 2 + 2, mPaint);
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

                for (int i = 0; i < PLAYER_LIFE; i++)
                    mLife[i].draw(mCanvas);

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
        mGameEnd.addRect(mWidth / 2 - 140, mBitmapBoss.getHeight() * 3 + heightAdjust(70) * 3 + 2,
                mWidth / 2 - 140 + mBitmapButton.getWidth(), mBitmapBoss.getHeight() * 3 + heightAdjust(70) * 3 + 2 + 90, Path.Direction.CW);
        mRegionGameEnd = new Region();
        mRegionGameEnd.setPath(mGameEnd, mRegionWholeScreen);

        mHpGage = new Path();
        mHpGage.addRect(mBitmapBullet.getHeight(), mBitmapBullet.getHeight(),
                mBitmapBullet.getHeight() + mBitmapPlayer.getHeight(),
                mBitmapBullet.getHeight() + heightAdjust((3 * (FIRST_BOSS_LIFE))), Path.Direction.CW);

        mRegionHpGage = new Region();
        mRegionHpGage.setPath(mHpGage, mRegionWholeScreen);
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
        mScore *= 400;
        mScore -= mPlayerDamage * 500;
        mScore += mBossDamage * 400;
        if (mLifeCount == 0)
            mScore += 20000;
        if (mLifeCount > 5000)
            mScore -= 3000;
        if (mIsFailed)
            mScore /= 4;
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

        if (mBackGround != null) {
            mBackGround.recycle();
            mBackGround = null;
        }

        if (mBitmapBoss != null) {
            mBitmapBoss.recycle();
            mBitmapBoss = null;
        }

        if (mBitmapBullet != null) {
            mBitmapBullet.recycle();
            mBitmapBullet = null;
        }

        for (int i = 0; i < PLAYER_LIFE; i++) {
            if (mBitmaplife[i] != null) {
                mBitmaplife[i].recycle();
                mBitmaplife[i] = null;
            }
        }


        mStageOne.stop();
        mIsAttached = false;
        while (mThread.isAlive()) ;
    }

    private void newPlayer() {
        mPlayer = new PlayerChara(mWidth / 2, mHeight - (2 * mBitmapPlayer.getHeight()), mBitmapPlayer.getWidth(), mBitmapPlayer.getHeight(),
                mBitmapPlayer);
        mIsFailed = false;
        mStartTime = System.currentTimeMillis();
    }

    private void newBoss() {
        mBoss = new BossFirst(mWidth / 2 - mBitmapBoss.getWidth() / 2, mBitmapBoss.getHeight() - mBitmapBullet.getHeight() * 3, mBitmapBoss.getWidth(), mBitmapBoss.getHeight(),
                mBitmapBoss);
    }

    private void newButton() {
        mButton = new Image(mWidth / 2 - 140, mBitmapBoss.getHeight() * 3 + heightAdjust(70) * 3 + 2,
                mWidth * 2 / 3, mBitmapBoss.getHeight() * 3 + heightAdjust(70) * 3 + 2 + 90,
                mBitmapButton);
    }

    private void newLife() {
        int top = mHeight - mBitmapBullet.getHeight() * 2;
        int i = 0;

        for (int left = mBitmapBullet.getWidth(); left <= mBitmapBullet.getWidth() * PLAYER_LIFE; left += mBitmapBullet.getWidth()) {
            mLife[i] = new Image(left, top, mBitmaplife[i].getWidth(), mBitmaplife[i].getHeight(), mBitmaplife[i]);
            i++;
        }
    }

    private void newBossBulletOne() {
        BulletObject bossBullet;

        int top = mBoss.getCenterY();
        int left = mBoss.getLeft() + mBitmapBullet.getWidth() + mRand.nextInt(mBitmapBoss.getWidth() / 2);
        int xSpeed = widthAdjust(mRand.nextInt(5));
        int ySpeed = heightAdjust(mRand.nextInt(10)) + 2;

        bossBullet = new DiagonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), xSpeed, ySpeed);
        mBulletList.add(bossBullet);

        xSpeed = widthAdjust(mRand.nextInt(4));
        ySpeed = heightAdjust(mRand.nextInt(11)) + 2;

        bossBullet = new DiagonalBullet(left, top, mBitmapBullet.getWidth(), mBitmapBullet.getHeight(), -xSpeed, ySpeed);
        mBulletList.add(bossBullet);

    }


    private void newPlayerBullet() {
        StraightShoot straightShoot;
        int left = mPlayer.getCenterX() - widthAdjust(31);
        int top = mPlayer.getTop() - 5;
        int ySpeed = heightAdjust(20);
        straightShoot = new StraightShoot(left, top, mBitmapPlayerBullet.getWidth(), mBitmapPlayerBullet.getHeight(), 0, -ySpeed);
        mPlayerBulletList.add(straightShoot);
    }


    private void bulletDelete() {
        Iterator<BulletObject> bullet = mBulletList.iterator();
        while (bullet.hasNext()) {
            BulletObject bulletObject = bullet.next();
            if (bulletObject.getButton() < mBitmapBullet.getHeight() * 4 ||
                    bulletObject.getLeft() < -mBitmapBullet.getWidth() * 4 ||
                    bulletObject.getRight() > mWidth + mBitmapBullet.getWidth() * 4 ||
                    bulletObject.getTop() > mHeight + mBitmapBullet.getHeight()) {
                bullet.remove();
            }
        }
    }

    private void charaTouchedBulletDelete() {
        Iterator<BulletObject> bullet = mBulletList.iterator();
        while (bullet.hasNext()) {
            BulletObject bulletObject = bullet.next();
            if ((mPlayer.getLeft() + mSafeArea + heightAdjust(8) < bulletObject.getRight()) &&
                    (mPlayer.getTop() + mSafeArea + heightAdjust(8) < bulletObject.getButton()) &&
                    (mPlayer.getRight() - mSafeArea > bulletObject.getLeft()) &&
                    (mPlayer.getButton() - mSafeArea > bulletObject.getTop())) {
                mLifeCount++;
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


    public int touchBoss(BossChara bosschara) {
        if ((mPlayer.getLeft() + mSafeArea + heightAdjust(11) < bosschara.getRight()) &&
                (mPlayer.getTop() + mSafeArea + heightAdjust(11) < bosschara.getButton()) && (
                mPlayer.getRight() - mSafeArea > bosschara.getLeft()) &&
                mPlayer.getButton() - mSafeArea > bosschara.getTop()) {
            mLifeCount = 10000;
        }
        return mLifeCount;
    }


}
