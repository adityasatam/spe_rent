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
import com.example.renting.models.UserInfoModel;

import org.w3c.dom.Text;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFundsActivity extends AppCompatActivity {

    EditText et_add_fund;
    TextView tv_current_fund;
    Button button_add_fund;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL= RetrofitInterface.BASE_URL;
    int currentFund = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds);

        tv_current_fund = findViewById(R.id.tv_current_fund);
        et_add_fund = findViewById(R.id.et_add_fund);
        button_add_fund = findViewById(R.id.button_add_fund);
        et_add_fund.setSelection(0);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        String user_mobile = prefs.getString("user_mobile", null);

        HashMap<String, String> map = new HashMap<>();
        map.put("user_mobile", user_mobile);
        Call<UserInfoModel> call = retrofitInterface.executeUserInfo(map);
        call.enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                if (response.code() == 200) {
                    UserInfoModel userInfoModel = response.body();
                    tv_current_fund.setText(userInfoModel.getUser_fund());
                    currentFund = Integer.parseInt(userInfoModel.getUser_fund());
                } else {
                    Toast.makeText(AddFundsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    tv_current_fund.setText("");
                }
            }
            @Override
            public void onFailure(Call<UserInfoModel> call, Throwable t) {
                Toast.makeText(AddFundsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        button_add_fund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int updatedFund = currentFund + Integer.parseInt(et_add_fund.getText().toString());
                HashMap<String, String> map = new HashMap<>();
                map.put("user_mobile", user_mobile);
                map.put("user_fund", ""+updatedFund);
                Call<Void> call = retrofitInterface.executeAddFund(map);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(AddFundsActivity.this, "Funds Updated Successfully", Toast.LENGTH_SHORT).show();
                            currentFund = updatedFund;
                            tv_current_fund.setText(""+currentFund);
                            et_add_fund.setText("");
                            SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                            editor.putString("user_fund", ""+currentFund);
                            editor.apply();
                        } else if (response.code() == 400) {
                            Toast.makeText(AddFundsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AddFundsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }
}