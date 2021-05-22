package com.service.serveigopartner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    Button buttonSignUp,buttonVerify;
    EditText editTextName,editTextEmail,editTextAddress;
    private String mVerificationId;
    private FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    Spinner spinnerState,spinnerCity,spinnerCategory,spinnerSubCategory;
    String uid,phone;
    Boolean opened=false;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUESTIDPROOF = 2;
    private Uri imageUri;
    private Uri imageUriIDPRoofAadharFront;
    private Uri imageUriIDPRoofAadharBack;
    private Uri imageUriIDPRoofPAN;
    private Uri imageUriIDPRoofInsurance;
    CircleImageView imageView;
    Button buttonIDProof,buttonIDProofAadharBack,buttonIDProofPAN,buttonIDProofInsurance,buttonIDProofLicence;
    String imageViewUrl="";
    TextView textViewIDProof,textViewIDProofAadharBack,textViewIDProofPAN,textViewIDProofInsurance,textViewIDProofLicence,textViewHint;
    String imageViewUrlIDProofAadharFront="";
    String imageViewUrlIDProofAadharBack="";
    String imageViewUrlIDProofPAN="";
    String imageViewUrlIDProofInsurance="";
    String imageViewUrlIDProofLicence="";
    int REQUEST_CODE=109;
    ProgressBar progressBar;
    ArrayList<String> arrayListState,arrayListCity,arrayListCategory,arrayListSubCategory;
    ArrayList<String> arrayListStateUid,arrayListCityUid,arrayListCategoryUid,arrayListSubCategoryUid;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String state,city,category,subCategory;
    boolean aadharFront,aadharBack,PanCard,insurance,licence,profile;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_REQUEST_AADHAR_FRONT = 1889;
    private static final int CAMERA_REQUEST_AADHAR_BACK = 1890;
    private static final int CAMERA_REQUEST_PAN = 1891;
    private static final int CAMERA_REQUEST_INSURANCE = 1892;
    private static final int CAMERA_REQUEST_LICENCE = 1893;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent=getIntent();
        final String contact=intent.getStringExtra("contact");
        phone=contact;
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        auth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        buttonSignUp=findViewById(R.id.button_signup);
        buttonIDProof=findViewById(R.id.button_idProof);
        buttonIDProofAadharBack=findViewById(R.id.button_idProofAadharBack);
        buttonIDProofPAN=findViewById(R.id.button_idProofPan);
        buttonIDProofLicence=findViewById(R.id.button_idProofLicence);
        buttonIDProofInsurance=findViewById(R.id.button_idProofInsurance);
        textViewIDProof=findViewById(R.id.textView_idProof);
        textViewIDProofAadharBack=findViewById(R.id.textView_idProofAadharBack);
        textViewIDProofPAN=findViewById(R.id.textView_idProofPan);
        textViewIDProofInsurance=findViewById(R.id.textView_idProofInsurance);
        textViewIDProofLicence=findViewById(R.id.textView_idProofLicence);
        textViewHint=findViewById(R.id.textViewProfile);
        imageView=findViewById(R.id.imageView);
        editTextName=findViewById(R.id.editText_name);
        editTextEmail=findViewById(R.id.editText_email);
        editTextAddress=findViewById(R.id.editText_address);
        spinnerState=findViewById(R.id.spinnerState);
        spinnerCity=findViewById(R.id.spinnerCity);
        spinnerCategory=findViewById(R.id.spinnerCategory);
        spinnerSubCategory=findViewById(R.id.spinnerSubCategory);
        progressBar=findViewById(R.id.progressBar);
        arrayListStateUid=new ArrayList<>();
        arrayListState=new ArrayList<>();
        arrayListCityUid=new ArrayList<>();
        arrayListCity=new ArrayList<>();
        arrayListCategoryUid=new ArrayList<>();
        arrayListCategory=new ArrayList<>();
        arrayListSubCategoryUid=new ArrayList<>();
        arrayListSubCategory=new ArrayList<>();

        //getResultCity("Chhattisgarh");
        getResultState();
        //editTextOTP=findViewById(R.id.editText_otp);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSubCategory.setVisibility(View.VISIBLE);
                category=arrayListCategoryUid.get(i);
                getResultSubCategory(state,city,category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCity.setVisibility(View.VISIBLE);
                state=arrayListStateUid.get(i);
                getResultCity(state);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCategory.setVisibility(View.VISIBLE);
                city=arrayListCityUid.get(i);
                getResultCategory(state,city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subCategory=arrayListSubCategoryUid.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        buttonIDProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_AADHAR_FRONT);
                }
            }
        });buttonIDProofAadharBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_AADHAR_BACK);
                }
            }
        });buttonIDProofPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_PAN);
                }
            }
        });buttonIDProofLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_LICENCE);
                }
            }
        });buttonIDProofInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_INSURANCE);
                }
            }
        });
       /* buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formCheck(editTextName,editTextContact,editTextEmail,editTextPassword,editTextConformPassword,editTextAddress,editTextOTP)) {
                    if (editTextContact.getText().toString().length() != 10) {
                        Toast.makeText(SignupActivity.this, "Contact Number must be of 10 digits", Toast.LENGTH_SHORT).show();
                    } else if (!editTextEmail.getText().toString().contains("@") || !editTextEmail.getText().toString().contains(".com")) {
                        editTextEmail.setError("Invalid Email address");
                        editTextEmail.requestFocus();
                        Toast.makeText(SignupActivity.this, "please, enter valid email address", Toast.LENGTH_SHORT).show();
                    } else if (editTextPassword.getText().toString().length() < 6) {
                        editTextPassword.setText("");
                        editTextPassword.setHint("Password");
                        editTextPassword.setError("Password must be atleast 6 character");
                        editTextPassword.requestFocus();
                        Toast.makeText(SignupActivity.this, "Password must be atleast 6 character", Toast.LENGTH_SHORT).show();
                    } else if (!editTextConformPassword.getText().toString().equals(editTextPassword.getText().toString())) {
                        Toast.makeText(SignupActivity.this, ""+editTextPassword.getText().toString(), Toast.LENGTH_SHORT).show();
                        editTextConformPassword.setText("");
                        editTextConformPassword.setHint("Confirm Password");
                        editTextConformPassword.setError("Password doesn't match");
                        editTextConformPassword.requestFocus();
                    } else if(imageUri==null){
                        Toast.makeText(SignupActivity.this, "Please select vendor image", Toast.LENGTH_SHORT).show();
                    }
                    else if(imageUriIDPRoof==null){
                        Toast.makeText(SignupActivity.this, "Please select vendor ID proof", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        buttonSignUp.setEnabled(true);
                        uploadData(editTextName.getText().toString().trim(),editTextEmail.getText().toString().trim(),editTextPassword.getText().toString().trim(),editTextAddress.getText().toString().trim(),editTextContact.getText().toString().trim(),spinnerState.getSelectedItem().toString(),spinnerCity.getSelectedItem().toString(),spinnerCategory.getSelectedItem().toString(),spinnerSubCategory.getSelectedItem().toString());
                    }
                }

            }
        });*/
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formCheck(editTextName,editTextEmail,editTextAddress)) {
                    if (!editTextEmail.getText().toString().contains("@") || !editTextEmail.getText().toString().contains(".com")) {
                        editTextEmail.setError("Invalid Email address");
                        editTextEmail.requestFocus();
                        Toast.makeText(SignupActivity.this, "please, enter valid email address", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        buttonSignUp.setEnabled(false);
                        addDetail(editTextName.getText().toString().trim(),editTextEmail.getText().toString().trim(),editTextAddress.getText().toString().trim(),contact,state,city,category,subCategory);
                    }
                }
            }
        });
    }

    public void addDetail(final String name, final String email, final String address, final String contact, final String state, final String city, final String category, final String subCategory){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token_id = instanceIdResult.getToken();

                uid=auth.getCurrentUser().getUid();
                final HashMap<String ,String> UserHashMap2 = new HashMap<>();
                UserHashMap2.put("vendorID",auth.getCurrentUser().getUid());
                final HashMap<String ,Object> UserHashMap = new HashMap<>();
                UserHashMap.put("altEmail",email);
                UserHashMap.put("name",name);
                UserHashMap.put("address",address);
                UserHashMap.put("contact",contact);
                UserHashMap.put("state",state);
                UserHashMap.put("city",city);
                UserHashMap.put("category",category);
                UserHashMap.put("paymentPending","0");
                UserHashMap.put("verified","False");
                UserHashMap.put("subCategory",subCategory);
                UserHashMap.put("visible",true);
                UserHashMap.put("token_id",token_id);
                UserHashMap.put("vendorImage",imageViewUrl);
                UserHashMap.put("uid",auth.getCurrentUser().getUid());
                UserHashMap.put("vendorID",auth.getCurrentUser().getUid());
                UserHashMap.put("vendorIDProof",imageViewUrlIDProofAadharFront);
                UserHashMap.put("vendorIDProofAadharBack",imageViewUrlIDProofAadharBack);
                UserHashMap.put("vendorIDProofPanCard",imageViewUrlIDProofPAN);
                UserHashMap.put("vendorIDProofVehicleInsurance",imageViewUrlIDProofInsurance);
                UserHashMap.put("vendorIDProofLicence",imageViewUrlIDProofLicence);

                firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).collection("Vendor").document(auth.getCurrentUser().getUid()).set(UserHashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseFirestore.collection("Vendor").document(auth.getCurrentUser().getUid()).set(UserHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    firebaseFirestore.collection("Vendor").document(auth.getCurrentUser().getUid()).update("paymentPending","0").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this, "Details updated Successfully, Wait for your ID verification..", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }else{
                                                progressBar.setVisibility(View.GONE);
                                                buttonSignUp.setEnabled(true);
                                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    buttonSignUp.setEnabled(true);
                                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });


        /*auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(SignupActivity.this, "Register Successfully.. Please check your email for verification", Toast.LENGTH_SHORT).show();
                                String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                UserHashMap2.put("vendorID",uid);

                                progressBar.setVisibility(View.GONE);
                                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                buttonSignUp.setEnabled(false);
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });*/
    }
    /*private void sendVerificationCode(String mobile) {
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

            Log.d("xyz","code1st"+phoneAuthCredential);
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextEmail.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            Log.d("xyz","inside oncodesent");
            mVerificationId = s;
            mResendToken = forceResendingToken;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(SignupActivity.this, SignupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

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
    }*/

    public boolean formCheck(EditText editTextName,EditText editTextEmail,EditText editTextAddress){
        if(!profile){
            textViewHint.setVisibility(View.VISIBLE);
            return false;
        }if(TextUtils.isEmpty(editTextName.getText().toString().trim())){
            editTextName.setText("");
            editTextName.setHint("Name");
            editTextName.setError("Field should not be empty");
            return false;
        }if(TextUtils.isEmpty(editTextEmail.getText().toString().trim())){
            editTextEmail.setText("");
            editTextEmail.setHint("Email");
            editTextEmail.setError("Field should not be empty");
            return false;
        }if(TextUtils.isEmpty(editTextAddress.getText().toString().trim())){
            editTextAddress.setText("");
            editTextAddress.setHint("Address");
            editTextAddress.setError("Field should not be empty");
            return false;
        }if(spinnerState.getSelectedItemPosition()==0){
            TextView errorText = (TextView)spinnerState.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select State");
            return false;
        }if(spinnerCity.getSelectedItemPosition()==0){
            TextView errorText = (TextView)spinnerCity.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Your City");
            return false;
        }if(spinnerCategory.getSelectedItemPosition()==0){
            TextView errorText = (TextView)spinnerCategory.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Category");
            return false;
        }if(spinnerSubCategory.getSelectedItemPosition()==0){
            TextView errorText = (TextView)spinnerSubCategory.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Sub Category");
            return false;
        }if(!aadharFront){
            textViewIDProof.setError("Upload Aadhaar Front");
            return false;
        }if(!aadharBack){
            textViewIDProof.setError("Upload Aadhaar Back");
            return false;
        }if(!PanCard){
            textViewIDProof.setError("Upload Pan Card");
            return false;
        }if(!licence){
            textViewIDProof.setError("Upload Licence");
            return false;
        }
        return true;
    }

    private void getResultState(){
        arrayListStateUid.clear();
        arrayListState.clear();
        arrayListStateUid.add("Select State");
        arrayListState.add("Select State");

        firebaseFirestore.collection("Area").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final HashSet<String> hs=new HashSet<>();
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        Log.d("xyz", snapshot.getId());
                        arrayListStateUid.add(snapshot.getId());
                        arrayListState.add(snapshot.get("Head").toString());
                    }
                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(SignupActivity.this,android.R.layout.simple_spinner_item,arrayListState);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerState.setAdapter(stateAdapter);
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
    private void getResultCity(final String state){
        arrayListCityUid.clear();
        arrayListCity.clear();
        arrayListCityUid.add("Select City");
        arrayListCity.add("Select City");
        firebaseFirestore.collection("Area").document(state).collection("City").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final HashSet<String> hs=new HashSet<>();
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        Log.d("xyz", snapshot.getId());
                        arrayListCityUid.add(snapshot.getId());
                        arrayListCity.add(snapshot.get("Head").toString());
                    }
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(SignupActivity.this,android.R.layout.simple_spinner_item,arrayListCity);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(cityAdapter);
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
    private void getResultCategory(final String state, final String city){
        arrayListCategoryUid.clear();
        arrayListCategory.clear();
        arrayListCategoryUid.add("Select Category");
        arrayListCategory.add("Select Category");

        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final HashSet<String> hs=new HashSet<>();
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        Log.d("xyz", snapshot.getId());
                        arrayListCategoryUid.add(snapshot.getId());
                        arrayListCategory.add(snapshot.get("Head").toString());
                    }
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(SignupActivity.this,android.R.layout.simple_spinner_item,arrayListCategory);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(categoryAdapter);
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
    private void getResultSubCategory(final String state, final String city, final String category){
        arrayListSubCategoryUid.clear();
        arrayListSubCategory.clear();
        arrayListSubCategoryUid.add("Select Sub Category");
        arrayListSubCategory.add("Select Sub Category");

        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final HashSet<String> hs=new HashSet<>();
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        Log.d("xyz", snapshot.getId());
                        arrayListSubCategoryUid.add(snapshot.getId());
                        arrayListSubCategory.add(snapshot.get("Head").toString());
                    }
                    ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<>(SignupActivity.this,android.R.layout.simple_spinner_item,arrayListSubCategory);
                    subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubCategory.setAdapter(subCategoryAdapter);
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void uploadData(byte[] bb){
        String filePathAndName = phone+"/Vendor";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded to firebase storage. get uri
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString();
                imageViewUrl=downloadUri;

                if (uriTask.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    profile=true;
                }else{
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }public void uploadDataAadharFront(byte[] bb){
        String filePathAndName = phone+"/IDProofAadharFront";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded to firebase storage. get uri
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUriIDProof = uriTask.getResult().toString();
                imageViewUrlIDProofAadharFront=downloadUriIDProof;

                if (uriTask.isSuccessful()) {
                    textViewIDProof.setText("Aadhaar Front Uploaded");
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    aadharFront=true;
                    //addDetail(name,email,address,contact,state,city,category,subCategory);
                }else{
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }public void uploadDataAadharBack(byte[] bb){
        String filePathAndName = phone+"/IDProofAadharBack";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded to firebase storage. get uri
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUriIDProof = uriTask.getResult().toString();
                imageViewUrlIDProofAadharBack=downloadUriIDProof;

                if (uriTask.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    textViewIDProofAadharBack.setText("Aadhaar Back Uploaded");
                    aadharBack=true;
                    //addDetail(name,email,address,contact,state,city,category,subCategory);
                }else{
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }public void uploadDataPAN(byte[] bb){
        String filePathAndName = phone+"/IDProofPAN";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded to firebase storage. get uri
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUriIDProof = uriTask.getResult().toString();
                imageViewUrlIDProofPAN=downloadUriIDProof;

                if (uriTask.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    textViewIDProofPAN.setText("PAN Uploaded");
                    PanCard=true;
                    //addDetail(name,email,address,contact,state,city,category,subCategory);
                }else{
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }public void uploadDataLicence(byte[] bb){
        String filePathAndName = phone+"/IDProofLicence";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded to firebase storage. get uri
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUriIDProof = uriTask.getResult().toString();
                imageViewUrlIDProofLicence=downloadUriIDProof;

                if (uriTask.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    textViewIDProofLicence.setText("Driving Licence Uploaded");
                    licence=true;
                    //addDetail(name,email,address,contact,state,city,category,subCategory);
                }else{
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }public void uploadDataInsurance(byte[] bb){
        String filePathAndName = phone+"/IDProofInsurance";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded to firebase storage. get uri
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUriIDProof = uriTask.getResult().toString();
                imageViewUrlIDProofInsurance=downloadUriIDProof;

                if (uriTask.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    textViewIDProofInsurance.setText("Insurance Uploaded");
                    insurance=true;
                    //addDetail(name,email,address,contact,state,city,category,subCategory);
                }else{
                    progressBar.setVisibility(View.GONE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }public void openFileChooserIDProof() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, PICK_IMAGE_REQUESTIDPROOF);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            imageViewUrl=imageUri.toString();

            Picasso.get().load(imageUri).into(imageView);

        }if (requestCode == PICK_IMAGE_REQUESTIDPROOF && resultCode == RESULT_OK && data != null && data.getData() != null) {

            /*imageUriIDPRoof = data.getData();
            imageViewUrlIDProof=imageUriIDPRoof.toString();
            textViewIDProof.setText(imageViewUrlIDProof);*/
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = photo;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] bb= baos.toByteArray();
            imageViewUrl = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            //imageViewUrl=data.getExtras().get("data").toString();
            imageView.setImageBitmap(photo);
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.GONE);
            uploadData(bb);
        }if (requestCode == CAMERA_REQUEST_AADHAR_FRONT && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = photo;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] bb= baos.toByteArray();
            imageViewUrlIDProofAadharFront=data.getExtras().get("data").toString();
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.GONE);
            uploadDataAadharFront(bb);
        }if (requestCode == CAMERA_REQUEST_AADHAR_BACK && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = photo;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] bb= baos.toByteArray();
            imageViewUrlIDProofAadharBack=data.getExtras().get("data").toString();
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.GONE);
            uploadDataAadharBack(bb);
        }if (requestCode == CAMERA_REQUEST_PAN && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = photo;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] bb= baos.toByteArray();
            imageViewUrlIDProofPAN=data.getExtras().get("data").toString();
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.GONE);
            uploadDataPAN(bb);
        }if (requestCode == CAMERA_REQUEST_LICENCE && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = photo;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] bb= baos.toByteArray();
            imageViewUrlIDProofLicence=data.getExtras().get("data").toString();
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.GONE);
            uploadDataLicence(bb);
        }if (requestCode == CAMERA_REQUEST_INSURANCE && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = photo;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] bb= baos.toByteArray();
            imageViewUrlIDProofInsurance=data.getExtras().get("data").toString();
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.GONE);
            uploadDataInsurance(bb);
        }
    }
}