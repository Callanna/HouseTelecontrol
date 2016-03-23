package com.github.callanna.appcontrol;

import android.app.Application;
import android.content.Intent;

import com.github.callanna.appcontrol.service.TVSpeechService;
import com.github.callanna.metarialframe.util.LogUtil;

/**
 * Created by Callanna on 2016/2/14.
 */
public class TVControlAppliction extends Application {
    private static  TVControlAppliction instance;
    public  TVSpeechService speechService;
    @Override
    public void onCreate(){
        super.onCreate();
        LogUtil.d("duanyl============>App Control OnCreate");
        instance = this;
        startService(new Intent(this, TVSpeechService.class));
    }

    public static TVControlAppliction getInstance(){
        return instance;
    }

}
