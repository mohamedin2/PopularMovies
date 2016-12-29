package com.mohamedin.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mohamedin.popularmovies.pojo.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MoviesAdapter extends ArrayAdapter<Movie> {

    public MoviesAdapter(Context context, List<Movie> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView movieView = (ImageView) convertView.findViewById(R.id.moviePoster);
        movieView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterUrl()).into(movieView);

        return convertView;
    }
}
