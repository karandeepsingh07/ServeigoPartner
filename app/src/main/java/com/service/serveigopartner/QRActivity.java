package com.service.serveigopartner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class QRActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    ImageView imageViewQR;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("QR Code");

        imageViewQR= findViewById(R.id.imageView_qr);
        progressBar= findViewById(R.id.progressBar);
        linearLayout= findViewById(R.id.linearLayout_payment);
        firebaseFirestore = FirebaseFirestore.getInstance();
        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getResult();
    }

    private void getResult() {
        firebaseFirestore.collection("Admin").document("QR").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String url= task.getResult().get("url").toString();
                Picasso.get().load(url).into(imageViewQR);
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}