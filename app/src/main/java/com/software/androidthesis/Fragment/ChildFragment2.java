package com.software.androidthesis.Fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.software.androidThesis.R;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.bar.BarDataHelper;
import com.software.androidthesis.entity.BarDataEntity;
import com.software.androidthesis.entity.BarEntity;
import com.software.androidthesis.entity.SourceEntity;
import com.software.androidthesis.entity.UserWords;
import com.software.androidthesis.util.TokenManager;
import com.software.androidthesis.view.BarGroup;
import com.software.androidthesis.view.BarView;
import com.software.androidthesis.viewmodel.CalenderViewModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/21 1:02
 * @Decription:
 */
public class ChildFragment2 extends Fragment {

    private DecimalFormat mFormat = new DecimalFormat("#.##");
    private HorizontalScrollView root;
    private BarGroup barGroup;
    private float sourceMax = 10f;
    private View popView;
    private PopupWindow popupWindow;
    private int initPopHeight = 0;
    private Map<String, List<BarDataEntity.Type>> wordDetailRecordings_data = new LinkedHashMap<>();
    private final List<String> pastSevenDays = new ArrayList<>();
    private long userId;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child2, container, false);

        // 从 SharedPreferences 获取 userId
        android.content.SharedPreferences preferences = getContext().getSharedPreferences("UserPreferences", getContext().MODE_PRIVATE);
        userId = preferences.getLong("id", -1);

        root = view.findViewById(R.id.bar_scroll);
        barGroup = view.findViewById(R.id.bar_group);
        popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_bg, null);

        initDateList();
        fetchAllBarData(view);

        return view;
    }

    private void initDateList() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -i);
            String date = sdf.format(calendar.getTime());
            pastSevenDays.add(date);
        }
    }

    private void fetchAllBarData(View view) {
        final int[] completedCount = {0};

        for (String date : pastSevenDays) {
            final String currentDate = date; // ✅ 捕获当前值
            BarDataHelper.getBarData(userId, currentDate, new ApiServiceImpl.ApiCallback<List<BarDataEntity.Type>>() {
                @Override
                public void onSuccess(List<BarDataEntity.Type> types) {
                    Log.d("BarChartDebug", "【成功】Date: " + currentDate);
                    for (BarDataEntity.Type type : types) {
                        Log.d("BarChartDebug", "类型：" + type.getTypeName() + "，数量：" + type.getTypeScale());
                    }

                    wordDetailRecordings_data.put(currentDate, types);
                    Log.d("BarChartDebug222", "=== 打印 wordDetailRecordings_data ===");
                    for (Map.Entry<String, List<BarDataEntity.Type>> entry : wordDetailRecordings_data.entrySet()) {
                        Log.d("BarChartDebug", "日期：" + entry.getKey());
                        for (BarDataEntity.Type type : entry.getValue()) {
                            Log.d("BarChartDebug", "   → 类型：" + type.getTypeName() + "，数量：" + type.getTypeScale());
                        }
                    }

                    completedCount[0]++;
                    if (completedCount[0] == pastSevenDays.size()) {
                        setBarChart(view);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("BarChartDebug", "【失败】Date: " + currentDate + "，Error: " + errorMessage);
                    wordDetailRecordings_data.put(currentDate, new ArrayList<>());
                    completedCount[0]++;
                    if (completedCount[0] == pastSevenDays.size()) {
                        setBarChart(view);
                    }
                }
            });
        }

    }


    private void setBarChart(View rootView) {
        barGroup = rootView.findViewById(R.id.bar_group);
        root = rootView.findViewById(R.id.bar_scroll);
        popView = LayoutInflater.from(getContext()).inflate(R.layout.pop_bg, null);

        final SourceEntity sourceEntity = new SourceEntity();
        List<SourceEntity.Source> sourceList = new ArrayList<>();


        for (String date : pastSevenDays) {
            List<BarDataEntity.Type> types = wordDetailRecordings_data.get(date);

            float good = 0, other = 0, bad = 0;
            for (BarDataEntity.Type type : types) {
                switch (type.getTypeName()) {
                    case "优秀":
                        good = (float) type.getTypeScale();
                        break;
                    case "良好":
                        other = (float) type.getTypeScale();
                        break;
                    case "一般":
                        bad = (float) type.getTypeScale();
                        break;
                }
            }

            SourceEntity.Source source = new SourceEntity.Source();
            // ✅ 设置为 MM-dd 格式
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
                String showDate = outputFormat.format(inputFormat.parse(date));
                source.setSource(showDate);
            } catch (ParseException e) {
                e.printStackTrace();
                source.setSource(date); // fallback
            }

            source.setGoodCount((int) good);
            source.setOtherCount((int) other);
            source.setBadCount((int) bad);
            source.setAllCount((int) (good + other + bad));
            source.setScale(1);
            sourceList.add(source);
        }

        sourceEntity.setList(sourceList);
        setYAxis(rootView, sourceList);

        barGroup.removeAllViews();
        List<BarEntity> datas = new ArrayList<>();
        int size = sourceList.size();

        for (SourceEntity.Source source : sourceList) {
            BarEntity barEntity = new BarEntity();
            barEntity.setNegativePer(Float.parseFloat(mFormat.format(source.getBadCount() / sourceMax)));
            barEntity.setNeutralPer(Float.parseFloat(mFormat.format(source.getOtherCount() / sourceMax)));
            barEntity.setPositivePer(Float.parseFloat(mFormat.format(source.getGoodCount() / sourceMax)));

            // 日期格式处理
            String fullDate = source.getSource();  // yyyy-MM-dd
            String shortDate = "";
            try {
                SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat sdfShort = new SimpleDateFormat("MM-dd", Locale.getDefault());
                Date date = sdfFull.parse(fullDate);
                if (date != null) {
                    shortDate = sdfShort.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                shortDate = fullDate;
            }
            barEntity.setTitle(shortDate);

            barEntity.setScale(source.getScale());
            barEntity.setAllcount(source.getAllCount());
            barEntity.setFillScale(1 - source.getAllCount() / sourceMax);
            datas.add(barEntity);
        }


        barGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                barGroup.getViewTreeObserver().removeOnPreDrawListener(this);

                int height = rootView.findViewById(R.id.bar_scroll).getMeasuredHeight();
                View baseLineView = rootView.findViewById(R.id.left_base_line);
                int baseLineTop = baseLineView.getTop();

                barGroup.setDatas(datas);
                barGroup.setHeight(sourceMax, height - baseLineTop - baseLineView.getHeight() / 2);  // 添加柱子

                barGroup.postDelayed(() -> {
                    View barContainer = barGroup.getChildAt(0);
                    if (barContainer != null) {
                        BarView barItem = barContainer.findViewById(R.id.barView);
                        if (barItem != null) {
                            int baseLineHeight = rootView.findViewById(R.id.base_line).getTop();
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) root.getLayoutParams();
                            lp.leftMargin = (int) (baseLineView.getLeft() + getResources().getDisplayMetrics().density * 3);
                            lp.topMargin = Math.abs(baseLineHeight - barItem.getHeight());
                            root.setLayoutParams(lp);
                        }
                    }
                }, 100);

                for (int i = 0; i < size; i++) {
                    final BarView barItem = barGroup.getChildAt(i).findViewById(R.id.barView);
                    barItem.setAnimTimeCell(1.0f);
                    barItem.startAnimation();
                    final int finalI = i;
                    barItem.setOnClickListener(v -> {
                        float top = v.getHeight() - barItem.getFillHeight();
                        SourceEntity.Source ss = sourceList.get(finalI);
                        String showText = "优秀：" + (int) ss.getGoodCount() + "个\n"
                                + "良好：" + (int) ss.getOtherCount() + "个\n"
                                + "一般：" + (int) ss.getBadCount() + "个\n"
                                + "总共：" + (int) ss.getAllCount() + "个";
                        ((TextView) popView.findViewById(R.id.txt)).setText(showText);
                        showPop(barItem, top);
                    });
                }

                root.post(() -> root.fullScroll(HorizontalScrollView.FOCUS_RIGHT));
                return false;
            }
        });
        Log.d("BarChartCheck", "sourceMax: " + sourceMax);

    }

    private void setYAxis(View rootView, List<SourceEntity.Source> list) {
        ((TextView) rootView.findViewById(R.id.tv_num1)).setText("2");
        ((TextView) rootView.findViewById(R.id.tv_num2)).setText("4");
        ((TextView) rootView.findViewById(R.id.tv_num3)).setText("6");
        ((TextView) rootView.findViewById(R.id.tv_num4)).setText("8");
        ((TextView) rootView.findViewById(R.id.tv_num5)).setText("10");
    }

    private void showPop(View barItem, float top) {
        if (popupWindow != null) popupWindow.dismiss();

        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(barItem, barItem.getWidth() / 2, -((int) top + initPopHeight));

        if (initPopHeight == 0) {
            popView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    popView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    initPopHeight = popView.getHeight();
                    popupWindow.update(barItem, barItem.getWidth() / 2,
                            -((int) top + initPopHeight),
                            popupWindow.getWidth(),
                            popupWindow.getHeight());
                }
            });
        }
    }
}

