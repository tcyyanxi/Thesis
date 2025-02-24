package com.software.androidthesis.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.BookAdapter;
import com.software.androidthesis.api.ApiServiceImpl;
import com.software.androidthesis.entity.BookItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/13 14:02
 * @Decription:
 */
public class BChildWordFragment extends Fragment {
    private ListView listView;
    private List<BookItem> bookList = new ArrayList<>();
    private BookAdapter adapter;

    // 预定义书籍的固定顺序
    private List<String> predefinedOrder = Arrays.asList("七上", "七下", "八上", "八下", "九全");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_word_list, container, false);
        listView = view.findViewById(R.id.listView); // 确保这里的 ID 对应你的 XML 文件中的 ListView

        if (bookList == null) {
            bookList = new ArrayList<>();
        }
        adapter = new BookAdapter(getContext(), bookList);
        listView.setAdapter(adapter);

        fetchBooksAndUnits();

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
                            if (index1 == -1 && index2 == -1) return 0;
                            if (index1 == -1) return 1;
                            if (index2 == -1) return -1;
                            return Integer.compare(index1, index2);
                        }
                    });

                    // 假设每个书籍和单元都有相关的内容
                    for (String book : response) {
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
                    bookList.add(new BookItem(book, units));  // 添加书籍及其单元到列表
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
        if (getContext() == null) {
            Log.e("BChildWordFragment", "Context is null, skipping adapter update");
            return;
        }

        if (bookList.isEmpty()) {
            Log.w("BChildWordFragment", "Book list is empty, skipping adapter update");
            return;
        }

        if (adapter == null) {
            adapter = new BookAdapter(getContext(), bookList);
            getActivity().runOnUiThread(() -> {
                if (listView != null) {
                    listView.setAdapter(adapter);
                }
            });
        } else {
            getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        }
    }

}
