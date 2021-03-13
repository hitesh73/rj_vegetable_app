package com.example.newdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newdemo.R;
import com.example.newdemo.fragment.HistoryFragment;
import com.example.newdemo.model.OrderModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class UserPaymentDetails extends AppCompatActivity {
    TextView tv_user_name, tv_mobile_no, tv_user_address, tv_date, tv_prodcut_price, tv_paymentMethod;
    Button Continue_btn;
    RadioGroup radioGroup;


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
        Continue_btn = findViewById(R.id.btn_continue);


        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final OrderModel orderModel = (OrderModel) bundle.getSerializable("order");


        tv_user_name.setText(getIntent().getStringExtra("u_name"));
        tv_mobile_no.setText(getIntent().getStringExtra("u_mobile"));
        tv_prodcut_price.setText(getIntent().getStringExtra("total_price"));
        tv_user_address.setText(getIntent().getStringExtra("u_address"));
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String curr_date = format.format(orderModel.getTimestamp());
        tv_date.setText(curr_date);
        tv_paymentMethod.setText(orderModel.getPaymentMethod());

        Continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gointent = new Intent(UserPaymentDetails.this,HistoryFragment.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",orderModel);
                gointent.putExtras(bundle);
                startActivity(gointent);
                }

        });


    }
}