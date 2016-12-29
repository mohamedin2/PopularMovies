package com.mohamedin.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mohamedin.popularmovies.pojo.Review;

import java.util.List;

/**
 * Created by MAM2 on 6/22/2016.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, List<Review> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(android.R.id.text1);
        TextView subTitle = (TextView) convertView.findViewById(android.R.id.text2);

        title.setText("Review By: " + review.getAuthor());
        int max = review.getSummary().length();
        subTitle.setText(max > 150? review.getSummary().substring(0, 150) + "..." : review.getSummary());
        return convertView;
    }

}
