package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.renting.adapters.NotificationListAdapter;
import com.example.renting.adapters.ProductListAdapter;
import com.example.renting.interfaces.RetrofitInterface;
import com.example.renting.models.NotificationModel;
import com.example.renting.models.ProductModel;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recycler_notification;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL= RetrofitInterface.BASE_URL;
    private static NotificationListAdapter notificationListAdapter;
    String user_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        String user_mobile = prefs.getString("user_mobile", null);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        recycler_notification = (RecyclerView) findViewById(R.id.recycler_notification);

        HashMap<String, String> map = new HashMap<>();
        map.put("user_mobile", user_mobile);
        Call<List<NotificationModel>> call = retrofitInterface.executeGetNotification(map);
        call.enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if(response.code()==200){
                    for(int i=0;i<response.body().size();i++){
                        NotificationModel notificationModel = response.body().get(i);
                        Log.v("Product_name", notificationModel.product_name);
                        Log.v("rent_price", notificationModel.product_rent_price);
                        Log.v("owner_mobile", notificationModel.product_owner_mobile);
                    }
                    notificationListAdapter = new NotificationListAdapter(response.body(), NotificationActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recycler_notification.setLayoutManager(mLayoutManager);
                    recycler_notification.setItemAnimator(new DefaultItemAnimator());
                    recycler_notification.setAdapter(notificationListAdapter);

                } else {
                    Toast.makeText(NotificationActivity.this, "No Notification Found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}