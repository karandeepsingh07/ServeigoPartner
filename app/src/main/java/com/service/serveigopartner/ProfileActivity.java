package com.service.serveigopartner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements  ItemListDialogFragment.ItemClickListener{

    FirebaseFirestore firebaseFirestore;
    CircleImageView imageView;
    ImageView imageViewAadharFront,imageViewAadharBack,imageViewPan,imageViewInsurance,imageViewLicence;
    EditText editTextDummy,editTextAddress,editTextName,editTextEmail,editTextContact;
    Button buttonSubmit,buttonBack;
    String name,email,address,contact;
    LinearLayout linearLayout;
    boolean opened=false;
    String imageViewUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    String user,imageViewUrlIDProofInsurance;
    TextView textViewState,textViewCity;
    ProgressBar progressBar,progressBar2;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Profile");


        firebaseFirestore= FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getUid();
        editTextName=findViewById(R.id.editTextUserName);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextAddress=findViewById(R.id.editTextAddress);
        editTextContact=findViewById(R.id.editTextContact);
        linearLayout=findViewById(R.id.linearLayout);
        progressBar=findViewById(R.id.progressBar);
        progressBar2=findViewById(R.id.progressBar2);
        imageView=findViewById(R.id.imageViewUser);
        imageViewAadharFront=findViewById(R.id.imageViewAadharFront);
        imageViewAadharBack=findViewById(R.id.imageViewAadharBack);
        imageViewPan=findViewById(R.id.imageViewPan);
        imageViewLicence=findViewById(R.id.imageViewLicence);
        imageViewInsurance=findViewById(R.id.imageViewInsurance);
        buttonSubmit=findViewById(R.id.buttonSubmitDetails);
        textViewState=findViewById(R.id.textView_state);
        textViewCity=findViewById(R.id.textView_city);
        buttonBack=toolbar.findViewById(R.id.button_back);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opened=true;
                openFileChooser();
            }
        });
        imageViewInsurance.setOnClickListener(new View.OnClickListener() {
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

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                buttonSubmit.setEnabled(false);
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                address = editTextAddress.getText().toString();
                contact = editTextContact.getText().toString();
                if(opened) {
                    String filePathAndName = "Posts/" + "post_";
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                    ref.putFile(Uri.parse(imageViewUrl)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {

                                firebaseFirestore.collection("Vendor").document(user).update("altEmail", email,"name", name,"address",address,"vendorImage",downloadUri,"contact",contact,"state","vendorIDProofVehicleInsurance",imageViewUrlIDProofInsurance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ProfileActivity.this, "Details updated successfully.. ", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        buttonSubmit.setEnabled(true);
                                        //Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileActivity.this, "Details not updated.. ", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                buttonSubmit.setEnabled(true);
                                            }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    firebaseFirestore.collection("Vendor").document(user).update("altEmail", email,"name", name,"address",address,"contact",contact,"vendorIDProofVehicleInsurance",imageViewUrlIDProofInsurance).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, "Details updated successfully.. ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            buttonSubmit.setEnabled(true);
                            //Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            //startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Details not updated.. ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            buttonSubmit.setEnabled(true);
                        }
                    });
                }
            }
        });

        /*spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state=arrayListStateUid.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city=arrayListCityUid.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        getResult();
    }

    private void getResult(){
        firebaseFirestore.collection("Vendor").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    editTextName.setText(task.getResult().getString("name"));
                    editTextEmail.setText(task.getResult().getString("altEmail"));
                    editTextContact.setText(task.getResult().getString("contact"));
                    editTextAddress.setText(task.getResult().getString("address"));
                    final String state=task.getResult().getString("state");
                    final String city=task.getResult().getString("city");
                    Log.d("xyzCity",city);
                    firebaseFirestore.collection("Area").document(state).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            textViewState.setText(task.getResult().getString("Head"));
                            firebaseFirestore.collection("Area").document(state).collection("City").document(city).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    textViewCity.setText(task.getResult().getString("Head"));
                                    progressBar2.setVisibility(View.GONE);
                                    linearLayout.setVisibility(View.VISIBLE);
                                    buttonSubmit.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                    imageViewUrlIDProofInsurance=task.getResult().getString("vendorIDProofVehicleInsurance");
                    Picasso.get().load(task.getResult().getString("vendorImage"))
                            .placeholder(R.drawable.plumbing)
                            .error(R.drawable.plumbing).into(imageView);
                    Picasso.get().load(task.getResult().getString("vendorIDProof")).into(imageViewAadharFront);
                    Picasso.get().load(task.getResult().getString("vendorIDProofAadharBack")).into(imageViewAadharBack);
                    Picasso.get().load(task.getResult().getString("vendorIDProofPanCard")).into(imageViewPan);
                    Picasso.get().load(task.getResult().getString("vendorIDProofLicence")).into(imageViewLicence);
                    if(!imageViewUrlIDProofInsurance.equals(""))
                        Picasso.get().load(task.getResult().getString("vendorIDProofVehicleInsurance"))
                                .placeholder(R.drawable.ic_baseline_cloud_upload_24)
                                .error(R.drawable.ic_baseline_cloud_upload_24).into(imageViewInsurance);
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    public void uploadDataInsurance(byte[] bb){
        String filePathAndName = contact+"/IDProofInsurance";
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
                    buttonSubmit.setVisibility(View.VISIBLE);
                    //addDetail(name,email,address,contact,state,city,category,subCategory);
                }else{
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.VISIBLE);
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editLayout(View view){
        final ItemListDialogFragment bottomSheetDialogFragment = new ItemListDialogFragment();
        editTextDummy=findViewById(view.getId());
        Bundle bundle = new Bundle();
        bundle.putString("data",editTextDummy.getText().toString());
        bottomSheetDialogFragment.setArguments(bundle);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    public void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
            buttonSubmit.setVisibility(View.GONE);
            uploadDataInsurance(bb);
        }
    }
    @Override
    public void onItemClick(String item) {
        editTextDummy.setText(item);
    }
}