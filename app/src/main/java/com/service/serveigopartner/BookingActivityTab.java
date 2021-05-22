package com.service.serveigopartner;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.service.serveigopartner.ui.main.SectionsPagerAdapter;

public class BookingActivityTab extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    private TabAdapter adaptertab;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_tab);;

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

        buttonBack=toolbar.findViewById(R.id.button_back);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        FragmentManager fragManager = getSupportFragmentManager();
        adaptertab = new TabAdapter(fragManager,1);
        adaptertab.addFragment(new OpenFragment(), "Pending");
        adaptertab.addFragment(new AcceptedFragment(), "Due Payment");
        adaptertab.addFragment(new CompletedFragment(), "Closed");
        adaptertab.addFragment(new RejectedFragment(), "Rejected");
        viewPager.setAdapter(adaptertab);
        tabLayout.setupWithViewPager(viewPager);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }
}