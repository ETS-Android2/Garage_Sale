package com.example.garagesale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.garagesale.models.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CustomersActivity extends AppCompatActivity {

    private RecyclerView mCustomersRecycler;
    private final List<UserDetail> mListOfUsers = new ArrayList<>();
    private UsersListAdapter mUsersListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        mCustomersRecycler = findViewById(R.id.rcv_customers);

        getAllUserData();
    }

    private void getAllUserData() {
        FirebaseFirestore.getInstance().collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDetail userDetail = document.toObject(UserDetail.class);
                                mListOfUsers.add(userDetail);
                            }
                            mUsersListAdapter = new UsersListAdapter(mListOfUsers);
                            mCustomersRecycler.setAdapter(mUsersListAdapter);

                        } else {
                            Toast.makeText(CustomersActivity.this, "Error getting users", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}