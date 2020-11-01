package com.example.bulletin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewsAdapter extends ArrayAdapter<News> {
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();
    public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if(listView == null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list,parent,false);
        }

        News news = getItem(position);

        TextView newsType = listView.findViewById(R.id.news_type);
        newsType.setText(news.getNewsType());

        TextView newsTitle = listView.findViewById(R.id.news_title);
        newsTitle.setText(news.getNewsTitle());

        TextView newsDate = listView.findViewById(R.id.news_date);
        TextView newsTime = listView.findViewById(R.id.news_time);
        /**
         * we get the date and time of the news like 2020-01-20T04:02:26Z
         * and our app would display Jan 01, 2020  4:02 am
         * so we convert the date and time String
         * */
        try {
            // we convert the Date and then set it with the reference view of Date.
            String date = seprateDate(news.getNewsDateTime());
            newsDate.setText(date);
            // we convert the Time and then set it with the reference view of Time.
            String time = seperateTime(news.getNewsDateTime());
            newsTime.setText(time);
        } catch (ParseException e) {
            Log.i(LOG_TAG,"MESG+ "+e);
            e.printStackTrace();
        }


        return listView;
    }
    /**
     * @PARAM String having Date and time.
     * we First convert the String into a Date Class
     * and then Convert the Date class to a String, How'd we like i.e Jan 01, 2020
     * */
    private String seprateDate(String dateTime) throws ParseException {
        String date_time[] = dateTime.split("T");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy",Locale.ENGLISH);
        // convert to Date Object
        Date date = format1.parse(date_time[0]);
        // convert To String Again
        String finalDate = format2.format(date);
        return finalDate;
    }
    /**
     * @PARAM String having Date and time.
     * we First convert the String into a Date Class
     * and then Convert the Date class to a String, How'd we like i.e 4:01 am
     * */
    private  String seperateTime(String dateTime) throws ParseException {
        String date_time[] = dateTime.split("T");
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss",Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("K:mm aa",Locale.ENGLISH);
        // convert to Date Object
        Date date = format1.parse(date_time[1]);
        // convert to String again
        String finalTime = format2.format(date);
        Log.i(LOG_TAG,"TIME: "+finalTime+"Acutal time: "+date_time[1]);
        return finalTime;
    }
}
