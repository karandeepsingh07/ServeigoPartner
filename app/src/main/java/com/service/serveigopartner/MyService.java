package com.service.serveigopartner;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class MyService extends Service {
    int inputPercentage;
    ArrayList<String> list;
    String amount;
    String vendorID;
    FirebaseFirestore firebaseFirestore;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Service startedReceiver", Toast.LENGTH_LONG).show();


        }
    };

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebaseFirestore=FirebaseFirestore.getInstance();
        list = (ArrayList<String>) intent.getSerializableExtra("list");
        vendorID = intent.getStringExtra("vendorUid");
        amount = intent.getStringExtra("amount");
        final int[] count = {0};
        for(String s:list) {
            firebaseFirestore.collection("Job").document("Closed").collection("UID").document(s).update("payment", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    count[0]++;
                    if(count[0]==list.size()){
                        onDestroy();
                    }
                }
            });
        }
        firebaseFirestore.collection("Vendor").document(vendorID).update("paymentPending", amount).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                count[0]++;
                if(count[0]==list.size()){
                    onDestroy();
                }
            }
        });
        //Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }
}
