package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    TextView tv_user_name;
    String user_name;
    LinearLayout ll_sign_out, ll_my_ads, ll_my_orders, ll_notification, ll_funds, ll_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        user_name = prefs.getString("user_name", "Renting App");
        tv_user_name = findViewById(R.id.tv_user_name);
        ll_sign_out = findViewById(R.id.ll_sign_out);
        ll_my_ads = findViewById(R.id.ll_my_ads);
        ll_my_orders = findViewById(R.id.ll_my_orders);
        ll_notification = findViewById(R.id.ll_notification);
        ll_funds = findViewById(R.id.ll_funds);
        ll_profile = findViewById(R.id.ll_profile);

        tv_user_name.setText(user_name);
        ll_funds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, AddFundsActivity.class));
            }
        });

        ll_my_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MyAdsActivity.class));
            }
        });

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, NotificationActivity.class));
            }
        });

        ll_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                editor.putString("user_mobile", null);
                editor.putString("user_name", null);
                editor.putString("user_fund", "0");
                editor.putString("isLogin", "no");
                editor.apply();
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}