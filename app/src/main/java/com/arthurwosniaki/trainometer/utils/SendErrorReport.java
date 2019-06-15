package com.arthurwosniaki.trainometer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SendErrorReport {
    private Context context;

    public SendErrorReport(Context context) {
        this.context = context;
    }

    public void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:domination616@gmail.com"));
        i.putExtra(Intent.EXTRA_SUBJECT, "Erro/ Sugestão...");

        if(context != null){
            try {
                context.startActivity(Intent.createChooser(i, "Send mail..."));

            } catch (android.content.ActivityNotFoundException ex) {
                ToastMessage.showMessage(context, "There are no email clients installed.");
            }
        }
    }
}
