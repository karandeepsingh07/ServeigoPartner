package com.service.serveigopartner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    LinearLayout itemMyJobs, itemAvailableJobs, itemSupport, linearLayout, itemMyServices;
    FirebaseFirestore firebaseFirestore;
    String state, city, category, subCategory, vendorID, imageUrl, altEmail, contact;
    boolean visible;
    float pendingPayment;
    ProgressBar progressBar, progressBarPayment;
    TextView textViewName, textViewServiceType;
    String name,pendingPaymentStr;
    ImageView imageViewVendor;
    FirebaseAuth firebaseAuth;
    Button buttonPayNow;
    Switch aSwitch;
    boolean blocked;
    RelativeLayout relativeLayout;
    TextView textViewGini;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribes";
                        if (!task.isSuccessful()) {
                            msg = "not";
                        }
                        Log.d("xyz", msg);
                        //Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        textViewName = findViewById(R.id.textView_vendorName);
        textViewServiceType = findViewById(R.id.textView_vendorProfession);
        imageViewVendor = findViewById(R.id.imageView_vendor);
        itemAvailableJobs = findViewById(R.id.item_availableJobs);
        itemMyJobs = findViewById(R.id.item_myJobs);
        itemSupport = findViewById(R.id.item_support);
        itemMyServices = findViewById(R.id.item_services);
        linearLayout = findViewById(R.id.linearLayout);
        progressBar = findViewById(R.id.progressBar);
        relativeLayout = findViewById(R.id.relative_layout);
        textViewGini = findViewById(R.id.textViewGini);
        aSwitch = findViewById(R.id.switch_online);
        buttonPayNow = findViewById(R.id.button_payNow);
        progressBarPayment = findViewById(R.id.progressBarPayment);
        progressBarPayment.setMax(1500);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent1, 0);
            }
        }

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b) {
                        relativeLayout.setBackgroundColor(Color.parseColor("#f7f7f7"));
                        textViewGini.setTextColor(Color.parseColor("#F7F7F7"));
                        toggleOff();
                    } else {
                        relativeLayout.setBackgroundColor(Color.GREEN);
                        textViewGini.setTextColor(Color.BLACK);
                        toggleOn();
                    }
                }
            });
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!blocked) {
                        if (aSwitch.isChecked()) {
                            relativeLayout.setBackgroundColor(Color.parseColor("#f7f7f7"));
                            textViewGini.setTextColor(Color.parseColor("#F7F7F7"));
                            aSwitch.setChecked(false);
                            toggleOff();
                        } else {
                            relativeLayout.setBackgroundColor(Color.GREEN);
                            textViewGini.setTextColor(Color.BLACK);
                            aSwitch.setChecked(true);
                            toggleOn();
                        }
                    }
                }
            });
        itemAvailableJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AvailableJobsActivity.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                intent.putExtra("category", category);
                intent.putExtra("subCategory", subCategory);
                intent.putExtra("vendorID", vendorID);
                startActivity(intent);
            }
        });
        itemMyJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookingActivityTab.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                intent.putExtra("category", category);
                intent.putExtra("subCategory", subCategory);
                intent.putExtra("vendorID", vendorID);
                startActivity(intent);
            }
        });
        itemSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SupportActivity.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                intent.putExtra("category", category);
                intent.putExtra("subCategory", subCategory);
                intent.putExtra("vendorID", vendorID);
                startActivity(intent);
            }
        });
        itemMyServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ServiceListActivity.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                intent.putExtra("category", category);
                intent.putExtra("subCategory", subCategory);
                intent.putExtra("vendorID", vendorID);
                startActivity(intent);
            }
        });
        buttonPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PendingPaymentActivity.class);
                intent.putExtra("state", state);
                intent.putExtra("city", city);
                intent.putExtra("category", category);
                intent.putExtra("subCategory", subCategory);
                intent.putExtra("pendingPayment", pendingPaymentStr);
                intent.putExtra("vendorID", vendorID);
                intent.putExtra("email", altEmail);
                intent.putExtra("contact", contact);
                startActivity(intent);
                finish();
            }
        });
        imageViewVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent1);
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        vendorID = firebaseAuth.getUid();

        getResult(vendorID);
    }

    private void toggleOn() {
        progressBar.setVisibility(View.VISIBLE);
            firebaseFirestore.collection("Vendor").document(vendorID).update("visible", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                /*Intent serviceIntent=new Intent(MainActivity.this,MyOrderService.class);
                serviceIntent.putExtra("state",state);
                serviceIntent.putExtra("city",city);
                serviceIntent.putExtra("category",category);
                serviceIntent.putExtra("subCategory",subCategory);
                serviceIntent.putExtra("vendorID",vendorID);
                startService(serviceIntent);*/
                    progressBar.setVisibility(View.GONE);
                }
            });
    }

    private void toggleOff() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Vendor").document(vendorID).update("visible", false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                /*Intent serviceIntent=new Intent(MainActivity.this,MyOrderService.class);
                stopService(serviceIntent);*/
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getResult(final String vendorID) {
        firebaseFirestore.collection("Vendor").document(vendorID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("xyz", task.getResult().toString());
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        state = document.getString("state");
                        city = document.getString("city");
                        category = document.getString("category");
                        subCategory = document.getString("subCategory");
                        name = document.getString("name");
                        imageUrl = document.getString("vendorImage");
                        pendingPaymentStr = document.getString("paymentPending");
                        altEmail = document.getString("altEmail");
                        contact = document.getString("contact");
                        pendingPayment = Float.parseFloat(pendingPaymentStr);
                        visible = document.getBoolean("visible");
                        if (visible) {
                            relativeLayout.setBackgroundColor(Color.GREEN);
                            textViewGini.setTextColor(Color.BLACK);
                            aSwitch.setChecked(true);
                        } else {
                            relativeLayout.setBackgroundColor(Color.parseColor("#f7f7f7"));
                            textViewGini.setTextColor(Color.parseColor("#F7F7F7"));
                            aSwitch.setChecked(false);
                        }

                        if (pendingPayment > 1500) {
                            relativeLayout.setBackgroundColor(Color.RED);
                            aSwitch.setVisibility(View.GONE);
                            textViewGini.setTextColor(Color.parseColor("#F7F7F7"));
                            textViewGini.setText("You are blocked! Please clear your payemnt first");
                            progressBarPayment.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                            blocked=true;
                        }
                        else
                            progressBarPayment.getProgressDrawable().setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.SRC_IN);
                        progressBarPayment.setProgress((int) pendingPayment, true);
                        textViewName.setText(name);
                        //textViewServiceType.setText(subCategory);
                        Picasso.get().load(imageUrl).placeholder(R.drawable.plumbing)
                                .error(R.drawable.plumbing).into(imageViewVendor);
                        textViewName.setVisibility(View.VISIBLE);
                        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                textViewServiceType.setText(task.getResult().get("Head").toString());
                                textViewServiceType.setVisibility(View.VISIBLE);
                            }
                        });
                        textViewServiceType.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.d("xyz", "No such document");
                    }
                } else {
                    Log.d("xyz", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item1) {
            Map<String, Object> tokenRemoveMap = new HashMap<>();
            tokenRemoveMap.put("token_id", "");
            firebaseFirestore.collection("Vendor").document(firebaseAuth.getCurrentUser().getUid()).update(tokenRemoveMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    firebaseAuth.signOut();
                    Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent1);
                    finish();
                }
            });
            return true;
        }
        if (id == R.id.item2) {
            firebaseFirestore.collection("Admin").document("Privacy Policy").collection("List").document("Policy Vendor Page").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String url = task.getResult().getString("url");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            return true;
        }
        if (id == R.id.item3) {
            firebaseFirestore.collection("Admin").document("About Us").collection("URL").document("About Page").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String url = task.getResult().getString("url");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            return true;
        }
        if (id == R.id.item4) {
            Intent intent1 = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("App", "OnActivity Result.");
        //check if received result code
        //  is equal our requested code for draw permission
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    finish();
                }
            }
        }
    }
}