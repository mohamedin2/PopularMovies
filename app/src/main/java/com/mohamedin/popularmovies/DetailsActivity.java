package com.mohamedin.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mohamedin.popularmovies.pojo.Movie;

public class DetailsActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            Intent intent = this.getIntent();
            Movie movie = null;
            if (intent != null) {
                movie = intent.getParcelableExtra("movie");
            }
            DetailsFragment detailsFragment = DetailsFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, detailsFragment)
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
