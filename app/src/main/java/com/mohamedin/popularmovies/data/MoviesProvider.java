package com.mohamedin.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int ONE_MOVIE = 101;
    static final int REVIEWS = 200;
    static final int VIDEOS = 300;

    private static final SQLiteQueryBuilder sMoviesQueryBuilder = new SQLiteQueryBuilder();
    private static final SQLiteQueryBuilder sReviewsQueryBuilder = new SQLiteQueryBuilder();
    private static final SQLiteQueryBuilder sVideosQueryBuilder = new SQLiteQueryBuilder();
    static{
        sMoviesQueryBuilder.setTables(MoviesContract.MovieEntry.TABLE_NAME);
        sReviewsQueryBuilder.setTables(MoviesContract.ReviewEntry.TABLE_NAME);
        sVideosQueryBuilder.setTables(MoviesContract.VideoEntry.TABLE_NAME);
    }

    static String[] movieProjection = new String[]{MoviesContract.MovieEntry.COLUMN_MOVIE_ID, MoviesContract.MovieEntry.COLUMN_POSTER_URL,
            MoviesContract.MovieEntry.COLUMN_RATING, MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_SYNOPSIS, MoviesContract.MovieEntry.COLUMN_TITLE, MoviesContract.MovieEntry.COLUMN_FAVORITE};

    static String[] reviewProjection = new String[]{MoviesContract.ReviewEntry.COLUMN_URL, MoviesContract.ReviewEntry.COLUMN_SUMMARY,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR};

    static String[] videoProjection = new String[]{MoviesContract.VideoEntry.COLUMN_TYPE, MoviesContract.VideoEntry.COLUMN_NAME,
            MoviesContract.VideoEntry.COLUMN_KEY};

    private Cursor getMoviesByType(Uri uri) {
        int type = MoviesContract.MovieEntry.getTypeFromUri(uri);
        String[] selectionArgs = new String[]{"1"};
        String selection = null;
        String orderBy = null;
        switch (type) {
            case MoviesContract.MovieEntry.TYPE_POP:
                selection = MoviesContract.MovieEntry.COLUMN_POP + " = ?";
                orderBy = MoviesContract.MovieEntry.COLUMN_POP_ORDER;
                break;
            case MoviesContract.MovieEntry.TYPE_TOP:
                selection = MoviesContract.MovieEntry.COLUMN_TOP_RATED + " = ?";
                orderBy = MoviesContract.MovieEntry.COLUMN_TOP_ORDER;
                break;
            case MoviesContract.MovieEntry.TYPE_FAV:
                selection = MoviesContract.MovieEntry.COLUMN_FAVORITE + " = ?";
                break;
        }


        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                movieProjection,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );
    }

    private Cursor getMovieById(Uri uri) {
        long movieId = MoviesContract.MovieEntry.getIdFromUri(uri);

        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        String selection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                movieProjection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private Cursor getReviewsByMovieId(Uri uri) {
        long movieId = MoviesContract.MovieEntry.getIdFromUri(uri);

        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        String selection = MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?";

        return sReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                reviewProjection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private Cursor getVideosByMovieId(Uri uri) {
        long movieId = MoviesContract.MovieEntry.getIdFromUri(uri);

        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        String selection = MoviesContract.VideoEntry.COLUMN_MOVIE_ID + " = ?";

        return sVideosQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                videoProjection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", ONE_MOVIE);

        matcher.addURI(authority, MoviesContract.PATH_REVIEW + "/#", REVIEWS);

        matcher.addURI(authority, MoviesContract.PATH_VIDEO + "/#", VIDEOS);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new MoviesDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ONE_MOVIE:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case REVIEWS:
                return MoviesContract.ReviewEntry.CONTENT_TYPE;
            case VIDEOS:
                return MoviesContract.VideoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
            {
                retCursor = getMoviesByType(uri);
                break;
            }
            case ONE_MOVIE: {
                retCursor = getMovieById(uri);
                break;
            }
            case REVIEWS: {
                retCursor = getReviewsByMovieId(uri);
                break;
            }
            case VIDEOS: {
                retCursor = getVideosByMovieId(uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ONE_MOVIE: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(MoviesContract.VideoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case ONE_MOVIE:
                long movieId = MoviesContract.MovieEntry.getIdFromUri(uri);

                String[] selArgs = new String[]{String.valueOf(movieId)};
                String sel = MoviesContract.VideoEntry.COLUMN_MOVIE_ID + " = ?";
                rowsDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_NAME, sel, selArgs);
                break;
            case MOVIES:
                rowsDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        MoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS:
                rowsDeleted = db.delete(
                        MoviesContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(MoviesContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsUpdated = db.update(MoviesContract.VideoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEOS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
