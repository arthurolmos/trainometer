package com.arthurwosniaki.trainometer.stopwatch.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.arthurwosniaki.trainometer.stopwatch.notifications.TimerNotification;
import com.arthurwosniaki.trainometer.utils.Converters;

import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.RESTART_TIMER_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_FRAGMENT_BROADCAST;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_ID_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_PAUSE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_RESTART;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_START;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_STOP;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_UPDATE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.UPDATE_TIMER_UI;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isTimerPaused;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isTimerRunningBackground;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isTimerServiceRunning;


public class TimerService extends Service {

    private String TAG = TimerService.class.getSimpleName();

    private CountDownTimer countdownTimer;

    private long time;
    private long initTime;

    private long idTraining;
    private long idExecution;
    private long idExercise;
    private int position;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    Notification notification;


    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new TimerNotification(this).createRunningNotificationChannel();
        new TimerNotification(this).createAlarmNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            idTraining = extras.getLong("id_training");
            idExecution = extras.getLong("id_execution");
            idExercise = extras.getLong("id_exercise");
            position  = extras.getInt("position");

            time = extras.getLong("time", time);
            initTime = extras.getLong("init_time", initTime);

            Log.v(TAG, "Background: " + isTimerRunningBackground);
        }

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.v(TAG, "Is Running?: " + isTimerServiceRunning);
        if(isTimerServiceRunning){
            Log.v(TAG, "Is Running Background?: " + isTimerRunningBackground);

            if(isTimerRunningBackground){
                createTimerNotification(true);
            }else{
                stopForeground(true);
            }

            initializeTimer();

        //Stop Service
        } else{
            stopCountdownTimer();
            updateActivityUI();

            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    private void initializeTimer(){
        //Timer Paused
        Log.v(TAG, "Is Timer paused?: " + isTimerPaused);
        if(isTimerPaused) {
            stopCountdownTimer();
            Log.v(TAG, "Timer STOPPED");

        //Timer Running
        }else {
            createTimer(time);
            countdownTimer.start();
        }

        //Running background - Notification
        Log.v(TAG, "Is Timer running Backgroung?: " + isTimerRunningBackground);

        updateTimer(time);
        updateActivityUI();
    }

    private void stopCountdownTimer() {
        if(countdownTimer != null){
            countdownTimer.cancel();
        }
    }

    private void updateActivityUI() {
        Intent localIntent = new Intent(TIMER_FRAGMENT_BROADCAST)
                .setAction(UPDATE_TIMER_UI);

        LocalBroadcastManager.getInstance(TimerService.this).sendBroadcast(localIntent);
    }

    private void createTimer(long t){
        countdownTimer = new CountDownTimer(t, 1000) {
            @Override
            public void onTick(long l) {
                long elapsedTime = l/1000;
                time = l;
                Log.v("Timer: ", "Timer = " + elapsedTime);

                updateTimer(time);
            }

            @Override
            public void onFinish() {
                cancel();
                createTimerNotification(false);
            }
        };
    }

    //=== FRAGMENT ===/
    private void updateTimer(Long t){
        String s = Converters.longTimeToString(time);

        Intent localIntent = new Intent(TIMER_FRAGMENT_BROADCAST)
                .setAction(TIMER_UPDATE)
                .putExtra("elapsed_time", s)
                .putExtra("time", t);

        if(isTimerRunningBackground && mBuilder != null && notification != null){
            mBuilder.setContentText(s);

            mNotificationManager.notify(TIMER_ID_SERVICE, mBuilder.build());
        }

        LocalBroadcastManager.getInstance(TimerService.this).sendBroadcast(localIntent);
    }


    private void createTimerNotification(boolean running){
        Bundle bundle = createBundle(true);

        if(running){
            mBuilder = new TimerNotification(this).createTimerRunningNotification(bundle);
        }else{
            mBuilder = new TimerNotification(this).createTimerAlarmNotification(bundle);
        }

        notification = mBuilder.setOngoing(true).build();

        startForeground(TIMER_ID_SERVICE, notification);
    }

    private Bundle createBundle(boolean notification){
        Bundle bundle = new Bundle();

        bundle.putLong("id_training", idTraining);
        bundle.putLong("id_execution", idExecution);
        bundle.putLong("id_exercise", idExercise);

        bundle.putInt("fragment_position", 1);
        bundle.putInt("position", position);

        bundle.putLong("init_time", initTime);
        bundle.putLong("time", time);

        //Parameter to ExecuteExerciseActivity check
        bundle.putBoolean("started_notification", notification);

        return bundle;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "on Destroy!");

         Intent intent = new Intent(TIMER_FRAGMENT_BROADCAST).setAction(RESTART_TIMER_SERVICE);
         intent.putExtras(createBundle(false));

         stopCountdownTimer();
         stopForeground(true);

         LocalBroadcastManager.getInstance(TimerService.this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //=== BROADCAST RECEIVER ===/
    public static class TimerServiceReceiver extends BroadcastReceiver{
        private String TAG = TimerService.class.getSimpleName();

        public TimerServiceReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(TAG, "Message received by Timer Service Broadcast!");

            if(intent != null){
                String action = intent.getAction();
                Bundle extras = intent.getExtras();

                //Updates tvTimer
                if(action != null){
                    switch(action){
                        case TIMER_START:
                            Log.v(TAG, "Notification sent START command!");

                            Intent start = new Intent(context, TimerService.class);
                            if(extras != null){
                                start.putExtras(extras);
                            }
                            isTimerPaused = false;

                            context.startService(start);
                            break;

                        case TIMER_PAUSE:
                            Log.v(TAG, "Notification sent PAUSE command!");

                            Intent pause = new Intent(context, TimerService.class);
                            if(extras != null){
                                pause.putExtras(extras);
                            }
                            isTimerPaused = true;

                            context.startService(pause);
                            break;

                        case TIMER_STOP:
                            Log.v(TAG, "Notification sent STOP command!");

                            Intent stop = new Intent(context, TimerService.class);
                            if(extras != null){
                                stop.putExtras(extras);
                            }
                            isTimerPaused = false;
                            isTimerServiceRunning = false;

                            context.startService(stop);
                            break;


                        case TIMER_RESTART:
                            Log.v(TAG, "Notification sent RESTART command!");

                            long initTime = 0;
                            Intent restart = new Intent(context, TimerService.class);
                            if(extras != null){
                                initTime = extras.getLong("init_time", 0);
                                restart.putExtras(extras);
                            }
                            isTimerPaused = false;
                            isTimerServiceRunning = true;

                            restart.putExtra("time", initTime);

                            context.startService(restart);
                            break;
                    }
                }
            }
        }
    }
}

