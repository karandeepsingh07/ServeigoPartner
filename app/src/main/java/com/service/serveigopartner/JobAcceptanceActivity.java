package com.service.serveigopartner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class JobAcceptanceActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_acceptance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Available Jobs");

        Intent intent = getIntent();
        final String jobID= intent.getStringExtra("jobID");
        final String vendorID= intent.getStringExtra("vendorID");
        final String state= intent.getStringExtra("state");
        final String city= intent.getStringExtra("city");
        final String category= intent.getStringExtra("category");
        final String subCategory= intent.getStringExtra("subCategory");

        textViewAddress=findViewById(R.id.textView_address);
        textViewDate=findViewById(R.id.textView_date);
        textViewTime=findViewById(R.id.textView_time);
        textViewUserName=findViewById(R.id.textView_customerName);
        textViewComments =findViewById(R.id.textView_comments);
        buttonAccept=findViewById(R.id.button_accept);
        buttonReject=findViewById(R.id.button_reject);
        progressBar=findViewById(R.id.progressBar);
        progressBarButton=findViewById(R.id.progressBarbutton);
        linearLayout=findViewById(R.id.linearLayout);
        linearLayoutButton=findViewById(R.id.linearLayoutbutton);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutButton.setVisibility(View.GONE);
                progressBarButton.setVisibility(View.VISIBLE);
                DocumentReference fromPath= firebaseFirestore.collection("Job").document("Open").collection("UID").document(jobID);
                DocumentReference toPath= firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID);
                moveFirestoreDocumentAccept(fromPath,toPath,state,city,category,subCategory,vendorID,jobID);
            }
        });
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutButton.setVisibility(View.GONE);
                progressBarButton.setVisibility(View.VISIBLE);
                DocumentReference fromPath= firebaseFirestore.collection("Job").document("Open").collection("UID").document(jobID);
                DocumentReference toPath= firebaseFirestore.collection("Job").document("Rejected").collection("UID").document(jobID);
                moveFirestoreDocumentReject(fromPath,toPath,state,city,category,subCategory,vendorID,jobID);
            }
        });

        Log.d("xyz1",jobID);
        getResult(jobID);
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
                        textViewAddress.setText(userAddress);
                        textViewDate.setText(date);
                        textViewTime.setText(time);
                        textViewUserName.setText(userName);
                        textViewComments.setText(comments);
                        Log.d("xyz",services);
                        arrOfService =new ArrayList<>(Arrays.asList(services.split("\n")));
                        arrOfPrice =new ArrayList<>(Arrays.asList(prices.split("\n")));
                        adapter=new AdapterServiceList(arrOfService,arrOfPrice,JobAcceptanceActivity.this);
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

    public void moveFirestoreDocumentAccept(final DocumentReference fromPath, final DocumentReference toPath,final String state,final String city,final String category,final String subCategory,final String vendorID,final String jobID) {
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
                                                                firebaseFirestore.collection("Vendor").document(vendorID).collection("Job").document(jobID).update("status","DuePayment").addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                                                Intent intent=new Intent(JobAcceptanceActivity.this,MainActivity.class);
                                                                                startActivity(intent);
                                                                                finish();
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
                                                                firebaseFirestore.collection("Vendor").document(vendorID).collection("Job").document(jobID).update("status","Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                                                Intent intent=new Intent(JobAcceptanceActivity.this,MainActivity.class);
                                                                                startActivity(intent);
                                                                                finish();
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