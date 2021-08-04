package com.example.garagesale.falak;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garagesale.R;
import com.example.garagesale.models.Product;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ProductViewHolder> {

    private List<Product> mProductList;

    // data is passed into the constructor
    public ManageProductAdapter(List<Product> productList) {
        this.mProductList = productList;
    }

    @NotNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_products, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ManageProductAdapter.ProductViewHolder holder, int position) {

        Product product = mProductList.get(position);
        holder.mTvProductName.setText(product.getProductName());
        holder.mTvProductPrice.setText("$ " + product.getProductPrice());
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        private TextView mTvProductName;
        private TextView mTvProductPrice;
        private ImageView mImageView;
        private MaterialButton mBtnEditProduct;

        public ProductViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvProductName = itemView.findViewById(R.id.tv_product_name);
            mTvProductPrice = itemView.findViewById(R.id.tv_product_price);
            mImageView = itemView.findViewById(R.id.iv_product);
            mBtnEditProduct = itemView.findViewById(R.id.btn_manage_product);

        }
    }
}

