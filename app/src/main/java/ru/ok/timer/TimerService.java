package ru.ok.timer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    private long startTime, differenceTime = 0; // последнее на случай паузы

    public enum TimerStatus {
        STOPPED, STARTED, PAUSED
    }
    private TimerStatus timerStatus;
    public NotificationCompat.Builder builder;

    private static final int NOTIFICATION_ID = 1;
    private static final String TIMER_APP_TAG = "TIMER_APP";
    private final IBinder serviceBinder = new RunServiceBinder();

    class RunServiceBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        startTime = 0;
        timerStatus = TimerStatus.STOPPED;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    public void startTimer() {
        if (timerStatus != TimerStatus.STARTED) {
            startTime = System.currentTimeMillis();
            timerStatus = TimerStatus.STARTED;
        }
    }

    public void pauseTimer(){
        if (timerStatus == TimerStatus.STARTED){
            timerStatus = TimerStatus.PAUSED;
            differenceTime += System.currentTimeMillis() - startTime;
            startTime = 0;
        }
    }

    public void resetTimer(){
        timerStatus = TimerStatus.STOPPED;
        startTime = 0;
        differenceTime = 0;
    }

    public TimerStatus getTimerStatus() {
        return timerStatus;
    }

    public long elapsedTime() {
        return startTime == 0 ?
                differenceTime :
                (System.currentTimeMillis() - startTime + differenceTime) ;
    }

    public void foreground() {
        startForeground(NOTIFICATION_ID, createNotification());
    }

    public void background() {
        stopForeground(true);
    }

    private Notification createNotification() {
        builder = new NotificationCompat.Builder(this, TIMER_APP_TAG )
                .setContentTitle("Таймер в работе")
                .setContentText("Нажми, чтобы вернуться в таймер")
                .setSmallIcon(R.drawable.ic_notification_timer);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }
//
//    @SuppressLint("DefaultLocale")
//    public void updateNotificationTimer(){
//        if (isNotificationCreated){
//            long elapsedTime = elapsedTime();
//            int minutes = (int) (elapsedTime / 60000L);
//            int seconds = (int) (elapsedTime % 60000L) / 1000;
//            int milliseconds = (int) (elapsedTime % 1000L);
//
//            builder.setContentText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));
//            builder.build();
//        }
//    }
}
