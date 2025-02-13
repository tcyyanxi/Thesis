package com.software.androidthesis.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.software.androidThesis.R;


/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:42
 * @Decription:
 */
public class BFragment extends Fragment {

    private MaterialButtonToggleGroup buttonGroup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);
        // 初始化按钮
        buttonGroup = view.findViewById(R.id.buttonGroup);

        // 设置按钮组的选择监听器
        buttonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return; // 如果按钮被取消选中，直接返回
            switch (checkedId) {
                case R.id.button1:
                    loadChildFragment(new BChildArticleCategoryFragment());
                    break;
                case R.id.button2:
                    loadChildFragment(new BChildWordFragment());
                    break;
            }
        });

        // 默认加载第一个子 Fragment 并选中第一个按钮
        loadChildFragment(new ChildFragment1());
        buttonGroup.check(R.id.button1);
        return view;
    }

    private void loadChildFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, fragment);
        transaction.commit();
    }


}
