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
    private EditText mEtSearch;
    private MaterialButton mBtnSearch;
    private MaterialButton mBtnReset;
    private AllProductAdapter mAllProductAdapter;
    private final List<Product> mProductList = new ArrayList<>();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        mProductRecycler = findViewById(R.id.rcv_product);
        mEtSearch = findViewById(R.id.et_search);
        mBtnSearch = findViewById(R.id.btn_search);
        mBtnReset = findViewById(R.id.btn_reset);
        getAllProducts();
        searchButton();
        resetButton();
    }
    private void searchButton() {
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchedText = mEtSearch.getText().toString().trim();
                if (searchedText.isEmpty()){
                    Toast.makeText(AllProductActivity.this, "Please enter search text", Toast.LENGTH_SHORT).show();
                    return;
                }
                final List<Product> mSearchedProductList = new ArrayList<>();
                for (int i = 0; i < mProductList.size(); i++){
                    if (searchedText.equals(mProductList.get(i).getProductName())){
                        mSearchedProductList.add(mProductList.get(i));
                    }
                }
                mAllProductAdapter.addNewList(mSearchedProductList);
            }
        });
    }
    private void resetButton() {
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.setText("");
                getAllProducts();
            }
        });
    }
    private void getAllProducts() {
        mProductList.clear();

        FirebaseFirestore.getInstance().collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product products = document.toObject(Product.class);
                                if (currentUser == null) {
                                    makeList(products);
                                } else if (!currentUser.getUid().equals(products.getProductOwnerUid())){
                                    makeList(products);
                                }
                            }
                            mAllProductAdapter = new AllProductAdapter(mProductList, AllProductActivity.this);
                            mAllProductAdapter = new AllProductAdapter(mProductList);
                            mProductRecycler.setAdapter(mAllProductAdapter);

                        } else {
                            Toast.makeText(AllProductActivity.this, "Error getting products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void makeList(Product product){
        if (product.getProductDisplay()){
            mProductList.add(product);
        }
    }
}