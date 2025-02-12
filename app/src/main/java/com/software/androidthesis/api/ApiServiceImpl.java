package com.software.androidthesis.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.software.androidthesis.api.ApiService;
import com.software.androidthesis.entity.UserEdit;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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

    private static final String BASE_URL = "http://192.168.1.197:2388/tancy/";  // 后端服务器URL
    private ApiService apiService;

    // 构造方法，初始化 Retrofit 和 ApiService
    public ApiServiceImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)  // 设置基础URL
                .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 转换器
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    // 登录接口请求方法
    public void login(String email, ApiCallback callback) {
        Log.d("ApiServiceImpl", "开始登录请求，email：" + email);

        // 调用 ApiService 中的 login 接口
        Call<Map<String, Object>> call = apiService.login(email);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ApiServiceImpl", "登录成功，服务器返回：" + response.body().toString());
                    callback.onSuccess(response.body());  // 请求成功后通过回调返回数据
                } else {
                    Log.e("ApiServiceImpl", "登录失败，错误代码：" + response.code());
                    callback.onError("服务器返回错误：" + response.code());  // 请求失败通过回调返回错误信息
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("ApiServiceImpl", "登录请求失败：" + t.getMessage());
                callback.onError("网络错误：" + t.getMessage());  // 网络请求失败时的回调
            }
        });
    }

    // 用户编辑信息接口请求
    public void updateUserInfo(JSONObject userJson, ApiCallback callback) {
        Log.d("ApiServiceImpl", "开始更新用户信息：" + userJson.toString());

        // 创建 RequestBody
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), userJson.toString());

        Call<Map<String, Object>> call = apiService.updateUserInfo(body);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ApiServiceImpl", "更新用户信息成功：" + response.body().toString());
                    callback.onSuccess(response.body());
                } else {
                    Log.e("ApiServiceImpl", "更新用户信息失败，错误代码：" + response.code());
                    callback.onError("服务器返回错误：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("ApiServiceImpl", "更新请求失败：" + t.getMessage());
                callback.onError("网络错误：" + t.getMessage());
            }
        });
    }

    // 获取用户信息接口请求
    public void getUserInfo(Long userId, ApiCallback apiCallback) {
        // 调用 Retrofit API 获取用户信息
        Call<UserEdit> call = apiService.getUserInfo(userId);

        call.enqueue(new Callback<UserEdit>() {
            @Override
            public void onResponse(Call<UserEdit> call, Response<UserEdit> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 获取到返回的 UserEdit 对象
                    UserEdit userEdit = response.body();

                    // 将 UserEdit 对象转换为 Map<String, Object>
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(userEdit);  // 将 UserEdit 转换为 JsonElement
                    Map<String, Object> userMap = gson.fromJson(jsonElement, Map.class);  // 将 JsonElement 转换为 Map

                    // 调用 onSuccess 方法并传递 Map
                    apiCallback.onSuccess(userMap);
                } else {
                    apiCallback.onError("获取用户信息失败: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserEdit> call, Throwable t) {
                apiCallback.onError("网络错误: " + t.getMessage());
            }
        });
    }

    // 定义回调接口
    public interface ApiCallback {
        void onSuccess(Map<String, Object> response);
        void onError(String errorMessage);
    }
}