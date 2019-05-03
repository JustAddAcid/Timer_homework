package ru.ok.timer;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class TimerUpdateHandler extends Handler {
    private final static int UPDATE_RATE_MS = 17; // 58.8 fps
    private final WeakReference<MainActivity> activity;
    private final static int MSG_UPDATE_TIME = 0;

    TimerUpdateHandler(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message message) {
        if (MSG_UPDATE_TIME == message.what) {
            activity.get().updateUITimer();
            sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
        }
    }
}
