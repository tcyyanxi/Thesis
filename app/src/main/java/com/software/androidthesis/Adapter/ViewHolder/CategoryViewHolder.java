package com.software.androidthesis.Adapter.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.software.androidThesis.R;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/3 13:03
 * @Decription:
 */
public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public TextView categoryNameTextView;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryNameTextView = itemView.findViewById(R.id.cbLibraryLeft);
    }
}
