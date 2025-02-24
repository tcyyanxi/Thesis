package com.software.androidthesis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.software.androidThesis.R;
import com.software.androidthesis.Activity.ArticleActivity;
import com.software.androidthesis.entity.Article;

import org.json.JSONArray;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/13 22:50
 * @Decription:
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private Context context;
    private List<Article> articleList;

    public ArticleAdapter(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        final Article article = articleList.get(position);
        holder.title.setText(article.getTitle());
        holder.words.setText(article.getArticlesSum() + " words");
        holder.badge.setText(article.getCategory());

        // 直接处理 byte[] 图片数据
        byte[] imageData = article.getImg();  // 获取 byte[] 数据
        if (imageData != null && imageData.length > 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                holder.image.setImageBitmap(bitmap);
            } catch (Exception e) {
                // 处理图片解码错误
                holder.image.setImageResource(R.drawable.ic_default_avatar); // 占位图
            }
        } else {
            holder.image.setImageResource(R.drawable.ic_default_avatar); // 占位图
        }

        // 为每个 item 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            // 创建 Intent 跳转到 ArticleActivity
            Intent intent = new Intent(context, ArticleActivity.class);
            // 将数据传递给 ArticleActivity
            intent.putExtra("articleId", article.getArticleId());
            intent.putExtra("title", article.getTitle());
            intent.putExtra("content", article.getContent());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title, words, badge;
        ImageView image;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_a);
            words = itemView.findViewById(R.id.words_a);
            badge = itemView.findViewById(R.id.badge_a);
            image = itemView.findViewById(R.id.image_a);
        }
    }
}
