package com.github.callanna.iflylibaray;

import android.app.Application;

/**
 * Created by Callanna on 2016/1/12.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    public MyDemoService wakeupService;
    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }

    public MyDemoService getWakeupService() {
        return wakeupService;
    }
}
