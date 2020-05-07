package com.java.qdlljy;

import android.app.Application;

import com.java.qdlljy.NewsUtil.NewsOperator;

public class MyApplication extends Application {
    public static NewsOperator newsOperator;
    @Override
    public void onCreate() {
        super.onCreate();
        newsOperator = new NewsOperator(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
