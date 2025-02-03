package com.software.androidthesis.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.software.androidThesis.R;
import com.software.androidthesis.Adapter.ViewHolder.CategoryViewHolder;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/3 12:58
 * @Decription:类别的adapter
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder>{
    private Context context;
    private List<String> categories;
    private OnItemClickListener mListener;
    //是否选择
    private int selectedPosition = -1;

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

    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_library_left, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryNameTextView.setText(category);
        holder.itemView.setSelected( position == selectedPosition);
        holder.itemView.setOnClickListener(v ->{
            int currentPosition = holder.getAdapterPosition();
            if (selectedPosition == currentPosition) {
                // 如果点击的是已选中的项，则取消选中并重置selectedPosition
                selectedPosition = -1;
            } else {
                selectedPosition = currentPosition;
            }
            // 通知数据集变更，重新绑定所有ViewHolder
            notifyDataSetChanged();
            if(mListener != null){
                mListener.onItemClick(categories.get(currentPosition));
            }else {
                Log.d("CategoryAdapter", "mListener is null"+ currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

}

