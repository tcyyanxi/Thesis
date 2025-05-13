package com.software.androidthesis.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.bar.BarDataHelper;
import com.software.androidthesis.entity.BarDataEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/25 11:18
 * @Decription:
 */
public class CalenderViewModel extends ViewModel {

    private MutableLiveData<Map<String, List<BarDataEntity.Type>>> wordDetailRecordingWeekLiveData = new MutableLiveData<>();
    private MutableLiveData<String> selectedDate = new MutableLiveData<>();

    public LiveData<Map<String, List<BarDataEntity.Type>>> getWordDetailRecordingWeekLiveData() {
        return wordDetailRecordingWeekLiveData;
    }
    public LiveData<String> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String date) {
        selectedDate.setValue(date);
    }

    public void getRecordingDataWeek(Context context, Long userId) {
        Map<String, List<BarDataEntity.Type>> resultMap = new LinkedHashMap<>();

        // 获取最近七天的日期
        List<String> last7Days = getLast7Days();

        // 递归方式处理异步数据
        fetchDataForDay(context, userId, last7Days, 0, resultMap);
    }

    private void fetchDataForDay(Context context, Long userId, List<String> dates, int index, Map<String, List<BarDataEntity.Type>> resultMap) {
        if (index >= dates.size()) {
            wordDetailRecordingWeekLiveData.postValue(resultMap);
            return;
        }

        String date = dates.get(index);
        BarDataHelper.getBarData(userId, date, new ApiServiceImpl.ApiCallback<List<BarDataEntity.Type>>() {
            @Override
            public void onSuccess(List<BarDataEntity.Type> types) {
                resultMap.put(date, types);
                fetchDataForDay(context, userId, dates, index + 1, resultMap);
            }

            @Override
            public void onError(String errorMessage) {
                fetchDataForDay(context, userId, dates, index + 1, resultMap);
            }
        });
    }

    private List<String> getLast7Days() {
        List<String> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            days.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        Collections.reverse(days); // 从旧到新排列
        return days;
    }
}
