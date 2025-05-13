package com.software.androidthesis.bar;

import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.BarDataEntity;
import com.software.androidthesis.entity.SourceEntity;
import com.software.androidthesis.entity.WordDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/3/1 20:19
 * @Decription:
 */
public class BarDataHelper {

    public static void getBarData(Long userId, String date, final ApiServiceImpl.ApiCallback<List<BarDataEntity.Type>> callback) {
        ApiServiceImpl apiService = new ApiServiceImpl();

        apiService.getWordsByUserIdAndDate(userId, date, new ApiServiceImpl.ApiCallback<List<WordDTO>>() {
            @Override
            public void onSuccess(List<WordDTO> words) {
                List<BarDataEntity.Type> types = new ArrayList<>();

                int excellentCount = 0; // 优秀
                int goodCount = 0; // 良好
                int normalCount = 0; // 一般

                for (WordDTO word : words) {
                    int score = word.getCount(); // 确保 WordDTO 有 getScore() 方法

                    if (score >= 90) {
                        excellentCount++;
                    } else if (score >= 75) {
                        goodCount++;
                    } else if (score > 0) {
                        normalCount++;
                    }
                }

                BarDataEntity.Type type1 = new BarDataEntity.Type();
                type1.setTypeName("优秀");
                type1.setTypeScale(excellentCount);
                types.add(type1);

                BarDataEntity.Type type2 = new BarDataEntity.Type();
                type2.setTypeName("良好");
                type2.setTypeScale(goodCount);
                types.add(type2);

                BarDataEntity.Type type3 = new BarDataEntity.Type();
                type3.setTypeName("一般");
                type3.setTypeScale(normalCount);
                types.add(type3);

                callback.onSuccess(types);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
