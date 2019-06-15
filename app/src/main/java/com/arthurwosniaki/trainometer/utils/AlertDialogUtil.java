package com.arthurwosniaki.trainometer.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class AlertDialogUtil {
    private Context context;

    public AlertDialogUtil(Context context) {
        this.context = context;
    }

    public AlertDialog.Builder createAlertDialog(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder (context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        return builder;
    }

    public AlertDialog.Builder createEditDialog(String title){
        final AlertDialog.Builder builder = new AlertDialog.Builder (context);
        builder.setTitle(title);
        builder.setCancelable(false);

        return builder;
    }
}
