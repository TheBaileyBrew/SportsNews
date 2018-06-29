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

public class FootballAdapter extends ArrayAdapter<Football> {

    public FootballAdapter(Activity context, ArrayList<Football> FootballNews) {
        super(context, 0, FootballNews);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.football_list_item, parent, false);
        }

        Football currentNews = getItem(position);
        //Sets the title of article
        TextView articleTitleDisplay = convertView.findViewById(R.id.article_title);
        String articleTitle = currentNews.getArticleName();
        articleTitleDisplay.setText(articleTitle);
        //Sets the date of article posting
        TextView articleDateDisplay = convertView.findViewById(R.id.football_news_date);
        String articleDate = currentNews.getArticleDate();
        articleDateDisplay.setText(articleDate);
        //Sets the time of article posting
        TextView articleTimeDisplay = convertView.findViewById(R.id.football_news_time);
        String articleTime = currentNews.getArticleTime();
        articleTimeDisplay.setText(articleTime);
        //Sets the icon drawable for category
        ImageView articleSourceDisplay = convertView.findViewById(R.id.source_image);
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
