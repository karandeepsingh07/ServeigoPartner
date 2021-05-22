package com.service.serveigopartner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    TextView textViewAmount, textViewPayment, textViewExtraCharges;
    ArrayList<String> arrOfService, arrOfPrice;
    TextView textViewCustomerName, textViewDate, textViewStatus, textViewContact, textViewTime, textViewAddress;
    EditText editTextAmount;
    Button buttonAccept, buttonBack;
    String userID;
    ProgressBar progressBar, progressBar2;
    LinearLayout linearLayout;
    RadioGroup radioGroup;
    RadioButton radioButtonCod, radioButtonOnline;
    String method, status, amount;
    LinearLayout linearLayoutContact;
    int REQUEST_PHONE_CALL=125;
    long tax, extraAmount;
    ImageButton imageButtonCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar = toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Payment");

        Intent intent = getIntent();
        final String jobID = intent.getStringExtra("jobID");
        final String vendorID = intent.getStringExtra("vendorID");
        final String state = intent.getStringExtra("state");
        final String city = intent.getStringExtra("city");
        final String category = intent.getStringExtra("category");
        final String subCategory = intent.getStringExtra("subCategory");
        status = intent.getStringExtra("status");
        Log.d("xyz1", vendorID);


        radioGroup = findViewById(R.id.radioGroup);
        radioButtonCod = findViewById(R.id.radioButton_cod);
        radioButtonOnline = findViewById(R.id.radioButton_online);
        textViewCustomerName = findViewById(R.id.textView_customerName);
        textViewDate = findViewById(R.id.textView_date);
        textViewStatus = findViewById(R.id.textView_status);
        textViewAmount = findViewById(R.id.textView_amount);
        textViewPayment = findViewById(R.id.textView_payment);
        textViewTime = findViewById(R.id.textView_time);
        textViewContact = findViewById(R.id.textView_number);
        textViewAddress = findViewById(R.id.textView_address);
        textViewExtraCharges = findViewById(R.id.textView_extraCharges);
        editTextAmount = findViewById(R.id.editText_amount);
        editTextAmount.setText(""+0);
        buttonAccept = findViewById(R.id.button_accept);
        imageButtonCall = findViewById(R.id.imageButton_call);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayoutContact = findViewById(R.id.linearLayoutContact);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        buttonBack = toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + textViewContact.getText().toString()));
                if (ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PaymentActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(intent);
                }
            }
        });
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar2.setVisibility(View.VISIBLE);
                double extraAmt=Double.parseDouble(editTextAmount.getText().toString().trim());
                double taxAmount = (extraAmt / 100.0f) * tax;
                double discountAmount = extraAmt+ taxAmount;
                amount = String.valueOf(Double.parseDouble(textViewAmount.getText().toString().trim())+discountAmount );
                firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID).update("workDone",true,"amount",amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar2.setVisibility(View.GONE);
                        finish();
                    }
                });
                //DocumentReference toPath= firebaseFirestore.collection("Job").document("Closed").collection("UID").document(jobID);
                /*if(radioGroup.getCheckedRadioButtonId()==R.id.radioButton_cod){
                    progressBar2.setVisibility(View.VISIBLE);
                    buttonAccept.setVisibility(View.GONE);
                    method="COD";
                    Intent intent=new Intent(PaymentActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.radioButton_online){
                    progressBar2.setVisibility(View.VISIBLE);
                    buttonAccept.setVisibility(View.GONE);
                    method="Online";
                    Intent intent = new Intent(PaymentActivity.this, QRActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(PaymentActivity.this, "Select Payment method", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        getResult(jobID);
    }

    private void getResult(final String jobID) {
        firebaseFirestore.collection("Admin").document("tax").collection("List").document("haqYQ0JXkuGLSxHPfQID").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                tax=task.getResult().getLong("tax");
                firebaseFirestore.collection("Job").document(status).collection("UID").document(jobID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        Log.d("xyz", task.getResult().toString());
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String services = document.getString("services");
                                String prices = document.getString("prices");
                                String userName = document.getString("userName");
                                String userAddress = document.getString("userAddress");
                                String number = document.getString("contact");
                                userID = document.getString("UserId");
                                String date = document.getString("date");
                                String status = document.getString("status");
                                String time = document.getString("time");
                                amount=document.getString("amount");
                                boolean payment= document.getBoolean("payment");
                                Log.d("xyz", services);
                                textViewCustomerName.setText(userName);
                                textViewDate.setText(date);
                                textViewTime.setText(time);
                                textViewStatus.setText(status);
                                textViewAddress.setText(userAddress);
                                textViewContact.setText(number);
                                if(document.getString("status").equals("DuePayment")){
                                    //radioGroup.setVisibility(View.VISIBLE);
                                    buttonAccept.setVisibility(View.VISIBLE);
                                    textViewAmount.setVisibility(View.VISIBLE);
                                    editTextAmount.setVisibility(View.VISIBLE);
                                    textViewPayment.setVisibility(View.GONE);
                                    textViewAmount.setText(document.getString("amount"));
                                    textViewStatus.setTextColor(Color.parseColor("#EF7F1A"));
                                    textViewStatus.setText("Pending");
                                }else if(document.getString("status").equals("Closed") && !payment){
                                    radioGroup.setVisibility(View.GONE);
                                    buttonAccept.setVisibility(View.GONE);
                                    textViewAmount.setVisibility(View.VISIBLE);
                                    linearLayoutContact.setVisibility(View.GONE);
                                    editTextAmount.setVisibility(View.GONE);
                                    textViewPayment.setVisibility(View.VISIBLE);
                                    textViewAmount.setText(document.getString("amount"));
                                    textViewStatus.setTextColor(Color.parseColor("#EF7F1A"));
                                    textViewStatus.setText("Due Payment");
                                }else if(document.getString("status").equals("Closed")){
                                    radioGroup.setVisibility(View.GONE);
                                    buttonAccept.setVisibility(View.GONE);
                                    textViewAmount.setVisibility(View.VISIBLE);
                                    editTextAmount.setVisibility(View.GONE);
                                    linearLayoutContact.setVisibility(View.GONE);
                                    textViewPayment.setVisibility(View.VISIBLE);
                                    textViewAmount.setText(document.getString("amount"));
                                    textViewStatus.setTextColor(Color.parseColor("#009846"));
                                    textViewStatus.setText("Due Payment");
                                }
                                if(document.getBoolean("workDone")){
                                    editTextAmount.setVisibility(View.GONE);
                                    buttonAccept.setVisibility(View.GONE);
                                    textViewExtraCharges.setVisibility(View.GONE);
                                }
                                arrOfService = new ArrayList<>(Arrays.asList(services.split("\n")));
                                arrOfPrice = new ArrayList<>(Arrays.asList(prices.split("\n")));
                                adapter = new AdapterServiceList(arrOfService,arrOfPrice, PaymentActivity.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d("xyz", "No such document");
                            }
                            linearLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.d("xyz", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
    }

    public void moveFirestoreDocument(final DocumentReference fromPath, final DocumentReference toPath,final String state,final String city,final String category,final String subCategory,final String vendorID,final String jobID) {
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
                                                        firebaseFirestore.collection("Job").document("Closed").collection("UID").document(jobID).update("status","Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                firebaseFirestore.collection("Users").document(userID).collection("Booking").document(jobID).update("status","Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        firebaseFirestore.collection("Area").document(state).collection(city).document(category).collection("List").document(subCategory).collection("Vendor").document(vendorID).collection("Job").document(jobID).update("status","Closed","payment method",method).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Log.d("xyz", "Done Update!");
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