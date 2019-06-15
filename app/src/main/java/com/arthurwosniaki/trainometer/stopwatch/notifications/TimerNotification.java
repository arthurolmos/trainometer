package com.arthurwosniaki.trainometer.stopwatch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.arthurwosniaki.trainometer.ExecuteExerciseActivity;
import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.stopwatch.services.TimerService;

import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHANNEL_DEFAULT_TIMER_ID;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHANNEL_HIGH_TIMER_ID;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_ID_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_PAUSE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_RESTART;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_START;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_STOP;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isTimerPaused;

public class TimerNotification {
    private Context context;

    public TimerNotification(Context context) {
        this.context = context;
    }

    //=== NOTIFICATION ===/
    public void createRunningNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.timer_channel_name);
            String description = context.getString(R.string.timer_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_DEFAULT_TIMER_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createAlarmNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.timer_channel_name);
            String description = context.getString(R.string.timer_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_HIGH_TIMER_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000});

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder createTimerRunningNotification(Bundle bundle){
        Intent intent = new Intent(context, ExecuteExerciseActivity.class);
        intent.putExtras(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(TIMER_ID_SERVICE, PendingIntent.FLAG_UPDATE_CURRENT);

        //Action Buttons & Intents
        //START & PAUSE ACTION
        String text;
        Intent actionIntent = new Intent(context, TimerService.TimerServiceReceiver.class);
        if(isTimerPaused){
            actionIntent.setAction(TIMER_START);
            text = context.getString(R.string.start);

        }else{
            actionIntent.setAction(TIMER_PAUSE);
            text = context.getString(R.string.pause_timer);
        }
        actionIntent.putExtras(bundle);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //STOP ACTION
        Intent stopIntent = new Intent(context, TimerService.TimerServiceReceiver.class);
        stopIntent.setAction(TIMER_STOP);
        stopIntent.putExtras(bundle);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //Create Notification
        return new NotificationCompat.Builder(context, CHANNEL_DEFAULT_TIMER_ID)
                .setContentTitle(context.getResources().getString(R.string.timer))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_timer_white_24dp)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)

                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .addAction(R.drawable.btn_notification_action, text, actionPendingIntent)
                .addAction(R.drawable.btn_notification_action, context.getString(R.string.stop), stopPendingIntent);
    }

    public NotificationCompat.Builder createTimerAlarmNotification(Bundle bundle){
        Intent intent = new Intent(context, ExecuteExerciseActivity.class);
        intent.putExtras(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(TIMER_ID_SERVICE, PendingIntent.FLAG_UPDATE_CURRENT);

        //Action Buttons & Intents
        Intent restartIntent = new Intent(context, TimerService.TimerServiceReceiver.class);
        restartIntent.setAction(TIMER_RESTART);
        restartIntent.putExtras(bundle);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(context, 0, restartIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(context, TimerService.TimerServiceReceiver.class);
        stopIntent.setAction(TIMER_STOP);
        stopIntent.putExtras(bundle);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        return new NotificationCompat.Builder(context, CHANNEL_HIGH_TIMER_ID)
                .setContentTitle(context.getResources().getString(R.string.timer))
                .setContentText(context.getResources().getString(R.string.timer_default_time))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_timer_off_white_24dp)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)

                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .addAction(R.drawable.btn_notification_action, context.getString(R.string.restart), restartPendingIntent)
                .addAction(R.drawable.btn_notification_action, context.getString(R.string.stop), stopPendingIntent);
    }
}
