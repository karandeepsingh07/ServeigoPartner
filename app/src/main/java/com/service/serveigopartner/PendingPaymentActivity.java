package com.service.serveigopartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class PendingPaymentActivity extends AppCompatActivity implements PaymentResultListener {

    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar,progressBar2;
    String pendingPayment,altEmail,contact;
    AdapterPendingPayment adapter;
    List<ClassBooking> listItems;
    List<String> listItemsSelected;
    float totalamount;
    Button buttonPay,buttonBack;
    TextView textViewPendingAmount;
    final int UPI_PAYMENT = 0;
    boolean loading;
    private static final String TAG = PendingPaymentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Pending Payment");


        Intent intent= getIntent();
        String vendorID= intent.getStringExtra("vendorID");
        String state= intent.getStringExtra("state");
        String city= intent.getStringExtra("city");
        String category= intent.getStringExtra("category");
        String subCategory= intent.getStringExtra("subCategory");
        pendingPayment= intent.getStringExtra("pendingPayment");
        altEmail= intent.getStringExtra("altEmail");
        contact= intent.getStringExtra("contact");

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        textViewPendingAmount=findViewById(R.id.textView_pendingPaymentAmount);
        recyclerView=findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.progressBar);
        progressBar2=findViewById(R.id.progressBar2);
        buttonPay=findViewById(R.id.button_pay);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textViewPendingAmount.setText(String.valueOf(totalamount));

        listItems= new ArrayList<>();
        listItemsSelected= new ArrayList<>();

        adapter=new AdapterPendingPayment((ArrayList<ClassBooking>) listItems,this,state,city,vendorID,category,subCategory);

        recyclerView.setAdapter(adapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String amount = String.valueOf(totalamount);
                final String note=firebaseAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("Admin").document("Payment").collection("List").document("4QEqdy0LhALrEE9rLhIk").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String upiId = task.getResult().getString("upiId");
                                String name = task.getResult().getString("name");
                                progressBar2.setVisibility(View.GONE);
                                if(isConnectionAvailable(PendingPaymentActivity.this))
                                    startPayment();
                               // payUsingUpi(amount, upiId, name, note);
                            }
                        }
                    });
            }
        });
        getResult();
    }
    private void getResult(){
        firebaseFirestore.collection("Vendor").document(firebaseAuth.getCurrentUser().getUid()).collection("Job").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            Log.d("xyz",status);
                            if(status.equals("Closed"))
                                firebaseFirestore.collection("Job").document(status).collection("UID").document(snapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task!=null) {
                                            final ClassBooking classBooking = task.getResult().toObject(ClassBooking.class);
                                            if(classBooking!=null && !classBooking.isPayment()) {
                                                //Log.d("xyz", classBooking.getVendorName());
                                                classBooking.setJobId(task.getResult().getId());
                                                Log.d("xyz",classBooking.getJobId());
                                                firebaseFirestore.collection("Users").document(classBooking.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        classBooking.userName=task.getResult().getString("name");
                                                        Log.d("xyz",classBooking.getUserName());
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

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String itemName = intent.getStringExtra("item");
            String amount = intent.getStringExtra("amount");
            String checkItem= intent.getStringExtra("task");

            if(checkItem.equals("add")) {
                listItemsSelected.add(itemName);
                totalamount+=Float.parseFloat(amount);
                textViewPendingAmount.setText(String.valueOf(totalamount));
            }
            HashSet<String> listToSet = new HashSet<>(listItemsSelected);

            if(checkItem.equals("remove")) {
                listToSet.remove(itemName);
                totalamount-=Float.parseFloat(amount);
                textViewPendingAmount.setText(String.valueOf(totalamount));
            }
            listItemsSelected.clear();
            if(listToSet.size()!=0)
                for (String i : listToSet) {
                    listItemsSelected.add(i);
                    Collections.reverse(listItemsSelected);
                }
            int n=listItemsSelected.size();
            if(n!=0){
                buttonPay.setVisibility(View.VISIBLE);
            }else{
                buttonPay.setVisibility(View.GONE);
            }
        }
    };

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_P2sva0ZBNUW006");

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.ic_launcher_foreground);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Serveigo");
            options.put("description", "Job Payment");
            //  options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", totalamount*100);//pass amount in currency subunits
            options.put("prefill.email", altEmail);
            options.put("prefill.contact","+91"+contact);
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }


    /*void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(PendingPaymentActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }

    }*/

    @Override
    public void onPaymentSuccess(String s) {
        try {
            loading = true;
            Toast.makeText(PendingPaymentActivity.this, "Transaction successful. Please do not exit or press back", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(PendingPaymentActivity.this, MyService.class);
            //Log.d("xyzP",listItemsSelected.toString());
            serviceIntent.putExtra("list", (ArrayList<String>) listItemsSelected);
            Log.d("xyzP",listItemsSelected.toString());
            serviceIntent.putExtra("vendorUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.d("xyzP",String.valueOf(Double.parseDouble(pendingPayment)));
            serviceIntent.putExtra("amount", String.valueOf(Double.parseDouble(pendingPayment) - Double.parseDouble(textViewPendingAmount.getText().toString())));
            Log.d("xyzP",""+(Double.parseDouble(pendingPayment) - Double.parseDouble(textViewPendingAmount.getText().toString())));
            startService(serviceIntent);
            startActivity(new Intent(PendingPaymentActivity.this,MainActivity.class));
            finish();
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Transaction Failed "+s, Toast.LENGTH_SHORT).show();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(PendingPaymentActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                loading=true;
                Toast.makeText(PendingPaymentActivity.this, "Transaction successful. Please do not exit or press back", Toast.LENGTH_SHORT).show();
                Intent serviceIntent=new Intent(PendingPaymentActivity.this,MyService.class);
                serviceIntent.putExtra("list",(ArrayList<String>)listItemsSelected);
                serviceIntent.putExtra("vendorUid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                serviceIntent.putExtra("amount",String.valueOf(Double.parseDouble(pendingPayment)-Double.parseDouble(textViewPendingAmount.getText().toString())));
                startService(serviceIntent);
                Log.d("UPI", "responseStr: " + approvalRefNo);
                finish();

            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(PendingPaymentActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PendingPaymentActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PendingPaymentActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }*/

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(!loading){
            super.onBackPressed();
        }
    }

}