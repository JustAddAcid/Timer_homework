package ru.ok.timer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.ok.timer.TimerService.TimerStatus;

public class MainActivity extends AppCompatActivity {

    private TimerService timerService;
    private boolean serviceBound;

    private TextView textCounter;
    private Button startBtn;

    private final Handler timerUpdateHandler = new TimerUpdateHandler(this);
    private final static int MSG_UPDATE_TIME = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = findViewById(R.id.btn_start_stop);
        textCounter = findViewById(R.id.timer);
    }

    public void startBtnClick(View view) {
        if (serviceBound){
            TimerStatus currentStatus = timerService.getTimerStatus();
            if (currentStatus != TimerStatus.STARTED){
                timerService.startTimer();
                updateUIStartRun();
            } else {
                timerService.pauseTimer();
                updateUIStopRun();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    public void resetBtnClick(View view) {
        if (serviceBound){
            timerService.resetTimer();
            updateUIStopRun();
            textCounter.setText("00:00:000");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this, TimerService.class);
        startService(i);
        bindService(i, mConnection, 0);
        timerUpdateHandler.sendEmptyMessage(MSG_UPDATE_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUIStopRun();
        if (serviceBound) {
            if (timerService.getTimerStatus() != TimerStatus.STOPPED) {
                timerService.foreground();
            } else {
                stopService(new Intent(this, TimerService.class));
            }
            unbindService(mConnection);
            serviceBound = false;
        }
    }

    private void updateUIStartRun() {
        timerUpdateHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        startBtn.setText(getString(R.string.stop));
    }

    private void updateUIStopRun() {
        timerUpdateHandler.removeMessages(MSG_UPDATE_TIME);
        startBtn.setText(getString(R.string.start));
    }

    @SuppressLint("DefaultLocale")
    public void updateUITimer() {
        if (serviceBound) {
            long elapsedTime = timerService.elapsedTime();
            int minutes = (int) (elapsedTime / 60000L);
            int seconds = (int) (elapsedTime % 60000L) / 1000;
            int milliseconds = (int) (elapsedTime % 1000L);

            textCounter.setText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TimerService.RunServiceBinder binder = (TimerService.RunServiceBinder) service;
            timerService = binder.getService();
            serviceBound = true;
            timerService.background();
            if (timerService.getTimerStatus() == TimerStatus.STARTED) {
                updateUIStartRun();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
}