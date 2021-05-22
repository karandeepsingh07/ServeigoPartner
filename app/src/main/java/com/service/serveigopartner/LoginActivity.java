package com.service.serveigopartner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername,editTextOTP;
    Button buttonLogin,buttonVerify;
    ProgressBar progressBar;
    FirebaseAuth auth;
    private String mVerificationId;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername=findViewById(R.id.editText_contact);
        editTextOTP=findViewById(R.id.edit_text_otp);
        buttonLogin=findViewById(R.id.button_login);
        buttonVerify=findViewById(R.id.button_verify);
        progressBar=findViewById(R.id.progressBar);
        auth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        /*buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formCheck(editTextUsername,editTextPassword)) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent1,0);
            }
        }

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);
                sendVerificationCode(editTextUsername.getText().toString());
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);
                if(formCheck(editTextUsername,editTextOTP)) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonLogin.setEnabled(false);
                    verifyVerificationCode(editTextOTP.getText().toString());
                }
            }
        });
    }
    public boolean formCheck(EditText editTextUsername,EditText editTextContact) {
        if (TextUtils.isEmpty(editTextUsername.getText().toString())) {
            editTextUsername.setText("");
            editTextUsername.setHint("Username");
            editTextUsername.setError("Field should not be empty");
            return false;
        }
        if (TextUtils.isEmpty(editTextOTP.getText().toString())) {
            editTextOTP.setText("");
            editTextOTP.setHint("Password");
            editTextOTP.setError("Field should not be empty");
            return false;
        }
        return true;
    }

    private void sendVerificationCode(String mobile) {
        Log.d("xyz","inside 1st method");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            Log.d("xyz","code1st"+phoneAuthCredential);
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextOTP.setText("");
                editTextOTP.setText(code);
                //verifying the code
                progressBar.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            buttonLogin.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            Log.d("xyz","inside oncodesent");
            //storing the verification id that is sent to the user
            mVerificationId = s;
            progressBar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        Log.d("xyz","code"+code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String uid= auth.getUid();
                            firebaseFirestore.collection("Vendor").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    final DocumentSnapshot document = task.getResult();
                                    if(document.exists()) {
                                        String state = document.getString("state").trim();
                                        String city = document.getString("city").trim();
                                        String category = document.getString("category").trim();
                                        String subCategory = document.getString("subCategory").trim();
                                        FirebaseFirestore.getInstance().collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).collection("Vendor").document(task.getResult().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot snapshot2 = task.getResult();
                                                Log.d("xyzsnp2", String.valueOf(snapshot2.exists()));
                                                if (!document.exists()) {
                                                    Log.d("xyz","here1");
                                                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                                    intent.putExtra("contact", editTextUsername.getText().toString());
                                                    startActivity(intent);
                                                    finish();
                                                } else if (!snapshot2.exists()) {
                                                    Log.d("xyz","here2");
                                                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                                    intent.putExtra("contact", editTextUsername.getText().toString());
                                                    startActivity(intent);
                                                    finish();
                                                } else if (document.getString("verified").equals("True")) {
                                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                        @Override
                                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                                            String token_id = instanceIdResult.getToken();
                                                            firebaseFirestore.collection("Vendor").document(uid).update("token_id",token_id);
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(LoginActivity.this, "User not Verified", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        });
                                        Log.d("xyz", "snapshot" + document);
                                    }else{
                                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                        intent.putExtra("contact", editTextUsername.getText().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                            //progressBar.setVisibility(View.GONE);
                            //verification successful we will start the profile activity
                        } else {
                            buttonLogin.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
}