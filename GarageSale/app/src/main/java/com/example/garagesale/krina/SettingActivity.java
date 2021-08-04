package com.example.garagesale.krina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.garagesale.R;

import android.content.DialogInterface;
import android.content.Intent;

import android.view.View;
import android.widget.Toast;

import com.example.garagesale.mehul.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;


public class SettingActivity extends AppCompatActivity {

    private MaterialButton mBtnManageProfile;
    private MaterialButton mBtnManageProduct;
    private MaterialButton mBtnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mBtnManageProfile = findViewById(R.id.btn_manage_profile);
        mBtnManageProduct = findViewById(R.id.btn_manage_product);
        mBtnLogout = findViewById(R.id.btn_logout);

        manageProfile();
        manageProduct();
        logoutUser();
    }

    private void logoutUser() {
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(v.getContext())
                        .setTitle("Logout")
                        .setMessage("Are you are sure want to logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                finish();
                                Toast.makeText(SettingActivity.this, "Logout successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void manageProduct() {
        mBtnManageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ManageProductActivity.class));
            }
        });
    }

    private void manageProfile() {
        mBtnManageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
            }
        });
    }
}