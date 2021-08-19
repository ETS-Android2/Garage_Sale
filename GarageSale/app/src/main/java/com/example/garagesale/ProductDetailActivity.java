package com.example.garagesale;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.garagesale.models.Product;
import com.example.garagesale.models.UserDetail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class ProductDetailActivity extends AppCompatActivity {

    private MaterialTextView mProductName;
    private MaterialTextView mProductPrice;
    private MaterialTextView mProductDescription;
    private ImageView mProductImage;
    private MaterialButton mReserve;
    private MaterialTextView mSellerName;
    private MaterialTextView mSellerAddress;
    private MaterialTextView mSellerMo;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mProductName = findViewById(R.id.tv_product_name);
        mProductPrice = findViewById(R.id.tv_product_price);
        mProductDescription = findViewById(R.id.tv_product_desc);
        mProductImage = findViewById(R.id.iv_product);
        mReserve = findViewById(R.id.btn_reserve);
        mSellerName = findViewById(R.id.tv_seller_name);
        mSellerAddress = findViewById(R.id.tv_seller_address);
        mSellerMo = findViewById(R.id.tv_seller_mobile);

        getPreviousScreenProductData();
        reserveButton();
    }

    private void reserveButton() {
        mReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialog viewDialog = new ViewDialog();
                viewDialog.showDialog(ProductDetailActivity.this);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getPreviousScreenProductData() {
        product = (Product) getIntent().getSerializableExtra("Product");

        mProductName.setText(product.getProductName());
        mProductPrice.setText("Product Price : " + product.getProductPrice());
        mProductDescription.setText("Product description : " + product.getProductDescription());
        Glide.with(this).load(product.getProductImage()).into(mProductImage);

        FirebaseFirestore.getInstance().collection("Users").document(product.getProductOwnerUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDetail userDetail = documentSnapshot.toObject(UserDetail.class);
                if (userDetail != null){
                    mSellerName.setText("Seller Name : " + userDetail.getFirstName() + " " + userDetail.getLastName());
                    mSellerAddress.setText("Seller address : " + userDetail.getAddress());
                    mSellerMo.setText("Seller phone no. : " + userDetail.getPhoneNumber());
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Seller not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ViewDialog {

        EditText userName;
        EditText userPhoneNo;
        private ProgressDialog mDialog;

        public void showDialog(Activity activity){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.add_user, null);
            builder.setView(dialogView);

            mDialog = new ProgressDialog(activity);
            mDialog.setMessage("Reserving product");
            mDialog.setCancelable(false);

            userName = dialogView.findViewById(R.id.et_name);
            userPhoneNo = dialogView.findViewById(R.id.et_phone_no);

            MaterialButton dialogBtn_cancel = dialogView.findViewById(R.id.btn_request_reserve);
            dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.show();

                    String name = userName.getText().toString().trim();
                    String phone = userPhoneNo.getText().toString().trim();

                    if (name.isEmpty()){
                        Toast.makeText(ProductDetailActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
                        mDialog.cancel();
                        return;
                    }
                    if (phone.isEmpty()){
                        Toast.makeText(ProductDetailActivity.this, "Phone number is empty", Toast.LENGTH_SHORT).show();
                        mDialog.cancel();
                        return;
                    }
                    if (10 > phone.length()){
                        Toast.makeText(ProductDetailActivity.this, "Invalid phone number is empty", Toast.LENGTH_SHORT).show();
                        mDialog.cancel();
                        return;
                    }

                    checkIsProductReservedAndUpdate(activity, name, phone);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void checkIsProductReservedAndUpdate(Activity activity, String userName, String userPhoneNo) {
            FirebaseFirestore.getInstance().collection("Products").document(product.getProductKey()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product != null){
                        if (!product.getProductReserve()){
                            Map<String, Object> updateReserveProduct = new HashMap<>();
                            updateReserveProduct.put("productReserve", true);
                            updateReserveProduct.put("customerName", userName);
                            updateReserveProduct.put("customerPhoneNo", userPhoneNo);

                            FirebaseFirestore.getInstance().collection("Products").document(product.getProductKey()).update(updateReserveProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    mDialog.cancel();
                                    activity.finish();
                                    Toast.makeText(ProductDetailActivity.this, "Reserve request sent successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(ProductDetailActivity.this, "Someone already requested for this product", Toast.LENGTH_LONG).show();
                            activity.finish();
                        }
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "This product might be removed", Toast.LENGTH_LONG).show();
                        activity.finish();
                    }
                    mDialog.dismiss();
                }
            });
        }
    }
}