package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.drawable.GradientDrawable;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(@NonNull Context context, @NonNull List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View listItemView = convertView;
        //check if the the view is null and inflating it (convert data from xml to object)
        if(listItemView==null)
        {
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.earthquack_list,parent,false);
        }
        Earthquake currentEarthquake=getItem(position);

        TextView magnitudeTextView= listItemView.findViewById(R.id.magnitude);

        String decimalMagnitude = formatMagnitude(currentEarthquake.getMagnitude());
        magnitudeTextView.setText(decimalMagnitude);

        //Define and set the background color of the magnitude
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);


        String currentLocation=currentEarthquake.getLocation();
        String originalLocation;
        String nearToLocation;

        if (currentLocation.contains(LOCATION_SEPARATOR)) {
            String[] parts = currentLocation.split(LOCATION_SEPARATOR);
            nearToLocation = parts[0] + LOCATION_SEPARATOR;
            originalLocation = parts[1];
        } else {
            nearToLocation = getContext().getString(R.string.near_the);
            originalLocation = currentLocation;
        }

        TextView locationNearToTextView= listItemView.findViewById(R.id.locationNearTo);
        locationNearToTextView.setText(nearToLocation);

        TextView locationOriginalTextView= listItemView.findViewById(R.id.locationOriginal);
        locationOriginalTextView.setText(originalLocation);

        Date dateObject = new Date(currentEarthquake.getTimeInMilliSeconds());

        TextView dateTextView= listItemView.findViewById(R.id.date);
        dateTextView.setText(formatDate(dateObject));

        TextView timeTextView=listItemView.findViewById(R.id.time);
        timeTextView.setText(formatTime(dateObject));

        return listItemView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        //switch statement cannot accept a double value,
        // so we should convert our decimal magnitude value into an integer.
        // Also, the precision of the decimal doesn’t matter at this point
        // because we just need to know if the magnitude falls into 1 of 10 possible buckets.
        int magnitudeLevel = (int) Math.floor(magnitude);
        switch (magnitudeLevel) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        //Color resource IDs just point to the resource we defined, but not the value of the color.
        //So we need the actual color value
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }


    private String formatDate(Date date)
    {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }

    private String formatTime(Date date)
    {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    private String formatMagnitude(double magnitude){
        DecimalFormat formatter=new DecimalFormat("0.0");
        return formatter.format(magnitude);
    }

}
