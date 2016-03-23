package com.github.callanna.appcontrol.task;

import android.content.Context;

import com.github.callanna.appcontrol.fragment.TabCardFragment;
import com.github.callanna.metarialframe.task.LiteHttpTask;
import com.github.callanna.metarialframe.util.Constants;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.NetUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.response.Response;

import org.json.JSONException;
import org.json.JSONObject;

import RxJava.RxBus;

/**
 * Created by Callanna on 2016/2/13.
 */
public class ControlTvTask {
    private static ControlTvTask instance;
    private Context mContext;
    private static String base_IP = "192.168.1.110";
    private static String base_url;
    private ControlTvTask(Context context){
        this.mContext = context;
        base_IP = NetUtil.getIpV4Address(context);

        base_url = String.format(Constants.URL_TVSERVER, base_IP);
    }

    public static ControlTvTask getInstance(Context context){
     if(instance == null){
         instance = new ControlTvTask(context);
     }
        LogUtil.d("duanyl===========>" + base_url);
        return instance;
    }

    public void findDevice(String ip){
        base_IP = ip;
        base_url = String.format(Constants.URL_TVSERVER, base_IP);
        LiteHttpTask.getInstance(mContext).getDataStringAsync(String.format(Constants.URL_TVSERVER, ip) + Constants.URL_TVSERVER_FIND_DEVICE, httpListener);
   }


    public void switchOn(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_SWITCH_ON,httpListener);
    }
    public void switchOff(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_SWITCH_OFF,httpListener);
    }
    public void silence(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_SILENCE,httpListener);
    }
    public void ring(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_RING,httpListener);
    }

    public void volumeAdd(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_SOUND_UP,httpListener);
    }

    public void volumeDown(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_SOUND_DOWN,httpListener);
    }


    public void programPre(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_PROGRAM_PRE,httpListener);
    }

    public void programNext(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_PROGRAM_NEXT,httpListener);
    }

    public void tvmenu(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_MENU,httpListener);
    }
    public void back(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_BACK,httpListener);
    }
    public void tvTop(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_TOP,httpListener);
    }
    public void tvBottom(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_BOTTOM,httpListener);
    }
    public void tvLeft(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_LEFT,httpListener);
    }
    public void tvRight(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_RIGHT,httpListener);
    }

    public void tvOK(){
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url+Constants.URL_TVSERVER_OK,httpListener);
    }


    public void getState() {
        LiteHttpTask.getInstance(mContext).getDataStringAsync(base_url + Constants.URL_TVSERVER_STATE,  httpListener);
    }

    HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSuccess(String s, Response<String> response) {
            super.onSuccess(s, response);
            LogUtil.d("duanyl=========>" + response.getResult());
            if (response.getResult().contains("result")) {

                try {
                    String si = new JSONObject(response.getResult().toString()).getString("result");
                    String id = new JSONObject(response.getResult().toString()).getString("deviceid");
                    LogUtil.d("duanylan==============>si "+si);
                    LogUtil.d("duanylan==============>id "+id);
                    TabCardFragment.nowDeviceId = id;
                    switch(si){
                        case "1":
                            TabCardFragment.nowstate = TabCardFragment.TV_STATE_ON;
                            break;
                        case "2":
                            TabCardFragment.nowstate = TabCardFragment.TV_STATE_OFF;
                            break;
                        case "3":
                            TabCardFragment.nowstate = TabCardFragment.TV_STATE_ON_SENLICE;
                            break;
                        default:
                            TabCardFragment.nowstate = TabCardFragment.TV_STATE_Offline;
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RxBus.get().post("GetState","oK");
            }

        }

        @Override
        public void onFailure(HttpException e, Response<String> response) {
            super.onFailure(e, response);
        }
    };

}
