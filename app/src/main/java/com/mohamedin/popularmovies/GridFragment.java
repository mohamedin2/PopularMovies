package com.mohamedin.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mohamedin.popularmovies.data.MoviesContract;
import com.mohamedin.popularmovies.pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridFragment extends Fragment {
    private MoviesAdapter mMoviesAdapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int mPosition = GridView.INVALID_POSITION;
    private final static String SELECTED_KEY = "selected_key";
    private GridView mGridView;

    private int mSortOrder = -1;

    public GridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridFragment newInstance(String param1, String param2) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSortOrder = Integer.parseInt(prefs.getString("sort_order", "0"));

        mMoviesAdapter =
                new MoviesAdapter(
                        getActivity(), new ArrayList<Movie>());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.moviesGrid);
        mGridView.setAdapter(mMoviesAdapter);
        mGridView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                v.requestFocus();
                                                mPosition = position;
                                                Movie item = (Movie) parent.getItemAtPosition(position);
                                                if (mListener != null) {
                                                    mListener.onFragmentInteraction(item);
                                                }
                                            }
                                        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMoviesTask movieTask = new FetchMoviesTask();
        movieTask.execute();
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int sortOrder = Integer.parseInt(prefs.getString("sort_order", "0"));

        if (sortOrder != mSortOrder) {
            mPosition = GridView.INVALID_POSITION;
            FetchMoviesTask movieTask = new FetchMoviesTask();
            movieTask.execute();

            mSortOrder = sortOrder;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        private List<Movie> getMoviesDataFromJson(String jsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String LIST = "results";
            final String POSTER_PATH = "poster_path";
            final String SYNOPSIS = "overview";
            final String RELEASE_DATE = "release_date";
            final String ID = "id";
            final String TITLE = "title";
            final String RATING = "vote_average";
            JSONObject moviesJson = new JSONObject(jsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(LIST);


            List<Movie> results = new ArrayList<>();
            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie = moviesArray.getJSONObject(i);

                Date releaseDate = null;
                try {
                    releaseDate = dateFormat.parse(movie.getString(RELEASE_DATE));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                results.add(new Movie(movie.getString(ID), movie.getString(TITLE), movie.getString(POSTER_PATH), movie.getString(SYNOPSIS), movie.getString(RATING), releaseDate));
            }
            return results;
        }
        @Override
        protected List<Movie> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
           /* if (params.length == 0) {
                return null;
            }*/

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        "https://api.themoviedb.org/3/movie/";
                final String POPULAR_PATH = "popular";
                final String TOP_RATED_PATH = "top_rated";
                final String API_PARAM = "api_key";

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int sortBy = Integer.parseInt(prefs.getString("sort_order", "0"));
                if (sortBy == 2) {
                    //TODO fix these hack when all uses the content provider
                    Uri movieUri = MoviesContract.MovieEntry.buildMovieType(MoviesContract.MovieEntry.TYPE_FAV);
                    Cursor cursor = getContext().getContentResolver().query(movieUri, null, null, null, null);
                    List<Movie> results = new ArrayList<>();

                    if (cursor.moveToFirst()) {
                        do {
                            ContentValues cv = new ContentValues();
                            DatabaseUtils.cursorRowToContentValues(cursor, cv);
                            results.add(new Movie(cv));
                        } while (cursor.moveToNext());
                    }
                    return results;
                } else {
                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendPath(sortBy == 0 ? POPULAR_PATH : TOP_RATED_PATH)
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
                }
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
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                mMoviesAdapter.clear();
                for(Movie movie : result) {
                    mMoviesAdapter.add(movie);
                }
                if (mPosition != GridView.INVALID_POSITION) {
                    mGridView.setSelection(mPosition);
                } else if (mListener.isTwoPane()){
                    mListener.onFragmentInteraction(result.get(0));
                    mPosition = 0;
                }
            }
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
        void onFragmentInteraction(Movie movie);
        boolean isTwoPane();
    }
}
