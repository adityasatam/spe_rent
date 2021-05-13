package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.renting.adapters.ProductListAdapter;
import com.example.renting.interfaces.RetrofitInterface;
import com.example.renting.models.ProductModel;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyAdsActivity extends AppCompatActivity {

    private RecyclerView recycler_products;
    private static ProductListAdapter productListAdapter;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL= RetrofitInterface.BASE_URL;
    String user_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        String user_mobile = prefs.getString("user_mobile", null);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        recycler_products = (RecyclerView) findViewById(R.id.recycler_products);

        HashMap<String, String> map = new HashMap<>();
        map.put("user_mobile", user_mobile);
        Call<List<ProductModel>> call = retrofitInterface.executeGetProductsByID(map);
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if(response.code()==200){
                    for(int i=0;i<response.body().size();i++){
                        ProductModel productModel =response.body().get(i);
                        Log.v("Product", productModel.product_name);
                        Log.v("Product_id", productModel._id);
                    }
                    productListAdapter = new ProductListAdapter(response.body(), MyAdsActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recycler_products.setLayoutManager(mLayoutManager);
                    recycler_products.setItemAnimator(new DefaultItemAnimator());
                    recycler_products.setAdapter(productListAdapter);

                } else {
                    Toast.makeText(MyAdsActivity.this, "No Product Found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(MyAdsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}