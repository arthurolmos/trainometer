package com.arthurwosniaki.trainometer.stopwatch.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Chronometer;

import com.arthurwosniaki.trainometer.stopwatch.notifications.ChronometerNotification;
import com.arthurwosniaki.trainometer.utils.Converters;

import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_FRAGMENT_BROADCAST;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_ID_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_PAUSE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_RESTART;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_START;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_STOP;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_UPDATE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.RESTART_CHRONOMETER_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.UPDATE_CHRONOMETER_UI;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isChronometerPaused;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isChronometerRunningBackground;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isChronometerServiceRunning;


public class ChronometerService extends Service {

    private String TAG = ChronometerService.class.getSimpleName();

    private Chronometer chronometer;

    private long time;

    private long idTraining;
    private long idExecution;
    private long idExercise;
    private int position;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Notification notification;


    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            time = SystemClock.elapsedRealtime() - chronometer.getBase();
            updateChronometer(time);

            timerHandler.postDelayed(timerRunnable, 1000);
        }
    };

    public ChronometerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new ChronometerNotification(this).createRunningNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Bundle extras = intent.getExtras();
        if(extras != null){
            idTraining = extras.getLong("id_training");
            idExecution = extras.getLong("id_execution");
            idExercise = extras.getLong("id_exercise");
            position  = extras.getInt("position");

            time = extras.getLong("time", time);
            Log.v(TAG, "Background: " + isChronometerRunningBackground);
        }

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.v(TAG, "Is Chronometer Service Running?: " + isChronometerServiceRunning);
        if(isChronometerServiceRunning){

            Log.v(TAG, "Is Chronometer Service Running Background?: " + isChronometerRunningBackground);
            if(isChronometerRunningBackground){
                createChronometerRunningNotification();
            }else{
                stopForeground(true);
            }

            initializeChronometer();

        //Stop Service
        } else{
            stopChronometer();
            updateActivityUI();

            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    private void initializeChronometer(){
        //Chronometer Paused
        Log.v(TAG, "Is Chronometer Paused?: " + isChronometerPaused);
        if(isChronometerPaused) {
            stopChronometer();
            Log.v(TAG, "Chronometer STOPPED");

        //Chronometer Running
        }else{
            chronometer = new Chronometer(this);
            chronometer.setBase(SystemClock.elapsedRealtime() - time);

            new Thread(() -> {
                chronometer.start();
                timerHandler.post(timerRunnable);
            }).start();
            Log.v(TAG, "Chronometer STARTED");

        }

        //Running background - Notification
        updateChronometer(time);
        updateActivityUI();
    }

    private void stopChronometer(){
        if(chronometer != null){
            chronometer.stop();
        }
        timerHandler.removeCallbacks(timerRunnable);
    }

    //=== FRAGMENT UPDATE ===/
    private void updateChronometer(Long t){
        String s = Converters.longTimeToString(time);

        Intent localIntent = new Intent(CHRONOMETER_FRAGMENT_BROADCAST)
                .setAction(CHRONOMETER_UPDATE)
                .putExtra("time", t)
                .putExtra("elapsed_time", s);

        if(isChronometerRunningBackground && mBuilder != null && notification != null){
            mBuilder.setContentText(s);
            mNotificationManager.notify(CHRONOMETER_ID_SERVICE, mBuilder.build());
        }

        LocalBroadcastManager.getInstance(ChronometerService.this).sendBroadcast(localIntent);
    }

    private void updateActivityUI() {
        Intent localIntent = new Intent(CHRONOMETER_FRAGMENT_BROADCAST)
                .setAction(UPDATE_CHRONOMETER_UI);

        LocalBroadcastManager.getInstance(ChronometerService.this).sendBroadcast(localIntent);
    }


    private void createChronometerRunningNotification(){
        Bundle bundle = createBundle(true);
        mBuilder = new ChronometerNotification(this).createChronometerRunningNotification(bundle);

        notification = mBuilder.setOngoing(true).build();

        startForeground(CHRONOMETER_ID_SERVICE, notification);
    }


    private Bundle createBundle(boolean notification){
        Bundle bundle = new Bundle();

        bundle.putLong("id_training", idTraining);
        bundle.putLong("id_execution", idExecution);
        bundle.putLong("id_exercise", idExercise);

        bundle.putInt("fragment_position", 0);
        bundle.putInt("position", position);

        //Parameter to ExecuteExerciseActivity check
        bundle.putBoolean("started_notification", notification);

        return bundle;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "on Destroy!");

         Intent intent = new Intent(CHRONOMETER_FRAGMENT_BROADCAST).setAction(RESTART_CHRONOMETER_SERVICE);
         intent.putExtras(createBundle(false));

         intent.putExtra("time", time);

         stopForeground(true);
         stopChronometer();

         LocalBroadcastManager.getInstance(ChronometerService.this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //=== BROADCAST RECEIVER ===/
    public static class ChronometerServiceReceiver extends BroadcastReceiver{
        private String TAG = ChronometerService.class.getSimpleName();

        public ChronometerServiceReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(TAG, "Message received by Chronometer Service Broadcast!");

            if(intent != null){
                String action = intent.getAction();
                Bundle extras = intent.getExtras();

                //Updates tvChronometer
                if(action != null){
                    switch(action){
                        case CHRONOMETER_START:
                            Log.v(TAG, "Notification sent START command!");

                            Intent start = new Intent(context, ChronometerService.class);
                            if(extras != null){
                                start.putExtras(extras);
                            }
                            isChronometerPaused = false;

                            context.startService(start);
                            break;

                        case CHRONOMETER_PAUSE:
                            Log.v(TAG, "Notification sent PAUSE command!");

                            Intent pause = new Intent(context, ChronometerService.class);
                            if(extras != null){
                                pause.putExtras(extras);
                            }
                            isChronometerPaused = true;

                            context.startService(pause);
                            break;

                        case CHRONOMETER_STOP:
                            Log.v(TAG, "Notification sent STOP command!");

                            Intent stop = new Intent(context, ChronometerService.class);
                            if(extras != null){
                                stop.putExtras(extras);
                            }
                            isChronometerPaused = false;
                            isChronometerServiceRunning = false;

                            context.startService(stop);
                            break;


                        case CHRONOMETER_RESTART:
                            Log.v(TAG, "Notification sent RESTART command!");

                            Intent restart = new Intent(context, ChronometerService.class);

                            isChronometerPaused = false;
                            isChronometerServiceRunning = true;

                            context.startService(restart);
                            break;
                    }
                }
            }
        }
    }
}

