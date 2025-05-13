package com.software.androidthesis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.software.androidThesis.R;
import com.software.androidthesis.Activity.WordActivity;
import com.software.androidthesis.Activity.WordReviewListActivity;
import com.software.androidthesis.entity.WordDTO;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/3/6 20:33
 * @Decription:
 */
public class WordReviewListAdapter extends RecyclerView.Adapter<WordReviewListAdapter.WordViewHolder> {

    private List<WordDTO> wordList;
    private WordReviewListActivity activity;

    public WordReviewListAdapter(List<WordDTO> wordList, WordReviewListActivity activity) {
        this.wordList = wordList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_list, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordDTO word = wordList.get(position);
        holder.wordText.setText(word.getWord());

        // 先清除旧的监听器，避免 RecyclerView 复用导致状态错乱
        holder.checkBox.setOnCheckedChangeListener(null);

        // 使用 activity 提供的方法来检查是否选中
        holder.checkBox.setChecked(activity.isWordSelected(word));

        // CheckBox 监听器
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            activity.updateSelectedWords(word, isChecked);
        });

        // 点击 Item 进入 WordActivity 查看单词详情
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, WordActivity.class);
            intent.putExtra("word", word.getWord());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordText;
        CheckBox checkBox;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.wordText);
            checkBox = itemView.findViewById(R.id.checkbox_word_item);
        }
    }
}
