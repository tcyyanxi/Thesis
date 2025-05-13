package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.UserArticleAdapter;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.UserArticle;

import java.util.ArrayList;
import java.util.List;

public class ArticleScoreActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserArticleAdapter adapter;
    private List<UserArticle> userArticles = new ArrayList<>();
    private int articleId;
    private ApiServiceImpl apiService;
    private Long userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_score);

        // 获取 RecyclerView
        recyclerView = findViewById(R.id.wordListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 从 Intent 中获取文章的 id
        Intent intent = getIntent();
        articleId = intent.getIntExtra("articleId", -1);
        // 获取用户ID（从SharedPreferences获取）
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        userId = preferences.getLong("id", -1);

        // 初始化适配器
        adapter = new UserArticleAdapter(userArticles);
        recyclerView.setAdapter(adapter);

        // 获取数据
        apiService = new ApiServiceImpl();
        fetchData();
    }

    private void fetchData() {
        apiService.getUserArticle(userId, articleId, new ApiServiceImpl.ApiCallback<List<UserArticle>>() {
            @Override
            public void onSuccess(List<UserArticle> data) {
                Log.d("ArticleScoreActivity", "收到的数据: " + data.size()); // 检查数据大小
                userArticles.clear();
                userArticles.addAll(data);
                adapter.notifyDataSetChanged(); // 更新列表
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ArticleScoreActivity", "获取数据失败: " + errorMessage);
            }
        });
    }

}
