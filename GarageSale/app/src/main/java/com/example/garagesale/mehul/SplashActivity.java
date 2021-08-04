package com.example.garagesale.mehul;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.garagesale.R;
import android.content.Intent;
import android.os.Handler;

import com.example.garagesale.harsh.HomeActivity;
import com.example.garagesale.krutarth.AdminActivity;
import com.google.firebase.auth.FirebaseAuth;
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        navigation();
    }
    private void navigation() {

        //Handler will handle small background tasks
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                else if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals("9bbUMlK7k3OQxP1OVfv83Nj5plR2")){
                    startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 3000);
    }
}