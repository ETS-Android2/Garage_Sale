package com.example.garagesale.mehul;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.garagesale.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        navigation();
    }
    private void navigation() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                else if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals("VIQBHRuVWCbrrO4ur0K43M5TyA22")){
                    Toast.makeText(SplashActivity.this, "Remove comment and add admin activity here", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 3000);
    }
}