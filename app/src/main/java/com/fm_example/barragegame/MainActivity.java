package com.fm_example.barragegame;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.media.MediaPlayer;


public class MainActivity extends Activity {

    private MediaPlayer mOpening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpening = MediaPlayer.create(this, R.raw.theme);

        mOpening.setLooping(true);

        mOpening.start();


        Button btnNext = (Button) this.findViewById(R.id.button3);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, CharacterMove.class);
                startActivity(intent);
            }
        });

        Button btnNext4 = (Button) this.findViewById(R.id.button4);
        btnNext4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, BossMove.class);
                startActivity(intent);
            }
        });


        Button btnNexte = (Button) this.findViewById(R.id.button12);
        btnNexte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, BossMoveSix.class);
                startActivity(intent);
            }
        });


        Button btnNextg = (Button) this.findViewById(R.id.button14);
        btnNextg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, BossMoveEight.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mOpening.stop();
            finish();
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserLeaveHint() {
        if (mOpening.isPlaying())
            mOpening.stop();
    }


}
