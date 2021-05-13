package com.example.renting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renting.interfaces.RetrofitInterface;
import com.example.renting.models.LoginResult;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button button_login;
    TextView tv_signup;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = RetrofitInterface.BASE_URL;
    EditText et_user_mobile, et_user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        String restoredText = prefs.getString("isLogin", null);
        if (restoredText != null && restoredText.equals("yes")) {
//            Toast.makeText(this, restoredText, Toast.LENGTH_SHORT).show();
            Log.i("testabcd", prefs.getString("user_mobile", "null"));
            Log.i("testabcdmob", prefs.getString("user_name", "null"));
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        button_login = findViewById(R.id.button_login);
        et_user_mobile = findViewById(R.id.et_user_mobile);
        et_user_password = findViewById(R.id.et_user_password);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                startActivity(intent);
                HashMap<String, String> map = new HashMap<>();
                map.put("user_mobile", et_user_mobile.getText().toString());
                map.put("user_password", et_user_password.getText().toString());
                Call<LoginResult> call = retrofitInterface.executeLogin(map);
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        if (response.code() == 200) {
                            LoginResult loginResult = response.body();
//                            Toast.makeText(LoginActivity.this, loginResult.getUser_name(), Toast.LENGTH_SHORT).show();
                            //Setting Shared Pref...
                            SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                            editor.putString("user_mobile", et_user_mobile.getText().toString());
                            editor.putString("user_name", loginResult.getUser_name().toString());
                            editor.putString("user_fund", loginResult.getUser_fund().toString());
                            editor.putString("isLogin", "yes");
                            editor.commit();

                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        tv_signup = findViewById(R.id.tv_signup);
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}