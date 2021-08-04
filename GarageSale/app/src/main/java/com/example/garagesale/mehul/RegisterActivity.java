package com.example.garagesale.mehul;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garagesale.models.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.garagesale.R;

public class RegisterActivity extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private MaterialButton mSignUp;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private TextView mAlreadySignIn;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mSignUp = findViewById(R.id.btn_login);
        mFirstName = findViewById(R.id.et_first_name);
        mLastName = findViewById(R.id.et_last_name);
        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_pass);
        mConfirmPassword = findViewById(R.id.et_confirm_password);
        mSignUp = findViewById(R.id.btn_sign_up);
        mAlreadySignIn = findViewById(R.id.tv_already_sign_in);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Registering User");
        mDialog.setCancelable(false);

        alreadyHaveAccount();
        register();
    }

    private void alreadyHaveAccount() {
        mAlreadySignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void register() {

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();

                final String firstName = mFirstName.getText().toString().trim();
                final String lastName = mLastName.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String confirmPassword = mConfirmPassword.getText().toString().trim();

                if (firstName.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "First name is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (lastName.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Last name is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (email.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (confirmPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Confirm password is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (!confirmPassword.equals(password)){
                    Toast.makeText(RegisterActivity.this, "Confirm password or Create password isn't matched", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    String userUid = task.getResult().getUser().getUid();
                                    final UserDetail userDetail = new UserDetail(firstName, lastName, email, userUid);

                                    FirebaseFirestore.getInstance().collection("Users").document(userUid).set(userDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            mDialog.cancel();
                                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                            finish();
                                            Toast.makeText(RegisterActivity.this, "Registered Success", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mDialog.cancel();
                                            Toast.makeText(RegisterActivity.this, "Fail to register user \n" + e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    mDialog.cancel();
                                    Log.e("createUserWithEmail", task.getException().getMessage());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}