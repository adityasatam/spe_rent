package com.example.renting.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renting.NotificationActivity;
import com.example.renting.R;
import com.example.renting.ViewProductAcitvity;
import com.example.renting.interfaces.RetrofitInterface;
import com.example.renting.models.NotificationModel;
import com.example.renting.models.UserInfoModel;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private List<NotificationModel> notificationModel;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_product_name, tv_request_user_name, tv_product_price, tv_request_time;
        public Button button_reject, button_accept;
        ImageView iv_product_image;
        public String user_mobile;


        public MyViewHolder(View view) {
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_request_user_name = (TextView) view.findViewById(R.id.tv_request_user_name);
            tv_product_price = (TextView) view.findViewById(R.id.tv_product_price);
            button_reject = (Button) view.findViewById(R.id.button_reject);
            button_accept = (Button) view.findViewById(R.id.button_accept);
            tv_request_time = (TextView) view.findViewById(R.id.tv_request_time);
            iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
        }
    }

    public NotificationListAdapter(List<NotificationModel> notificationModel, Context context) {
        this.notificationModel = notificationModel;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.MyViewHolder holder, int position) {
        NotificationModel NotificationModel = notificationModel.get(position);
        holder.tv_product_name.setText(NotificationModel.product_name);
        holder.tv_request_user_name.setText(NotificationModel.request_user_name);
        holder.tv_product_price.setText(NotificationModel.product_rent_price);
        holder.tv_request_time.setText(NotificationModel.date_time);
        holder.iv_product_image.setImageBitmap(StringToBitMap(NotificationModel.product_image));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ViewProductAcitvity.class);
//                intent.putExtra("Product_Details", ProductModel);
//                context.startActivity(intent);

//                SharedPreferences prefs = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
//                holder.user_mobile = prefs.getString("user_mobile", null);
//                rejectNotification(NotificationModel);
            }
        });

        holder.button_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                holder.user_mobile = prefs.getString("user_mobile", null);
                rejectNotification(NotificationModel);
            }
        });

        holder.button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                holder.user_mobile = prefs.getString("user_mobile", null);
                addRentProduct(NotificationModel);
            }
        });
    }

    public void addRentProduct(NotificationModel notificationModel) {
        Retrofit retrofit;
        RetrofitInterface retrofitInterface;
        String BASE_URL = RetrofitInterface.BASE_URL;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        HashMap<String, String> map = new HashMap<>();
        Log.v("product_name", notificationModel.product_name);
        map.put("product_name", notificationModel.product_name);
        map.put("_id", notificationModel.product_id);
        map.put("user_mobile", notificationModel.product_owner_mobile);
        map.put("isRented", "true");
        map.put("rented_user_name", notificationModel.request_user_name);
        map.put("rented_user_mobile", notificationModel.request_user_mobile);
        Call<Void> call = retrofitInterface.executeAddRentProduct(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Toast.makeText(context, "Rent Request Accepted", Toast.LENGTH_SHORT).show();
                    rejectNotification(notificationModel);
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void rejectNotification(NotificationModel notificationModel) {
        Retrofit retrofit;
        RetrofitInterface retrofitInterface;
        String BASE_URL = RetrofitInterface.BASE_URL;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("product_owner_mobile", notificationModel.product_owner_mobile);
        map.put("product_id", notificationModel.product_id);
        map.put("request_user_mobile", notificationModel.request_user_mobile);
        Call<Void> call = retrofitInterface.executeRejectNotification(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    context.startActivity(new Intent(context, NotificationActivity.class));
                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
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

    @NonNull
    @Override
    public NotificationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_for_rent_notification, parent, false);
        return new NotificationListAdapter.MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return notificationModel.size();
    }

}
