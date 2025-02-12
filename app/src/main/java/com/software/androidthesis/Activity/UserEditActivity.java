package com.software.androidthesis.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.software.androidThesis.R;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.UserEdit;
import com.software.androidthesis.view.ToastView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;


public class UserEditActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private Button submitButton, pickDateButton;
    private EditText usernameEditText;
    private RadioGroup genderRadioGroup;
    private TextView birthdayTextView;
    private Long userId;

    private ImageView avatarImg;

    private BottomSheetDialog bottomSheetDialog;
    private String username, gender,birthdayStr;
    private Date birthday;  // 修改为 Date 类型
    private Uri avatarUri;  // 保存头像的Uri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        // 初始化UI组件
        submitButton = findViewById(R.id.In_btn);
        usernameEditText = findViewById(R.id.et_username);
        genderRadioGroup = findViewById(R.id.radioGroup);
        birthdayTextView = findViewById(R.id.show_selected_date);
        pickDateButton = findViewById(R.id.pick_date_button);
        avatarImg = findViewById(R.id.iv_individual_avatar);

        // 获取用户ID（从SharedPreferences获取）
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        userId = preferences.getLong("id", -1);  // 从SharedPreferences获取已登录用户的ID

        // 检查是否成功获取用户ID
        if (userId == -1) {
            Toast.makeText(UserEditActivity.this, "用户未登录或ID获取失败", Toast.LENGTH_SHORT).show();
            finish();  // 结束当前Activity，防止空指针异常
        }

        // 从后端获取用户信息
        fetchUserInfo(userId);

        // 初始化 BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // 获取 BottomSheetDialog 里面的按钮
        Button btnViewAvatar = bottomSheetView.findViewById(R.id.btn_view_avatar);
        Button btnSelectAvatar = bottomSheetView.findViewById(R.id.btn_select_avatar);

        if (birthdayStr != null) {
            try {
                birthday = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayStr);  // 将后端返回的生日字符串转换为 Date
                Log.d("UserEditActivity", "Parsed Birthday: " + birthday);  // 输出解析后的生日
                birthdayTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(birthday));  // 显示在 UI 上
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("UserEditActivity", "Parse Exception: " + e.getMessage());  // 打印异常
            }
        }


// 设置 MaterialDatePicker 的点击事件
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("选择日期");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

// 日期选择按钮点击事件
        pickDateButton.setOnClickListener(v -> {
            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });

// 日期选择后回调处理
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        long selectedDateInMillis = (Long) selection;
                        birthday = new Date(selectedDateInMillis);  // 更新本地生日日期
                        Log.d("UserEditActivity", "Selected Birthday: " + birthday);  // 输出选择的日期
                        birthdayTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(birthday));  // 显示选中的日期
                    }
                });



        // 提交按钮点击事件
        submitButton.setOnClickListener(v -> {
            // 获取用户输入的数据
            username = usernameEditText.getText().toString();
            gender = getSelectedGender();  // 获取选中的性别

            // 验证输入数据
            if (username.isEmpty() || gender.isEmpty() || birthday == null) {
                ToastView.showCustomToast(UserEditActivity.this, "请填写完整信息");
                return;
            }

            // 创建 JSON 对象
            JSONObject userJson = new JSONObject();
            try {
                userJson.put("id", userId);
                userJson.put("username", username);
                userJson.put("gender", gender);
                userJson.put("birthday", new SimpleDateFormat("yyyy-MM-dd").format(birthday));  // 将日期格式化为字符串
                if (avatarUri != null) {
                    userJson.put("avatar", avatarUri.toString());  // 将头像Uri转为字符串
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 调用 API 更新用户信息
            ApiServiceImpl apiServiceImpl = new ApiServiceImpl();
            apiServiceImpl.updateUserInfo(userJson, new ApiServiceImpl.ApiCallback() {
                @Override
                public void onSuccess(Map<String, Object> response) {
                    runOnUiThread(() -> {
                        // 成功回调，跳转到 MainActivity
                        String status = (String) response.get("status");
                        if ("success".equals(status)) {
                            ToastView.showCustomToast(UserEditActivity.this, "更新成功");
                            Intent intent = new Intent(UserEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();  // 结束当前 Activity
                        } else {
                            ToastView.showCustomToast(UserEditActivity.this, "更新失败");
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(UserEditActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        });

        // 点击头像，弹出 BottomSheetDialog
        avatarImg.setOnClickListener(v -> bottomSheetDialog.show());

        // 点击 "查看" 按钮，显示当前头像
        btnViewAvatar.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showAvatarDialog();
        });

        // 点击 "从相册选择" 按钮，打开相册选择图片
        btnSelectAvatar.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openGallery();
        });
    }

    // 获取选中的性别
    private String getSelectedGender() {
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        switch (selectedId) {
            case R.id.radio_button_1:
                return "保密";
            case R.id.radio_button_2:
                return "男";
            case R.id.radio_button_3:
                return "女";
            default:
                return "";
        }
    }

    /**
     * 从后端获取用户信息并填充到 UI 中
     */
    private void fetchUserInfo(Long userId) {
        ApiServiceImpl apiServiceImpl = new ApiServiceImpl();
        apiServiceImpl.getUserInfo(userId, new ApiServiceImpl.ApiCallback() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                runOnUiThread(() -> {
                    // 假设后端返回的用户数据包含 "username", "gender", "birthday", "avatar"
                    if (response != null) {
                        username = (String) response.get("username");
                        gender = (String) response.get("gender");
                        birthdayStr = (String) response.get("birthday");
                        Log.d("UserEditActivity", "生日: " + birthdayStr);  // 输出后端返回的生日字符串
                        String avatarPath = (String) response.get("avatar");
                        Log.d("UserEditActivity", "Response: " + response);  // 输出整个响应内容

                        // 设置UI
                        usernameEditText.setText(username);
                        if ("男".equals(gender)) {
                            genderRadioGroup.check(R.id.radio_button_2);
                        } else if ("女".equals(gender)) {
                            genderRadioGroup.check(R.id.radio_button_3);
                        } else {
                            genderRadioGroup.check(R.id.radio_button_1);  // 默认保密
                        }

                        try {
                            if (birthdayStr != null) {
                                birthday = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayStr);  // 将后端返回的日期字符串转换为 Date
                                birthdayTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(birthday));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // 设置头像
                        if (avatarPath != null && !avatarPath.isEmpty()) {
                            avatarUri = Uri.parse(avatarPath);
                            avatarImg.setImageURI(avatarUri);  // 加载头像
                        }
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ApiService", "获取用户信息失败: " + errorMessage);
                runOnUiThread(() -> Toast.makeText(UserEditActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * 显示头像的 Dialog
     */
    private void showAvatarDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_view_avatar); // 头像查看的布局
        ImageView avatarPreview = dialog.findViewById(R.id.avatarPreview);

        // 设置当前头像
        avatarPreview.setImageDrawable(avatarImg.getDrawable());

        // 点击 Dialog 关闭
        avatarPreview.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * 让用户从相册中选择头像
     */
    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_SELECT_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    avatarUri = imageUri;  // 保存选中的图片Uri
                    try {
                        // 适配 Android 9+ 使用 ImageDecoder
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            avatarImg.setImageBitmap(bitmap);  // 显示选中的头像
                        } else {
                            // 适配低版本使用 BitmapFactory
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            avatarImg.setImageBitmap(bitmap);  // 显示选中的头像
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(UserEditActivity.this, "加载图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}
