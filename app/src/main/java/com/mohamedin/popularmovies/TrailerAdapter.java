package com.mohamedin.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mohamedin.popularmovies.pojo.Trailer;

import java.util.List;

public class TrailerAdapter extends ArrayAdapter<Trailer>{

    public TrailerAdapter(Context context, List<Trailer> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
        }

        TextView trailerInfo = (TextView) convertView.findViewById(R.id.trailer_name);
        trailerInfo.setText(trailer.getName() + " (" + trailer.getType() + ")");

        return convertView;
    }

}
