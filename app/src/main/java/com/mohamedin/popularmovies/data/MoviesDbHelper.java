package com.mohamedin.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +

                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_TOP_RATED + " INTEGER, " +
                MoviesContract.MovieEntry.COLUMN_FAVORITE + " INTEGER, " +
                MoviesContract.MovieEntry.COLUMN_POP + " INTEGER, " +
                MoviesContract.MovieEntry.COLUMN_POP_ORDER + " INTEGER, " +
                MoviesContract.MovieEntry.COLUMN_TOP_ORDER + " INTEGER " +
                " );";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + MoviesContract.VideoEntry.TABLE_NAME + " (" +
                MoviesContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesContract.VideoEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MoviesContract.VideoEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                MoviesContract.VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + ") " +
                " );";
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_SUMMARY + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + ") " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //TODO create real upgrade script
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
