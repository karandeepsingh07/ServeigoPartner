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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceListActivity extends AppCompatActivity {

    List<ClassService> serviceList;
    List<String> serviceSelectedList;
    FirebaseFirestore firebaseFirestore;
    AdapterVendorServiceList adapter;
    RecyclerView recyclerView;
    Button buttonSubmit,buttonBack;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("My Services");

        Intent intent=getIntent();
        final String vendorID= intent.getStringExtra("vendorID");
        final String state= intent.getStringExtra("state").trim();
        final String city= intent.getStringExtra("city").trim();
        final String category= intent.getStringExtra("category").trim();
        final String subCategory= intent.getStringExtra("subCategory").trim();

        serviceList=new ArrayList<>();
        serviceSelectedList=new ArrayList<>();
        buttonSubmit=findViewById(R.id.button);
        progressBar=findViewById(R.id.progressBar);
        firebaseFirestore= FirebaseFirestore.getInstance();
        adapter=new AdapterVendorServiceList(serviceList,ServiceListActivity.this);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(ClassService classService:serviceList){
                    if(classService.isChecked()){
                        Map<String,String> map=new HashMap<>();
                        map.put("Service",classService.getUid());
                        firebaseFirestore.collection("Vendor").document(vendorID).collection("Services").document(classService.getUid()).set(map);
                    }else{
                        firebaseFirestore.collection("Vendor").document(vendorID).collection("Services").document(classService.getUid()).delete();
                    }
                }
                finish();
            }
        });

        getResult(state,city,category,subCategory,vendorID);
    }
    public void getResult(final String state, final String city, final String category, final String subCategory, final String vendorID){
        firebaseFirestore.collection("Vendor").document(vendorID).collection("Services").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size()==0){
                        progressBar.setVisibility(View.GONE);
                    }
                    if(task.getResult().size()!=0)
                        for(DocumentSnapshot snapshot: task.getResult()) {
                            String service = snapshot.getId();
                            Log.d("xyz1",service);
                            serviceSelectedList.add(service);
                        }
                        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).collection("Services").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot snapshot: task.getResult()){
                                    ClassService classService;
                                    classService=snapshot.toObject(ClassService.class);
                                    for(String s:serviceSelectedList){
                                        if(s.equals(snapshot.getId())){
                                            classService.setChecked(true);
                                            Log.d("xyz",snapshot.getId());
                                        }
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    serviceList.add(classService);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                } else {
                    Log.d("xyz", "get failed with ", task.getException());
                }
            }
        });
    }
}