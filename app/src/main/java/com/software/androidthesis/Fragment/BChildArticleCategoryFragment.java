package com.software.androidthesis.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/3 13:30
 * @Decription:
 */
public class BChildArticleCategoryFragment extends Fragment {

    private ViewPager2 viewPager2;
    private CategoryAdapter categoryAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_article_child, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.leftRv);
        viewPager2 = view.findViewById(R.id.viewPager);
        viewPager2.setUserInputEnabled(false);  // 禁用滑动

        List<String> categoryList = new ArrayList<>(Arrays.asList(
                "精选阅读", "人工智能", "前沿技术", "太空宇宙", "生物医疗", "自然科学",
                "环境生态", "历史文化", "艺术文学", "休闲生活", "社会现象", "成长教育", "心理感情"));

        // 设置左侧分类的 adapter
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoryAdapter);

        // 设置点击事件，切换到对应的分类
        categoryAdapter.setOnItemClickListener(category -> {
            Log.d("BFragment", "onItemClick: " + category);
            int position = categoryList.indexOf(category);
            replaceChildFragment(position, category);
        });

        // 设置 ViewPager2 Adapter
        viewPager2.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @Override
            public Fragment createFragment(int position) {
                // 创建新的 BArticleListFragment 并传递 category
                String category = categoryList.get(position);
                return new BArticleListFragment(category);
            }

            @Override
            public int getItemCount() {
                return categoryList.size();
            }
        });

        // 设置默认选中项为“精选阅读”
        replaceChildFragment(0, categoryList.get(0));

        return view;
    }

    // 切换子 fragment
    private void replaceChildFragment(int position, String category) {
        // 设置 ViewPager2 到指定位置
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(position, false); // 设置 ViewPager2 到指定位置
        } else {
            Log.e("BFragment", "viewPager2 is null when trying to replace fragment");
        }
    }
}
