package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.WordReviewListAdapter;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.WordDTO;

import java.util.ArrayList;
import java.util.List;

public class WordReviewListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WordReviewListAdapter wordListAdapter;
    private List<WordDTO> wordList = new ArrayList<>();
    private List<WordDTO> selectedWords = new ArrayList<>(); // 记录选中的单词
    private String selectedDate;
    private Long userId;
    private Button reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_review_list);

        // 获取传递的选中日期
        selectedDate = getIntent().getStringExtra("selectedDate");

        // 获取 SharedPreferences 中的 userId
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        userId = preferences.getLong("id", -1);

        // 初始化 RecyclerView 和适配器
        recyclerView = findViewById(R.id.wordListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wordListAdapter = new WordReviewListAdapter(wordList, this);
        recyclerView.setAdapter(wordListAdapter);

        // "去复习" 按钮
        reviewButton = findViewById(R.id.Txt_listen);
        reviewButton.setOnClickListener(v -> {
            if (!selectedWords.isEmpty()) {
                Intent intent = new Intent(WordReviewListActivity.this, ListenActivity.class);
                intent.putExtra("wordList", new ArrayList<>(selectedWords)); // 传输选中的单词列表
                startActivity(intent);
            } else {
                Toast.makeText(this, "请至少选择一个单词", Toast.LENGTH_SHORT).show();
            }
        });

        // 获取单词列表
        getWordsByUserIdAndDate(userId, selectedDate);
    }

    // 更新选中单词列表
    public void updateSelectedWords(WordDTO word, boolean isChecked) {
        if (isChecked) {
            selectedWords.add(word);
        } else {
            selectedWords.remove(word);
        }
    }

    // 从后台获取单词列表
    private void getWordsByUserIdAndDate(Long id, String date) {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getWordsByUserIdAndDate(id, date, new ApiServiceImpl.ApiCallback<List<WordDTO>>() {
            @Override
            public void onSuccess(List<WordDTO> words) {
                wordList.clear();
                wordList.addAll(words);
                wordListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(WordReviewListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 添加 isWordSelected() 方法，用于判断单词是否已被选中
    public boolean isWordSelected(WordDTO word) {
        return selectedWords.contains(word);
    }

}

