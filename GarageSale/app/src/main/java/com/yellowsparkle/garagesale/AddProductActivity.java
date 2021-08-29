package com.yellowsparkle.garagesale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yellowsparkle.garagesale.models.Product;
import com.yellowsparkle.garagesale.models.UserDetail;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class AddProductActivity extends AppCompatActivity {

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductDescription;
    private MaterialButton mSubmit;
    private MaterialTextView mTvChooseImage;
    private ImageView mIvProduct;
    private double lat = 0.0;
    private double lng = 0.0;

    private ProgressDialog mDialog;
    private Uri uri;
    private String ownerName;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), imageUri -> {
                if (imageUri != null) {
                    uri = imageUri;
                    mIvProduct.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mProductName = findViewById(R.id.et_p_name);
        mProductPrice = findViewById(R.id.et_p_price);
        mProductDescription = findViewById(R.id.et_p_description);
        mSubmit = findViewById(R.id.btn_submit);

        mTvChooseImage = findViewById(R.id.tv_choose_image);
        mIvProduct = findViewById(R.id.iv_product);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Adding Product");
        mDialog.setCancelable(false);

        getUserData();
        getPreviousScreenCurrentLocation();
        submitProductData();
        pickImage();
    }

    private void getUserData() {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDetail userDetail = documentSnapshot.toObject(UserDetail.class);
                if (userDetail != null) {
                    ownerName = userDetail.getFirstName();
                } else {
                    ownerName = "No owner name";
                }
            }
        });
    }
    private void pickImage() {
        mTvChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch("image/*");
            }
        });
        mIvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch("image/*");
            }
        });
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
                if (pPrice <= 0.0) {
                    Toast.makeText(AddProductActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                if (pDescription.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Product description is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }

                if (uri == null){
                    Toast.makeText(AddProductActivity.this, "Product image is empty", Toast.LENGTH_SHORT).show();
                    mDialog.cancel();
                    return;
                }
                mDialog.setMessage("Uploading product image");
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("UserProfile/" + System.currentTimeMillis());
                UploadTask uploadTask = ref.putFile(uri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        mDialog.setMessage("Adding product data");
                        if (task.isSuccessful()) {
                            return ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Product product = new Product(pKey, pUid, pName, pPrice, pDescription, lat, lng, false, false, uri.toString(),"","", ownerName, true);
                                    uploadUserData(product);
                                }
                            });
                        } else {
                            throw Objects.requireNonNull(task.getException());
                        }
                    }
                });
            }
        });
    }

    private void uploadUserData(Product product) {
        FirebaseFirestore.getInstance().collection("Products").document(product.getProductKey()).set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mDialog.cancel();
                startActivity(new Intent(AddProductActivity.this, HomeActivity.class));
                finish();
                Toast.makeText(AddProductActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                mDialog.cancel();
                Toast.makeText(AddProductActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddProductActivity.this, HomeActivity.class));
        finish();
    }
}