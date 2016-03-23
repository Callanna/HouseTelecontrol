package com.github.callanna.metarialframe;

import com.github.callanna.metarialframe.data.Weather;
import com.github.callanna.metarialframe.util.LogUtil;

/**
 * Created by Callanna on 2015/12/18.
 */
public class Application extends android.app.Application {
    private static  Application instance;
    private Weather weather;

    @Override
    public void onCreate(){
        super.onCreate();
        LogUtil.d("duanyl============>App OnCreate");
        instance = this;
        // crashHandler = CrashHandler.getInstance();
        //crashHandler.init(getApplicationContext());
    }

    public static Application getInstance(){
        return instance;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
