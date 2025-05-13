package com.software.androidthesis.util;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/5/12 15:10
 * @Decription:
 */

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 网易有道智云语音合成服务api调用demo
 * api接口: https://openapi.youdao.com/ttsapi
 */
public class TtsUtil {

    private static final String TAG = "TtsUtil";

    // 请替换为你的 APP_KEY 和 APP_SECRET
    private static final String APP_KEY = "5080440fbf4ca963";
    private static final String APP_SECRET = "uZ7AVC4xUxlb6lRKxKWRmtj8PVCamisw";

    /**
     * 合成语音文本并保存为 MP3 文件
     * @param context Android 上下文
     * @param text 要合成的文本
     * @return 成功返回音频文件路径，失败返回 null
     */
    public static String synthesizeTextToMp3(Context context, String text) {
        try {
            // 设置请求参数
            Map<String, String[]> params = new HashMap<>();
            params.put("q", new String[]{text});
            params.put("voiceName", new String[]{"youxiaoxun"});  // 或 vimary、vignmale 等
            params.put("format", new String[]{"mp3"});

            // 添加鉴权参数
            AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params);

            // 请求 TTS 接口
            byte[] result = HttpUtil.doPost("https://openapi.youdao.com/ttsapi", null, params, "audio");

            if (result != null) {
                File dir = new File(context.getExternalFilesDir(null), "tts");
                if (!dir.exists()) dir.mkdirs();

                File mp3File = new File(dir, "tts_" + System.currentTimeMillis() + ".mp3");
                try (FileOutputStream fos = new FileOutputStream(mp3File)) {
                    fos.write(result);
                }

                Log.d(TAG, "语音合成成功，路径: " + mp3File.getAbsolutePath());
                return mp3File.getAbsolutePath();
            } else {
                Log.e(TAG, "语音合成失败：返回为空");
            }

        } catch (Exception e) {
            Log.e(TAG, "语音合成异常", e);
        }

        return null;
    }
}