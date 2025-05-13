package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.software.androidThesis.R;
import com.software.androidthesis.api.ApiService;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.WordDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListenActivity extends AppCompatActivity {

    private String selectedDate;
    private Long userId;
    private List<WordDTO> wordList = new ArrayList<>();
    private int currentWordIndex = 0;
    private String correctWord;

    private EditText editTextWord;
    private Button buttonPlayAudio;
    private Button buttonCheck;
    private TextView textViewMeaning;
    private TextView textViewCorrectWord;
    private Button btnFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        // 获取用户ID（从SharedPreferences获取）
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        userId = preferences.getLong("id", -1);
        Log.d("ListenActivity", "用户ID已从SharedPreferences: " + userId);

        // 获取传递的选中日期
        selectedDate = getIntent().getStringExtra("selectedDate");

        wordList = (ArrayList<WordDTO>) getIntent().getSerializableExtra("wordList");

        // 初始化UI组件
        editTextWord = findViewById(R.id.edit_word);
        buttonPlayAudio = findViewById(R.id.button1);
        buttonCheck = findViewById(R.id.button2);
        textViewMeaning = findViewById(R.id.text_mean);
        textViewCorrectWord = findViewById(R.id.textViewCorrectWord);
        btnFin = findViewById(R.id.finish);

        // 判断逻辑
        if (TextUtils.isEmpty(selectedDate) && !wordList.isEmpty()) {
            // selectedDate 为空，但 wordList 不为空，直接使用 wordList
            initializeWords();
        } else {
            // selectedDate 不为空 或 wordList 为空，则从后端获取数据
            getWordsByUserIdAndDate(userId, selectedDate);
        }

        // 播放音频按钮点击事件
        buttonPlayAudio.setOnClickListener(v -> playAudio());

        // 检查单词按钮点击事件
        buttonCheck.setOnClickListener(v -> checkWord());

        // 完成按钮点击事件
        btnFin.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // 播放当前单词的音频
    private void playAudio() {
        if (!TextUtils.isEmpty(correctWord)) {
            String audioUrl = "https://dict.youdao.com/dictvoice?audio=" + correctWord + "&type=2";
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> mp.start());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ListenActivity.this, "音频播放失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkWord() {
        String userWord = editTextWord.getText().toString().trim();  // 去除前后空格
        if (TextUtils.isEmpty(userWord)) {
            Toast.makeText(this, "请输入单词", Toast.LENGTH_SHORT).show();
        } else if (userWord.equalsIgnoreCase(correctWord.trim())) {  // 比较时忽略大小写和空格
            Toast.makeText(this, "正确", Toast.LENGTH_SHORT).show();
            editTextWord.setBackgroundColor(Color.TRANSPARENT);
            textViewCorrectWord.setVisibility(View.GONE);
            currentWordIndex++;

            // 如果当前是最后一个单词，不清空输入框，只改变按钮的显示
            if (currentWordIndex < wordList.size()) {
                // 不是最后一个单词，更新当前单词
                correctWord = wordList.get(currentWordIndex).getWord();
                textViewMeaning.setText(wordList.get(currentWordIndex).getMean());

                // 自动播放下一个单词的音频
                playAudio();
            } else {
                // 如果当前是最后一个单词，隐藏检查按钮，显示完成按钮
                btnFin.setVisibility(View.VISIBLE);
                buttonCheck.setVisibility(View.INVISIBLE);
                // 不清空输入框
            }

            // 清空输入框的操作只在非最后一个单词时进行
            if (currentWordIndex < wordList.size()) {
                editTextWord.setText("");
            }
        } else {
            Toast.makeText(this, "错误，请再试一次", Toast.LENGTH_SHORT).show();
            editTextWord.setBackgroundColor(Color.parseColor("#ffb5a0"));
            textViewCorrectWord.setText("正确的单词是: " + correctWord);
            textViewCorrectWord.setVisibility(View.VISIBLE);
        }
    }


    // 初始化单词列表
    private void initializeWords() {
        if (!wordList.isEmpty()) {
            correctWord = wordList.get(currentWordIndex).getWord();
            textViewMeaning.setText(wordList.get(currentWordIndex).getMean());
            playAudio();
        } else {
            Toast.makeText(this, "暂无复习单词", Toast.LENGTH_SHORT).show();
        }
    }

    // 从后端获取复习单词
    public void getWordsByUserIdAndDate(Long id, String date) {
        if (id == -1) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getWordsByUserIdAndDate(id, date, new ApiServiceImpl.ApiCallback<List<WordDTO>>() {
            @Override
            public void onSuccess(List<WordDTO> words) {
                wordList = words;

                // 打印获取到的单词列表
                if (words != null && !words.isEmpty()) {
                    for (WordDTO word : words) {
                        Log.d("ListenActivity", "Word: " + word.getWord() + ", Meaning: " + word.getMean());
                    }
                } else {
                    Log.d("ListenActivity", "没有获取到单词");
                }

                initializeWords();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ListenActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
