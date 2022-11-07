package com.example.prm_392_nho_ho_ban.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.schedulingservice.AlarmReceiver;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("dd,MMM,yyyy");
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

    private ConstraintLayout loAlarm;
    private Vibrator v;
    private MediaPlayer mPlayer = new MediaPlayer();
    private Button btnDelay;
    private TextView tvTurnOff;
    private TextView tvCurrentDate;
    private TextView tvClock;
    private TextView tvTitle;
    private Note note;
    private float originDX;
    private float originDY;
    private int lastAction;
    private float dX;
    private float dY;
    private float screenHeight;
    private int opacity;

    private void bindingAction() {
        btnDelay.setOnLongClickListener(this::delay);
        loAlarm.setOnTouchListener(this::drag);
    }


    private boolean drag(View view, MotionEvent event) {
        originDX = event.getY();
        opacity = Math.round(((event.getRawY() - 450) * 255 / screenHeight));
        if (opacity <= 225 && opacity >= 0) {
            loAlarm.getBackground().setAlpha(opacity);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (event.getRawY() < screenHeight / 5) {
                    turnOffAlarm();
                } else {
                    loAlarm.getBackground().setAlpha(255);
                    view.setY(originDY);
                }
                break;

            default:
                return false;
        }
        return true;
    }


    private boolean delay(View view) {
        turnOffAlarm();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("Alarm");
        intent.putExtra("noteJson", getIntent().getStringExtra("noteJson"));
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);// fix this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager
                    .setExact(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10000, pendingIntent);
        } else {
            alarmManager
                    .set(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10000, pendingIntent);
        }

        return true;
    }

    private void bindingUI() {
        String noteJson = getIntent().getStringExtra("noteJson");
        note = new Gson().fromJson(noteJson, Note.class);
        loAlarm = findViewById(R.id.loAlarm);
        btnDelay = findViewById(R.id.btnDelay);
        tvTurnOff = findViewById(R.id.tvTurnOff);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvClock = findViewById(R.id.tvClock);
        tvTitle = findViewById(R.id.tvTitle);

        tvCurrentDate.setText(sdf.format(new Date()));
        tvClock.setText(sdf2.format(new Date()));
        tvTitle.setText(note.getTitle());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(this.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        bindingUI();
        bindingAction();
        playMusic();

    }

    private void playMusic() {
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 800, 200, 1000, 300, 1000, 200, 2000};

        mPlayer = MediaPlayer.create(this, R.raw.spirit_of_the_night);
        mPlayer.setLooping(true);
        mPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        v.vibrate(pattern, 0);
    }


    private void turnOffAlarm() {
        loAlarm.getBackground().setAlpha(255);
        finish();
        v.cancel();
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
        }
    }

    protected void onPause() {
        super.onPause();
        mPlayer.release();
    }
}
