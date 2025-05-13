package com.software.androidthesis.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 22:24
 * @Decription:
 */
public class TokenManager {
    private static final String TOKEN_PREFS = "TokenPrefs";
    private static final String USER_ID = "userId";
    private static final String USER_AVATAR = "userAvatar";
    private static final String USER_NAME = "userName";
    private static final String PREFS_NAME = "WordIdPreferences";
    private static final String KEY_WORD_IDS = "wordIds";

    /**
     * @param context:
     * @param userId:
     * @return void
     * @author Lee
     * @description 登录/注册成功，存一下userId
     * @date 2024/4/26 14:14
     */
    public static void saveUserId(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    /**
     * @param context:
     * @return String
     * @author Lee
     * @description 取userId
     * @date 2024/4/26 14:14
     */
//    public static Long getUserId(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
//        String userIdStr = sharedPreferences.getString(USER_ID, null);
//        if (userIdStr != null) {
//            try {
//                return Long.parseLong(userIdStr);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    /**
     * @param context:
     * @param userAvatar:
     * @return void
     * @author Lee
     * @description 存一下用户的头像url
     * @date 2024/5/21 8:38
     */
    public static void saveUserAvatar(Context context, String userAvatar) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userAvatar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_AVATAR, userAvatar);
        editor.apply();
    }

    /**
     * @param context:
     * @return String
     * @author Lee
     * @description 取用户头像url
     * @date 2024/5/21 8:39
     */
    public static String getUserAvatar(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userAvatar", Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_AVATAR, null);
    }

    /**
     * @param context:
     * @param userName:
     * @return void
     * @author Lee
     * @description 存用户名
     * @date 2024/5/22 9:07
     */
    public static void saveUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    /**
     * @param context:
     * @return String
     * @author Lee
     * @description 取用户名
     * @date 2024/5/22 9:07
     */
    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userName", Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_NAME, null);
    }
