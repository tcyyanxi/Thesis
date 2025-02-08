package com.software.androidthesis.Activity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.software.androidThesis.R;


import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.util.SendEmail;
import com.software.androidthesis.view.ToastView;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SignInActivity extends AppCompatActivity {

    private EditText emailInput, codeInput;
    private Button sendCodeBtn, verifyBtn;
    private String userEmail;
    private int generatedCode;  // 生成的验证码
    private boolean isCodeSent = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler();
    private CountDownTimer countDownTimer; // 倒计时对象
    // Retrofit API服务接口
    private ApiServiceImpl apiServiceImpl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailInput = findViewById(R.id.edTxt_verify_email);
        codeInput = findViewById(R.id.edtTxt_verify_number);
        sendCodeBtn = findViewById(R.id.Txt_verify_number);
        verifyBtn = findViewById(R.id.btn_verify);

        // 初始化 ApiServiceImpl
        apiServiceImpl = new ApiServiceImpl();

// 发送验证码按钮
        sendCodeBtn.setOnClickListener(v -> {
            userEmail = emailInput.getText().toString().trim();
            if (userEmail.isEmpty() || !userEmail.contains("@")) {
                ToastView.showCustomToast(SignInActivity.this, "请输入正确的邮箱地址！");
                return;
            }

            // 生成6位随机验证码
            generatedCode = new Random().nextInt(900000) + 100000;
            Log.d("验证码", "生成的验证码：" + generatedCode);

            // 开始倒计时 60s
            startCountdown();

            // 发送验证码邮件
            executorService.execute(() -> {
                SendEmail.sendMail(userEmail, generatedCode);
                isCodeSent = true;
                mainHandler.post(() -> ToastView.showCustomToast(SignInActivity.this, "验证码已发送到您的邮箱！"));
            });
        });

        // 验证验证码按钮
        verifyBtn.setOnClickListener(v -> {
            if (!isCodeSent) {
                ToastView.showCustomToast(SignInActivity.this, "请先发送验证码");
                return;
            }

            String inputCodeStr = codeInput.getText().toString().trim();
            if (inputCodeStr.isEmpty()) {
                ToastView.showCustomToast(SignInActivity.this, "请输入验证码");
                return;
            }

            // 校验验证码
            if (!verifyCode(inputCodeStr)) {
                ToastView.showCustomToast(SignInActivity.this, "验证码错误，请重新输入");
                return;
            }

            // 验证码正确，进行后端登录操作
            loginUser(userEmail);
        });
    }

    /**
     * 开始60秒倒计时
     */
    private void startCountdown() {
        sendCodeBtn.setEnabled(false); // 禁用按钮，防止重复点击
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendCodeBtn.setText(millisUntilFinished / 1000 + "秒后重新获取");
            }

            @Override
            public void onFinish() {
                sendCodeBtn.setText("获取验证码");
                sendCodeBtn.setEnabled(true); // 重新启用按钮
            }
        }.start();
    }

    /**
     * 验证验证码是否正确
     */
    private boolean verifyCode(String inputCodeStr) {
        try {
            int inputCode = Integer.parseInt(inputCodeStr);
            return generatedCode == inputCode; // 检查验证码是否匹配
        } catch (NumberFormatException e) {
            return false; // 如果格式不正确，返回false
        }
    }

    /**
     * 登录用户
     */
    private void loginUser(String email) {
        Log.d("SignInActivity", "正在调用 loginUser 方法，email：" + email);

        apiServiceImpl.login(email, new ApiServiceImpl.ApiCallback() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                Log.d("SignInActivity", "登录成功，返回数据：" + response.toString());

                String status = (String) response.get("status");
                if ("first_login".equals(status)) {
                    Log.d("SignInActivity", "首次登录，跳转到 UserEditActivity");
                    Intent intent = new Intent(SignInActivity.this, UserEditActivity.class);
                    intent.putExtra("user_email", email);
                    startActivity(intent);
                } else {
                    Log.d("SignInActivity", "普通登录，跳转到 MainActivity");
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("SignInActivity", "登录失败：" + errorMessage);
                ToastView.showCustomToast(SignInActivity.this, "登录失败：" + errorMessage);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}