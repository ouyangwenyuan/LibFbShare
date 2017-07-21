package com.example.administrator.widget;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by liuyanfei on 2017/7/18.
 */


public class WidgetApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取Context
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(WidgetApp.getContext());
        AppEventsLogger.activateApp(this);
    }

    //返回
    public static Context getContext() {
        return context;
    }
}


