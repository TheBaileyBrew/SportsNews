package com.thebaileybrew.sportsnews;

public class Sport {
    public String articleName; //webTitle
    public String articleDate; //webPublicationDate
    public String articleTime; //webPublicationDate
    public String articleUrl; //webUrl
    public String articleCategory; //pillarName
    public String articleAuthor;

    public Sport(String articleName, String articleDate, String articleTime, String articleUrl, String articleCategory, String articleAuthor) {
        this.articleName = articleName;
        this.articleDate = articleDate;
        this.articleTime = articleTime;
        this.articleUrl = articleUrl;
        this.articleCategory = articleCategory;
        this.articleAuthor = articleAuthor;
    }

    public String getArticleName() {
        return articleName;
    }
    public String getArticleDate() {
        return articleDate;
    }
    public String getArticleTime() {
        return articleTime;
    }
    public String getArticleUrl() {
        return articleUrl;
    }
    public String getArticleCategory() {
        return articleCategory;
    }
    public String getArticleAuthor() {
        return articleAuthor;
    }
}
