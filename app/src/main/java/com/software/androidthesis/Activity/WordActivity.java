package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.software.androidThesis.R;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.Word;

public class WordActivity extends AppCompatActivity {
    private TextView wordTextView, proTextView, meanTextView;
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        // 初始化 TextViews
        wordTextView = findViewById(R.id.wordTextView);
        proTextView = findViewById(R.id.proTextView);
        meanTextView = findViewById(R.id.meanTextView);

        // 获取传递的单词
        word = getIntent().getStringExtra("word");

        // 获取该单词的详细信息
        fetchWordDetails(word);
    }

    private void fetchWordDetails(String word) {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getWordAll(word, new ApiServiceImpl.ApiCallback<Word>() {
            @Override
            public void onSuccess(Word wordDetails) {
                if (wordDetails != null) {
                    // 显示单词信息
                    wordTextView.setText("Word: " + wordDetails.getWord());
                    proTextView.setText("Pronunciation: " + wordDetails.getPro());
                    meanTextView.setText("Meaning: " + wordDetails.getMean());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ApiService", "获取单词详情失败: " + errorMessage);
            }
        });
    }
}
