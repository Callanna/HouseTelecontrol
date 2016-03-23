package com.github.callanna.housetelecontrol.nanohttpd;

import android.content.Context;

import com.github.callanna.housetelecontrol.TVAppliction;
import com.github.callanna.metarialframe.base.BaseActivity;
import com.github.callanna.metarialframe.util.FileUtil;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.OSUtil;

import RxJava.RxBus;

/**
 * Created by Callanna on 2016/2/12.
 */
public class TvControlServer extends NanoHTTPD {
    private Context mContext;
    public TvControlServer(Context context) {
        super(8885);
        this.mContext = context;
    }
    @Override
    public Response serve( IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        LogUtil.d("duanyl==========>" + method + " '" + uri + "' ");
        switch (uri){
            case "/":
                String tvApi = FileUtil.getFromAssets("tvApi.html",mContext);
                return newFixedLengthResponse(tvApi);
            case "/api/tvserver/findDevice":
                LogUtil.d("duanyl========>findDevice  ");
                RxBus.get().post("FindDevice","OK");
                return newFixedLengthResponse(UrlTVState());
            case "/api/tvserver/switch/on":
                LogUtil.d("duanyl========>switch on ");
                RxBus.get().post("SwitchON","OK");
                return newFixedLengthResponse(UrlTVState());
            case "/api/tvserver/switch/off":
                LogUtil.d("duanyl========>switch off ");
                RxBus.get().post("SwitchOFF", "OK");
                return newFixedLengthResponse(UrlTVState());
            case "/api/tvserver/silence":
                LogUtil.d("duanyl========>silence ");
                OSUtil.silenceMode(mContext);
                BaseActivity.nowTvState = BaseActivity.TV_STATE_ON_SENLICE;
                return newFixedLengthResponse(UrlTVState());
            case "/api/tvserver/ring":
                LogUtil.d("duanyl========>ring ");
                BaseActivity.nowTvState = BaseActivity.TV_STATE_ON;
                OSUtil.RingNormalMode(mContext);
                return newFixedLengthResponse(UrlTVState());
            case "/api/tvserver/sound/add":
                LogUtil.d("duanyl========>sound  add ");
                OSUtil.VolumeUp(mContext);
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/sound/down":
                LogUtil.d("duanyl========>sound   down ");
                OSUtil.VolumeDown(mContext);
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/program/pre":
                LogUtil.d("duanyl========>program  pre ");
                RxBus.get().post("ProgramPre", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/program/next":
                LogUtil.d("duanyl========>program  next ");
                RxBus.get().post("ProgramNext", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/menu":
                LogUtil.d("duanyl========>menu ");
                RxBus.get().post("Menu", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/back":
                LogUtil.d("duanyl========>back ");
                RxBus.get().post("Back", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/OK":
                LogUtil.d("duanyl========>OK ");
                RxBus.get().post("OK", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/top":
                LogUtil.d("duanyl========>top ");
                RxBus.get().post("Top", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/left":
                LogUtil.d("duanyl========>left ");
                RxBus.get().post("Left", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/right":
                LogUtil.d("duanyl========>right ");
                RxBus.get().post("Right", "OK");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/bottom":
                RxBus.get().post("Bottom", "OK");
                LogUtil.d("duanyl========>bottom ");
                return newFixedLengthResponse(UrlOK());
            case "/api/tvserver/getState":
                LogUtil.d("duanyl========>bottom ");
                return newFixedLengthResponse(UrlTVState());
        }

        return newFixedLengthResponse(URL404(uri));
    }

    private String UrlTVState() {
        StringBuilder builder = new StringBuilder();
        //builder.append("<!DOCTYPE html><html><body>");
        builder.append(" {\"result\":\""+BaseActivity.nowTvState+"\",\"deviceid\":\" "+ TVAppliction.DeviceID+"\"}");
        //builder.append("</body></html>\n");
        return builder.toString();
    }

    private String UrlOK() {
        StringBuilder builder = new StringBuilder();
        //builder.append("<!DOCTYPE html><html><body>");
        builder.append(" OK!");
        //builder.append("</body></html>\n");
        return builder.toString();
    }


    public String URL404(String url){
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("404 -- Sorry, Can't Found "+ url + " !");
        builder.append("</body></html>\n");
        return builder.toString();
    }
}
