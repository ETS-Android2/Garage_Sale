package com.example.garagesale;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garagesale.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.MyAdminProductViewHolder> {

    private List<Product> productList;
    private ProgressDialog mDialog;

    public AdminProductAdapter(List<Product> mProduct){
        productList = mProduct;
    }

    @NonNull
    @Override
    public MyAdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAdminProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdminProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = productList.get(position);
        holder.mTvProductName.setText(product.getProductName());
        Glide.with(holder.itemView).load(product.getProductImage()).into(holder.mImageView);

        mDialog = new ProgressDialog(holder.itemView.getContext());
        mDialog.setMessage("Deleting item");
        mDialog.setCancelable(false);

        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("Products").document(product.getProductKey()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mDialog.cancel();
                                    productList.remove(position);
                                    notifyItemChanged(position);
                                } else {
                                    mDialog.cancel();
                                    Toast.makeText(holder.itemView.getContext(), "Error While Deleting Product", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class MyAdminProductViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView mTvProductName;
        private final ImageView mImageView;
        private final MaterialButton mBtnDelete;

        public MyAdminProductViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvProductName = itemView.findViewById(R.id.tv_product_name);
            mImageView = itemView.findViewById(R.id.iv_product);
            mBtnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
