package com.software.androidthesis.api;

import android.util.Log;

import com.software.androidthesis.api.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/6 17:04
 * @Decription:
 */
public class ApiServiceImpl {

    private static final String BASE_URL = "http://192.168.1.197:2388/tancy/";
    private ApiService apiService;

    // 构造方法初始化 Retrofit 和 ApiService
    public ApiServiceImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)  // 设置API基础URL
                .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 转换器
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void login(String email, ApiCallback callback) {
        Log.d("ApiServiceImpl", "开始登录请求，email：" + email);

        Call<Map<String, Object>> call = apiService.login(email);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ApiServiceImpl", "登录成功，服务器返回：" + response.body().toString());
                    callback.onSuccess(response.body());
                } else {
                    Log.e("ApiServiceImpl", "登录失败，错误代码：" + response.code());
                    callback.onError("服务器返回错误：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("ApiServiceImpl", "登录请求失败：" + t.getMessage());
                callback.onError("网络错误：" + t.getMessage());
            }
        });
    }


    // 定义一个接口作为回调
    public interface ApiCallback {
        void onSuccess(Map<String, Object> response);
        void onError(String errorMessage);
    }
}