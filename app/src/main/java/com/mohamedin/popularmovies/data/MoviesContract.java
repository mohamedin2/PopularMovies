package com.mohamedin.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.mohamedin.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";
    public static final String PATH_VIDEO = "videos";
    public static final String PATH_REVIEW = "reviews";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster_url";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_FAVORITE = "is_fav";
        public static final String COLUMN_TOP_RATED = "is_top";
        public static final String COLUMN_POP = "is_pop";

        public static final String PARAM_TYPE = "type";

        public static final String COLUMN_POP_ORDER = "pop_order";
        public static final String COLUMN_TOP_ORDER = "top_order";

        public static final int TYPE_POP = 1;
        public static final int TYPE_TOP = 2;
        public static final int TYPE_FAV = 3;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieType(int type) {
            return CONTENT_URI.buildUpon().appendQueryParameter(PARAM_TYPE, String.valueOf(type)).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
        public static int getTypeFromUri(Uri uri) {
            String typeString = uri.getQueryParameter(PARAM_TYPE);
            if (null != typeString && typeString.length() > 0)
                return Integer.parseInt(typeString);
            else
                return TYPE_POP;
        }
    }

    public static final class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        // Table name
        public static final String TABLE_NAME = "videos";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_KEY = "key";

        public static final String COLUMN_TYPE = "type";

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // Table name
        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_URL = "url";

        public static final String COLUMN_SUMMARY = "summary";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
