package com.mohamedin.popularmovies.pojo;

/**
 * Created by MAM2 on 6/22/2016.
 */
public class Review {
    private String author, url, summary;

    public Review() {
    }

    public Review(String author, String url, String summary) {
        this.author = author;
        this.url = url;
        this.summary = summary;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
