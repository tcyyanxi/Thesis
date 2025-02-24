package com.software.androidthesis.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.software.androidThesis.R;
import com.software.androidthesis.Activity.UserEditActivity;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.viewmodel.BadgeViewModel;

import java.util.Map;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:43
 * @Decription:
 */
public class DFragment extends Fragment {

    private ImageView avatar;
    private TextView nameText, idText;
    private Button userEditBtn;
    private Long userId;
    private String gender;
    private String username;
    private String birthdayStr;
    private Uri avatarUri;

    private EditText editText;
    private BadgeViewModel badgeViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d, container, false);
        userEditBtn = view.findViewById(R.id.userEditBtn);
        avatar = view.findViewById(R.id.image_avatar);
        nameText = view.findViewById(R.id.text_name);
        idText = view.findViewById(R.id.text_id);
        editText = view.findViewById(R.id.edit_word_count);

        // 设置 EditText 初始值为 10
        editText.setText("10");
        // 让 EditText 只允许输入数字（不会弹出其他字符键盘）
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // 获取 ViewModel
        badgeViewModel = new ViewModelProvider(requireActivity()).get(BadgeViewModel.class);
        // 获取用户ID（从SharedPreferences获取）
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        userId = preferences.getLong("id", -1);  // 从SharedPreferences获取已登录用户的ID

        // 检查是否成功获取用户ID
        if (userId == -1) {
            Toast.makeText(getActivity(), "用户未登录或ID获取失败", Toast.LENGTH_SHORT).show();
            getActivity().finish();  // 如果用户ID获取失败，退出当前Activity
            return view;
        }

        // 在Fragment加载时尝试获取用户信息
        fetchUserInfo(userId);

        // 点击按钮进入用户编辑页面
        userEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserEditActivity.class);
            startActivity(intent);
        });

        // 监听 EditText 焦点变化
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateAndUpdateEditText();
            }
        });



        return view;
    }

    // 判断是否为数字
    private boolean isNumeric(String str) {
        try {
            return Integer.parseInt(str) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 更新底部导航栏角标
    private void updateNavigationBadge(String number) {
        BottomNavigationView navigation = getActivity().findViewById(R.id.nav_view);
        BadgeDrawable badge = navigation.getOrCreateBadge(R.id.navigation_b);

        if (number.isEmpty() || Integer.parseInt(number) == 0) {
            badge.setVisible(false);
        } else {
            badge.setNumber(Integer.parseInt(number));
            badge.setVisible(true);
        }
    }

    // **方法：验证输入是否合法**
    private void validateAndUpdateEditText() {
        String inputText = editText.getText().toString().trim();

        if (!isNumeric(inputText) || Integer.parseInt(inputText) < 10) {
            Toast.makeText(getActivity(), "输入不能小于10，已自动调整", Toast.LENGTH_SHORT).show();
            editText.setText("10"); // 强制调整为 10
        }

        // 更新 ViewModel 和导航角标
        badgeViewModel.setBadgeNumber(editText.getText().toString());
        updateNavigationBadge(editText.getText().toString());
    }


    /**
     * 从后端获取用户信息并填充到 UI 中
     */
    private void fetchUserInfo(Long userId) {
        ApiServiceImpl apiServiceImpl = new ApiServiceImpl();
        apiServiceImpl.getUserInfo(userId, new ApiServiceImpl.ApiCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                getActivity().runOnUiThread(() -> {
                    // 假设后端返回的用户数据包含 "username", "gender", "birthday", "avatar"
                    if (response != null) {
                        username = (String) response.get("username");
                        gender = (String) response.get("gender");
                        birthdayStr = (String) response.get("birthday");
                        Log.d("DFragment", "生日: " + birthdayStr);  // 输出后端返回的生日字符串
                        String avatarPath = (String) response.get("avatar");
                        Log.d("DFragment", "Response: " + response);  // 输出整个响应内容

                        // 设置 UI
                        nameText.setText(username);  // 设置用户名
                        // 将 userId 转换为字符串，并确保它是 10 位数，不足十位时前面补零
                        String userIdString = String.format("%010d", userId);

                        idText.setText("ID: " + userIdString);  // 设置用户 ID

                        // 设置头像
                        if (avatarPath != null && !avatarPath.isEmpty()) {
                            avatarUri = Uri.parse(avatarPath);
                            avatar.setImageURI(avatarUri);  // 加载头像
                        }
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ApiService", "获取用户信息失败: " + errorMessage);
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
