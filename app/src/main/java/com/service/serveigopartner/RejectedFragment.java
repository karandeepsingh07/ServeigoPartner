package com.service.serveigopartner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RejectedFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    AdapterJobList adapter;
    List<ClassBooking> listItems;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_rejected, container, false);

        Intent intent=getActivity().getIntent();
        String vendorID= intent.getStringExtra("vendorID");
        String state= intent.getStringExtra("state");
        String city= intent.getStringExtra("city");
        String category= intent.getStringExtra("category");
        String subCategory= intent.getStringExtra("subCategory");

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        progressBar= view.findViewById(R.id.progressBar);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listItems= new ArrayList<>();

        adapter=new AdapterJobList((ArrayList<ClassBooking>) listItems,getContext(),state,city,vendorID,category,subCategory);

        recyclerView.setAdapter(adapter);

        getResult(state,city,category,subCategory,vendorID);
        return view;
    }

    private void getResult(final String state,final String city, final String category, final String subCategory, final String vendorID){
        firebaseFirestore.collection("Vendor").document(vendorID).collection("Job").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final int[] count = {0};
                    if(task.getResult().size() ==0){
                        progressBar.setVisibility(View.GONE);
                        //   textViewNoResult.setVisibility(View.VISIBLE);
                    }
                    if(Objects.requireNonNull(task.getResult()).size()!=0) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            String status= snapshot.getString("status");
                            if(status.equals("Rejected"))
                                firebaseFirestore.collection("Job").document(status).collection("UID").document(snapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task!=null) {
                                            final ClassBooking classBooking = task.getResult().toObject(ClassBooking.class);
                                            if(classBooking!=null) {
                                                //Log.d("xyz", classBooking.getVendorName());
                                                classBooking.setJobId(task.getResult().getId());
                                                firebaseFirestore.collection("Users").document(classBooking.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        classBooking.userName=task.getResult().getString("name");
                                                        classBooking.userAddress=task.getResult().getString("address");
                                                        listItems.add(classBooking);
                                                        adapter.notifyDataSetChanged();
                                                        progressBar.setVisibility(View.GONE);
                                                        count[0]++;
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
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