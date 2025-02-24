package com.software.androidthesis.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.software.androidThesis.R;
import com.software.androidthesis.entity.WordDTO;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/24 12:32
 * @Decription:
 */
public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private Context context;
    private List<WordDTO> wordList;

    public WordAdapter(Context context, List<WordDTO> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordDTO word = wordList.get(position);
        holder.wordText.setText(word.getWord());
        holder.proText.setText(word.getPro());
        holder.meanText.setText(word.getMean());

    }

    @Override
    public int getItemCount() {
        return wordList != null ? wordList.size() : 0;
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordText, proText, meanText;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.text_word);
            proText = itemView.findViewById(R.id.text_pro);
            meanText = itemView.findViewById(R.id.text_mean);
        }
    }
}
