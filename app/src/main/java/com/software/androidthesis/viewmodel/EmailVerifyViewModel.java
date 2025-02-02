package com.software.androidthesis.viewmodel;

import static android.content.Context.USER_SERVICE;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.software.androidthesis.api.ApiService;
import com.software.androidthesis.entity.User;
import com.software.androidthesis.http.requesst.RetrofitManager;
import com.software.androidthesis.response.BaseResponse;
import com.software.androidthesis.response.EmailRequest;
import com.software.androidthesis.util.TokenManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 17:23
 * @Decription:
 */
public class EmailVerifyViewModel extends ViewModel {
    //与服务器进行通信，执行发送邮箱验证码和验证邮箱验证码的操作

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> verifyCode = new MutableLiveData<>();
    private MutableLiveData<String> statusMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToMain = new MutableLiveData<>();

    private ApiService apiService;
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }
//    public MutableLiveData<String> getEmail() {
//        return this.email;
//    }

    public LiveData<String> getVerifyCode() {
        return verifyCode;
    }

    public LiveData<String> getStatusMsg() {
        return statusMsg;
    }

    public void setVerifyCode(String code) {
        this.verifyCode.setValue(code);
    }

    public void updateStatus(String message) {
        statusMsg.setValue(message);
    }

    public LiveData<Boolean> getNavigateToMain() {
        return navigateToMain;
    }

    //请求发送验证码

    public void requestSendVerificationCode(Context context) {
        EmailRequest request = new EmailRequest(email.getValue());
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email.getValue());
        RetrofitManager.getInstance(context,USER_SERVICE).
                getApi(ApiService.class)
                .sendVerificationEmail(params)
                .enqueue(new Callback<BaseResponse<User>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BaseResponse<User> body = response.body();
                            if (body.isSuccess()) {

                                User user = body.getData();
                                Log.e("aaaaaaaa", user.toString());
                                String userId = user.getUserId();
                                TokenManager.saveUserId(context.getApplicationContext(), userId);
                                TokenManager.saveUserAvatar(context.getApplicationContext(), user.getAvatar());
                                String avatar = TokenManager.getUserAvatar(context.getApplicationContext());
                                Log.e("avatar", avatar);

                            } else {
                                Log.e("Request Error", "Error from server: " + body.getMessage());
                            }
                        } else {
                            // HTTP错误处理
                            Log.e("HTTP Error", "Response Code: " + response.code() + " Message: " + response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                        Log.e("LoginActivity-Error", "NetWOrk-Error");
                        t.printStackTrace();
                    }
                });

    }

    //验证验证码是否正确以登录

    public void verifyVerificationCode() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email.getValue());
        params.put("code", verifyCode.getValue());
        RetrofitManager.getInstance(null,USER_SERVICE).
                getApi(ApiService.class)
                .verifyEmail(params)
                .enqueue(new Callback<BaseResponse<Void>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean flag = response.body().isFlag();

                            navigateToMain.postValue(true);
                        } else {
                            navigateToMain.postValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                        Log.e("LoginActivity-Error", "NetWOrk-Error");
                        t.printStackTrace();
                        navigateToMain.postValue(false);
                    }
                });
    }


}
