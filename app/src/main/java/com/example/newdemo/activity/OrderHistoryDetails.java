package com.example.newdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newdemo.R;
import com.example.newdemo.adapter.CartProductList;
import com.example.newdemo.adapter.OrderListAdapter;
import com.example.newdemo.adapter.OrderProductList;
import com.example.newdemo.adapter.RecyclerAdapterItem;
import com.example.newdemo.model.CartModel;
import com.example.newdemo.model.OrderModel;
import com.example.newdemo.model.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryDetails extends AppCompatActivity {
    TextView tv_mobile_no, tv_date, tv_address, tv_payment, tv_total_price;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String mobile_no;
    List<CartModel> cartModels;
    RecyclerView recyclerViewItems;


    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_details);

        tv_mobile_no = findViewById(R.id.User__mobile_no);
        tv_date = findViewById(R.id.User__date);
        tv_address = findViewById(R.id.User__address);
        tv_payment = findViewById(R.id.User__paymentmethod);
        tv_total_price = findViewById(R.id.User__totalprice);
        recyclerViewItems = findViewById(R.id.orderItem);

        cartModels = new ArrayList<>();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        OrderModel orderModel = (OrderModel) bundle.getSerializable("order");

        tv_total_price.setText("Total Price : "+orderModel.getOrderTotal());
        tv_payment.setText("Payment Method : "+orderModel.getPaymentMethod());


        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String date = format.format(orderModel.getTimestamp());
        tv_date.setText("Date "+date);

        preferences = getSharedPreferences("user", MODE_PRIVATE);
        editor=preferences.edit();

        FirebaseFirestore.getInstance().collection("USERS")
                .whereEqualTo("email", preferences.getString("email", ""))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            value.getDocuments().get(0).getReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Users user = documentSnapshot.toObject(Users.class);

                                    tv_mobile_no.setText("mobile no : "+user.mobile);
                                    tv_address.setText("Address : "+user.address);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }

                            });
                        }
                        if (error != null) {
                            Toast.makeText(OrderHistoryDetails.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        orderModel.getCartModels();

        OrderListAdapter orderListAdapter = new OrderListAdapter(OrderHistoryDetails.this,orderModel.getCartModels());
        recyclerViewItems.setAdapter(orderListAdapter);
        recyclerViewItems.setLayoutManager(new GridLayoutManager(this,1));

    }
}