package com.example.newdemo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.newdemo.R;
import com.example.newdemo.model.OrderModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;

public class UserPaymentDetails extends AppCompatActivity {
    TextView tv_user_name, tv_mobile_no, tv_user_address, tv_date, tv_prodcut_price, tv_paymentMethod;
    Button continue_btn;
//    OrderModel orderModel;
//    RadioGroup radioGroup;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_payment_details);

        tv_user_name = findViewById(R.id.cus_name);
        tv_mobile_no = findViewById(R.id.number);
        tv_user_address = findViewById(R.id.adds);
        tv_date = findViewById(R.id.date);
        tv_prodcut_price = findViewById(R.id.price);
        tv_paymentMethod = findViewById(R.id.pay_method);
        continue_btn = findViewById(R.id.btn_continue);

        getClearProducts();

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final OrderModel orderModel = (OrderModel) bundle.getSerializable("order");


        tv_user_name.setText(getIntent().getStringExtra("u_name"));
        tv_mobile_no.setText(getIntent().getStringExtra("u_mobile"));
        tv_prodcut_price.setText("₹."+orderModel.getOrderTotal());
        tv_user_address.setText(getIntent().getStringExtra("u_address"));
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String curr_date = format.format(orderModel.getTimestamp());
        tv_date.setText(curr_date);
        tv_paymentMethod.setText(orderModel.getPaymentMethod());

//        if (tv_paymentMethod.getText().toString().equals(orderModel.getPaymentMethod())){
//            continue_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intentonline = new Intent(UserPaymentDetails.this,PaymentActivity.class);
//                    Bundle bundle1 =  new Bundle();
//                    bundle1.putSerializable("order",orderModel);
//                    intentonline.putExtras(bundle1);
//                    intentonline.putExtra("total",orderModel.getOrderTotal());
//                    startActivity(intentonline);
//                }
//            });
//        }

        FirebaseFirestore.getInstance().collection("USERS").document("rahul@gmail.com")
                .collection("CART")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()){

                        }
                    }
                });

    }

    private void getClearProducts() {
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