package com.example.garagesale.mehul;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import com.example.garagesale.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText mEmail;
    private MaterialButton mSubmit;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mEmail = findViewById(R.id.et_email);
        mSubmit = findViewById(R.id.btn_submit);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Sending email");
        mDialog.setCancelable(false);

        submit();
    }

    private void submit() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();

                String email = mEmail.getText().toString().trim();

                if (email.isEmpty()){
                    Toast.makeText(ForgetPasswordActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mDialog.cancel();
                            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(ForgetPasswordActivity.this, "Reset link sent on email", Toast.LENGTH_SHORT).show();
                        } else {
                            mDialog.cancel();
                            Toast.makeText(ForgetPasswordActivity.this, "Email sending failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}