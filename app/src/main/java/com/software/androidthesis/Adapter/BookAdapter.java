package com.software.androidthesis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.software.androidThesis.R;
import com.software.androidthesis.Activity.WordListActivity;
import com.software.androidthesis.entity.BookItem;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/14 23:57
 * @Decription:
 */
public class BookAdapter extends BaseAdapter {
    private Context context;
    private List<BookItem> bookList;
    private LayoutInflater inflater;

    public BookAdapter(Context context, List<BookItem> bookList) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.bookList = bookList;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_book_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = convertView.findViewById(R.id.checkbox_parent);
            viewHolder.listView = convertView.findViewById(R.id.expandable_listview_units);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final BookItem bookItem = bookList.get(position);
        viewHolder.checkBox.setText(bookItem.getBookName());

        // 设置 CheckBox 的点击事件
        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // 展开单元的 ListView
                UnitAdapter unitAdapter = new UnitAdapter(context, bookItem.getUnits(), bookItem.getBookName());
                viewHolder.listView.setAdapter(unitAdapter);
                viewHolder.listView.setVisibility(View.VISIBLE);

                // 手动设置 ListView 的高度
                setListViewHeight(viewHolder.listView);

                // 点击单元时跳转到 WordActivity
                viewHolder.listView.setOnItemClickListener((parentView, view, unitPosition, id) -> {
                    String unit = bookItem.getUnits().get(unitPosition);
                    Intent intent = new Intent(context, WordListActivity.class);
                    intent.putExtra("book", bookItem.getBookName());
                    intent.putExtra("unit", unit);
                    context.startActivity(intent);
                });

            } else {
                // 隐藏单元的 ListView
                viewHolder.listView.setVisibility(View.GONE);
            }
        });

        return convertView;
    }

    // 设置 ListView 的高度
    private void setListViewHeight(ListView listView) {
        // 获取 ListView 的 Adapter
        UnitAdapter unitAdapter = (UnitAdapter) listView.getAdapter();
        if (unitAdapter == null) {
            return;
        }

        // 计算 ListView 高度
        int totalHeight = 0;
        for (int i = 0; i < unitAdapter.getCount(); i++) {
            View listItem = unitAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        // 设置 ListView 的高度
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (unitAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    // ViewHolder 类来缓存视图
    static class ViewHolder {
        CheckBox checkBox;
        ListView listView;
    }
}

