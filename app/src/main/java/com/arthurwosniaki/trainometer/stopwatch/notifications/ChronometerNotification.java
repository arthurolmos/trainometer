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
import com.arthurwosniaki.trainometer.stopwatch.services.ChronometerService;

import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHANNEL_DEFAULT_CHRONOMETER_ID;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_ID_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_PAUSE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_START;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_STOP;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isChronometerPaused;

public class ChronometerNotification {
    private Context context;

    public ChronometerNotification(Context context) {
        this.context = context;
    }

    public void createRunningNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.chronometer_channel_name);
            String description = context.getString(R.string.chronometer_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_DEFAULT_CHRONOMETER_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder createChronometerRunningNotification(Bundle bundle){
        Intent intent = new Intent(context, ExecuteExerciseActivity.class);
        intent.putExtras(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(CHRONOMETER_ID_SERVICE, PendingIntent.FLAG_UPDATE_CURRENT);

        //Action Buttons & Intents
        //START & PAUSE ACTION
        String text;
        Intent actionIntent = new Intent(context, ChronometerService.ChronometerServiceReceiver.class);
        if(isChronometerPaused){
            actionIntent.setAction(CHRONOMETER_START);
            text = context.getString(R.string.start);

        }else{
            actionIntent.setAction(CHRONOMETER_PAUSE);
            text = context.getString(R.string.pause_timer);
        }
        actionIntent.putExtras(bundle);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //STOP ACTION
        Intent stopIntent = new Intent(context, ChronometerService.ChronometerServiceReceiver.class);
        stopIntent.setAction(CHRONOMETER_STOP);
        stopIntent.putExtras(bundle);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //Create Notification
        return new NotificationCompat.Builder(context, CHANNEL_DEFAULT_CHRONOMETER_ID)
                .setContentTitle(context.getResources().getString(R.string.chronometer))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_timer_white_24dp)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)

                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .addAction(R.drawable.btn_notification_action, text, actionPendingIntent)
                .addAction(R.drawable.btn_notification_action, context.getString(R.string.stop), stopPendingIntent);
    }
}
