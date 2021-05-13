package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renting.interfaces.RetrofitInterface;
import com.example.renting.models.LoginResult;
import com.example.renting.models.ProductModel;
import com.example.renting.models.UserInfoModel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewProductAcitvity extends AppCompatActivity {

    TextView tv_product_name;
    TextView tv_product_description;
    TextView tv_product_price;
    TextView tv_owner_name;
    TextView tv_owner_location;
    ImageView iv_product_image;
    Button button_book_for_rent;
    String user_mobile, user_name;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = RetrofitInterface.BASE_URL;
    ProductModel productModel;
    String user_fund;
    TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_acitvity);

        Intent i = getIntent();
        productModel = (ProductModel) i.getSerializableExtra("Product_Details");

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        user_mobile = prefs.getString("user_mobile", null);
        user_name = prefs.getString("user_name", null);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        getCurrentFund();

        tv_product_name = findViewById(R.id.tv_product_name);
        tv_product_description = findViewById(R.id.tv_product_description);
        tv_product_price = findViewById(R.id.tv_product_price);
        tv_owner_name = findViewById(R.id.tv_owner_name);
        tv_owner_location = findViewById(R.id.tv_owner_location);
        iv_product_image = findViewById(R.id.iv_product_image);
        button_book_for_rent = findViewById(R.id.button_book_for_rent);
        tv_message = findViewById(R.id.tv_message);

        if(user_mobile.equals(productModel.user_mobile)){
            button_book_for_rent.setVisibility(View.GONE);
            if(productModel.isRented.equals("true")){
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText("This product is rented to "+productModel.rented_user_name);
            }
        } else {
            button_book_for_rent.setVisibility(View.VISIBLE);
            tv_message.setVisibility(View.GONE);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("user_mobile", productModel.user_mobile);
        Call<UserInfoModel> call = retrofitInterface.executeUserInfo(map);
        call.enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
               if (response.code() == 200) {
                    UserInfoModel userInfoModel = response.body();
                    tv_product_name.setText(productModel.product_name);
                    tv_product_description.setText(productModel.product_description);
                    tv_product_price.setText(productModel.product_rent_price);
                    tv_owner_name.setText(userInfoModel.getUser_name());
                    tv_owner_location.setText(productModel.owner_location);
                    iv_product_image.setImageBitmap(StringToBitMap(productModel.product_image));
                } else {
                    Toast.makeText(ViewProductAcitvity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    tv_product_name.setText("");
                    tv_product_description.setText("");
                    tv_product_price.setText("");
                    tv_owner_name.setText("");
                    tv_owner_location.setText("");
                }
            }
            @Override
            public void onFailure(Call<UserInfoModel> call, Throwable t) {
                Toast.makeText(ViewProductAcitvity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        button_book_for_rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(user_fund) >= Integer.parseInt(productModel.product_rent_price)){
                    requestProductForRent();
                } else {
                    Toast.makeText(ViewProductAcitvity.this, "Please add more fund in your account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void getCurrentFund(){
        HashMap<String, String> map = new HashMap<>();
        map.put("user_mobile", user_mobile);
        Call<UserInfoModel> call = retrofitInterface.executeUserInfo(map);
        call.enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                if (response.code() == 200) {
                    UserInfoModel userInfoModel = response.body();
                    user_fund = userInfoModel.getUser_fund();
                } else {
                    Toast.makeText(ViewProductAcitvity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserInfoModel> call, Throwable t) {
                Toast.makeText(ViewProductAcitvity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void requestProductForRent(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String currentDate = formatter.format(date);

        HashMap<String, String> map = new HashMap<>();
        map.put("request_user_mobile", user_mobile);
        map.put("product_id", productModel._id);
        map.put("product_image", productModel.product_image);
        map.put("product_owner_mobile", productModel.user_mobile);
        map.put("date_time", currentDate);
        map.put("request_user_name", user_name);
        map.put("product_name", productModel.product_name);
        map.put("product_rent_price", productModel.product_rent_price);
        Call<Void> call = retrofitInterface.executeAddNotification(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 101){
                    Toast.makeText(ViewProductAcitvity.this, "Already request for rent", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 200) {
                    Toast.makeText(ViewProductAcitvity.this, "Requested For Product For Rent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewProductAcitvity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ViewProductAcitvity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}