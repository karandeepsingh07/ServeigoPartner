package com.service.serveigopartner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp = MediaPlayer.create(context, R.raw.buzzer);
        mp.start();
        mp.setLooping(true);
        //Intent intent1=new Intent(context.getPackageManager().getLaunchIntentForPackage("com.service.serveigo"));
        /*Intent i = new Intent();
        i.setClassName(context.getPackageName(), MainActivity.class.getName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
        Log.d("Alarm ", "Alarm triggered!");



        Intent activityIntent = new Intent(context, MainActivity.class).putExtra("byAlarm","yes");
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}
