package com.software.androidthesis.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:42
 * @Decription:
 */
public class BFragment extends Fragment {

    private ViewPager2 viewPager2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.leftRv);
        viewPager2 = view.findViewById(R.id.viewPager);
        List<String> categoryList =new ArrayList<>(Arrays.asList(
                "精选阅读","人工智能","前沿技术","太空宇宙","生物医疗","自然科学",
                "环境生态","历史文化","艺术文学","休闲生活","社会现象","成长教育",
                "心理感情"));

        //左列表adapter
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter.setOnItemClickListener(category -> {
            //替换子fragment
            Log.d("CFragment", "onItemClick: " + category);
            int position = categoryList.indexOf(category);
            replaceChildFragment(position);
            //
        });
        recyclerView.setAdapter(categoryAdapter);
        return view;
    }

    //切换子fragment
    private void replaceChildFragment(int position) {
        // 设置ViewPager2到指定位置
        if (viewPager2 != null) { // 防止空指针异常
            viewPager2.setCurrentItem(position, false); // 设置ViewPager2到指定位置
        } else {
            Log.e("BFragment", "viewPager2 is null when trying to replace fragment");
        }
    }
}
