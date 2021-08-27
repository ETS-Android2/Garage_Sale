package com.yellowsparkle.garagesale;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yellowsparkle.garagesale.models.Product;
import com.yellowsparkle.garagesale.models.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.MyUsersListViewHolder> {

    private final List<UserDetail> mUserList;
    private final List<Product> mProductAdded = new ArrayList<>();
    private final List<Product> mProductReserved = new ArrayList<>();

    // 1st called
    // getting list from the constructor
    public UsersListAdapter(List<UserDetail> userList) {
        mUserList = userList;
    }

    // 3rd called
    @NonNull
    @NotNull
    @Override
    public UsersListAdapter.MyUsersListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customers, parent, false);
        return new MyUsersListViewHolder(mView);
    }

    //5th called
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull UsersListAdapter.MyUsersListViewHolder holder, int position) {
        UserDetail userDetail = mUserList.get(position);
        holder.mTvUserName.setText(userDetail.getFirstName());
        holder.mTvUserNo.setText("No. :" + userDetail.getPhoneNumber());
        holder.mTvUserEmail.setText("Email : " + userDetail.getEmail());

        mProductAdded.clear();
        mProductReserved.clear();
        FirebaseFirestore.getInstance().collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product products = document.toObject(Product.class);
                                if (products.getProductOwnerUid().equals(userDetail.getUserUid())){
                                    mProductAdded.add(products);
                                }
                                if (products.getRequestApproved()){
                                    if (products.getCustomerName().equals(userDetail.getFirstName())){
                                        mProductReserved.add(products);
                                    }
                                }
                            }
                            holder.mTvUserProductAdded.setText("Added Products : " + mProductAdded.size());
                            holder.mTvUserProductReserved.setText("Reserved Products : " + mProductReserved.size());
                        }
                    }
                });

        Glide.with(holder.itemView).load(userDetail.getUserImage()).into(holder.mImageView);
    }

    // 2nd called
    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    // 4th called
    public static class MyUsersListViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTvUserName;
        private final TextView mTvUserNo;
        private final TextView mTvUserEmail;
        private final TextView mTvUserProductAdded;
        private final TextView mTvUserProductReserved;
        private final ImageView mImageView;

        public MyUsersListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvUserName = itemView.findViewById(R.id.tv_customer_name);
            mTvUserNo = itemView.findViewById(R.id.tv_customer_no);
            mTvUserEmail = itemView.findViewById(R.id.tv_customer_email);
            mTvUserProductAdded = itemView.findViewById(R.id.tv_customer_pro_added);
            mTvUserProductReserved = itemView.findViewById(R.id.tv_customer_pro_reserved);
            mImageView = itemView.findViewById(R.id.iv_product);
        }
    }
}
