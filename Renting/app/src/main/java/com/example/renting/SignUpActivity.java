package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renting.interfaces.RetrofitInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {
    Button button_signup;
    TextView tv_login;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = RetrofitInterface.BASE_URL;
    EditText et_user_name, et_user_mobile, et_user_password, et_user_reenter_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        et_user_name = findViewById(R.id.et_user_name);
        et_user_mobile = findViewById(R.id.et_user_mobile);
        et_user_password = findViewById(R.id.et_user_password);
        et_user_reenter_password = findViewById(R.id.et_user_reenter_password);

        button_signup = findViewById(R.id.button_signup);
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_name", et_user_name.getText().toString());
                map.put("user_mobile", et_user_mobile.getText().toString());
                map.put("user_password", et_user_password.getText().toString());
                Call<Void> call = retrofitInterface.executeSignup(map);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(SignUpActivity.this, "Signup Successfuly", Toast.LENGTH_SHORT).show();
                            //Setting Shared Pref...
                            SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                            editor.putString("user_mobile", et_user_mobile.getText().toString());
                            editor.putString("user_name", et_user_name.getText().toString());
                            editor.putString("user_fund", "0");
                            editor.putString("isLogin", "yes");
                            editor.commit();

                            Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(SignUpActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        tv_login = findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}