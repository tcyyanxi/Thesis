package com.software.androidthesis.api;

import com.software.androidthesis.entity.Article;
import com.software.androidthesis.entity.UserEdit;
import com.software.androidthesis.entity.Word;
import com.software.androidthesis.entity.WordDTO;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ApiService {
    @POST("tancy/user/login")
    Call<Map<String, Object>> login(@Query("email") String email);
    @POST("userEdit/submit")
    Call<Map<String, Object>> updateUserInfo(@Body RequestBody body);
    @GET("userEdit/{id}")
    Call<UserEdit> getUserInfo(@Path("id") Long userId);
    @GET("words/books")
    Call<List<String>> getBooks();

    @GET("words/units")
    Call<List<String>> getUnits(@Query("book") String book);

    @GET("words/list")
    Call<List<Word>> getWords(@Query("book") String book, @Query("unit") String unit);

    @GET("words/word")
    Call<Word> getWordAll(@Query("word")String word);

    @GET("article/article")
    Call<List<Article>> getArticle(@Query("category")String category);

    @GET("article/articles")
    Call<List<Article>> getArticles();

    @POST("user-words/add")
    @Headers("Content-Type: application/json")
    Call<String> addUserWords(@Query("id") Long id, @Body List<String> words);

    // 获取用户已选单词
    @GET("user-words/selected")
    @Headers("Content-Type: application/json")
    Call<List<String>> getSelectedWords(@Query("id") Long id);

    @GET("words/getWords")
    Call<List<WordDTO>> getWords(@Query("id") Long id, @Query("date") String date);

    @PUT("user-words/update")
    Call<String> updateUserWord(@Query("id") Long id,
                                             @Query("word") String word,
                                             @Query("count") int count,
                                             @Query("date") String date);

}
