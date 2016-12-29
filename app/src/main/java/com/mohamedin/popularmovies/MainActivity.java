package com.mohamedin.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mohamedin.popularmovies.pojo.Movie;

public class MainActivity extends AppCompatActivity implements GridFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener {
    private boolean mTwoPane = false;
    private final static String DETAILS_FRAGMENT_TAG = "DFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName() );
            intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(Movie movie) {
        if (mTwoPane) {
            DetailsFragment detailsFragment = DetailsFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailsFragment, DETAILS_FRAGMENT_TAG)
                    .commit();
        } else {
            //Create intent
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("movie", movie);

            //Start details activity
            startActivity(intent);
        }
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }
}
