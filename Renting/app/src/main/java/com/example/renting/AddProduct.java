package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddProduct extends AppCompatActivity {
    Button button_upload_images;
    EditText et_product_name, et_product_description, et_product_rent_price, et_user_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        button_upload_images = findViewById(R.id.button_upload_images);
        et_product_name = findViewById(R.id.et_product_name);
        et_product_description = findViewById(R.id.et_product_description);
        et_product_rent_price = findViewById(R.id.et_product_rent_price);
        et_user_location = findViewById(R.id.et_user_location);

        button_upload_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProduct.this, ImageUpload.class);
                intent.putExtra("et_product_name", et_product_name.getText().toString());
                intent.putExtra("et_product_description", et_product_description.getText().toString());
                intent.putExtra("et_product_rent_price", et_product_rent_price.getText().toString());
                intent.putExtra("et_user_location", et_user_location.getText().toString());

                startActivity(intent);
            }
        });
    }
}