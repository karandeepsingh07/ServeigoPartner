package com.service.serveigopartner;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.service.serveigopartner.MyFirebaseMessagingService.CHANNEL_ID;

public class MyOrderService extends Service {
    MediaPlayer mp;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ArrayList<String> arrOfService,arrOfPrice;
    TextView textViewAddress,textViewComments,textViewDate,textViewTime,textViewUserName;
    Button buttonAccept,buttonReject,buttonBack;
    String userID,vendorName;
    ProgressBar progressBar,progressBarButton;
    LinearLayout linearLayout,linearLayoutButton;
    AlertDialog alert;
   /* int mProgressStatus;
    int inputPercentage;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Service startedReceiver", Toast.LENGTH_LONG).show();


*//*            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);

            float percentage = level/ (float) scale;
            mProgressStatus = (int)((percentage)*100);

            Toast.makeText(context, "Battery"+mProgressStatus, Toast.LENGTH_LONG).show();
            if(mProgressStatus>=inputPercentage){
                FirebaseDatabase.getInstance().getReference().child("S4").setValue(0);
                stopSelf();
            }*//*
        }
    };*/

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Create started", Toast.LENGTH_LONG).show();
    }

    public MyOrderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp = MediaPlayer.create(this, R.raw.buzzer);
        if(mp.isPlaying()){
            mp.stop();
        }
        mp.setLooping(true);
        mp.start();
        //inputPercentage = Integer.parseInt(intent.getStringExtra("bPer"));
        final String vendorID= intent.getStringExtra("vendorID");
        final String jobID= intent.getStringExtra("jobID");
        final String state= intent.getStringExtra("state");
        final String city= intent.getStringExtra("city");
        final String category= intent.getStringExtra("category");
        final String subCategory= intent.getStringExtra("subCategory");
        //Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
        /*Intent notificationIntent= new Intent(this,MainActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,notificationIntent,0);


        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "0XX1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, "SSU App Order Service Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("SSU channel for app text FCM");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification=new NotificationCompat.Builder(this,channelId)
                .setOngoing(true)
                .setContentTitle("Order Service")
                .setContentText("You got a new order")
                .setSmallIcon(R.drawable.serveigopartnersquaree)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(m, notification);*/
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customLayout = li.inflate(R.layout.activity_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle("Test dialog")
        .setIcon(R.drawable.serveigopartnersquaree)
        .setMessage("Content")
        .setView(customLayout);
        /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Do something
                mp.stop();
                dialog.dismiss();
                onDestroy();
            }
        })
        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mp.stop();
                    dialog.dismiss();
                    onDestroy();
                }
        });*/
        alert = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        textViewAddress=customLayout.findViewById(R.id.textView_address);
        textViewDate=customLayout.findViewById(R.id.textView_date);
        textViewTime=customLayout.findViewById(R.id.textView_time);
        textViewUserName=customLayout.findViewById(R.id.textView_customerName);
        textViewComments =customLayout.findViewById(R.id.textView_comments);
        buttonAccept=customLayout.findViewById(R.id.button_accept);
        buttonReject=customLayout.findViewById(R.id.button_reject);
        progressBar=customLayout.findViewById(R.id.progressBar);
        progressBarButton=customLayout.findViewById(R.id.progressBarbutton);
        linearLayout=customLayout.findViewById(R.id.linearLayout);
        linearLayoutButton=customLayout.findViewById(R.id.linearLayoutbutton);
        recyclerView=customLayout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        getResult(jobID);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MyOrderService.this, "Accept toast", Toast.LENGTH_SHORT).show();
                linearLayoutButton.setVisibility(View.GONE);
                progressBarButton.setVisibility(View.VISIBLE);
                DocumentReference fromPath= firebaseFirestore.collection("Job").document("Open").collection("UID").document(jobID);
                DocumentReference toPath= firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID);
                moveFirestoreDocumentAccept(fromPath,toPath,state,city,category,subCategory,vendorID,jobID);
            }
        });buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MyOrderService.this, "Accept toast", Toast.LENGTH_SHORT).show();
                linearLayoutButton.setVisibility(View.GONE);
                progressBarButton.setVisibility(View.VISIBLE);
                DocumentReference fromPath= firebaseFirestore.collection("Job").document("Open").collection("UID").document(jobID);
                DocumentReference toPath= firebaseFirestore.collection("Job").document("Rejected").collection("UID").document(jobID);
                moveFirestoreDocumentReject(fromPath,toPath,state,city,category,subCategory,vendorID,jobID);
            }
        });
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(mp.isPlaying())
                    mp.stop();
                mp.release();
            }
        });
        alert.show();
        /*Intent intent1=new Intent(getPackageManager().getLaunchIntentForPackage("com.service.serveigo"));
        *//*intent1.putExtra("state",state);
        intent1.putExtra("city",city);
        intent1.putExtra("category",category);
        intent1.putExtra("subCategory",subCategory);
        intent1.putExtra("vendorID",vendorID);*//*
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);*/
        /*IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, iFilter);*/
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp.isPlaying())
            mp.stop();
        alert.dismiss();
        //Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }


