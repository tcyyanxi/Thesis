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

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.ArticleAdapter;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/13 22:13
 * @Decription:
 */
public class BArticleListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList = new ArrayList<>();
    private String category;

    // 通过构造器传递 category
    public BArticleListFragment(String category) {
        this.category = category;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_article_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        // 设置 LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getArticle(category);

        return view;
    }

    private void getArticle(String category) {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getArticle(category, new ApiServiceImpl.ApiCallback<List<Article>>() {
            @Override
            public void onSuccess(List<Article> response) {
                // 数据请求成功，更新RecyclerView
                articleList.clear();
                articleList.addAll(response);

                // 更新 adapter，通知 RecyclerView 刷新
                articleAdapter = new ArticleAdapter(getContext(), articleList);
                recyclerView.setAdapter(articleAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                // 请求失败，显示错误信息
                Log.e("BArticleListFragment", "Error: " + errorMessage);
            }
        });
    }
}
