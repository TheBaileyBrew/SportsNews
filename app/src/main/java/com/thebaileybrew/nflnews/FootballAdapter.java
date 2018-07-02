package com.thebaileybrew.nflnews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FootballAdapter extends ArrayAdapter<Football> {
    //ButterKnife Views to Bind
    @BindView(R.id.article_title) private TextView articleTitleDisplay;
    @BindView(R.id.football_news_date) private TextView articleDateDisplay;
    @BindView(R.id.football_news_time) private TextView articleTimeDisplay;
    @BindView(R.id.source_image) private ImageView articleSourceDisplay;

    public FootballAdapter(Activity context, ArrayList<Football> FootballNews) {
        super(context, 0, FootballNews);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.football_list_item, parent, false);
        }
        ButterKnife.bind(this, convertView);
        Football currentNews = getItem(position);
        //Sets the title of article
        String articleTitle = currentNews.getArticleName();
        articleTitleDisplay.setText(articleTitle);
        //Sets the date of article posting
        String articleDate = currentNews.getArticleDate();
        articleDateDisplay.setText(articleDate);
        //Sets the time of article posting
        String articleTime = currentNews.getArticleTime();
        articleTimeDisplay.setText(articleTime);
        //Sets the icon drawable for category
        String articleSource = currentNews.getArticleCategory();
        switch (articleSource) {
            case "News":
                articleSourceDisplay.setImageResource(R.drawable.news_vector);
                break;
            case "Sport":
                articleSourceDisplay.setImageResource(R.drawable.quarterback_vector);
                break;
            default:
                break;
        }
        return convertView;
    }
}
