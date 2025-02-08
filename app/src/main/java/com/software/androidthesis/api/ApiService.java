package com.software.androidthesis.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
public interface ApiService {
    @POST("tancy/user/login")
    Call<Map<String, Object>> login(@Query("email") String email);
}
