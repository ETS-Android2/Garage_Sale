package com.example.garagesale;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garagesale.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.MyAllProductViewHolder> {

    private List<Product> mProductList;
    private Activity mActivity;

    // 1st called
    // getting list from the constructor
    public AllProductAdapter(List<Product> productList, Activity activity) {
        mProductList = productList;
        mActivity = activity;
    }

    // 3rd called
    @NonNull
    @NotNull
    @Override
    public AllProductAdapter.MyAllProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false);
        return new MyAllProductViewHolder(mView);
    }

    //5th called
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull AllProductAdapter.MyAllProductViewHolder holder, int position) {
        Product product = mProductList.get(position);
        holder.mTvProductName.setText(product.getProductName());
        holder.mTvProductPrice.setText("$ " + product.getProductPrice());

        //for image - using Glide
        Glide.with(holder.itemView).load(product.getProductImage()).into(holder.mImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ProductDetailActivity.class);
                intent.putExtra("Product", product);
                mActivity.startActivity(intent);
            }
        });
    }

    // 2nd called
    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    // 4th called
    public static class MyAllProductViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTvProductName;
        private final TextView mTvProductPrice;
        private final ImageView mImageView;
        private final MaterialCardView cvProduct;

        public MyAllProductViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvProductName = itemView.findViewById(R.id.tv_product_name);
            mTvProductPrice = itemView.findViewById(R.id.tv_product_price);
            mImageView = itemView.findViewById(R.id.iv_product);
            cvProduct = itemView.findViewById(R.id.cv_product);
        }
    }
    public void addNewList(List<Product> mNewProductList){
        mProductList = mNewProductList;
        notifyDataSetChanged();
    }
}