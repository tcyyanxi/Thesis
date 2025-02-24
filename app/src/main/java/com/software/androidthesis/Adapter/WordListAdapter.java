package com.software.androidthesis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.software.androidThesis.R;
import com.software.androidthesis.Activity.WordActivity;
import com.software.androidthesis.Activity.WordListActivity;
import com.software.androidthesis.entity.Word;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/15 15:56
 * @Decription:
 */
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class WordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_SELECTED = 1;
    private Context context;
    private List<Word> wordList;

    public WordListAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @Override
    public int getItemViewType(int position) {
        Word word = wordList.get(position);
        return word.isSelected() ? TYPE_SELECTED : TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SELECTED) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_word_list_selected, parent, false);
            return new SelectedViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_word_list, parent, false);
            return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Word word = wordList.get(position);
        if (holder instanceof SelectedViewHolder) {
            ((SelectedViewHolder) holder).bind(word);
        } else {
            ((DefaultViewHolder) holder).bind(word);
        }

        // 为每个单词项设置点击监听
        holder.itemView.setOnClickListener(v -> {
            // 创建 Intent 跳转到 WordActivity
            Intent intent = new Intent(context, WordActivity.class);
            // 将单词信息传递到 WordActivity
            intent.putExtra("word", word.getWord());
            intent.putExtra("pro", word.getPro());
            intent.putExtra("mean", word.getMean());
            // 启动 Activity
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return wordList.size();
    }

    // 默认布局 ViewHolder
    class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView wordText;
        CheckBox wordCheckBox;  // 添加 CheckBox

        public DefaultViewHolder(View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.wordText);
            wordCheckBox = itemView.findViewById(R.id.checkbox_word_item);  // 获取 CheckBox
        }

        public void bind(Word word) {
            wordText.setText(word.getWord());
            wordCheckBox.setChecked(word.isSelected());  // 根据单词的选中状态设置 CheckBox 的状态

            // 监听 CheckBox 的点击事件
            wordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                word.setSelected(isChecked);
                // 如果需要，更新永久选中状态等
                if (isChecked) {
                    word.setPermanentSelected(true);  // 永久标记已选中
                } else {
                    word.setPermanentSelected(false); // 取消永久选中状态
                }
            });
        }
    }

    // 已选布局 ViewHolder
    class SelectedViewHolder extends RecyclerView.ViewHolder {
        TextView wordText;

        public SelectedViewHolder(View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.wordText);
        }

        public void bind(Word word) {
            wordText.setText(word.getWord());
        }
    }
}
