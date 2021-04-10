package com.example.newdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newdemo.R;
import com.example.newdemo.fragment.HistoryFragment;
import com.example.newdemo.model.CartModel;
import com.example.newdemo.model.OrderModel;
import com.example.newdemo.model.ProductItem;
import com.example.newdemo.model.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceOrder extends AppCompatActivity {
    TextView addtotal, addDate;
    EditText user_address, user_name, user_mobile_no;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RadioButton rb_cod, rb_ONLINE;
    TextView textView;
    RadioGroup radioGroup_paymnt;
    List<CartModel> cartModels;
    Button paymentbtn;
    Users user;
    private FirebaseAuth mAuth;
    String UserAddress, UserName, UserMobile;

    Date DATE;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        addtotal = findViewById(R.id.add_Prdct_price);
        addDate = findViewById(R.id.date_time);
        user_address = findViewById(R.id.user_loca);
        user_mobile_no = findViewById(R.id.user_mobile);
        user_name = findViewById(R.id.user_name);
        paymentbtn = findViewById(R.id.paymentbtn);
        radioGroup_paymnt = findViewById(R.id.radiogrp);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final OrderModel orderModel = (OrderModel) bundle.getSerializable("order");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        final String date = format.format(orderModel.getTimestamp());
        addDate.setText("Date : " + date);
        addtotal.setText("₹." + orderModel.getOrderTotal());
//        addtotal.setText("₹." + getIntent().getStringExtra("total"));

        UserAddress = intent.getStringExtra("user_address");
//        UserAddress= preferences.getString("address","");
        UserName = intent.getStringExtra("user_name");
        UserMobile = intent.getStringExtra("user_mobile");


        user_address.setText(UserAddress);
        user_name.setText(UserName);
        user_mobile_no.setText(UserMobile);
        paymentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String name = user_name.getText().toString();
                String mobile = user_mobile_no.getText().toString().trim();


                if (mobile.isEmpty() || mobile.length() < 10) {
                    user_mobile_no.setError("enter valid number please");
                    user_mobile_no.requestFocus();
                    return;
                }
//                if (!.isSelected()){
//                    Toast.makeText(PlaceOrder.this, "please select payment method", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (name.isEmpty()) {
                    user_name.setError("name is required");
                    user_name.requestFocus();
                    return;
                } else {
                    if (radioGroup_paymnt.getCheckedRadioButtonId() == R.id.rb_COD) {
                        orderModel.setPaymentMethod("cash on delivery");
                        FirebaseFirestore.getInstance().collection("USERS").document("rahul@gmail.com")
                                .collection("ORDERS")
                                .add(orderModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    task.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            final Map<String, Object> orderMap = new HashMap<>();
                                            orderMap.put("orderId", documentReference.getId());
                                            documentReference.update(orderMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    orderMap.clear();
                                                    clearProducts();
                                                    Intent intn = new Intent(PlaceOrder.this, HomeActivity.class);
                                                    startActivity(intn);
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PlaceOrder.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Toast.makeText(PlaceOrder.this, "Order Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PlaceOrder.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    if (radioGroup_paymnt.getCheckedRadioButtonId() == R.id.rb_ONLINE) {
//                        FirebaseFirestore.getInstance().collection("USERS").document("rahul@gmail.com")
//                                .collection("CART").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@Nullable final QuerySnapshot value, @Nullable final FirebaseFirestoreException error) {
//                                if (value != null && !value.isEmpty()) {
//                                    cartModels = value.toObjects(CartModel.class);
//                                    orderModel.setCartModels(cartModels);
//                                    orderModel.setPaymentMethod("Online Transaction");
//                                    FirebaseFirestore.getInstance().collection("USERS").document("rahul@gmail.com")
//                                            .collection("ORDERS").add(orderModel)
//                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                @Override
//                                                public void onSuccess(DocumentReference documentReference) {
//                                                    if (!value.isEmpty()) {
//                                                        Intent user_pay = new Intent(PlaceOrder.this, PaymentActivity.class);
//                                                        Bundle bundle = new Bundle();
//                                                        bundle.putSerializable("order", orderModel);
//                                                        user_pay.putExtras(bundle);
//                                                        user_pay.putExtra("total", orderModel.getOrderTotal());
//                                                        user_pay.putExtra("u_address", user_address.getText().toString());
//                                                        user_pay.putExtra("u_name", user_name.getText().toString());
//                                                        user_pay.putExtra("u_mobile", user_mobile_no.getText().toString());
//                                                        startActivity(user_pay);
//                                                    } else
//                                                        Toast.makeText(PlaceOrder.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                }
//                                if (error != null) {
//                                    Toast.makeText(PlaceOrder.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
                        Intent user_pay = new Intent(PlaceOrder.this, PaymentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("order", orderModel);
                        user_pay.putExtras(bundle);
                        user_pay.putExtra("total", orderModel.getOrderTotal());
                        user_pay.putExtra("u_address", user_address.getText().toString());
                        user_pay.putExtra("u_name", user_name.getText().toString());
                        user_pay.putExtra("u_mobile", user_mobile_no.getText().toString());
                        startActivity(user_pay);
                    }
                }
            }
        });
    }

    private void clearProducts() {
        FirebaseFirestore.getInstance().collection("USERS").document("rahul@gmail.com").collection("CART")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()) {
                            value.getDocuments().get(0).getReference().delete();
                        }
                    }
                });
    }
}
