package com.software.androidthesis.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.software.androidThesis.R;
import com.software.androidthesis.Activity.WordListActivity;
import com.software.androidthesis.api.ApiServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/13 14:02
 * @Decription:
 */
public class BChildWordFragment extends Fragment {
    private ExpandableListView expandableListView;
    private Map<String, List<String>> bookUnitMap = new HashMap<>();
    private List<String> bookList = new ArrayList<>();
    private SimpleExpandableListAdapter adapter;
    // 预定义书籍的固定顺序
    private List<String> predefinedOrder = Arrays.asList(
            "七上", "七下", "八上", "八下", "九全"
    );


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_word_list, container, false);
        expandableListView = view.findViewById(R.id.expandableListView);

        fetchBooksAndUnits();

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String book = bookList.get(groupPosition);
            String unit = bookUnitMap.get(book).get(childPosition);

            Intent intent = new Intent(getActivity(), WordListActivity.class);
            intent.putExtra("book", book);
            intent.putExtra("unit", unit);
            startActivity(intent);
            return true;
        });

        return view;
    }

    private void fetchBooksAndUnits() {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getBooks(new ApiServiceImpl.ApiCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> response) {
                // 处理返回的书籍列表
                if (response != null && !response.isEmpty()) {
                    // 根据预定义的顺序进行排序
                    Collections.sort(response, new Comparator<String>() {
                        @Override
                        public int compare(String book1, String book2) {
                            int index1 = predefinedOrder.indexOf(book1);
                            int index2 = predefinedOrder.indexOf(book2);

                            // 如果没有在预定义顺序中找到，按照原始顺序显示
                            if (index1 == -1 && index2 == -1) {
                                return 0;
                            }
                            if (index1 == -1) {
                                return 1;  // book1 在预定义顺序中找不到，排到后面
                            }
                            if (index2 == -1) {
                                return -1; // book2 在预定义顺序中找不到，排到后面
                            }
                            return Integer.compare(index1, index2);
                        }
                    });

                    // 假设每个书籍和单元都有相关的内容
                    for (String book : response) {
                        // 获取每本书的单元
                        fetchUnitsForBook(book);
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ApiService", "获取书籍列表失败: " + errorMessage);
            }
        });
    }

    private void fetchUnitsForBook(String book) {
        ApiServiceImpl apiService = new ApiServiceImpl();
        apiService.getUnits(book, new ApiServiceImpl.ApiCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> units) {
                if (units != null && !units.isEmpty()) {
                    // 存储单元到相应书籍下
                    bookUnitMap.put(book, units);
                    bookList.add(book);  // 添加book到bookList
                }
                updateAdapter();  // 更新适配器
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ApiService", "获取单元失败: " + errorMessage);
            }
        });
    }

    private void updateAdapter() {
        List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childData = new ArrayList<>();

        for (String book : bookList) {
            Map<String, String> group = new HashMap<>();
            group.put("book", book);
            groupData.add(group);

            List<Map<String, String>> children = new ArrayList<>();
            for (String unit : bookUnitMap.get(book)) {
                Map<String, String> child = new HashMap<>();
                child.put("unit", unit);
                children.add(child);
            }
            childData.add(children);
        }

        adapter = new SimpleExpandableListAdapter(
                getContext(),
                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{"book"},
                new int[]{android.R.id.text1},
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"unit"},
                new int[]{android.R.id.text1}
        );

        getActivity().runOnUiThread(() -> expandableListView.setAdapter(adapter));
    }

}
