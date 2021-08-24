package com.example.garagesale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.garagesale.models.Product;
import com.example.garagesale.models.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private final List<Product> mProductList = new ArrayList<>();
    private final List<Product> mTransactionList = new ArrayList<>();
    private final List<UserDetail> mUsersList = new ArrayList<>();
    private MaterialTextView mTvProducts;
    private MaterialTextView mTvCustomers;
    private MaterialTextView mTvTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mTvProducts = findViewById(R.id.tv_products);
        mTvCustomers = findViewById(R.id.tv_customers);
        mTvTransactions = findViewById(R.id.tv_transactions);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_products:
                        startActivity(new Intent(AdminActivity.this, AdminProductsActivity.class));
                        break;

                    case R.id.navigation_customers:
                        startActivity(new Intent(AdminActivity.this, CustomersActivity.class));
                        break;

                    case R.id.navigation_transactions:
                        startActivity(new Intent(AdminActivity.this, TransactionsActivity.class));
                        break;

                    case R.id.navigation_logout:
                        logoutAdmin();
                        break;
                }
                return true;
            }
        });

        getProductAndTransactionCount();
        getCustomerCount();
    }

    private void getProductAndTransactionCount() {
        FirebaseFirestore.getInstance().collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product products = document.toObject(Product.class);
                                if (products.getRequestApproved()){
                                    mTransactionList.add(products);
                                }
                                mProductList.add(products);
                            }
                            mTvProducts.setText("Total Products : " + mProductList.size());
                            mTvTransactions.setText("Total Transactions : " + mTransactionList.size());
                        } else {
                            Toast.makeText(AdminActivity.this, "Error getting products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCustomerCount() {
        FirebaseFirestore.getInstance().collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDetail userDetail = document.toObject(UserDetail.class);
                                mUsersList.add(userDetail);
                            }
                            mTvCustomers.setText("Total Customers : " + mUsersList.size());
                        } else {
                            Toast.makeText(AdminActivity.this, "Error getting products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void logoutAdmin() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you are sure want to logout ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(AdminActivity.this, HomeActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}