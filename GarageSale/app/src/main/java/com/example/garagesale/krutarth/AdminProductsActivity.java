package com.example.garagesale.krutarth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.garagesale.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.example.garagesale.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminProductsActivity extends AppCompatActivity {

    private RecyclerView mAdminProductRecycler;
    private AdminProductAdapter mAdminProductAdapter;
    private final List<Product> mAdminProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products);

        mAdminProductRecycler = findViewById(R.id.rcv_product);

        getAdminProductList();
    }

    private void getAdminProductList() {
        FirebaseFirestore.getInstance().collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mAdminProductList.add(document.toObject(Product.class));
                            }
                            mAdminProductAdapter = new AdminProductAdapter(mAdminProductList);
                            mAdminProductRecycler.setAdapter(mAdminProductAdapter);

                        } else {
                            Toast.makeText(AdminProductsActivity.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}