//    /**
//     * @param words:
//     * @param context:
//     * @return void
//     * @author Lee
//     * @description 每次调用该方法存入8个单词，再次调用覆盖前面存的(从网络获取)
//     * @date 2024/5/7 9:16
//     */
//    public static void saveServerWordsToSharedPreferences(List<String> words, Context context, Runnable onComplete) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("WordPreferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // 限制保存的单词数量不超过8个
//        int wordsCount = Math.min(words.size(), 8);  // 确保不超过8个单词
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < wordsCount; i++) {
//            if (stringBuilder.length() > 0) {
//                stringBuilder.append(",");
//            }
//            stringBuilder.append(words.get(i));
//        }
//
//        // 保存字符串到SharedPreferences
//        editor.putString("words", stringBuilder.toString());
//        editor.apply();  // 异步写入
//
//        if (onComplete != null) {
//            onComplete.run();  // 在数据保存后执行回调
//        }
//    }
//
//    /**
//     * @param context:
//     * @return List<String>
//     * @author Lee
//     * @description 取出存的8个单词，如果为空返回空列表(从网络获取)
//     * @date 2024/5/7 9:20
//     */
//    public static List<String> loadServerWordsFromSharedPreferences(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("WordPreferences", Context.MODE_PRIVATE);
//        String wordsString = sharedPreferences.getString("words", "");
//
//        // 将字符串分割成单词列表
//        if (!wordsString.isEmpty()) {
//            return Arrays.asList(wordsString.split(","));
//        } else {
//            return new ArrayList<>(); // 如果没有数据，返回空列表
//        }
//    }
//
//    /**
//     * @param newWordIds:
//     * @param context:
//     * @return void
//     * @author Lee
//     * @description 存储单词id
//     * @date 2024/5/13 17:26
//     */
//    public static void SaveALLWordIds(List<Integer> newWordIds, Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // 加载现有的单词ID
//        List<Integer> existingWordIds = loadALLWordIds(context);
//        HashSet<Integer> allWordIds = new HashSet<>(existingWordIds); // 使用HashSet避免重复
//
//        // 添加新的单词ID
//        for (Integer wordId : newWordIds) {
//            allWordIds.add(wordId);
//            if (allWordIds.size() == 100) {
//                break; // 达到最大容量，不再添加
//            }
//        }
//
//        // 将更新后的单词ID列表转回字符串格式
//        String wordIdsString = allWordIds.stream()
//                .map(String::valueOf)
//                .collect(Collectors.joining(","));
//
//        // 保存更新后的单词ID列表到SharedPreferences
//        editor.putString(KEY_WORD_IDS, wordIdsString);
//        editor.apply();  // 异步写入
//    }
//
//    /**
//     * @param scores:
//     * @param context:
//     * @return void
//     * @author Lee
//     * @description 存4个WordDetailRecording
//     * @date 2024/6/6 8:35
//     */
//    public static void saveYuYinScore(List<WordDetailRecording> scores, Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("YuYinWord", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        //限制保存的最大数量为4
//        int yuYinCnt = Math.min(scores.size(), 4); //确保不超过4个
//
//        Gson gson = new Gson(); // 使用 Gson 序列化对象
//
//        for (int i = 0; i < yuYinCnt; i++) {
//            String scoreJson = gson.toJson(scores.get(i)); // 将对象转换为 JSON 字符串
//            editor.putString("score_" + i, scoreJson); // 使用索引区分不同的对象
//        }
//
//        editor.putInt("score_count", yuYinCnt); // 保存实际保存的对象数量
//        editor.apply();
//    }
//
//    /**
//     * @param context:
//     * @return List<WordDetailRecording>
//     * @author Lee
//     * @description 取4个WordDetailRecording
//     * @date 2024/6/6 8:35
//     */
//    public static List<WordDetailRecording> getYuYinScores(Context context) {
//        List<WordDetailRecording> scores = new ArrayList<>();
//        SharedPreferences sharedPreferences = context.getSharedPreferences("YuYinWord", Context.MODE_PRIVATE);
//
//        // 获取保存的分数数量
//        int scoreCount = sharedPreferences.getInt("score_count", 0);
//
//        Gson gson = new Gson();
//
//        for (int i = 0; i < scoreCount; i++) {
//            String scoreJson = sharedPreferences.getString("score_" + i, null);
//            if (scoreJson != null) {
//                // 将JSON字符串反序列化为WordDetailRecording对象，并添加到列表中
//                WordDetailRecording score = gson.fromJson(scoreJson, WordDetailRecording.class);
//                scores.add(score);
//            }
//        }
//
//        return scores;
//    }
//
//    /**
//     * @param context:
//     * @return List<Integer>
//     * @author Lee
//     * @description 加载所有单词id
//     * @date 2024/5/13 17:30
//     */
//
//    public static List<Integer> loadALLWordIds(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        String wordIdsString = sharedPreferences.getString(KEY_WORD_IDS, "");
//
//        // 将字符串分割成单词ID列表
//        if (!wordIdsString.isEmpty()) {
//            return Arrays.stream(wordIdsString.split(","))
//                    .map(Integer::parseInt)
//                    .collect(Collectors.toList());
//        } else {
//            return new ArrayList<>(); // 如果没有数据，返回空列表
//        }
//    }
//
//
//    /**
//     * @param wordMap:
//     * @param context:
//     * @return void
//     * @author Lee
//     * @description 存一下单词和对应id
//     * @date 2024/5/9 8:33
//     */
//    public static void saveWordsAndIds(Map<String, Integer> wordMap, Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("WordAndIdPreferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // 使用 StringBuilder 来构建字符串
//        StringBuilder stringBuilder = new StringBuilder();
//        int count = 0;
//        for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
//            if (count >= 8) break; // 只存储最多8个条目
//            if (stringBuilder.length() > 0) {
//                stringBuilder.append(","); // 在条目之间添加逗号作为分隔符
//            }
//            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue());
//            count++;
//        }
//
//        // 将构建好的字符串存储到 SharedPreferences
//        editor.putString("words", stringBuilder.toString());
//        editor.apply(); // 异步提交更改
//    }
//
//    /**
//     * @param context:
//     * @return Map<String,Integer>
//     * @author Lee
//     * @description 取单词和对应的id
//     * @date 2024/5/9 8:34
//     */
//    public static Map<String, Integer> loadWordsAndIds(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("WordAndIdPreferences", Context.MODE_PRIVATE);
//        String wordsString = sharedPreferences.getString("words", "");
//        Map<String, Integer> wordMap = new HashMap<>();
//
//        if (!wordsString.isEmpty()) {
//            String[] entries = wordsString.split(",");
//            for (String entry : entries) {
//                String[] parts = entry.split(":");
//                if (parts.length == 2) {
//                    try {
//                        String word = parts[0];
//                        int id = Integer.parseInt(parts[1]);
//                        wordMap.put(word, id);
//                    } catch (NumberFormatException e) {
//                        Log.e("SharedPreferences", "Error parsing ID for word: " + parts[0]);
//                    }
//                }
//            }
//        }
//        return wordMap;
//    }
//
//    /**
//     * @param yuYinMap:
//     * @param context:
//     * @return void
//     * @author Lee
//     * @description 存语音单词和对应的单词Id
//     * @date 2024/6/5 15:57
//     */
//    public static void saveYuYinWordAndIds(Map<String, Integer> yuYinMap, Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("YuYinWordAndID", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // 使用 StringBuilder 来构建字符串
//        StringBuilder stringBuilder = new StringBuilder();
//        int count = 0;
//        for (Map.Entry<String, Integer> entry : yuYinMap.entrySet()) {
//            if (count >= 4) break; // 只存储最多4个条目
//            if (stringBuilder.length() > 0) {
//                stringBuilder.append(","); // 在条目之间添加逗号作为分隔符
//            }
//            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue());
//            count++;
//        }
//
//        // 将构建好的字符串存储到 SharedPreferences
//        editor.putString("yuYin", stringBuilder.toString());
//        editor.apply(); // 异步提交更改
//    }
//
//    /**
//     * @param context:
//     * @return Map<String,Integer>
//     * @author Lee
//     * @description 取语音单词和对应的单词Id
//     * @date 2024/6/5 15:58
//     */
//    public static Map<String, Integer> loadYuYinWordsAndIds(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("YuYinWordAndID", Context.MODE_PRIVATE);
//        String wordsString = sharedPreferences.getString("yuYin", "");
//        Map<String, Integer> wordMap = new HashMap<>();
//
//        if (!wordsString.isEmpty()) {
//            String[] entries = wordsString.split(",");
//            for (String entry : entries) {
//                String[] parts = entry.split(":");
//                if (parts.length == 2) {
//                    try {
//                        String word = parts[0];
//                        int id = Integer.parseInt(parts[1]);
//                        wordMap.put(word, id);
//                    } catch (NumberFormatException e) {
//                        Log.e("SharedPreferences", "Error parsing ID for word: " + parts[0]);
//                    }
//                }
//            }
//        }
//        return wordMap;
//    }
//
//    /**
//     * @param context:
//     * @return void
//     * @author Lee
//     * @description 打印存在SharedPreference里面的数据
//     * @date 2024/6/5 15:58
//     */
//    public static void printAllSharedPreferences(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences("YourPreferencesName", Context.MODE_PRIVATE);
//        Map<String, ?> allEntries = prefs.getAll();
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            Log.e("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
//        }
//    }

}

