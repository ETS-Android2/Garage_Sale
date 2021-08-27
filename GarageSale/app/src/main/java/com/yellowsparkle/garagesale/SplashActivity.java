package com.yellowsparkle.garagesale;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
public class SplashActivity extends AppCompatActivity {

    public String adminStaticUID = "jtd43rOGgVTBEeMX4AHQPIcngaS2";

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
                else if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(adminStaticUID)){
                    startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 2000);
    }
}