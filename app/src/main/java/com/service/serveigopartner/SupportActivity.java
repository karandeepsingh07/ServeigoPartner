package com.service.serveigopartner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class SupportActivity extends AppCompatActivity {

    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;
    String website,instagram,twitter,facebook,email;
    TextView textViewEmail,textViewWebsite;
    ImageView imageViewInstagram,imageViewTwitter,imageViewFacebook;
    RelativeLayout relativeLayout;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Support");


        firebaseFirestore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar);
        textViewEmail=findViewById(R.id.textView_email);
        textViewWebsite=findViewById(R.id.textView_website);
        imageViewFacebook=findViewById(R.id.imageView_facebook);
        imageViewInstagram=findViewById(R.id.imageView_instagram);
        imageViewTwitter=findViewById(R.id.imageView_twitter);
        relativeLayout=findViewById(R.id.relativeLayout);
        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        textViewWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(browserIntent);
            }
        });
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mailClient = new Intent(Intent.ACTION_SEND);
                mailClient.setData(Uri.parse("email"));
                String[] s={email};
                mailClient.putExtra(Intent.EXTRA_EMAIL,s);
                mailClient.setType("message/rfc822");
                startActivity(mailClient);
            }
        });
        imageViewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitter)));
            }
        });
        imageViewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(instagram)));
            }
        });
        imageViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebook)));
            }
        });

        getResult();
    }

    private void getResult() {
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
        firebaseFirestore.collection("Admin").document("Contact").collection("List").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        if (snapshot.getId().equals("g1MjzoEmQ1mvS5KO80Lk")) {
                            email = snapshot.get("url").toString();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                textViewEmail.setText(Html.fromHtml(email, Html.FROM_HTML_MODE_COMPACT));
                            } else{
                                textViewEmail.setText(email);
                            }
                        } else if (snapshot.getId().equals("zmbVpw3sVTNlHczsT7uc")) {
                            website = snapshot.get("url").toString();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                textViewWebsite.setText(Html.fromHtml(website, Html.FROM_HTML_MODE_COMPACT));
                            } else{
                                textViewWebsite.setText(website);
                            }
                        } else if (snapshot.getId().equals("UviH8qu39FIuomGjyJlN")) {
                            instagram = snapshot.get("url").toString();
                        } else if (snapshot.getId().equals("e87XcwUrV526HnE7EmQ1")) {
                            twitter = snapshot.get("url").toString();
                        } else if (snapshot.getId().equals("Jc3NgoHSn8eYyeFIFrc1")) {
                            facebook = snapshot.get("url").toString();
                        }
                        progressBar.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                }
        });
    }
}