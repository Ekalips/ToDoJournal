package com.example.ekalips.vitya;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

/**
 * Created by ekalips on 6/2/16.
 */

public class Receiver extends BroadcastReceiver {

    final String LOG_TAG = "ALARM!";

    @Override
    public void onReceive(Context ctx, Intent intent) {

        Log.d(LOG_TAG, "action = " + intent.getAction());
       // Log.d(LOG_TAG, "extra = " + intent.getStringExtra("extra"));
        String str = intent.getStringExtra("extra");
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String[] splited = str.split(";");
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx).
                setContentTitle("Reminder").setContentText(splited[0]).setSound(uri).setSmallIcon(R.drawable.fab_bg_mini);
        manager.notify(new Random().nextInt(),builder.build());
    }
}
