package com.software.androidthesis.api;

import com.software.androidthesis.entity.UserEdit;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ApiService {
    @POST("tancy/user/login")
    Call<Map<String, Object>> login(@Query("email") String email);
    @POST("userEdit/submit")
    Call<Map<String, Object>> updateUserInfo(@Body RequestBody body);
    @GET("userEdit/{id}")
    Call<UserEdit> getUserInfo(@Path("id") Long userId);
}
