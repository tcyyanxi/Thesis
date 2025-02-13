package com.software.androidthesis.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.ViewHolder.CategoryViewHolder;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/3 12:58
 * @Decription:类别的adapter
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private Context context;
    private List<String> categories;
    private OnItemClickListener mListener;

    // 选中项的位置，默认是“精选阅读”
    private int selectedPosition = 0;  // 假设默认选中第一项

    // 定义一个接口用于处理item点击事件
    public interface OnItemClickListener {
        void onItemClick(String category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_library_left, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryNameTextView.setText(category);

        // 处理选中项的样式
        if (position == selectedPosition) {
            // 设置选中项的背景为 R.drawable.article_background
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.article_background));
        } else {
            // 设置未选中项的背景为透明
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (selectedPosition != currentPosition) {
                selectedPosition = currentPosition;  // 更新选中项
                notifyDataSetChanged();  // 刷新适配器，更新样式
            }
            if (mListener != null) {
                mListener.onItemClick(categories.get(currentPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // 获取选中项
    public int getSelectedPosition() {
        return selectedPosition;
    }
}

