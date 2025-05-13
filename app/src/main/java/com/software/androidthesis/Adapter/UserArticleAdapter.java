package com.software.androidthesis.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.software.androidThesis.R;
import com.software.androidthesis.entity.UserArticle;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/4/2 22:40
 * @Decription:
 */
public class UserArticleAdapter extends RecyclerView.Adapter<UserArticleAdapter.ViewHolder> {
    private List<UserArticle> userArticles;

    public UserArticleAdapter(List<UserArticle> userArticles) {
        this.userArticles = userArticles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserArticle userArticle = userArticles.get(position);
        Log.d("Adapter", "绑定数据: articleId=" + userArticle.getArticleId() +
                ", count=" + userArticle.getCount() +
                ", time=" + userArticle.getTime());

        holder.countTextView.setText("分数: " + userArticle.getCount());
        holder.timeTextView.setText("时间: " + userArticle.getTime());
    }

    @Override
    public int getItemCount() {
        return userArticles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView countTextView, timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countTextView = itemView.findViewById(R.id.countTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
