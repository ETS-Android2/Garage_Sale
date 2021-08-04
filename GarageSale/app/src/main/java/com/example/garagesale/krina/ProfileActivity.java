package com.example.garagesale.krina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.garagesale.R;
import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.garagesale.models.UserDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPhoneNo;
    private EditText mAddress;
    private MaterialButton mSubmit;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mSubmit = findViewById(R.id.btn_submit);
        mEmail = findViewById(R.id.et_email);
        mPhoneNo = findViewById(R.id.et_phone_no);
        mAddress = findViewById(R.id.et_address);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Updating User");
        mDialog.setCancelable(false);

        getCurrentUserDataFromFirebase();
        submitUserDataToFirebase();
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

                Map<String,Object> updatedProfileData = new HashMap<>();
                updatedProfileData.put("phoneNumber", phoneNo);
                updatedProfileData.put("address", address);

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
                } else {
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}