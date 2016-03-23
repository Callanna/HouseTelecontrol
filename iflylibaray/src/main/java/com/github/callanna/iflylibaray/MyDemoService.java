package com.github.callanna.iflylibaray;

import android.util.Log;
import android.widget.Toast;

import com.github.callanna.iflylibaray.data.ResultCommands;
import com.github.callanna.iflylibaray.service.IFLYWakeUpService;

/**
 * Created by Callanna on 2016/1/12.
 */
public class MyDemoService extends IFLYWakeUpService {

    //必须设置AppID
    @Override
    public void setAppID(StringBuffer appid) {
        appid.append("564541e2");
    }

    @Override
    public void init() {
        //全局应用持有
        Log.d("MyDemoService","duanyl====================>init service");
       MyApplication.getInstance().wakeupService = this;
    }

    /**
     *   //跟据你的情况是否显示Toast
     * @param msg
     */
    @Override
    public void showErrorMsg(String msg) {

        Toast.makeText(mContext,""+msg,Toast.LENGTH_LONG).show();
    }

    /**
     * * 将解析的结果作出处理，比如更新UI，发送串口。
     * @param commands
     */
    @Override
    public void parseControlCommand(ResultCommands commands){
        String command = commands.getCommand();
        String value = commands.getValue();// 约定，所有命令，1为开，0为关
        switch(command){
            case "power":
                if(value.equals("1")){
                    Toast.makeText(MyDemoService.this, "开机", Toast.LENGTH_SHORT).show();
                    //control.setPower("1");
                }
                if(value.equals("0")){
                    Toast.makeText(MyDemoService.this, "关机", Toast.LENGTH_SHORT).show();
                    //control.setPower("0");
                }
                break;
            case "lighting":
                if(value.equals("1")){
                    Toast.makeText(MyDemoService.this, "开灯", Toast.LENGTH_SHORT).show();
                    //control.setLight("1");
                }else{
                    Toast.makeText(MyDemoService.this, "关灯", Toast.LENGTH_SHORT).show();
                    //control.setLight("0");
                }
                break;
            case "speed":
                if (value.equals("0")) { // 无风
                    Toast.makeText(MyDemoService.this, "无风", Toast.LENGTH_SHORT).show();
                    //control.setWind("0");
                } else if (value.equals("1")) { // 大风
                    Toast.makeText(MyDemoService.this, "大风", Toast.LENGTH_SHORT).show();
                    //control.setWind("1");
                } else if (value.equals("2")) { // 小风
                    Toast.makeText(MyDemoService.this, "小风", Toast.LENGTH_SHORT).show();
                    //control.setWind("3");
                } else if (value.equals("3")) { // 中风
                    Toast.makeText(MyDemoService.this, "中风", Toast.LENGTH_SHORT).show();
                    //control.setWind("2");
                }
                break;
            case "disinfect":
                if(value.equals("0")){
                    //control.setDisInfect("0");
                }else {
                    //control.setDisInfect("3");
                }
                break;

            case "stoving":
                if(value.equals("0")){
                    ///control.setDrying("0");
                }else {
                    //control.setDrying("3");
                }
                break;

        }
    }

    @Override
    public void finish() {
        MyApplication.getInstance().wakeupService= null;
    }
}
