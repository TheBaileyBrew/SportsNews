package com.thebaileybrew.sportsnews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SportsAdapter extends RecyclerView.Adapter<SportsAdapter.SportHolder> {
    private LayoutInflater layoutInflater;
    private List<Sport> SportsDatabase;

    //Create the Recycler
    public SportsAdapter(Context context, List<Sport> SportsDatabase) {
        this.layoutInflater = LayoutInflater.from(context);
        this.SportsDatabase = SportsDatabase;
    }

    //Create the inflated view via the Recycler
    @Override
    public SportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.football_list_item, parent, false);
        return new SportHolder(view);
    }

    //Bind the Array Data to the individual layout views
    @Override
    public void onBindViewHolder(SportHolder holder, int position) {
        final Sport currentSport = SportsDatabase.get(position);
        //Assign the values to the Holder positions
        holder.articleTitleDisplay.setText(currentSport.getArticleName());
        holder.articleAuthorDisplay.setText(currentSport.getArticleAuthor());
        holder.articleTimeDisplay.setText(currentSport.getArticleTime());
        holder.articleDateDisplay.setText(currentSport.getArticleDate());
        holder.articleSourceText.setText(currentSport.getArticleCategory());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri sportUri = Uri.parse(currentSport.getArticleUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, sportUri);
                v.getContext().startActivity(websiteIntent);
            }
        });
    }

    //Return the Array Size
    @Override
    public int getItemCount() {
        if (SportsDatabase == null) {
            return 0;
        } else {
            return SportsDatabase.size();
        }
    }

    //Define and pair your custom holder views
    class SportHolder extends RecyclerView.ViewHolder {
        TextView articleTitleDisplay;
        TextView articleDateDisplay;
        TextView articleTimeDisplay;
        TextView articleAuthorDisplay;
        TextView articleSourceText;

        private SportHolder(View convertView) {
            super(convertView);
            articleTitleDisplay = convertView.findViewById(R.id.article_title);
            articleDateDisplay = convertView.findViewById(R.id.football_news_date);
            articleTimeDisplay = convertView.findViewById(R.id.football_news_time);
            articleAuthorDisplay = convertView.findViewById(R.id.article_author);
            articleSourceText = convertView.findViewById(R.id.source_type);
        }
    }
}



