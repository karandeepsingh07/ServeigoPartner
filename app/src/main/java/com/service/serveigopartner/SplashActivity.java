package com.service.serveigopartner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null) {
            // This method will be executed once the timer is over
            FirebaseFirestore.getInstance().collection("Vendor").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    final DocumentSnapshot snapshot = task.getResult();
                    if(auth.getCurrentUser()!=null && snapshot.exists()) {
                        String state = snapshot.getString("state").trim();
                        String city = snapshot.getString("city").trim();
                        String category = snapshot.getString("category").trim();
                        String subCategory = snapshot.getString("subCategory").trim();
                        Log.d("xyz",task.getResult().getId());
                        FirebaseFirestore.getInstance().collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).collection("Vendor").document(task.getResult().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot snapshot2 = task.getResult();
                                Log.d("xyzsnp2", String.valueOf(snapshot2.get("uid")));
                                if (auth.getCurrentUser() != null && snapshot.exists() && snapshot.getString("verified").equals("True") && snapshot2.exists()) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                                }
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                                finish();
                            }
                        });
                    }else{
                        startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    }
                }
            });
        }else{
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        }
    }
}