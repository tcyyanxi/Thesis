package com.software.androidthesis.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.software.androidthesis.api.ApiService;
import com.software.androidthesis.entity.Article;
import com.software.androidthesis.entity.UserArticle;
import com.software.androidthesis.entity.UserEdit;
import com.software.androidthesis.entity.Word;
import com.software.androidthesis.entity.WordDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
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
        // 创建 OkHttp 的日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);  // 记录请求和响应的完整内容

        // 配置 OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)  // 添加日志拦截器
                .build();

        // 使用 Gson 转换器并设置宽松模式
        Gson gson = new GsonBuilder()
                .setLenient()  // 设置宽松模式，允许解析不标准的JSON
                .create();

        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)  // 设置基础URL
                .client(okHttpClient)  // 设置 OkHttpClient
                .addConverterFactory(GsonConverterFactory.create(gson))  // 使用 Gson 转换器
                .build();

        // 初始化 apiService
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


    public void addUserWords(Long id, List<String> words) {
        Call<String> call = apiService.addUserWords(id, words);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    Log.d("ApiService", "Response: " + result);  // 打印响应体内容

                    // 解析 JSON
                    try {
                        String responseBody = response.body();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        Log.d("ApiService", "Parsed JSON: " + jsonObject.toString());
                    } catch (JSONException e) {
                        Log.e("ApiService", "JSON parsing error", e);
                    }

                } else {
                    Log.e("ApiService", "Failed to add user words: " + response.message());
                    Log.e("ApiService", "Response code: " + response.code());

                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("ApiService", "Error body: " + errorBody);  // 打印错误响应体
                        } catch (IOException e) {
                            Log.e("ApiService", "Error reading error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ApiService", "Error: " + t.getMessage());
            }
        });
    }
    public void getSelectedWords(Long userId, ApiCallback<List<String>> callback) {
        Log.d("ApiServiceImpl", "用户ID已从SharedPreferences: " + userId);
        String url = BASE_URL + "user-words/selected?id=" + userId.toString();
        Log.d("ApiService", "Request URL: " + url);  // 打印请求 URL

        Call<List<String>> call = apiService.getSelectedWords(userId);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    Log.d("ApiService", "Response: " + response.body());
                    callback.onSuccess(response.body());
                } else {
                    Log.e("ApiService", "Failed to get selected words: " + response.message());
                    callback.onError("获取已选单词失败");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("ApiService", "Error: " + t.getMessage());
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }

    // 获取单词信息
    public void getWords(Long id, String date, ApiCallback<List<WordDTO>> callback) {
        // 打印请求参数
        Log.d("API", "Requesting words with ID: " + id + " and Date: " + date);

        Call<List<WordDTO>> call = apiService.getWords(id, date);

        call.enqueue(new Callback<List<WordDTO>>() {
            @Override
            public void onResponse(Call<List<WordDTO>> call, Response<List<WordDTO>> response) {
                // 打印响应信息
                Log.d("API", "Response code: " + response.code());
                Log.d("API", "Response message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API", "Fetched " + response.body().size() + " words.");
                    callback.onSuccess(response.body());
                } else {
                    // 打印详细错误信息
                    Log.e("API", "Response not successful. Body: " + response.body());
                    callback.onError("没有获取到数据或发生错误");
                }
            }

            @Override
            public void onFailure(Call<List<WordDTO>> call, Throwable t) {
                // 打印失败的错误信息
                Log.e("API", "Request failed: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }



    public void updateUserWord(Long id, String word, int count, String date, ApiCallback<String> callback) {
        Log.d("ApiServiceImpl", "开始更新用户单词请求，id：" + id + ", word：" + word);

        // 调用 ApiService 中的 updateUserWord 接口，返回类型修改为 String
        Call<String> call = apiService.updateUserWord(id, word, count, date);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // 服务器返回的是简单的字符串
                    Log.d("ApiServiceImpl", "更新成功，服务器返回：" + response.body());
                    callback.onSuccess(response.body());  // 这里返回的应该是更新成功的字符串
                } else {
                    Log.e("ApiServiceImpl", "更新失败，错误代码：" + response.code());
                    callback.onError("服务器返回错误：" + response.code());  // 请求失败通过回调返回错误信息
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ApiServiceImpl", "更新请求失败：" + t.getMessage());
                callback.onError("网络错误：" + t.getMessage());  // 网络请求失败时的回调
            }
        });
    }

    public void getWordsByUserIdAndDate(Long id, String date, final ApiCallback<List<WordDTO>> callback) {
        // 构建 API 请求
        Call<List<WordDTO>> call = apiService.getWordsByUserIdAndDateListen(id, date);

        // 执行请求
        call.enqueue(new Callback<List<WordDTO>>() {
            @Override
            public void onResponse(Call<List<WordDTO>> call, Response<List<WordDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 如果请求成功，回调 onSuccess
                    callback.onSuccess(response.body());
                } else {
                    // 请求失败，回调 onError
                    callback.onError("没有获取到数据或发生错误");
                    Log.e("API", "Response not successful. Body: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<WordDTO>> call, Throwable t) {
                // 请求失败，回调 onError
                callback.onError(t.getMessage());
                Log.e("API", "Error fetching words: " + t.getMessage());
            }
        });
    }

    // 获取用户文章记录
    public void getUserArticle(Long id, Integer articleId, final ApiCallback<List<UserArticle>> callback) {
        Call<List<UserArticle>> call = apiService.getUserArticle(id, articleId);

        call.enqueue(new Callback<List<UserArticle>>() {
            @Override
            public void onResponse(Call<List<UserArticle>> call, Response<List<UserArticle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API", "返回的数据: " + new Gson().toJson(response.body())); // 打印 JSON 数据
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("查询失败: 服务器返回空数据或发生错误");
                    Log.e("API", "查询失败: Response body is null. Code: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<List<UserArticle>> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
                Log.e("API", "网络错误: " + t.getMessage(), t);
            }
        });
    }

    // 添加用户文章记录
    public void addUserArticle(UserArticle userArticle, final ApiCallback<String> callback) {
        Call<String> call = apiService.addUserArticle(userArticle);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("添加失败: 服务器返回空数据或发生错误");
                    Log.e("API", "添加失败: Response body is null. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
                Log.e("API", "网络错误: " + t.getMessage(), t);
            }
        });
    }

    public void reviewAndSchedule(Long userId, String word, int score, String date, final ApiCallback<String> callback) {
        Call<String> call = apiService.reviewAndSchedule(userId, word, score, date);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("提交失败: 服务器返回空数据或发生错误");
                    Log.e("API", "提交失败: Response body is null. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
                Log.e("API", "网络错误: " + t.getMessage(), t);
            }
        });
    }

    public void rescheduleUnfinishedTasks(Long userId, final ApiCallback<String> callback) {
        Call<String> call = apiService.rescheduleUnfinishedTasks(userId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("顺延失败: 服务器返回空数据或发生错误");
                    Log.e("API", "顺延失败: Response body is null. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
                Log.e("API", "网络错误: " + t.getMessage(), t);
            }
        });
    }




    // 定义回调接口
    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String errorMessage);
    }
}