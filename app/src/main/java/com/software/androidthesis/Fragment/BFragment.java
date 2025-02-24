package com.software.androidthesis.Fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.software.androidThesis.R;
import com.software.androidthesis.viewmodel.BadgeViewModel;


/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:42
 * @Decription:
 */
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.shape.MaterialShapeDrawable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.badge.BadgeDrawable;

public class BFragment extends Fragment {

    private BadgeViewModel badgeViewModel;
    private Button button1, button2;
    private TextView badgeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);

        button1 = view.findViewById(R.id.button1);
        button2 = view.findViewById(R.id.button2);
        badgeTextView = view.findViewById(R.id.badge_text);

        // 获取 ViewModel
        badgeViewModel = new ViewModelProvider(requireActivity()).get(BadgeViewModel.class);

        // 监听 ViewModel 角标数据的变化
        badgeViewModel.getBadgeNumber().observe(getViewLifecycleOwner(), this::updateBadge);

        button1.setOnClickListener(v -> {
            selectButton(button1);
            loadChildFragment(new BChildArticleCategoryFragment());
        });

        button2.setOnClickListener(v -> {
            selectButton(button2);
            loadChildFragment(new BChildWordFragment());
        });

        // 默认加载第一个按钮的样式和第一个子 Fragment
        selectButton(button1);
        loadChildFragment(new BChildArticleCategoryFragment());

        return view;
    }

    // 更新角标显示
    private void updateBadge(String number) {
        if (number == null || number.isEmpty() || Integer.parseInt(number) == 0) {
            badgeTextView.setVisibility(View.GONE);  // 如果数字为0或者为空，隐藏角标
        } else {
            badgeTextView.setText(number);  // 显示角标
            badgeTextView.setVisibility(View.VISIBLE);
            badgeTextView.bringToFront();  // 确保角标显示在最前面
        }
        updateNavigationBadge(number);
    }

    // 更新底部导航栏的角标
    private void updateNavigationBadge(String number) {
        BottomNavigationView navigation = getActivity().findViewById(R.id.nav_view);
        BadgeDrawable badge = navigation.getOrCreateBadge(R.id.navigation_b);

        if (number == null || number.isEmpty() || Integer.parseInt(number) == 0) {
            badge.setVisible(false);  // 如果数字为0或者为空，隐藏底部导航栏角标
        } else {
            badge.setNumber(Integer.parseInt(number));  // 设置角标数字
            badge.setVisible(true);  // 显示底部导航栏角标
        }
    }

    // 选中按钮样式
    private void selectButton(Button selectedButton) {
        button1.setBackgroundResource(R.drawable.button_unselected_background);  // 未选中的样式
        button2.setBackgroundResource(R.drawable.button_unselected_background);  // 未选中的样式
        selectedButton.setBackgroundResource(R.drawable.button_selected_background);  // 选中的样式
    }

    // 加载子 Fragment
    private void loadChildFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, fragment);  // 替换子 Fragment
        transaction.commit();
    }
}
