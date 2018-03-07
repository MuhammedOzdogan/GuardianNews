package com.example.muhammed.guardiannews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Muhammed on 2/20/2018.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    //View lookup cache
    private static class ViewHolder {
        TextView section;
        TextView date;
        TextView title;
    }

    public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item from this position.
        final News news = getItem(position);
        //Check if an existing view is being reused, otherwise inflate the view.
        ViewHolder viewHolder;//view lookup cache stored in tag.
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, parent, false);
            viewHolder.section = (TextView)
                    convertView.findViewById(R.id.section_text_view);
            viewHolder.date = (TextView)
                    convertView.findViewById(R.id.web_publication_date);
            viewHolder.title = (TextView)
                    convertView.findViewById(R.id.web_title);
            //Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

      //Populate the data from the data object via the viewHolder object
        //into the template view.


        Date date = new Date(news.getWebPublicationDateMilliSecond());

        viewHolder.section.setText(news.getSectionName());
        viewHolder.date.setText(formatDate(date));
        viewHolder.title.setText(news.getWebTitle());

        convertView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri page = Uri.parse(news.getWebUrl());

                Intent webPage = new Intent(Intent.ACTION_VIEW, page);

                if(webPage.resolveActivity(getContext().getPackageManager()) != null) {
                    //Send the intent to launch a new activity.
                    getContext().startActivity(webPage);
                }
            }
        });
        //Return the completed view to render on screen.
        return convertView;

    }

    private String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return simpleDateFormat.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(date);
    }
}
