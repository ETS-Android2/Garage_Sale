package com.yellowsparkle.garagesale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;


import com.yellowsparkle.garagesale.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import com.yellowsparkle.garagesale.R;


public class TransactionsActivity extends AppCompatActivity {

    private RecyclerView mTransactionsRecycler;
    private final List<Product> mListOfTransactions = new ArrayList<>();
    private TransactionAdapter mTransactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        mTransactionsRecycler = findViewById(R.id.rcv_transactions);

        getAllTransactions();
    }

    private void getAllTransactions() {
        FirebaseFirestore.getInstance().collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product products = document.toObject(Product.class);
                                if (products.getRequestApproved()){
                                    mListOfTransactions.add(products);
                                }
                            }
                            mTransactionAdapter = new TransactionAdapter(mListOfTransactions);
                            mTransactionsRecycler.setAdapter(mTransactionAdapter);

                        } else {
                            Toast.makeText(TransactionsActivity.this, "Error getting products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}