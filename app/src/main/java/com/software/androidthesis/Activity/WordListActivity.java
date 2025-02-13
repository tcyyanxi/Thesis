package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.software.androidThesis.R;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordListActivity extends AppCompatActivity {
    private ListView wordListView;
    private List<String> wordList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String book, unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        wordListView = findViewById(R.id.wordListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordList);
        wordListView.setAdapter(adapter);

        book = getIntent().getStringExtra("book");
        unit = getIntent().getStringExtra("unit");

        fetchWords();

        // 添加点击事件监听器
        wordListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedWord = wordList.get(position);  // 获取选中的单词
            Intent intent = new Intent(WordListActivity.this, WordActivity.class);
            intent.putExtra("word", selectedWord);  // 传递选中的单词
            startActivity(intent);
        });
    }

    private void fetchWords() {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getWords(book, unit, new ApiServiceImpl.ApiCallback<List<Word>>() {
            @Override
            public void onSuccess(List<Word> words) {
                if (words != null && !words.isEmpty()) {
                    // 假设 Word 对象有一个 getWord() 方法返回单词字符串
                    for (Word word : words) {
                        wordList.add(word.getWord());  // 添加单词到 wordList
                    }

                    // 更新 UI
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ApiService", "获取单词失败: " + errorMessage);
            }
        });
    }
}
