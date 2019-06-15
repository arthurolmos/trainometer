package com.arthurwosniaki.trainometer.utils;

import android.content.Context;
import android.widget.Toast;

public abstract class ToastMessage {
    public static void showMessage(Context context, String m){
        Toast.makeText(context, m, Toast.LENGTH_SHORT).show();
    }
}
