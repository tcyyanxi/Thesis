package com.software.androidthesis.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/18 14:32
 * @Decription:
 */
public class BadgeViewModel extends AndroidViewModel {
    private final MutableLiveData<String> badgeNumber = new MutableLiveData<>();

    public BadgeViewModel(@NonNull Application application) {
        super(application);
        badgeNumber.setValue("10");  // 设置初始值
    }

    public LiveData<String> getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String number) {
        badgeNumber.setValue(number);
    }
}




