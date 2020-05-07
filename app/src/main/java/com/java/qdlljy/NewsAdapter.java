package com.java.qdlljy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.java.qdlljy.NewsUtil.News;
import com.java.qdlljy.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<News> mnewsList;
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder{
//        ImageView newsImage;
        TextView newsTitle;
        TextView newsSection;
        TextView publisher;
        TextView publishTime;
        MyImageView newsImage;
        public ViewHolder(View view) {
            super(view);
//            newsImage = (ImageView)view.findViewById(R.id.news_image);
            newsTitle = (TextView)view.findViewById(R.id.title_card);
            newsSection = (TextView)view.findViewById(R.id.section_card);
            publisher = (TextView)view.findViewById(R.id.author_card);
            publishTime = (TextView)view.findViewById(R.id.date_card);
            newsImage = (MyImageView)view.findViewById(R.id.thumbnail_image_card);
        }
    }

    public  NewsAdapter(List<News> newsList) {
        mnewsList = newsList;
    }

    public void clear() {
        mnewsList.clear();
    }

    public void addList(List<News> newsData) {
        mnewsList.addAll(newsData);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.newsTitle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                News now = mnewsList.get(position);
                MyApplication.newsOperator.ReadNews(now);
//                now.isRead = 1;
                notifyDataSetChanged();
                Intent intent = new Intent(context, NewsActivity.class);
                intent.putExtra("news_data", now);
                context.startActivity(intent);
//                test

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News news = mnewsList.get(position);
//        holder.newsImage.setI();
        holder.newsTitle.setText(news.title);
        holder.publisher.setText(news.publisher);
        holder.newsSection.setText(news.content);
        holder.publishTime.setText(news.publishTime);
        if (news.isRead == 1)
            holder.newsTitle.setTextColor(Color.GRAY);
//        Log.d("fuck", "onBindViewHolder: "+news.Images.size());
//        Log.d("fuck", "onBindViewHolder: "+news.newsID);
        if (news.Images.size() > 0) {
//            Log.d("fuck"+news.title, news.Images.get(0));
//            Log.d("image", "onBindViewHolder: "+position);
            Glide.with(context).load(news.Images.get(0)).into(holder.newsImage);
//            holder.newsImage.setImageURL(news.Images.get(0));
        }
        else {
            holder.newsImage.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return mnewsList.size();
    }

}
