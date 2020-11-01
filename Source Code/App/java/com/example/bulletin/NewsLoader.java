package com.example.bulletin;

import android.content.Context;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static String url_requested;
    public NewsLoader(@NonNull Context context,String url) {
        super(context);
        this.url_requested = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        if(url_requested == null || url_requested.isEmpty()){
            return null;
        }
        return QueryUtils.fetchDataFromUrl(url_requested);
    }
}
