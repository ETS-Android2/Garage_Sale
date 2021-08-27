package com.yellowsparkle.garagesale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yellowsparkle.garagesale.models.UserDetail;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ProfileActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPhoneNo;
    private EditText mAddress;
    private MaterialButton mSubmit;
    private MaterialTextView mTvChooseImage;
    private ImageView mIvProfile;

    private ProgressDialog mDialog;

    private Uri uri;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), imageUri -> {
                if (imageUri != null) {
                    uri = imageUri;
                    Glide.with(ProfileActivity.this).load(uri).circleCrop().into(mIvProfile);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mSubmit = findViewById(R.id.btn_submit);
        mEmail = findViewById(R.id.et_email);
        mPhoneNo = findViewById(R.id.et_phone_no);
        mAddress = findViewById(R.id.et_address);

        mTvChooseImage = findViewById(R.id.tv_choose_image);
        mIvProfile = findViewById(R.id.iv_profile);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Updating User");
        mDialog.setCancelable(false);

        getCurrentUserDataFromFirebase();
        submitUserDataToFirebase();
        pickImage();
    }

    private void pickImage() {
        mTvChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch("image/*");
            }
        });
        mIvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch("image/*");
            }
        });
    }

    private void submitUserDataToFirebase() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();

                String phoneNo = mPhoneNo.getText().toString().trim();
                String address = mAddress.getText().toString().trim();

                if (phoneNo.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Phone number is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (address.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "address is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }

                if (10 > phoneNo.length()){
                    Toast.makeText(ProfileActivity.this, "Invalid phone number is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (uri == null) {
                    mDialog.cancel();
                    uploadUserData(phoneNo, address,"");
                } else {
                    mDialog.setMessage("Uploading Profile");
                    final StorageReference ref = FirebaseStorage.getInstance().getReference().child("UserProfile/" + System.currentTimeMillis());
                    UploadTask uploadTask = ref.putFile(uri);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            mDialog.setMessage("Updating data");
                            if (task.isSuccessful()) {
                                return ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uploadUserData(phoneNo, address, uri.toString());
                                    }
                                });
                            } else {
                                uploadUserData(phoneNo, address, "");
                                throw Objects.requireNonNull(task.getException());
                            }
                        }
                    });
                }
            }
        });
    }

    private void uploadUserData(String phoneNo, String address, String profileImage) {
        Map<String, Object> updatedProfileData = new HashMap<>();
        updatedProfileData.put("phoneNumber", phoneNo);
        updatedProfileData.put("address", address);
        updatedProfileData.put("userImage", profileImage);
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(updatedProfileData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mDialog.cancel();
                finish();
                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                mDialog.cancel();
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getCurrentUserDataFromFirebase() {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDetail userDetail = documentSnapshot.toObject(UserDetail.class);
                if (userDetail != null){
                    mEmail.setText(userDetail.getEmail());
                    mPhoneNo.setText(userDetail.getPhoneNumber());
                    mAddress.setText(userDetail.getAddress());
                    if (userDetail.getUserImage() != null){
                        Glide.with(ProfileActivity.this).load(userDetail.getUserImage()).circleCrop().into(mIvProfile);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}