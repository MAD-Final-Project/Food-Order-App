package com.food.fd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddNewOrder extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;
    private ProgressDialog loadingBar;

    private String totalAmount = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_order);


        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(AddNewOrder.this, "Total price = Rs"+ totalAmount ,Toast.LENGTH_SHORT).show();



        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shippment_name);
        phoneEditText = (EditText) findViewById(R.id.shippment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shippment_address);
        cityEditText = (EditText) findViewById(R.id.shippment_city);


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Check();
            }
        });
    }

    private void Check() {

        String name = nameEditText.getText().toString();
        String phone  = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String city = cityEditText.getText().toString();


        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please provide your full name.", Toast.LENGTH_SHORT).
            show();
        }
        else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please provide your phone number.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please provide your address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "Please provide your city name.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

           AddOrder(name,phone,address,city);
        }
    }

    private void AddOrder(final String name, final String phone, final String address, final String city) {

        final DatabaseReference myref;
        myref = FirebaseDatabase.getInstance().getReference();

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Orders").child(phone).exists())) {
                    HashMap<String, Object> orderDetails = new HashMap<>();
                    orderDetails.put("phone", phone);
                    orderDetails.put("name", name);
                    orderDetails.put("address", address);
                    orderDetails.put("city", city);

                    myref.child("Orders").child(phone).updateChildren(orderDetails)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddNewOrder.this, "New order is successfully...", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(AddNewOrder.this, UserLogin.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AddNewOrder.this, "Network Error try again...", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });


                } else {

                    Toast.makeText(AddNewOrder.this, "Already has account...", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(AddNewOrder.this, "Try agian...", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AddNewOrder.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


}

}
