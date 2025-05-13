package com.software.androidthesis.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.software.androidThesis.R;
import com.software.androidthesis.Activity.ListenActivity;
import com.software.androidthesis.Activity.WordReviewListActivity;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.bar.BarDataHelper;
import com.software.androidthesis.entity.BarDataEntity;
import com.software.androidthesis.view.ToastView;
import com.software.androidthesis.viewmodel.CalenderViewModel;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/21 0:54
 * @Decription:
 */
import java.util.Calendar;

public class ChildFragment1 extends Fragment {

    private CalendarView calendarView;
    private Long userId;
    private CalenderViewModel calendarViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child1, container, false);

        calendarView = view.findViewById(R.id.calendarView);

        // 获取 SharedPreferences 中的 userId
        SharedPreferences preferences = getContext().getSharedPreferences("UserPreferences", getContext().MODE_PRIVATE);
        userId = preferences.getLong("id", -1);
        calendarViewModel = new ViewModelProvider(requireActivity()).get(CalenderViewModel.class);

        // 获取今天的日期
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);  // 设置时间为00:00:00
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayInMillis = today.getTimeInMillis();

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // 将选中的日期转换为 Calendar 对象
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            // 检查选中的日期是否在今天之后
            if (selectedDate.getTimeInMillis() > todayInMillis) {
                ToastView.showCustomToast(getContext(), "这天还没到哦");
                return;  // 不进行日期选择
            }

            String selectedDateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            calendarViewModel.setSelectedDate(selectedDateString);

            // 通过 API 获取数据，检查是否有学习数据
            BarDataHelper.getBarData(userId, selectedDateString, new ApiServiceImpl.ApiCallback<List<BarDataEntity.Type>>() {
                @Override
                public void onSuccess(List<BarDataEntity.Type> types) {
                    // 如果数据均为 0，直接显示没有数据对话框
                    boolean allZero = types != null && types.stream().allMatch(type -> type.getTypeScale() == 0);

                    if (allZero) {
                        showNoDataDialog();
                    } else {
                        showBarChartDialog();  // 数据有效，显示原来的 BarChart 对话框
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), "获取数据失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });


        return view;
    }

    private void showBarChartDialog() {
        // 获取当前选择的日期
        String selectedDate = calendarViewModel.getSelectedDate().getValue();
        if (selectedDate == null) {
            Toast.makeText(getContext(), "请先选择日期", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建 Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_bar_chart);

        // 允许点击外部区域关闭 Dialog
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        // 设置 Dialog 宽度为 MATCH_PARENT，高度为 WRAP_CONTENT
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 确保 Dialog 透明，点击背景可关闭
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 获取 Dialog 内的日期 TextView 并设置文本
        TextView dateTextView = dialog.findViewById(R.id.dialog_date);
        dateTextView.setText("选择的日期: " + selectedDate);  // 设置日期文本

        // 获取 Dialog 内的 BarChart 容器
        LinearLayout barChartContainer = dialog.findViewById(R.id.dialog_bar_chart_container);

        // 通过 API 获取数据
        BarDataHelper.getBarData(userId, selectedDate, new ApiServiceImpl.ApiCallback<List<BarDataEntity.Type>>() {
            @Override
            public void onSuccess(List<BarDataEntity.Type> types) {
                // 如果数据不为空，则绑定数据
                if (types != null && !types.isEmpty()) {
                    bindData(barChartContainer, types);
                } else {
                    showNoDataDialog();  // 如果数据为空，显示没有数据的对话框
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "获取数据失败: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        //获取查看详情按钮并设置点击事件
        Button reviewListButton = dialog.findViewById(R.id.button_review_word_list);
        reviewListButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WordReviewListActivity.class);
            intent.putExtra("selectedDate", selectedDate); // 传递选中的日期
            startActivity(intent);
            dialog.dismiss(); // 关闭 Dialog
        });

        // 获取 "开始复习" 按钮并设置点击事件
        Button startReviewButton = dialog.findViewById(R.id.button_start_review);
        startReviewButton.setOnClickListener(v -> {
            // 跳转到 ListenActivity 并传递选中的日期
            Intent intent = new Intent(requireContext(), ListenActivity.class);
            intent.putExtra("selectedDate", selectedDate); // 传递选中的日期
            startActivity(intent);
            dialog.dismiss(); // 关闭 Dialog
        });

        dialog.show();
    }


    private void showNoDataDialog() {
        // 创建 No Data Dialog
        Dialog noDataDialog = new Dialog(requireContext());
        noDataDialog.setContentView(R.layout.dialog_no_word);

        // 允许点击外部区域关闭 Dialog
        noDataDialog.setCancelable(true);
        noDataDialog.setCanceledOnTouchOutside(true);

        // 设置 Dialog 宽度为 MATCH_PARENT，高度为 WRAP_CONTENT
        noDataDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 确保 Dialog 透明，点击背景可关闭
        noDataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 显示 No Data Dialog
        noDataDialog.show();
    }

    private void bindData(LinearLayout container, List<BarDataEntity.Type> types) {
        container.removeAllViews(); // 清空旧视图

        if (types == null || types.isEmpty()) return; // 避免空数据错误

        int color1 = Color.parseColor("#c5ecce");
        int color2 = Color.parseColor("#f5e1a7");
        int color3 = Color.parseColor("#ffb5a0");

        // 计算最大比例值
        double maxScale = types.stream().mapToDouble(BarDataEntity.Type::getTypeScale).max().orElse(1);

        for (int i = 0; i < types.size(); i++) {
            final View item = LayoutInflater.from(getContext()).inflate(R.layout.item_calander_bar, container, false);
            final BarDataEntity.Type type = types.get(i);

            ((TextView) item.findViewById(R.id.name)).setText(type.getTypeName());
            final View bar = item.findViewById(R.id.bar);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(40);
            drawable.setColor(i == 0 ? color1 : (i == 1 ? color2 : color3));

            bar.setBackground(drawable);
            ((TextView) item.findViewById(R.id.percent)).setText(String.format("%.0f", type.getTypeScale()));
            ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.BLACK);

            final double finalMaxScale = maxScale;
            item.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    item.getViewTreeObserver().removeOnPreDrawListener(this);
                    int barContainerWidth = item.findViewById(R.id.bar_container).getWidth();
                    int percentTxtWidth = item.findViewById(R.id.percent).getWidth();
                    final int initWidth = barContainerWidth - percentTxtWidth;
                    final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bar.getLayoutParams();
                    lp.width = (int) (initWidth * type.getTypeScale() / finalMaxScale);
                    bar.setLayoutParams(lp);
                    return false;
                }
            });

            container.addView(item);
        }
    }
}