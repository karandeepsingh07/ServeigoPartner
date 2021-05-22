package com.service.serveigopartner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String CHANNEL_ID="SSUAChannel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("msg", "onMessageReceived: " + remoteMessage.getData());
        String messageTitle = remoteMessage.getData().get("title");
        String messageBody = remoteMessage.getData().get("body");
        final String jobId = remoteMessage.getData().get("jobID");
        final String vendorID = remoteMessage.getData().get("vendorID");
        Log.d("vendorID",vendorID);

        String click_action = remoteMessage.getData().get("click_action");

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        /*Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, "SSU App Service Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("SSU channel for app text FCM");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            manager.createNotificationChannel(notificationChannel);
        }

        //String dataMessage = remoteMessage.getData().get("message");
        //String dataFrom = remoteMessage.getData().get("from_user_id");
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("dataMessage",dataMessage);
        //intent.putExtra("dataFrom",dataFrom);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.serveigopartnersquaree)
                .setContentTitle(messageTitle)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());

        if(click_action.equals("allV")){
            Bitmap bitmap = getBitmapfromUrl(remoteMessage.getData().get("jobID"));
            builder.setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .bigLargeIcon(null)
            ).setLargeIcon(bitmap);
        }

        manager.notify(m, builder.build());
        if(click_action.equals("order")){
            FirebaseFirestore.getInstance().collection("Vendor").document(vendorID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Intent serviceIntent=new Intent(getApplicationContext(),MyOrderService.class);
                    serviceIntent.putExtra("jobID", jobId);
                    serviceIntent.putExtra("state",task.getResult().getString("state"));
                    serviceIntent.putExtra("city",task.getResult().getString("city"));
                    serviceIntent.putExtra("category",task.getResult().getString("category"));
                    serviceIntent.putExtra("subCategory",task.getResult().getString("subCategory"));
                    serviceIntent.putExtra("vendorID",vendorID);
                    startService(serviceIntent);
                }
            });
           Log.d("receivb","Started");
           //startAlert();
        }
    }
    public void startAlert(){
        int i = 1;
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        //Toast.makeText(this, "Alarm set in " + i + " seconds",Toast.LENGTH_LONG).show();
        Log.d("receivb","Alarm set in " + i + " seconds");
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }

}
