package com.service.serveigopartner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyJobsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ClassBooking> listItems;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    Button buttonBack;
    TextView textViewNoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("My Jobs");

        Intent intent=getIntent();
        String vendorID= intent.getStringExtra("vendorID");
        String state= intent.getStringExtra("state");
        String city= intent.getStringExtra("city");
        String category= intent.getStringExtra("category");
        String subCategory= intent.getStringExtra("subCategory");

        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        buttonBack=toolbar.findViewById(R.id.button_back);
        textViewNoResult=findViewById(R.id.textView_noResult);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listItems= new ArrayList<>();

        adapter=new AdapterJobList((ArrayList<ClassBooking>) listItems,this,state,city,vendorID,category,subCategory);

        recyclerView.setAdapter(adapter);

        getResult(state,city,category,subCategory,vendorID);
    }

    private void getResult(final String state,final String city, final String category, final String subCategory, final String vendorID){
        firebaseFirestore.collection("Vendor").document(vendorID).collection("Job").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().size() ==0){
                        progressBar.setVisibility(View.GONE);
                        textViewNoResult.setVisibility(View.VISIBLE);
                    }
                    if(Objects.requireNonNull(task.getResult()).size()!=0) {
                        for (final DocumentSnapshot snapshot : task.getResult()) {
                            //Log.d("xyz", ""+snapshot);
                            Log.d("xyz", snapshot.getId());
                            firebaseFirestore.collection("Job").document(snapshot.getString("status")).collection("UID").document(snapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ClassBooking classBooking = task.getResult().toObject(ClassBooking.class);
                                    if(classBooking!=null)
                                        classBooking.setJobId(snapshot.getId());
                                    if(!classBooking.getStatus().equals("Open"))
                                        listItems.add(classBooking);
                                    adapter.notifyDataSetChanged();
                                    recyclerView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    textViewNoResult.setVisibility(View.VISIBLE);
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
}