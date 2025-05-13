package com.software.androidthesis.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

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
import android.content.SharedPreferences;

import android.widget.Toast;

import com.software.androidthesis.Activity.WordReviewListActivity;
import com.software.androidthesis.Adapter.WordAdapter;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.WordDTO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:41
 * @Decription:
 */

public class AFragment extends Fragment {

    private static final String TAG = "AFragment";

    private ViewPager2 viewPager;
    private List<WordDTO> wordList = new ArrayList<>();
    private WordAdapter wordAdapter;
    private Long userId;
    private String date;

    private WaveView waveView;
    private Button button2; // 录音按钮
    private Button btnSpeak;

    private TextView resultTextView,unselectedTextView;

    private String currentWord; // 当前要播放的单词
    private SimpleExoPlayer player;//用于播放示例音频

    private SimpleExoPlayer player1;  // 用于播放音频
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_MIC_PERMISSION = 103;
    private SpeechEvaluator mIse;
    private String audioFilePath = null;  // 录音文件路径
    private String mLastResult;  // 存储评测结果
    private int sum;
    private RelativeLayout relativeLayout;

    private boolean isEvaluationComplete = false;  // 用来标记是否完成语音测评

    private Date selectedDate;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        // 从 SharedPreferences 获取 userId
        SharedPreferences preferences = getContext().getSharedPreferences("UserPreferences", getContext().MODE_PRIVATE);
        userId = preferences.getLong("id", -1);

        selectedDate = new Date();

        viewPager = view.findViewById(R.id.viewPager);
        waveView = view.findViewById(R.id.waveView);
        button2 = view.findViewById(R.id.button2);
        btnSpeak = view.findViewById(R.id.button1);
        resultTextView = view.findViewById(R.id.textView3);
        relativeLayout = view.findViewById(R.id.layout);
        unselectedTextView = view.findViewById(R.id.unselected);
        // 获取单词数据并初始化 ViewPager
        fetchWordData();

        // 初始化 ExoPlayer
        player = new SimpleExoPlayer.Builder(getContext()).build();
        player1 = new SimpleExoPlayer.Builder(getContext()).build();

        // 请求权限
        checkAndRequestPermissions();

        // 初始化科大讯飞 SDK
        SpeechUtility.createUtility(getContext(), SpeechConstant.APPID + "=c89e39a8");
        // 初始化语音评测器
        mIse = SpeechEvaluator.createEvaluator(getContext(), null);
        setParams();  // 设置评测参数

        // 检查 SpeechEvaluator 初始化是否成功
        if (mIse == null) {
            Log.e(TAG, "SpeechEvaluator 初始化失败");
            Toast.makeText(getContext(), "语音评测初始化失败", Toast.LENGTH_SHORT).show();
            return view;
        }

        // 设置波浪参数
        setupWaveView();
        // 初始状态：隐藏波浪按钮
        waveView.setVisibility(View.GONE);

        btnSpeak.setVisibility(View.GONE);

        // 设置日期为今天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        date = sdf.format(new Date());  // 获取今天的日期

        viewPager.setUserInputEnabled(false);  // 禁用滑动


        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                float x = event.getX();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int currentPosition = viewPager.getCurrentItem();

