package com.example.garagesale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.garagesale.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;


public class AddProductActivity extends AppCompatActivity {

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductDescription;
    private MaterialButton mSubmit;
    private double lat = 0.0;
    private double lng = 0.0;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mProductName = findViewById(R.id.et_p_name);
        mProductPrice = findViewById(R.id.et_p_price);
        mProductDescription = findViewById(R.id.et_p_description);
        mSubmit = findViewById(R.id.btn_submit);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Adding Product");
        mDialog.setCancelable(false);

        getPreviousScreenCurrentLocation();
        submitProductData();
    }

    private void getPreviousScreenCurrentLocation(){
        lat = getIntent().getExtras().getDouble("lat");
        lng = getIntent().getExtras().getDouble("lng");
    }

    private void submitProductData() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();

                String pName = mProductName.getText().toString().trim();
                double pPrice = Double.parseDouble(mProductPrice.getText().toString().trim());
                String pDescription = mProductDescription.getText().toString().trim();
                String pKey = FirebaseFirestore.getInstance().collection("Products").document().getId();
                String pUid = FirebaseAuth.getInstance().getUid();

                if (pName.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Product name is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (pPrice == 0.0) {
                    Toast.makeText(AddProductActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (pDescription.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Product description is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }

                Product product = new Product(pKey, pUid, pName, pPrice, pDescription, lat, lng);

                FirebaseFirestore.getInstance().collection("Products").document(pKey).set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mDialog.cancel();
                        finish();
                        Toast.makeText(AddProductActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        mDialog.cancel();
                        Toast.makeText(AddProductActivity.this, "Product adding operation failed.. please try again", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}