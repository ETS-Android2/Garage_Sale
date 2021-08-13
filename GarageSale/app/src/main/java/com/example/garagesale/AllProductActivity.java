package com.example.garagesale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

public class AllProductActivity extends AppCompatActivity {

    private RecyclerView mProductRecycler;
    private AllProductAdapter mAllProductAdapter;
    private final List<Product> mProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        mProductRecycler = findViewById(R.id.rcv_product);

        FirebaseFirestore.getInstance().collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mProductList.add(document.toObject(Product.class));
                            }
                            mAllProductAdapter = new AllProductAdapter(mProductList);
                            mProductRecycler.setAdapter(mAllProductAdapter);

                        } else {
                            Toast.makeText(AllProductActivity.this, "Error getting products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}