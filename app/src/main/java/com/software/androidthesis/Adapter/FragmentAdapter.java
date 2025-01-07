package com.software.androidthesis.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.software.androidthesis.Fragment.AFragment;
import com.software.androidthesis.Fragment.BFragment;
import com.software.androidthesis.Fragment.CFragment;
import com.software.androidthesis.Fragment.DFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:39
 * @Decription:
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    // 私有成员mFragments，加载页面碎片
    private List<Fragment> mFragments = new ArrayList<>();

    public FragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        // 加载初始化Fragment
        mFragments.add(new AFragment());
        mFragments.add(new BFragment());
        mFragments.add(new CFragment());
        mFragments.add(new DFragment());
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mFragments.get(0);
                break;
            case 1:
                fragment = mFragments.get(1);
                break;
            case 2:
                fragment = mFragments.get(2);
                break;
            case 3:
                fragment = mFragments.get(3);
                break;
            default:
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}

