package com.software.androidthesis.util;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/27 12:52
 * @Decription:
 */
public class AudioUtils {
    private static AudioUtils audioUtils;
    private SpeechSynthesizer mySynthesizer;

    public AudioUtils() {
    }

    public static AudioUtils getInstance() {
        if (audioUtils == null) {
            synchronized (AudioUtils.class) {
                if (audioUtils == null) {
                    audioUtils = new AudioUtils();
                }
            }
        }
        return audioUtils;
    }

    private InitListener myInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
        }
    };

    public void init(Context context) {
        mySynthesizer = SpeechSynthesizer.createSynthesizer(context, myInitListener);

        // 设置语音合成器的语音参数
        mySynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); // 设置声音为“xiaoyan”

        // 设置语速为正常语速（默认值）
        mySynthesizer.setParameter(SpeechConstant.SPEED, "40");  // 正常语速，范围为 0-100，50为默认速度

        // 设置语调为正常语调（默认值）
        mySynthesizer.setParameter(SpeechConstant.PITCH, "60");  // 正常语调，范围为 0-100，50为默认语调

        // 设置音量为 50（正常音量）
        mySynthesizer.setParameter(SpeechConstant.VOLUME, "50");
    }

    public void speakText(String content) {
        int code = mySynthesizer.startSpeaking(content, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {
            }

            @Override
            public void onSpeakPaused() {
            }

            @Override
            public void onSpeakResumed() {
            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {
            }

            @Override
            public void onCompleted(SpeechError speechError) {
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
            }
        });
    }
}
