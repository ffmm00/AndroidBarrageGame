package com.fm_example.barragegame;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

        Button btnNext3 = (Button) this.findViewById(R.id.button1);
        btnNext3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, PracticeModeMove.class);
                startActivity(intent);
            }
        });

        Button btnNext2 = (Button) this.findViewById(R.id.button2);
        btnNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, AvoidModeMove.class);
                startActivity(intent);
            }
        });


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

        Button btnNexta = (Button) this.findViewById(R.id.button8);
        btnNexta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, BossMoveTwo.class);
                startActivity(intent);
            }
        });

        Button btnNextb = (Button) this.findViewById(R.id.button9);
        btnNextb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, BossMoveThree.class);
                startActivity(intent);
            }
        });

        Button btnNextc = (Button) this.findViewById(R.id.button10);
        btnNextc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, BossMoveFour.class);
                startActivity(intent);
            }
        });

        Button btnNextd = (Button) this.findViewById(R.id.button11);
        btnNextd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpening.stop();
                Intent intent = new Intent(MainActivity.this, BossMoveFive.class);
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
}
