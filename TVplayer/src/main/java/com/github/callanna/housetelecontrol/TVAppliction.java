package com.github.callanna.housetelecontrol;

import android.app.Application;

import com.github.callanna.metarialframe.util.LogUtil;

/**
 * Created by Callanna on 2016/2/13.
 */
public class TVAppliction extends Application {

    private static  Application instance;
    public static String nowPage;
    public static String DeviceID;
    @Override
    public void onCreate(){
        super.onCreate();
        LogUtil.d("duanyl============>App OnCreate");
        instance = this;
    }

    public static Application getInstance(){
        return instance;
    }

}