/*    public void startAlert(){
        int i = 1;
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (i * 1000), pendingIntent);
        Toast.makeText(this, "Alarm set in " + i + " seconds",Toast.LENGTH_LONG).show();
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
    private void getResult(final String jobID){
        firebaseFirestore.collection("Job").document("Open").collection("UID").document(jobID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Log.d("xyz",task.getResult().toString());
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String services= document.getString("services");
                        String prices= document.getString("prices");
                        String comments= document.getString("comments");
                        String userAddress= document.getString("userAddress");
                        String date= document.getString("date");
                        String time= document.getString("time");
                        String userName= document.getString("userName");
                        userID= document.getString("userId");
                        vendorName=document.getString("vendorName");
                        Log.d("xyzUI",userID);
                        textViewAddress.setText(userAddress);
                        textViewDate.setText(date);
                        textViewTime.setText(time);
                        textViewUserName.setText(userName);
                        textViewComments.setText(comments);
                        Log.d("xyz",services);
                        arrOfService =new ArrayList<>(Arrays.asList(services.split("\n")));
                        arrOfPrice =new ArrayList<>(Arrays.asList(prices.split("\n")));
                        adapter=new AdapterServiceList(arrOfService,arrOfPrice,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Log.d("xyz", "No such document");
                    }
                } else {
                    Log.d("xyz", "get failed with ", task.getException());
                }
            }
        });
    }
    public void moveFirestoreDocumentAccept(final DocumentReference fromPath, final DocumentReference toPath, final String state, final String city, final String category, final String subCategory, final String vendorID, final String jobID) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("xyz", "DocumentSnapshot successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("xyz", "DocumentSnapshot successfully deleted!");
                                                        firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID).update("status","DuePayment").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                firebaseFirestore.collection("Vendor").document(firebaseUser.getUid()).collection("Job").document(jobID).update("status","DuePayment").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        firebaseFirestore.collection("Users").document(userID).collection("Booking").document(jobID).update("status","DuePayment").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                final Map<String,Object> notificationVendor=new HashMap<>();
                                                                                notificationVendor.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                notificationVendor.put("message",vendorName+" accepted your order.");
                                                                                firebaseFirestore.collection("Users").document(userID).collection("NotificationAR").document().set(notificationVendor);
                                                                                Log.d("xyz", "Done Update!");
                                                                                onDestroy();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("xyz", "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("xyz", "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d("xyz", "No such document");
                    }
                } else {
                    Log.d("xyz", "get failed with ", task.getException());
                }
            }
        });
    }
    public void moveFirestoreDocumentReject(final DocumentReference fromPath, final DocumentReference toPath,final String state,final String city,final String category,final String subCategory,final String vendorID,final String jobID) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("xyz", "DocumentSnapshot successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("xyz", "DocumentSnapshot successfully deleted!");
                                                        firebaseFirestore.collection("Job").document("Rejected").collection("UID").document(jobID).update("status","Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                firebaseFirestore.collection("Vendor").document(firebaseUser.getUid()).collection("Job").document(jobID).update("status","Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        firebaseFirestore.collection("Users").document(userID).collection("Booking").document(jobID).update("status","Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                final Map<String,Object> notificationVendor=new HashMap<>();
                                                                                notificationVendor.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                notificationVendor.put("message",vendorName+" rejected your order.");
                                                                                firebaseFirestore.collection("Users").document(userID).collection("NotificationAR").document().set(notificationVendor);
                                                                                Log.d("xyz", "Done Update!");
                                                                                onDestroy();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("xyz", "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("xyz", "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d("xyz", "No such document");
                    }
                } else {
                    Log.d("xyz", "get failed with ", task.getException());
                }
            }
        });
    }
}
