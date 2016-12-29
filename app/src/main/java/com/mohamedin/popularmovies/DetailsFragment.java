package com.mohamedin.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mohamedin.popularmovies.data.MoviesContract;
import com.mohamedin.popularmovies.pojo.Movie;
import com.mohamedin.popularmovies.pojo.Review;
import com.mohamedin.popularmovies.pojo.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    private static final String ARG_PARAM_MOVIE = "movie";

    private Movie mParamMovie;

    private TrailerAdapter mTrailerAdapter;
    private String movieId = null;
    private ListView trailerListView;

    private ReviewAdapter mReviewAdapter;
    private ListView reviewListView;

    private OnFragmentInteractionListener mListener;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramMovie Movie Parameter.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(Movie paramMovie) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_MOVIE, paramMovie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamMovie = getArguments().getParcelable(ARG_PARAM_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        if (mParamMovie != null) {
            final Movie movie = mParamMovie;
            movieId = movie.getId();
            Calendar releaseDate = Calendar.getInstance();
            releaseDate.setTimeInMillis(movie.getReleaseDate().getTime());

            ImageView movieView = (ImageView) rootView.findViewById(R.id.moviePoster);
            movieView.setScaleType(ImageView.ScaleType.FIT_START);
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterUrl()).into(movieView);

            TextView titleView = (TextView) rootView.findViewById(R.id.movieTitle);
            titleView.setText(movie.getTitle());

            TextView yearView = (TextView) rootView.findViewById(R.id.releaseYear);
            yearView.setText(String.valueOf(releaseDate.get(Calendar.YEAR)));

            TextView ratingView = (TextView) rootView.findViewById(R.id.movieRating);
            ratingView.setText(movie.getRating()+"/10");

            final Uri movieUri = MoviesContract.MovieEntry.buildMovieUri(Long.parseLong(movie.getId()));
            final CheckBox addToFav = (CheckBox) rootView.findViewById(R.id.addToFavorite);
            addToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addToFav.isChecked()) {
                        getContext().getContentResolver().insert(movieUri, movie.toCV());
                    } else {
                        getContext().getContentResolver().delete(movieUri, null, null);
                    }
                }
            });
            Cursor cursor = getContext().getContentResolver().query(movieUri, null, null, null, null);
            boolean isFav = false;
            if (cursor.moveToFirst()) {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, cv);
                isFav = cv.getAsInteger(MoviesContract.MovieEntry.COLUMN_FAVORITE) == 1;
            }
            addToFav.setChecked(isFav);

            TextView synopsisView = (TextView) rootView.findViewById(R.id.movieSynopsis);
            synopsisView.setText(movie.getSynopsis());

            trailerListView = (ListView) rootView.findViewById(R.id.trailersList);
            trailerListView.setAdapter(mTrailerAdapter);

            trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Trailer item = (Trailer) parent.getItemAtPosition(position);
                    Uri youTubeVideo = Uri.parse("http://www.youtube.com/watch").buildUpon().appendQueryParameter("v", item.getKey()).build();
                    Intent videoIntent = new Intent(Intent.ACTION_VIEW, youTubeVideo);
                    if (videoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(videoIntent);
                    }
                }
            });

            reviewListView = (ListView) rootView.findViewById(R.id.reviewsList);

            reviewListView.setAdapter(mReviewAdapter);
            reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Review item = (Review) parent.getItemAtPosition(position);
                    Uri reviewUri = Uri.parse(item.getUrl());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, reviewUri);
                    if (browserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(browserIntent);
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMoviesTrailersTask trailersTask = new FetchMoviesTrailersTask();
        trailersTask.execute(movieId);
        FetchMoviesReviewsTask reviewsTask = new FetchMoviesReviewsTask();
        reviewsTask.execute(movieId);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public class FetchMoviesTrailersTask extends AsyncTask<String, Void, List<Trailer>> {
        private final String LOG_TAG = FetchMoviesTrailersTask.class.getSimpleName();

        private List<Trailer> getTrailersDataFromJson(String jsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String LIST = "results";
            final String TYPE = "type";
            final String KEY = "key";
            final String NAME = "name";
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailersArray = trailerJson.getJSONArray(LIST);


            List<Trailer> results = new ArrayList<>();
            for(int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailer = trailersArray.getJSONObject(i);

                results.add(new Trailer(trailer.getString(NAME), trailer.getString(KEY), trailer.getString(TYPE)));
            }
            return results;
        }
        @Override
        protected List<Trailer> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            String id = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        "https://api.themoviedb.org/3/movie/";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendPath(id).appendPath("videos")
                        .appendQueryParameter(API_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> result) {
            if (result != null) {
                mTrailerAdapter.clear();
                for(Trailer trailer : result) {
                    mTrailerAdapter.add(trailer);
                }
                //TODO Use another lightweight solution!
                setListViewHeightBasedOnChildren(trailerListView);
            }
        }
    }

    public class FetchMoviesReviewsTask extends AsyncTask<String, Void, List<Review>> {
        private final String LOG_TAG = FetchMoviesReviewsTask.class.getSimpleName();

        private List<Review> getReviewsDataFromJson(String jsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String LIST = "results";
            final String AUTHOR = "author";
            final String URL = "url";
            final String SUMMARY = "content";
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewsArray = reviewJson.getJSONArray(LIST);


            List<Review> results = new ArrayList<>();
            for(int i = 0; i < reviewsArray.length(); i++) {
                JSONObject review = reviewsArray.getJSONObject(i);

                results.add(new Review(review.getString(AUTHOR), review.getString(URL), review.getString(SUMMARY)));
            }
            return results;
        }
        @Override
        protected List<Review> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            String id = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        "https://api.themoviedb.org/3/movie/";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendPath(id).appendPath("reviews")
                        .appendQueryParameter(API_PARAM, BuildConfig.MOVIES_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Review> result) {
            if (result != null) {
                mReviewAdapter.clear();
                for(Review review : result) {
                    mReviewAdapter.add(review);
                }
                //TODO Use another lightweight solution!
                setListViewHeightBasedOnChildren(reviewListView);
            }
        }
    }
}
