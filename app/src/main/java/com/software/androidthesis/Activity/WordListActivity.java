package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.WordListAdapter;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.Word;
import com.software.androidthesis.viewmodel.BadgeViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordListActivity extends AppCompatActivity {

    private RecyclerView wordListView;
    private List<Word> wordList = new ArrayList<>();
    private WordListAdapter adapter;
    private String book, unit;
    private BadgeViewModel badgeViewModel;
    private Long userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        wordListView = findViewById(R.id.wordListView);
        wordListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordListAdapter(this, wordList);
//        checkBox = findViewById(R.id.checkbox_word);
        wordListView.setAdapter(adapter);

        // 获取用户ID
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        userId = preferences.getLong("id", -1);
        Log.d("WordListActivity", "用户ID: " + userId);

        // 获取 BadgeViewModel
        badgeViewModel = new ViewModelProvider(this).get(BadgeViewModel.class);

        // 获取 Intent 中传递的 book 和 unit 参数
        book = getIntent().getStringExtra("book");
        unit = getIntent().getStringExtra("unit");

        // 获取单词列表
        fetchWords();

        // 观察 badgeNumber 的变化，选择单词
        badgeViewModel.getBadgeNumber().observe(this, badgeNumber -> {
            if (badgeNumber != null) {
                Log.d("WordListActivity", "接收到的 BadgeNumber: " + badgeNumber);
            }
        });

        // 完成按钮点击事件
        Button completeButton = findViewById(R.id.Txt_finish);
        completeButton.setOnClickListener(v -> {
            sendSelectedWordsToBackend();
            updatePermanentSelection();
            updateBadgeNumber();
        });
    }


    // 获取单词列表
    private void fetchWords() {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getWords(book, unit, new ApiServiceImpl.ApiCallback<List<Word>>() {
            @Override
            public void onSuccess(List<Word> words) {
                if (words != null && !words.isEmpty()) {
                    wordList.clear();
                    wordList.addAll(words);
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        Log.d("WordListActivity", "成功获取单词列表, 数量: " + words.size());

                        // 在此时调用 fetchSelectedWords 确保数据已加载
                        fetchSelectedWords();  // 调用已选单词的获取方法
                    });

                    // 打印并清洗前端单词
                    for (Word word : words) {
                        String cleanedWord = cleanWord(word.getWord());  // 清洗单词
                        Log.d("WordListActivity", "清洗后的前端单词: " + cleanedWord);  // 打印清洗后的单词
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("WordListActivity", "获取单词失败: " + errorMessage);
            }
        });
    }

    private void fetchSelectedWords() {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getSelectedWords(userId, new ApiServiceImpl.ApiCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> selectedWords) {
                // 清洗后端返回的单词
                for (String word : selectedWords) {
                    String cleanedWord = cleanWord(word);
                    Log.d("WordListActivity", "清洗后的后端单词: " + cleanedWord);
                }
                // 处理已选单词与前端单词的比较等操作
                compareWords(selectedWords);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("WordListActivity", "获取已选单词失败: " + errorMessage);
            }
        });
    }

    // 清洗单词（去掉非字母和数字的字符，统一小写）
    private String cleanWord(String word) {
        if (word == null) return "";
        word = word.trim().toLowerCase();  // 统一转换为小写，去除前后空格
        word = word.replaceAll("[^a-zA-Z0-9]", "");  // 移除非字母和数字的字符
        return word;
    }
    private void compareWords(List<String> selectedWords) {
        // 记录匹配的单词
        List<Word> matchedWords = new ArrayList<>();

        // 循环遍历 wordList 中的单词，与 selectedWords 比较
        for (Word word : wordList) {
            String cleanedWord = cleanWord(word.getWord()); // 清洗单词
            for (String selectedWord : selectedWords) {
                if (cleanedWord.equalsIgnoreCase(cleanWord(selectedWord))) {
                    word.setSelected(true); // 标记为已选
                    word.setMatched(true);  // 标记为已匹配
                    matchedWords.add(word); // 将匹配的单词加入已匹配单词列表
                    break; // 一旦找到匹配的单词，跳出内层循环
                }
            }
        }

        // 更新 UI 或执行其他操作（例如，更新适配器）
        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
        });

        // 输出匹配结果
        Log.d("WordListActivity", "匹配的单词: " + matchedWords.size() + "个");
        for (Word word : matchedWords) {
            Log.d("WordListActivity", "已选单词: " + word.getWord());
        }
    }


    // 发送选中的单词到后端
    private void sendSelectedWordsToBackend() {
        List<String> selectedWords = new ArrayList<>();
        for (Word word : wordList) {
            // 只发送没有匹配的单词
            if (word.isSelected() && !word.isMatched()) {
                selectedWords.add(word.getWord());
                Log.d("WordListActivity", "选中且未匹配的单词: " + word.getWord());
            }
        }
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.addUserWords(userId, selectedWords);
    }

    // 更新永久选中状态
    private void updatePermanentSelection() {
        for (Word word : wordList) {
            if (word.isSelected()) {
                word.setPermanentSelected(true);
                Log.d("WordListActivity", "永久选中单词: " + word.getWord());
            }
        }
        adapter.notifyDataSetChanged();
    }

    // 更新徽章数量
    private void updateBadgeNumber() {
        int remainingCount = Integer.parseInt(badgeViewModel.getBadgeNumber().getValue() != null ? badgeViewModel.getBadgeNumber().getValue() : "0") - getSelectedWordsCount();
        badgeViewModel.setBadgeNumber(String.valueOf(remainingCount));
        Log.d("WordListActivity", "更新 ViewModel 中的剩余数量: " + remainingCount);
    }

    // 获取选中的单词数量
    private int getSelectedWordsCount() {
        int count = 0;
        for (Word word : wordList) {
            if (word.isSelected()) {
                count++;
            }
        }
        return count;
    }

    // 按顺序选择单词
    private void selectWordsToCheckbox(int selectedCount) {
        Log.d("WordListActivity", "当前选中的单词数量: " + selectedCount);
        int count = 0;

        // 遍历 wordList，按顺序选择未匹配的单词
        for (Word word : wordList) {
            // 只选择未匹配的单词
            if (!word.isSelected() && !word.isPermanentSelected() && count < selectedCount) {
                word.setSelected(true);  // 设置为已选中
                count++;
                Log.d("WordListActivity", "选中单词: " + word.getWord());
            }
        }

        // 更新 RecyclerView 以反映选中状态的变化
        adapter.notifyDataSetChanged();
    }

}
