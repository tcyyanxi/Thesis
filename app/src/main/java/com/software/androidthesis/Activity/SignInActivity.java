package com.software.androidthesis.Activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.software.androidthesis.constants.BuildConfig;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.software.androidThesis.R;
import com.software.androidThesis.databinding.ActivitySignInBinding;
import com.software.androidthesis.api.ApiService;
import com.software.androidthesis.http.requesst.RetrofitManager;
import com.software.androidthesis.view.ToastView;
import com.software.androidthesis.viewmodel.EmailVerifyViewModel;


public class SignInActivity extends AppCompatActivity {
    private EmailVerifyViewModel viewModel;
    private ActivitySignInBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in);

        viewModel = new ViewModelProvider(this).get(EmailVerifyViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        ApiService apiService = RetrofitManager.getInstance(this,BuildConfig.USER_SERVICE).getApi(ApiService.class);
        viewModel.setApiService(apiService);

        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=" + "5e62dc3d");
        // 设置按钮的点击事件
        binding.edtTxtVerifyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailOnclick(view);
            }
        });

        binding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeInput = binding.edtTxtVerifyNumber.getText().toString();
                if (!TextUtils.isEmpty(codeInput)) {
                    viewModel.setVerifyCode(codeInput);
                }
                viewModel.verifyVerificationCode();
            }
        });

        viewModel.getNavigateToMain().observe(this, shouldNavigate -> {
            if(shouldNavigate) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                ToastView.showCustomToast(this, "验证失败");
            }
        });
    }
    public void emailOnclick(View view) {
        String emailInput = binding.edTxtVerifyEmail.getText().toString();
        Log.e("EmailVerifyActivity", "Email input: " + emailInput);
        if (!TextUtils.isEmpty(emailInput)) {
            ToastView.showCustomToast(this, "请接收验证码" + emailInput);
            viewModel.setEmail(emailInput);
            viewModel.requestSendVerificationCode(this);

            binding.TxtVerifyNumber.setEnabled(false);  // 禁用按钮

            // 开始一个1分钟的倒计时
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    binding.TxtVerifyNumber.setText(millisUntilFinished / 1000 + " 秒");
                }
                public void onFinish() {
                    binding.TxtVerifyNumber.setEnabled(true);  // 启用按钮
                    binding.TxtVerifyNumber.setText("获取验证码");  // 重置文本
                }
            }.start();
        } else {
            ToastView.showCustomToast(this, "请输入邮箱");
        }
    }
}