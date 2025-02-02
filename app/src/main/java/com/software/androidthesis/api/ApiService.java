package com.software.androidthesis.api;

import com.software.androidthesis.entity.User;
import com.software.androidthesis.response.BaseResponse;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 21:08
 * @Decription:
 */
public interface ApiService {

    //发送邮箱验证码
    @POST("user/login")
    Call<BaseResponse<User>> sendVerificationEmail(@QueryMap HashMap<String, String> params);

    //验证邮箱验证码
    @POST("user/checkLogin")
    Call<BaseResponse<Void>> verifyEmail(@QueryMap HashMap<String, String> params);


//    //发送请求获取单词总数
//    @GET("word/getWordNum")
//    Call<BaseResponse<Integer>> getWordNum();
//
//    //发送请求测试单词
//    @GET("word/getWords")
//    Call<BaseResponse<List<WordDetail>>> getWords(@Query("currentPage") int currentPage);
//
//    // 将测试结果发送给我服务端
//    @POST("userLevel/insertData")
//    Call<BaseResponse<Void>> sendTestResultToServer(@QueryMap HashMap<String, String> params);
//
//
//    //更新个人资料
//    @POST("userInfo/addUser")
//    Call<BaseResponse<Void>> sendNewUserInfo(@QueryMap HashMap<String, String> params);
//
//    @GET("userLevel/{userId}")
//    Call<BaseResponse<UserLevel>> isTest(@Path("userId") String userId);
//
//    @GET("article/{lexile}/{currentPage}")
//    Call<BaseResponse<List<Article>>> getArticles(@Path("lexile") int lexile, @Path("currentPage") int currentPage);
//
//    //获取该难度对应的文章总数
//    @GET("article/getArticleByLexileNum/{lexile}")
//    Call<BaseResponse<Integer>> getArticleNum(@Path("lexile") int lexile);
//
//    //获取文库所有文章
//    @GET("article/getAllArticles")
//    Call<BaseResponse<List<Article>>> getAllArticle();
//
//    //将用户自行选择的难度值传给后端
//    @POST("userInfo/updateLexile")
//    Call<BaseResponse<Void>> updateLexile(@QueryMap HashMap<String, String> params);
//
//    //将头像传给后端
//    @Multipart
//    @POST("userInfo/updateAvatar/{userId}")
//    Call<BaseResponse<String>> uploadAvatar(@Path("userId") String userId, @Part MultipartBody.Part file);
//
//    //获取推荐单词
//    @GET("wordRec/getWordRec/{userId}/{currentPage}")
//    Call<BaseResponse<List<WordDetail>>> getRecommendWords(@Path("userId") String userId, @Path("currentPage") int currentPage);
//
//    //发送请求获取阅读时长
//    @GET("readLog/{userId}")
//    Call<BaseResponse<Long>> getTodayReadDuration(@Path("userId") String userId);
//
//    //发送请求获取总单词数目
//    @GET("readLog/totalWord/{userId}")
//    Call<BaseResponse<Integer>> getTotalWordNum(@Path("userId") String userId);
//
//    //发送请求获取文章单词数目
//    @GET("readLog/wordNum/{userId}")
//    Call<BaseResponse<Integer>> getTodayReadWordNumByuserId(@Path("userId") String userId);
//
//    // 插入阅读记录
//    @POST("readLog/insert")
//    Call<BaseResponse<ReadLog>> insertReadLog(@QueryMap HashMap<String, String> params);
//
//    // 拿到用户阅读数据统计
//    @GET("/dataCount/{userId}")
//    Call<BaseResponse<List<ReadLogDataCount>>> getReadLongDataCount(@Path("userId") String userId);
//
//    // 拿到用户评分统计数据
//    @GET("recording/{userId}/{month}")
//    Call<BaseResponse<List<WordDetailRecording>>> getRecoringData(@Path("userId") String userId, @Path("month") String month);
//
//    // 获取柱状图数据，获取最近七天
//    @GET("recording/{userId}")
//    Call<BaseResponse<List<WordDetailRecording>>> getRecordingDataWeek(@Path("userId") String userId);
//
//    //发送用户语音测评评分
//    @POST("/recording/insertData")
//    Call<BaseResponse<Void>> insertData(@Body WordDetailRecording wordDetailRecording);
}