                    // 点击左端，不返回上一个单词
                    if (x < screenWidth / 3) {
                        return true; // 阻止返回操作
                    } else if (x > screenWidth - screenWidth / 3) {
                        // 只有在完成测评的情况下，点击右端才能进入下一页
                        if (isEvaluationComplete) {
                            if (currentPosition < viewPager.getAdapter().getItemCount() - 1) {
                                viewPager.setCurrentItem(currentPosition + 1);
                                updateCurrentWord(currentPosition + 1);  // 更新当前单词
                                resultTextView.setText(" ");
                                isEvaluationComplete = false;  // 重置测评状态
                                btnSpeak.setVisibility(View.GONE);
                                button2.setVisibility(View.VISIBLE);
                            }
                            // 如果是最后一个单词，隐藏单词信息，显示完成按钮
                            if (currentPosition == wordList.size() - 1) {
                                hideWordDetails();
                                showFinishButtons();
                            }
                        } else {
                            // 如果没有完成测评，不允许点击右端
                            return true;  // 阻止点击事件
                        }
                    }
                }
                return true;  // 返回 true 表示我们已经处理了点击事件
            }
        });


        // 按钮点击事件
        btnSpeak.setOnClickListener(v -> {
            if (currentWord != null && !currentWord.isEmpty()) {
                String word = "";
                try {
                    word = currentWord.replace("\u00A0", "");

                    // 进行 URL 编码
                    word = URLEncoder.encode(word, "UTF-8").replace("+", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // 拼接音频 URL
                String audioUrl = "https://dict.youdao.com/dictvoice?audio=" + word + "&type=2";
                Log.d("TTS Audio URL", audioUrl);

                // 创建数据源
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                        getContext(), Util.getUserAgent(getContext(), "yourApplicationName"));

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
                            playAudio(audioFilePath);  // 播放录音
                        } else if (playbackState == Player.STATE_BUFFERING) {
                            Toast.makeText(getContext(), "正在缓冲...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "没有可播放的单词", Toast.LENGTH_SHORT).show();
            }
        });



        // 设置按钮点击事件
        button2.setOnClickListener(v -> startRecording());
        waveView.setOnClickListener(v -> stopRecordingAndEvaluate());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次重新进入 fragment 时重新获取数据
        fetchWordData();
    }

    private void fetchWordData() {
        // 调用 ApiServiceImpl 获取单词数据
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getWords(userId, date, new ApiServiceImpl.ApiCallback<List<WordDTO>>() {
            @Override
            public void onSuccess(List<WordDTO> words) {
                if (words != null && !words.isEmpty()) {
                    Log.d("API", "Fetched words: " + words.size());  // 打印获取到的数据数量
                    unselectedTextView.setVisibility(View.GONE);
                    wordList = words;
                    wordAdapter = new WordAdapter(getContext(), wordList);
                    viewPager.setAdapter(wordAdapter);
                    updateCurrentWord(0);  // 更新第一个单词
                } else {
                    Log.d("API", "No words received.");
                    unselectedTextView.setVisibility(View.VISIBLE);  // 没有数据时显示提示文本
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("API", "Error fetching words: " + errorMessage);  // 打印错误信息
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void updateCurrentWord(int position) {
        if (wordList != null && !wordList.isEmpty()) {
            WordDTO wordDTO = wordList.get(position);
            currentWord = wordDTO.getWord();  // 获取当前页面的单词
        }
    }

    private void checkAndRequestPermissions() {
        // 检查麦克风权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_MIC_PERMISSION);
        }

        // 检查存储权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    private void setParams() {
        mIse.setParameter(SpeechConstant.LANGUAGE, "en_us");
        mIse.setParameter(SpeechConstant.ISE_CATEGORY, "read_sentence");
        mIse.setParameter(SpeechConstant.RESULT_LEVEL, "complete");
        mIse.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "5000");
        mIse.setParameter(SpeechConstant.VAD_BOS, "5000");
        mIse.setParameter(SpeechConstant.VAD_EOS, "1800");
        mIse.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
    }

    private void setupWaveView() {
        int waveHeightA = 30;  // 波浪A的振幅
        float waveACycle = 0.04f;  // 波浪A的周期
        int waveSpeedA = 3;  // 波浪A的速度
        int waveHeightB = 15;  // 波浪B的振幅
        float waveBCycle = 0.05f;  // 波浪B的周期
        int waveSpeedB = 5;  // 波浪B的速度
        int waveColor = Color.parseColor("#d6e3ff");  // 波浪的颜色
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_background3);  // 圆形遮罩的位图

        waveView.setWaveParameters(waveHeightA, waveACycle, waveSpeedA, waveHeightB, waveBCycle, waveSpeedB, waveColor);
        waveView.setBallBitmap(bitmap);
    }

    // 获取录音文件存储路径并确保目录存在
    private void startRecording() {
        if (currentWord != null && !currentWord.isEmpty()) {
            // 隐藏 button2，显示 waveView
            button2.setVisibility(View.GONE);
            waveView.setVisibility(View.VISIBLE);

            File directory = new File(getActivity().getExternalFilesDir(null), "audioFiles");

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
            mIse.startEvaluating(currentWord, null, mEvaluatorListener);
        } else {
            Toast.makeText(getContext(), "请选择一个句子", Toast.LENGTH_SHORT).show();
        }
    }

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

        int score = sum;
        String word = currentWord;

        ApiServiceImpl apiService = new ApiServiceImpl();

        // 先更新评分
        apiService.updateUserWord(userId, word, score, date, new ApiServiceImpl.ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "成功更新单词评分：" + response);
                Toast.makeText(getContext(), "单词评分更新成功", Toast.LENGTH_SHORT).show();

                // 更新成功后再调用新增复习计划
                apiService.reviewAndSchedule(userId, word, score, date, new ApiServiceImpl.ApiCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "复习计划已生成：" + result);
                        Toast.makeText(getContext(), "复习计划已生成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMsg) {
                        Log.e(TAG, "复习计划生成失败：" + errorMsg);
                        Toast.makeText(getContext(), "复习计划生成失败: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "更新单词评分失败：" + errorMessage);
                Toast.makeText(getContext(), "更新失败: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        isEvaluationComplete = true;

        if (audioFilePath != null) {
            new Handler().postDelayed(() -> {
                button2.setVisibility(View.GONE);
                playAudio(audioFilePath);
            }, 1000);
        }
    }


    // 播放录音
    private void playAudio(String filePath) {
        // 播放录音文件
        if (audioFilePath != null) {
            File audioFile = new File(audioFilePath);
            if (audioFile.exists()) {
                Uri audioUri = Uri.fromFile(audioFile);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "yourApplicationName"));
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(audioUri));

                player1.setMediaSource(mediaSource);
                player1.prepare();
                player1.play();
                Log.d(TAG, "开始播放录音...");
                // 隐藏录音按钮
                button2.setVisibility(View.GONE);
                btnSpeak.setVisibility(View.VISIBLE);
            }
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
//            highlightLowScoringWords(wordScores, wordContents, currentSentenceIndex);

        } catch (Exception e) {
            Log.e(TAG, "解析 XML 结果时发生错误", e);
        }
    }

    // 隐藏单词的发音、意思等信息
    private void hideWordDetails() {
        TextView textWord = getView().findViewById(R.id.text_word);
        TextView textPro = getView().findViewById(R.id.text_pro);
        TextView textMean = getView().findViewById(R.id.text_mean);

        textWord.setVisibility(View.GONE);
        textPro.setVisibility(View.GONE);
        textMean.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);
    }

    // 显示“完成任务”按钮
    private void showFinishButtons() {
        TextView textFinish = getView().findViewById(R.id.text_finish);
        Button txtWordFinish = getView().findViewById(R.id.Txt_word_finish);

        textFinish.setVisibility(View.VISIBLE);
        txtWordFinish.setVisibility(View.VISIBLE);
        txtWordFinish.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WordReviewListActivity.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate);
            intent.putExtra("selectedDate", formattedDate); // 传递选中的日期
            startActivity(intent);
        });
    }


    // EvaluatorListener
    private final EvaluatorListener mEvaluatorListener = new EvaluatorListener() {
        @Override
        public void onResult(EvaluatorResult result, boolean isLast) {
            if (isLast) {
                String resultString = result.getResultString();
                Log.d(TAG, "评测结果: " + resultString);

                // 解析 XML 格式的评测结果
                parseScore(resultString);
                // 显示“下一条”按钮
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
}