package com.service.serveigopartner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class DialogActivity extends Dialog implements
        android.view.View.OnClickListener {


    public Activity c;
    public Dialog d;
    public Button yes, no;

    public DialogActivity(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.button_accept);
        no = (Button) findViewById(R.id.button_reject);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_accept:
                c.finish();
                break;
            case R.id.button_reject:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
