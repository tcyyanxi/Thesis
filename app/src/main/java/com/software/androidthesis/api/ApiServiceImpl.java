package com.software.androidthesis.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.software.androidthesis.api.ApiService;
import com.software.androidthesis.entity.Article;
import com.software.androidthesis.entity.UserEdit;
import com.software.androidthesis.entity.Word;

import org.json.JSONObject;

import java.util.List;
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

    private static final String BASE_URL = "http://192.168.137.1:2388/tancy/";  // 后端服务器URL
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
    // 获取书籍列表接口请求
    public void getBooks(ApiCallback<List<String>> callback) {
        // 调用 Retrofit API 获取书籍列表
        Call<List<String>> call = apiService.getBooks();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("获取书籍列表失败: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }

    // 获取单元列表接口请求
    public void getUnits(String book, ApiCallback<List<String>> callback) {
        // 调用 Retrofit API 获取单元列表
        Call<List<String>> call = apiService.getUnits(book);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("获取单元列表失败: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }

    // 获取单词列表接口请求
    public void getWords(String book, String unit, ApiCallback<List<Word>> callback) {
        // 调用 Retrofit API 获取单词列表
        Call<List<Word>> call = apiService.getWords(book, unit);

        call.enqueue(new Callback<List<Word>>() {
            @Override
            public void onResponse(Call<List<Word>> call, Response<List<Word>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("获取单词列表失败: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Word>> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }

    public void getWordAll(String word, ApiCallback<Word> callback) {
        // 调用 Retrofit API 获取单词详细信息
        Call<Word> call = apiService.getWordAll(word);

        call.enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());  // 请求成功，返回 Word 对象
                } else {
                    callback.onError("获取单词信息失败: " + response.code() + " " + response.message());  // 错误时回调
                }
            }

            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());  // 网络请求失败时回调
            }
        });
    }

    public void getArticle(String category, ApiCallback<List<Article>> callback) {
        // 如果是精品阅读请求所有文章
        if ("精选阅读".equals(category)) {
            getAllArticles(callback);
        } else {
            // 否则按类别获取文章
            getArticlesByCategory(category, callback);
        }
    }

    // 获取所有文章的方法
    private void getAllArticles(ApiCallback<List<Article>> callback) {
        Call<List<Article>> call = apiService.getArticles(); // 调用接口获取所有文章

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    // 根据分类获取文章的方法
    private void getArticlesByCategory(String category, ApiCallback<List<Article>> callback) {
        // 假设你有一个按分类获取文章的接口
        Call<List<Article>> call = apiService.getArticle(category);

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }





    // 定义回调接口
    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String errorMessage);
    }
}