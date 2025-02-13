package com.software.androidthesis.api;

import com.software.androidthesis.entity.Article;
import com.software.androidthesis.entity.UserEdit;
import com.software.androidthesis.entity.Word;

import java.util.List;
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

}
