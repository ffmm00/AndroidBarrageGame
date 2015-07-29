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
import android.view.TextureView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameActivity extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int PLAYER_POS = 100;
    private static final int NO_OUTSIDE = 80;

    private int mWidth;
    private int mHeight;

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


    private Bitmap mBitmapBullet;
    private BulletObject mBullet;

    private List<BulletObject> mBulletList = new ArrayList<BulletObject>(30);

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
        mBitmapPlayer = BitmapFactory.decodeResource(rsc, R.mipmap.ic_launcher);

        mRand = new Random();

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

        mPlayer.move(CharacterMove.role, CharacterMove.pitch);


        try {

            mCanvas = getHolder().lockCanvas();

            if (!((mIsClear) || (mIsFailed))) {
                mPaint.setColor(Color.DKGRAY);

                mCanvas.drawBitmap(mBitmapPlayer, mPlayer.getLeft(), mPlayer.getTop(), null);
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
        //if (mBitmapBullet != null) {
        //   mBitmapBullet.recycle();
        //    mBitmapBullet = null;
        // }
        mIsAttached = false;
        while (mThread.isAlive()) ;
    }

    private void newPlayer() {
        mPlayer = new PlayerChara(mWidth / 2, mHeight / 2, mBitmapPlayer.getWidth(), mBitmapPlayer.getHeight());
        mIsClear = false;
        mIsFailed = false;
        mStartTime = System.currentTimeMillis();
    }


}
