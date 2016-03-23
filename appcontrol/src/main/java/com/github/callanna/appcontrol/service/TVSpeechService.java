package com.github.callanna.appcontrol.service;

import android.util.Log;
import android.widget.Toast;

import com.github.callanna.appcontrol.TVControlAppliction;
import com.github.callanna.appcontrol.task.ControlTvTask;
import com.github.callanna.iflylibaray.data.ResultCommands;
import com.github.callanna.iflylibaray.service.IFLYWakeUpService;

/**
 * Created by Callanna on 2016/2/14.
 */
public class TVSpeechService extends IFLYWakeUpService {
    @Override
    public void setAppID(StringBuffer appid) {
        appid.append("56c02c23");
    }

    @Override
    public void init() {
        //全局应用持有
        Log.d("MyDemoService", "duanyl====================>init TVservice");
        TVControlAppliction.getInstance().speechService = this;
    }

    @Override
    public void showErrorMsg(String msg) {
        Toast.makeText(mContext, "" + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void parseControlCommand(ResultCommands commands) {
        String command = commands.getCommand();
        String value = commands.getValue();// 约定，所有命令，1为开，0为关
        switch(command){
            case "top":
                ControlTvTask.getInstance(mContext).tvTop();
                break;
            case "bottom":
                ControlTvTask.getInstance(mContext).tvBottom();
                break;
            case "left":
                ControlTvTask.getInstance(mContext).tvLeft();
                break;
            case "right":
                ControlTvTask.getInstance(mContext).tvRight();
                break;
            case "OK":
                ControlTvTask.getInstance(mContext).tvOK();
                break;
            case "silence":
                ControlTvTask.getInstance(mContext).silence();
                break;
            case "ring":
                ControlTvTask.getInstance(mContext).ring();
                break;
            case "soundup":
                ControlTvTask.getInstance(mContext).volumeAdd();
                break;
            case "sounddown":
                ControlTvTask.getInstance(mContext).volumeDown();
                break;
            case "preprogram":
                ControlTvTask.getInstance(mContext).programPre();
                break;
            case "nextprogram":
                ControlTvTask.getInstance(mContext).programNext();
                break;
            case "back":
                ControlTvTask.getInstance(mContext).back();
                break;
            case "power":
                if(value.equals("1")){
                    ControlTvTask.getInstance(mContext).switchOn();
                }
                if(value.equals("0")){
                    ControlTvTask.getInstance(mContext).switchOff();
                }
                break;
        }
    }

    @Override
    public void finish() {
        TVControlAppliction.getInstance().speechService = null;
    }
}
