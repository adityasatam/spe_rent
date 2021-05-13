package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.renting.adapters.ProductListAdapter;
import com.example.renting.interfaces.RetrofitInterface;
import com.example.renting.models.LoginResult;
import com.example.renting.models.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {
    ImageView iv_add_product, iv_user_menu;
    private RecyclerView recycler_products;
    private static ProductListAdapter productListAdapter;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = RetrofitInterface.BASE_URL;
    String user_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        String user_mobile = prefs.getString("user_mobile", null);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        iv_user_menu = findViewById(R.id.iv_user_menu);
        iv_add_product = findViewById(R.id.iv_add_product);
        recycler_products = (RecyclerView) findViewById(R.id.recycler_products);

        iv_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DashboardActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });

        HashMap<String, String> map = new HashMap<>();
        map.put("user_mobile", user_mobile);
        Call<List<ProductModel>> call = retrofitInterface.executeGetProduct(map);
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.code() == 200) {
                    List<ProductModel> productModelList = new ArrayList<ProductModel>();
                    for (int i = 0; i < response.body().size(); i++) {
//                        if (response.body().get(i).isRented.equals("false")) {
                            ProductModel productModel = response.body().get(i);
                            Log.v("Product", productModel.product_name);
                            Log.v("Product_id", productModel._id);
                            Log.v("rented_user_name", productModel.rented_user_name);
                            productModelList.add(productModel);
//                        }
                    }
                    if(productModelList.size()>0){
                        productListAdapter = new ProductListAdapter(productModelList, DashboardActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_products.setLayoutManager(mLayoutManager);
                        recycler_products.setItemAnimator(new DefaultItemAnimator());
                        recycler_products.setAdapter(productListAdapter);
                    } else {
                        Toast.makeText(DashboardActivity.this, "No Product Available", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(DashboardActivity.this, "No Product Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        iv_user_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

    }
}