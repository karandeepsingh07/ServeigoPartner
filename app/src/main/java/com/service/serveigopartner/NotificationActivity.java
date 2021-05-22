package com.service.serveigopartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    AdapterNotificationList adapter;
    List<ClassNotification> listItems;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Notifications");

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        recyclerView=findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.progressBar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        listItems= new ArrayList<>();

        adapter=new AdapterNotificationList((ArrayList<ClassNotification>) listItems,this);

        recyclerView.setAdapter(adapter);
        getResult();
    }
    private void getResult(){
        firebaseFirestore.collection("Admin").document("Universal_notification").collection("List").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final int[] count = {0};
                    if(task.getResult().size() ==0){
                        progressBar.setVisibility(View.GONE);
                        //   textViewNoResult.setVisibility(View.VISIBLE);
                    }else{
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            final ClassNotification classNotification = snapshot.toObject(ClassNotification.class);
                            listItems.add(classNotification);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            count[0]++;
                            if(count[0]==0) {
                                //   textViewNoResult.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                }
                else {
                    progressBar.setVisibility(View.GONE);
                    //      textViewNoResult.setVisibility(View.VISIBLE);
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
}