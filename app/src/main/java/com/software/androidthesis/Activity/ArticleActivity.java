package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.cloud.SpeechUtility;
import com.software.androidThesis.R;
import com.software.androidthesis.util.AudioUtils;
import com.software.androidthesis.view.NoUnderlineMovementMethod;
import com.software.androidthesis.view.ToastView;
import com.software.androidthesis.view.WaveView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleActivity extends AppCompatActivity {

    private static final String TAG = "ArticleActivity";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_RECORD_AUDIO = 101;
    private static final int REQUEST_CODE_MIC_PERMISSION = 103;
    private TextView articleTextView, resultTextView;
    private SpannableString spannableText; // 用于操作的 SpannableString
    private String selectedSentence; // 当前选中的句子
    private SpeechEvaluator mIse;

    private WaveView waveView;
    private Button button2;//录音按钮
    private Button btnSpeak;
    private Button nextButton;  // 新增的"下一条"按钮
    private Button backButton;

    private Button finishButton;

    private String[] sentences; // 存储拆分后的句子
    private int currentSentenceIndex;  // 当前选中的句子索引
    private int wordSum = 0;
    private final Set<Integer> evaluatedSentenceIndices = new HashSet<>();
    private SimpleExoPlayer player;//用于播放示例音频
    private SimpleExoPlayer player1;  // 用于播放音频
    private String audioFilePath = null;  // 录音文件路径
    private String mLastResult;  // 存储评测结果
    private int sum;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        player = new SimpleExoPlayer.Builder(this).build();
        // 初始化播放器
        player1 = new SimpleExoPlayer.Builder(this).build();
        player1.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    // 播放完成后，处理评分并播放语音
                    handleScoreFeedback(sum);
                }
            }
        });

        // 请求权限
        checkAndRequestPermissions();

        // 初始化科大讯飞 SDK
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=c89e39a8");

        // 初始化语音评测器
        mIse = SpeechEvaluator.createEvaluator(this, null);
        setParams();  // 设置评测参数

        if (mIse == null) {
            Log.e(TAG, "SpeechEvaluator 初始化失败");
            Toast.makeText(this, "语音评测初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }


        articleTextView = findViewById(R.id.articleTextView);
        resultTextView = findViewById(R.id.textView3);
        waveView = findViewById(R.id.waveView);
        button2 = findViewById(R.id.button2);
        btnSpeak = findViewById(R.id.button1);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        finishButton = findViewById(R.id.finishButton);

        // 隐藏“下一条”按钮，直到测评完成
        nextButton.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        finishButton.setVisibility(View.GONE);

        // 设置波浪参数
        setupWaveView();

        // 初始状态：隐藏波浪按钮
        waveView.setVisibility(View.GONE);

        // 示例文章内容
        String articleContent = "This is the first sentence! Here is another one; it uses a semicolon. What about this one?";

        // 使用 SpannableString 包装文章内容
        spannableText = new SpannableString(articleContent);

        // 将文章按句分割，支持 !；：等符号
        sentences = articleContent.split("(?<=[.!?;:])");
        for (String sentence : sentences) {
            Log.d(TAG, "Sentence: " + sentence); // 输出每个句子
        }

        // 确保 sentences 不为 null 且不为空
        if (sentences == null || sentences.length == 0) {
            Log.e(TAG, "Sentences array is null or empty!");
            return;  // 提前返回，防止出现 NullPointerException
        }



        // 初始化时将所有句子的颜色设置为黑色
        int startIndex = 0;
        for (String sentence : sentences) {
            int endIndex = startIndex + sentence.length();
            spannableText.setSpan(new ForegroundColorSpan(Color.BLACK), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            startIndex = endIndex;
        }


        // 设置 SpannableString 到 TextView
        articleTextView.setText(spannableText);
        articleTextView.setMovementMethod(new android.text.method.LinkMovementMethod());

        // 默认选中第一条
        currentSentenceIndex = 0;
        selectedSentence = sentences[currentSentenceIndex]; // 给 selectedSentence 设置为第一个句子
//        resultTextView.setText("当前选中句子：" + selectedSentence);  // 显示当前选中的句子
        makeSentenceClickable(sentences[currentSentenceIndex], 0, sentences[currentSentenceIndex].length());

        // 使用自定义的 MovementMethod 去除下划线
        articleTextView.setMovementMethod(NoUnderlineMovementMethod.getInstance());



        // 按钮点击事件
        btnSpeak.setOnClickListener(v -> {
            if (selectedSentence != null && !selectedSentence.isEmpty()) {
                String word = null;
                try {
                    word = URLEncoder.encode(selectedSentence, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // 拼接音频 URL（这里使用有道的 TTS 服务）
                String audioUrl = "https://dict.youdao.com/dictvoice?audio=" + word + "&type=2";

                // 创建数据源
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"));

                // 创建媒体源
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(audioUrl));

                // 准备播放器
                player.setMediaSource(mediaSource);
                player.prepare();

                // 开始播放
                player.play();

                // 添加播放状态监听器
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        Player.Listener.super.onPlaybackStateChanged(playbackState);
                        if (playbackState == Player.STATE_ENDED) {
                            // 播放结束
//                            Toast.makeText(ArticleActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
                        } else if (playbackState == Player.STATE_BUFFERING) {
                            // 缓冲中
                            ToastView.showCustomToast(ArticleActivity.this, "正在缓冲...");
//                            Toast.makeText(ArticleActivity.this, "正在缓冲...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(ArticleActivity.this, "请选择一个句子", Toast.LENGTH_SHORT).show();
            }
        });

        // 下一句按钮的点击事件
        nextButton.setOnClickListener(v -> {
            if (currentSentenceIndex < sentences.length - 1) {
                // 重置所有句子的颜色为默认颜色
                resetTextColor();

                wordSum += sentences[currentSentenceIndex].length(); // 累计当前句子的长度
                currentSentenceIndex++; // 进入下一句

                // 更新句子显示
                int start = wordSum;
                int end = sentences[currentSentenceIndex].length() + wordSum;
                makeSentenceClickable(sentences[currentSentenceIndex], start, end);

                // 更新 selectedSentence
                selectedSentence = sentences[currentSentenceIndex];

                // 隐藏 "下一条" 按钮，直到下一次测评完成
                nextButton.setVisibility(View.GONE);

                // 更新按钮的可见性
                updateButtonVisibility();
            } else {
                Log.d(TAG, "Already at the last sentence.");
            }
            resultTextView.setText("  ");
        });

        // 上一句按钮的点击事件
        backButton.setOnClickListener(v -> {
            if (currentSentenceIndex > 0) {
                // 重置所有句子的颜色为默认颜色
                resetTextColor();

                // 减小索引
                currentSentenceIndex--;
                wordSum -= sentences[currentSentenceIndex].length();

                // 更新句子显示
                int start = wordSum;
                int end = wordSum + sentences[currentSentenceIndex].length();
                makeSentenceClickable(sentences[currentSentenceIndex], start, end);

                // 更新 selectedSentence
                selectedSentence = sentences[currentSentenceIndex];

                // 更新按钮的可见性
                updateButtonVisibility();
            } else {
                Log.d(TAG, "Already at the first sentence.");
            }
            resultTextView.setText("  ");
        });

        // 设置按钮点击事件
        button2.setOnClickListener(v -> startRecording());
        waveView.setOnClickListener(v -> stopRecordingAndEvaluate());

    }

    private void checkAndRequestPermissions() {
        // 检查麦克风权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_MIC_PERMISSION);
        }

        // 检查存储权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_MIC_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "麦克风权限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "麦克风权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "存储权限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "存储权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void setupWaveView() {
        int waveHeightA = 30; // 波浪A的振幅
        float waveACycle = 0.04f; // 波浪A的周期
        int waveSpeedA = 3; // 波浪A的速度
        int waveHeightB = 15; // 波浪B的振幅
        float waveBCycle = 0.05f; // 波浪B的周期
        int waveSpeedB = 5; // 波浪B的速度
        int waveColor = Color.parseColor("#d6e3ff"); // 波浪的颜色
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_background3); // 圆形遮罩的位图

        waveView.setWaveParameters(waveHeightA, waveACycle, waveSpeedA, waveHeightB, waveBCycle, waveSpeedB, waveColor);
        waveView.setBallBitmap(bitmap);
    }


    // 更新句子的显示和颜色
    private void makeSentenceClickable(String sentence, int start, int end) {
        Log.d(TAG, "Sentence: " + sentence + ", Start: " + start + ", End: " + end);

//        resultTextView.setText("当前选中句子：" + sentence);

        // 设置当前句子的颜色为红色
        spannableText.setSpan(new ForegroundColorSpan(Color.parseColor("#aac7ff")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 更新 TextView
        articleTextView.setText(spannableText);

    }

    // 重置句子颜色
    private void resetTextColor() {
        if (sentences == null || sentences.length == 0) {
            Log.e(TAG, "Sentences array is null or empty!");
            return;
        }

        // 重新遍历每个句子并将其颜色重置为黑色
        int startIndex = 0;
        for (int i = 0; i < sentences.length; i++) {
            int endIndex = startIndex + sentences[i].length();

            // 如果句子已经被评测过，设置为蓝色
            if (evaluatedSentenceIndices.contains(i)) {
                spannableText.setSpan(new ForegroundColorSpan(Color.parseColor("#d6e3ff")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            // 否则设置为黑色
            else {
                spannableText.setSpan(new ForegroundColorSpan(Color.BLACK), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            startIndex = endIndex;
        }

        // 更新 TextView
        articleTextView.setText(spannableText);
    }


    // 更新按钮的可见性
    private void updateButtonVisibility() {
        if (currentSentenceIndex == 0) {
            // 第一条句子时隐藏 backButton
            backButton.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.VISIBLE);
        }

        // 判断当前句子是否已测评过，只有已测评过的句子才显示 nextButton
        if (evaluatedSentenceIndices.contains(currentSentenceIndex)) {
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
        }

        if (currentSentenceIndex == sentences.length - 1) {
            // 最后一条句子时隐藏 nextButton
            nextButton.setVisibility(View.GONE);
        }
    }

    private void updateAfterEvaluation(int currentIndex) {
        // 1. 更新所有句子的颜色，已测评的句子显示为蓝色，当前评测中的句子显示为红色
        int startIndex = 0;
        for (int i = 0; i < sentences.length; i++) {
            int endIndex = startIndex + sentences[i].length();

            // 先设置当前句子的颜色为红色，如果当前是正在评测的句子
            if (i == currentIndex) {
                spannableText.setSpan(new ForegroundColorSpan(Color.parseColor("#aac7ff")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            // 如果句子已经评测过，显示为蓝色
            else if (evaluatedSentenceIndices.contains(i)) {
                spannableText.setSpan(new ForegroundColorSpan(Color.parseColor("#d6e3ff")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            // 否则显示为黑色
            else {
                spannableText.setSpan(new ForegroundColorSpan(Color.BLACK), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            startIndex = endIndex;
        }

        // 更新 TextView 显示
        articleTextView.setText(spannableText);

        // 2. 更新按钮的可见性
        updateButtonVisibility();
    }


    // 设置参数
    private void setParams() {
        mIse.setParameter(SpeechConstant.LANGUAGE, "en_us");
        mIse.setParameter(SpeechConstant.ISE_CATEGORY, "read_sentence");
        mIse.setParameter(SpeechConstant.RESULT_LEVEL, "complete");
        mIse.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "5000");
        mIse.setParameter(SpeechConstant.VAD_BOS, "5000");
        mIse.setParameter(SpeechConstant.VAD_EOS, "1800");
        mIse.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");


    }

    // 获取录音文件存储路径并确保目录存在
    private void startRecording() {
        if (selectedSentence != null && !selectedSentence.isEmpty()) {
            // 隐藏 button2，显示 waveView
            button2.setVisibility(View.GONE);
            waveView.setVisibility(View.VISIBLE);

            File directory = new File(getExternalFilesDir(null), "audioFiles");
            if (!directory.exists()) {
                directory.mkdirs();  // 确保目录存在
            }

            audioFilePath = new File(directory, "ise_" + System.currentTimeMillis() + ".wav").getAbsolutePath();
            Log.d("ArticleActivity", "音频文件路径: " + audioFilePath);

            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                try {
                    audioFile.createNewFile();  // 如果文件不存在，创建文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mIse.setParameter(SpeechConstant.ISE_AUDIO_PATH, audioFilePath);


            // 开始录音
            mIse.startEvaluating(selectedSentence, null, mEvaluatorListener);
        } else {
            Toast.makeText(ArticleActivity.this, "请选择一个句子", Toast.LENGTH_SHORT).show();
        }
    }

    // 停止录音并获取测评结果
    private void stopRecordingAndEvaluate() {
        // 停止录音
        mIse.stopEvaluating();

        // 解析测评结果
        if (mLastResult != null) {
            parseScore(mLastResult);
        }

        // 隐藏 waveView，显示 button2
        waveView.setVisibility(View.GONE);
        button2.setVisibility(View.VISIBLE);

        // 播放录音前延迟 2 秒
        if (audioFilePath != null) {
            // 延迟 2 秒后播放录音
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 调用播放录音的函数
                    playAudio(audioFilePath);
                }
            }, 500); // 延迟  秒
        }
    }

    // 播放录音
    private void playAudio(String filePath) {
        File audioFile = new File(filePath);
        if (audioFile.exists() && audioFile.length() > 0) {
            // 如果文件存在且非空，播放音频
            Uri audioUri = Uri.parse(filePath);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(audioUri));

            player1.setMediaSource(mediaSource);
            player1.prepare();
            player1.play();
            Log.d(TAG, "播放音频: " + filePath);
        } else {
            // 如果文件不存在或为空，显示错误日志
            Log.e(TAG, "音频文件未找到或文件为空，路径: " + filePath);
            Toast.makeText(this, "音频文件未找到，请检查路径", Toast.LENGTH_SHORT).show();
        }
    }


    private void parseScore(String xmlResponse) {
        try {
            // 创建XmlPullParser对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8));
            parser.setInput(inputStream, null);

            // 变量用来存储评分信息
            String totalScore = "";
            List<String> wordScores = new ArrayList<>();
            List<String> syllableScores = new ArrayList<>();
            List<String> wordContents = new ArrayList<>();

            int eventType = parser.getEventType();
            String tagName = "";

            // 解析 XML
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = tag;
                        // 检查并提取 total_score
                        if ("read_chapter".equals(tagName)) {
                            String totalScoreString = parser.getAttributeValue(null, "total_score");
                            try {
                                // 将totalScore转换为浮动的整数，乘以20然后取整
                                sum = (int) (Float.parseFloat(totalScoreString) * 20);
                                Log.d(TAG, "总评分提取并处理: " + sum); // 输出处理后的总评分
                                resultTextView.setText("评分：" + sum); // 显示评分

                            } catch (NumberFormatException e) {
                                Log.e(TAG, "解析总评分失败: " + totalScoreString, e); // 如果转换失败，输出错误日志
                                resultTextView.setText("评分解析失败");
                            }
                        }
                        // 提取每个 word 中的评分
                        if ("word".equals(tagName)) {
                            String wordScore = parser.getAttributeValue(null, "total_score");
                            String wordContent = parser.getAttributeValue(null, "content");
                            wordScores.add(wordScore);
                            wordContents.add(wordContent);
                            Log.d(TAG, "提取单词评分: " + wordContent + " 评分: " + wordScore); // 输出单词评分
                        }
                        // 提取每个 syll 中的评分
                        if ("syll".equals(tagName)) {
                            String syllScore = parser.getAttributeValue(null, "syll_score");
                            syllableScores.add(syllScore);
                            Log.d(TAG, "提取音节评分: " + syllScore); // 输出音节评分
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = "";
                        break;
                }
                eventType = parser.next();
            }

            // 调用方法来高亮显示评分低于 4.5 的单词
            highlightLowScoringWords(wordScores, wordContents, currentSentenceIndex);

        } catch (Exception e) {
            Log.e(TAG, "解析 XML 结果时发生错误", e);
        }
    }

    private void handleScoreFeedback(int totalScore) {
        String bobao = "";
        // 判断评分，并设置对应的语音播报内容和按钮可见性
        if (totalScore > 90) {
            // 播报 "很优秀，下一个"
            bobao = "很优秀，下一个";
            nextButton.setVisibility(View.VISIBLE); // 显示"下一条"按钮
        } else if (totalScore >= 75 && totalScore <= 90) {
            // 播报 "良好，下一个"
            bobao = "良好，下一个";
            nextButton.setVisibility(View.VISIBLE); // 显示"下一条"按钮
        } else {
            // 播报 "再来一次"
            bobao = "再来一次";
            nextButton.setVisibility(View.INVISIBLE); // 隐藏"下一条"按钮
        }

        // 判断是否为最后一个句子
        if (isLastSentence()) {
            nextButton.setVisibility(View.INVISIBLE); // 隐藏"下一条"按钮
            finishButton.setVisibility(View.VISIBLE); // 显示"完成"按钮
            bobao = "恭喜你完成了"; // 添加最后一句话的提示
            finishButton.setVisibility(View.VISIBLE);
        }

        // 播放语音
        AudioUtils.getInstance().init(this); // 初始化语音对象
        AudioUtils.getInstance().speakText(bobao); // 播放语音
    }

    // 假设这个方法用来判断当前是否为最后一个句子
    private boolean isLastSentence() {
        // 判断条件：当前句子是最后一个
        return currentSentenceIndex == sentences.length - 1;
    }



    private void highlightLowScoringWords(List<String> wordScores, List<String> wordContents, int currentSentenceIndex) {
        // 获取已经设置样式的 SpannableString
        SpannableString spannableTextForWords = (SpannableString) articleTextView.getText();

        // 获取当前句子的起始位置和结束位置
        int startIndex = 0;
        for (int i = 0; i < currentSentenceIndex; i++) {
            startIndex += sentences[i].length();
        }
        int endIndex = startIndex + sentences[currentSentenceIndex].length();

        // 截取出当前句子的文本
        String currentSentence = spannableTextForWords.subSequence(startIndex, endIndex).toString();

        // 遍历每个单词和评分
        for (int i = 0; i < wordScores.size(); i++) {
            String wordContent = wordContents.get(i);
            String wordScore = wordScores.get(i);

            // 检查 wordContent 和 wordScore 是否为 null
            if (wordContent == null || wordScore == null) {
                Log.e(TAG, "单词或评分为 null，跳过该项");
                continue;  // 如果有任何一个为 null，跳过该项
            }

            // 判断评分是否低于 4.5，并且该单词是否在当前句子中
            try {
                float score = Float.parseFloat(wordScore);
                if (score < 4.5 && currentSentence.contains(wordContent)) {
                    // 正则匹配单词，确保匹配完整单词
                    Pattern pattern = Pattern.compile("\\b" + Pattern.quote(wordContent) + "\\b"); // 使用 \b 确保匹配完整单词
                    Matcher matcher = pattern.matcher(currentSentence);

                    // 查找匹配的单词，并应用红色样式
                    while (matcher.find()) {
                        int wordStartIndex = matcher.start();
                        int wordEndIndex = matcher.end();

                        // 将当前句子中匹配到的单词的位置转换为在整个文本中的位置
                        int globalStartIndex = startIndex + wordStartIndex;
                        int globalEndIndex = startIndex + wordEndIndex;

                        // 设置低分单词为红色
                        spannableTextForWords.setSpan(new ForegroundColorSpan(Color.RED), globalStartIndex, globalEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Log.d(TAG, "设置低分单词为红色: " + wordContent + " 起始位置: " + globalStartIndex + " 结束位置: " + globalEndIndex);
                    }
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "无法解析单词评分: " + wordScore, e);
            }
        }

        // 设置处理后的文本到 TextView
        articleTextView.setText(spannableTextForWords);
    }




    // EvaluatorListener
    private final EvaluatorListener mEvaluatorListener = new EvaluatorListener() {
        @Override
        public void onResult(EvaluatorResult result, boolean isLast) {
            if (isLast) {
                String resultString = result.getResultString();
                Log.d(TAG, "评测结果: " + resultString);

                // 更新已测评状态并切换到下一条
                evaluatedSentenceIndices.add(currentSentenceIndex); // 标记当前句子为已测评
                updateAfterEvaluation(currentSentenceIndex); // 更新颜色和按钮
                // 解析 XML 格式的评测结果
                parseScore(resultString);
                // 显示“下一条”按钮
//                nextButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError(SpeechError error) {
            Log.d(TAG, "evaluator over");
            resultTextView.setText("测评失败，请重试！");
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "evaluator begin");
            resultTextView.setText("开始录音...");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "evaluator stopped");
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "当前音量：" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 可选的扩展事件，暂不处理
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放播放器资源
        if (player != null) {
            player.release();
        }
    }

}
