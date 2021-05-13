package com.example.renting.interfaces;

import com.example.renting.models.LoginResult;
import com.example.renting.models.NotificationModel;
import com.example.renting.models.ProductModel;
import com.example.renting.models.UserInfoModel;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    public static String BASE_URL = "http://192.168.88.210:3000"; //wifi
//    public static String BASE_URL = "http://192.168.43.193:3000"; //mobile-jio

    @POST("/login")
    Call<LoginResult> executeLogin (@Body HashMap<String, String> map);

    @POST("/signup")
    Call<Void> executeSignup (@Body HashMap<String, String> map);

    @POST("/add_product")
    Call<Void> executeAddProduct (@Body HashMap<String, String> map);

    @POST("/get_products")
    Call<List<ProductModel>> executeGetProduct (@Body HashMap<String, String> map);

    @POST("/user_info")

    Call<UserInfoModel> executeUserInfo (@Body HashMap<String, String> map);

    @POST("/add_funds")
    Call<Void> executeAddFund (@Body HashMap<String, String> map);

    @POST("/get_products_by_id")
    Call<List<ProductModel>> executeGetProductsByID (@Body HashMap<String, String> map);

    @POST("/add_notification")
    Call<Void> executeAddNotification (@Body HashMap<String, String> map);

    @POST("/get_notification")
    Call<List<NotificationModel>> executeGetNotification (@Body HashMap<String, String> map);

    @POST("/reject_notification")
    Call<Void> executeRejectNotification (@Body HashMap<String, String> map);

    @POST("/add_rent_product")
    Call<Void> executeAddRentProduct (@Body HashMap<String, String> map);
}
