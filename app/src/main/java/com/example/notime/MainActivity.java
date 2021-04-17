package com.example.notime;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static String SHARED_PREFS = "sharedprefs";
    private static String TIME = "time";
    private static String MOY = "moy";

    private static long START_TIME_IN_MILIS;
    private static int resetCounter = 0 ;
    private static float moyReset;

    private RelativeLayout mMyLayout;
    private TextView mTextViewCountdown ;
    private TextView mTextViewCountdownMilis ;
    private TextView mTextViewResetCounter ;
    private TextView mTextViewAverage ;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMilis;

    public int bgPrimaryColor ;
    public int bgSecondaryColor;

    private MediaPlayer mpReset;
    private MediaPlayer mpAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyLayout = findViewById(R.id.mylayout);
        mTextViewCountdown = findViewById(R.id.text_countdown);
        mTextViewCountdownMilis = findViewById(R.id.text_countdown_milis);
        mTextViewResetCounter = findViewById(R.id.reset_counter);
        mTextViewAverage = findViewById(R.id.average);

        bgPrimaryColor = getResources().getColor(R.color.green_700);
        bgSecondaryColor = getResources().getColor(R.color.red_400);

        mpReset = MediaPlayer.create(this , R.raw.click_sound);
        mpAlarm = MediaPlayer.create(this , R.raw.alarm_sound);


        loadData();
        updateTimerText();

        mMyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning)
                {
                    resetTimer();
                    startTimer();
                }
                else
                {
                    if (mTimeLeftInMilis <= 100)
                    {
                        resetTimer();
                        Snackbar.make(mMyLayout,"Timer Reseted" ,BaseTransientBottomBar.LENGTH_LONG).show();

                    }
                    else
                    startTimer();
                }
            }
        });

        mMyLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mTimerRunning)
                {
                    pauseTimer();
                    mTimeLeftInMilis = mTimeLeftInMilis + 1000 ;
                    updateTimerText();
                }
                else
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("");
                    dialog.setMessage("Pick the period (seconds)");
                    final NumberPicker input = new NumberPicker(MainActivity.this);
                    input.setMaxValue(999);
                    input.setMinValue(5);
                    dialog.setView(input);
                    dialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            START_TIME_IN_MILIS = input.getValue() * 1000;
                            mTimeLeftInMilis = START_TIME_IN_MILIS;
                            saveData();
                            updateTimerText();
                        }

                    });
                    if (mTimeLeftInMilis < START_TIME_IN_MILIS)
                    {
                        dialog.setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetTimer();
                        }
                    });
                    }
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
                return true;
            }
        });


    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMilis , 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMilis = millisUntilFinished;
                updateTimerText();
                if (mTimeLeftInMilis <3100 && mTimeLeftInMilis >=3000){
                    changeBg(140 , bgPrimaryColor , bgSecondaryColor);
                }
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                Snackbar.make(mMyLayout, "Time's up !" ,BaseTransientBottomBar.LENGTH_LONG).show();
                mTimeLeftInMilis=0;
                updateTimerText();
                blinkBgTwice(450);
                mpAlarm.start();
            }
        }.start();
        mTimerRunning = true;
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        Toast.makeText(this , "Time paused",Toast.LENGTH_SHORT).show();
    }
    private void resetTimer() {
        mpReset.start();
        mCountDownTimer.cancel();
//        Snackbar.make(mMyLayout, String.valueOf(moyReset) + "*" + String.valueOf(resetCounter) + "+" + String.valueOf(START_TIME_IN_MILIS-mTimeLeftInMilis/1000) + "/" + String.valueOf(resetCounter+1) ,BaseTransientBottomBar.LENGTH_SHORT).show();
//        Snackbar.make(mMyLayout, String.valueOf((moyReset*resetCounter + ((float)(START_TIME_IN_MILIS-mTimeLeftInMilis)/1000))) ,BaseTransientBottomBar.LENGTH_LONG).show();
        moyReset = (moyReset*resetCounter + ((float)(START_TIME_IN_MILIS-mTimeLeftInMilis)/1000)) / (resetCounter+1);
        resetCounter++;
        mMyLayout.setBackgroundColor(bgPrimaryColor);
        mTimeLeftInMilis = START_TIME_IN_MILIS;
        blinkBg(200);
        updateTimerText();
    }

    private void updateTimerText(){
        int seconds = (int) mTimeLeftInMilis / 1000 % 60 ;
        int minuts = (int) mTimeLeftInMilis / 1000 / 60 ;
        int milis = (int) mTimeLeftInMilis % 1000 ;
        if (minuts > 0)
        {
            String timeLeftFormatted = String.format(Locale.getDefault(),"%02d" , minuts );
            String milisTimeLeftFormatted = String.format(Locale.getDefault(),"%02d.%01d" , seconds ,milis/100);
            mTextViewCountdown.setText(timeLeftFormatted);
            mTextViewCountdownMilis.setText(milisTimeLeftFormatted);
        }
        else
        {
            mTextViewCountdown.setText(String.format(Locale.getDefault() , "%02d" , seconds));
            mTextViewCountdownMilis.setText(String.format(Locale.getDefault() , ".%02d" ,  milis/10));
        }
            mTextViewResetCounter.setText(String.format(Locale.getDefault() , "%2d" , resetCounter));
            mTextViewAverage.setText(String.format(Locale.getDefault() , "%02d s" , (int)moyReset));
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS , MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(TIME , START_TIME_IN_MILIS);
        editor.putFloat(MOY , moyReset);

        editor.apply();
//        Toast.makeText(this,"Data saved" , Toast.LENGTH_SHORT).show();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS , MODE_PRIVATE);
        START_TIME_IN_MILIS = sharedPreferences.getLong(TIME , 196000);
        mTimeLeftInMilis = START_TIME_IN_MILIS;
//        mTextViewAverage.setText(String.valueOf(sharedPreferences.getFloat(MOY , 0)));
    }

    private void blinkBgTwice(int duration){
        ObjectAnimator anim = ObjectAnimator.ofInt(mMyLayout , "BackgroundColor" , Color.WHITE , getResources().getColor(R.color.red_500) ,Color.WHITE , getResources().getColor(R.color.red_600) );
        anim.setDuration(duration);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }
    private void blinkBg(int duration){
        ObjectAnimator anim = ObjectAnimator.ofInt(mMyLayout , "BackgroundColor" , Color.WHITE , bgPrimaryColor  );
        anim.setDuration(duration);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }
    private void changeBg(int duration, int from , int to){
        ObjectAnimator anim = ObjectAnimator.ofInt(mMyLayout , "BackgroundColor" ,from , to );
        anim.setDuration(duration);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }
}