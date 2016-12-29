package com.mohamedin.popularmovies.pojo;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.mohamedin.popularmovies.data.MoviesContract;

import java.util.Date;


public class Movie implements Parcelable {
    private String id, title, posterUrl, synopsis, rating;
    private Date releaseDate;

    public Movie(){}

    public Movie(String id, String title, String posterUrl, String synopsis, String rating, Date releaseDate) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        posterUrl = in.readString();
        synopsis = in.readString();
        rating = in.readString();
        releaseDate = new Date(in.readLong());
    }

    public Movie(ContentValues cv) {
        id = cv.getAsString(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
        title = cv.getAsString(MoviesContract.MovieEntry.COLUMN_TITLE);
        posterUrl = cv.getAsString(MoviesContract.MovieEntry.COLUMN_POSTER_URL);
        synopsis = cv.getAsString(MoviesContract.MovieEntry.COLUMN_SYNOPSIS);
        rating = cv.getAsString(MoviesContract.MovieEntry.COLUMN_RATING);
        releaseDate = new Date(cv.getAsLong(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
    }

    public ContentValues toCV() {
        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, id);
        cv.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
        cv.put(MoviesContract.MovieEntry.COLUMN_POSTER_URL, posterUrl);
        cv.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);
        cv.put(MoviesContract.MovieEntry.COLUMN_RATING, rating);
        cv.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate.getTime());
        //TODO currently we are using the content provider for favorites only
        cv.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, 1);
        return cv;
    }
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterUrl);
        dest.writeString(synopsis);
        dest.writeString(rating);
        dest.writeLong(releaseDate.getTime());
    }
}
