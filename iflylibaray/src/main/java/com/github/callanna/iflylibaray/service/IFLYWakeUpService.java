package com.github.callanna.iflylibaray.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.callanna.iflylibaray.data.IFlyJsonResult;
import com.github.callanna.iflylibaray.data.ResultCommands;
import com.github.callanna.iflylibaray.iflytask.BuildLocalGrammar;
import com.github.callanna.iflylibaray.iflytask.IFlySpeechRecognizer;
import com.github.callanna.iflylibaray.iflytask.WakeUpReconginzer;
import com.github.callanna.iflylibaray.util.IflyConfig;
import com.github.callanna.iflylibaray.util.FileUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.util.ResourceUtil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author duanyl
 */
public abstract class IFLYWakeUpService extends Service {

    private static final String TAG = "IFLYWakeUpService";


    private StringBuffer IFLY_APPID = new StringBuffer();

    private ExecutorService scheduler;

    protected Context mContext;

    private static IFLYWakeUpService insance;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //如果语法文件已经产生，将其目录删除
        FileUtil.deleteDirectorys(new File(IflyConfig.getInstance(mContext).IFLYGRAMMAR_MSC));
        mContext = this;
        insance = this;
        //设置APPID
        setAppID(IFLY_APPID);

        init();
        // 加载动态链接此法识别引擎库

        initSpeechUtility();
        //初始化唤醒包
        initWakeUp();
        //构造语法
        BuildLocalGrammar.getInstance(mContext).buildLocalGrammar("cmd.bnf");
        //初始化唤醒
        WakeUpReconginzer.getInstance(mContext);
        //初始化识别
        IFlySpeechRecognizer.getInstance(mContext);
        super.onCreate();
    }

    private void initWakeUp() {
        // 加载识唤醒地资源，resPath为本地识别资源路径
        StringBuffer wparam = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + IFLY_APPID + ".jet");
        wparam.append(SpeechConstant.IVW_RES_PATH + "=" + resPath);
        wparam.append("," + ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);
        boolean ret = SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START, wparam.toString());
        if (!ret) {
            Log.d(TAG, "启动本地引擎失败！");
        }
    }

    private void initSpeechUtility() {
        Log.d("WakeService", "duanyl========>onCreate");
        StringBuffer param = new StringBuffer();
        param.append("appid=" + IFLY_APPID);// 必要参数  设置你申请的应用appid
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        // 加载动态链接此法识别引擎库
        SpeechUtility.createUtility(mContext, param.toString());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "duanyl=========>onStartCommand");
        WakeUpReconginzer.getInstance(mContext);
        scheduler = Executors.newSingleThreadExecutor();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开启唤醒识别
     */
    public void startReconginzer() {
        if (scheduler != null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.submit(IflyRecognizerThread);
    }

    /**
     * 停止唤醒识别
     */
    public void stopReconginzer() {
        WakeUpReconginzer.getInstance(mContext).stopWakeUp();
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    /**
     * //唤醒识别线程
      */

    private Runnable IflyRecognizerThread = new Runnable() {
        @Override
        public void run() {
            Log.d("Service", "duanyl=========>now is run wakerecongnizer");
            WakeUpReconginzer.getInstance(mContext).startWakeUp(wakeUpCallback);
        }

    };
    /**
     *  唤醒回调
     */

    private WakeUpReconginzer.WakeUpCallback wakeUpCallback = new WakeUpReconginzer.WakeUpCallback() {
        @Override
        public void resultMsg(String msg) {
            //返回error消息
            showErrorMsg(msg);
        }

        @Override
        public void restartWakeup() {
            //识别后，再启动唤醒。
            scheduler.submit(IflyRecognizerThread);
        }

        @Override
        public void resultData(IFlyJsonResult jsoncmd) {
            IFlyJsonResult result = jsoncmd;
            if (Integer.parseInt(result.getConfidence()) > IflyConfig.CONFIDENCE) {// 置信度大于30的时候，解析词法命令
                ResultCommands cmd = analysisCommand(result);
                String strcmd = cmd.getCommand();
                String val = cmd.getValue();
                Log.d("service", "duanyl========>Service resultData cmd:"
                        + strcmd);
                Log.d("service", "duanyl========>Service resultData val:"
                                + val);
                switch(strcmd){
                    case "unreconginzer":
                        IflyConfig.getInstance(mContext).setEnabledReconginzer(false);
                        break;
                    case "reconginzer":
                        IflyConfig.getInstance(mContext).setEnabledReconginzer(true);
                        break;
                }

                 if(IflyConfig.getInstance(mContext).getEnabledReconginzer()) {
                      parseControlCommand(cmd);
                 }
            }
        }
    };
    @Override
    public void onDestroy() {
        Log.d(TAG,"duanyl============>onDestory  service");
        scheduler.shutdownNow();
        finish();
    }
    public void closeService(){
        Log.d(TAG,"duanyl============>close  service");
        //停止唤醒
        stopReconginzer();
        //停止识别
        IFlySpeechRecognizer.getInstance(this).stopListening();
        finish();
        onDestroy();
    }
    /**
     * 解析语法，对应功能按键开关
     * @param cmd
     * @return
     */
    private ResultCommands analysisCommand(IFlyJsonResult cmd) {
        ResultCommands mResultCommands = new ResultCommands();

        if (cmd.slots.size() == 1) {
            if (cmd.slots.get(0).contains("function")) {
                mResultCommands.setCmdAndValueByFunction(cmd.words.get(0));
                return mResultCommands;
            }
            if (cmd.slots.get(0).contains("action")) {
                mResultCommands.setCmdAndValueByAction(cmd.words.get(0));
                return mResultCommands;
            }
        } else {
            String act = "", briefuc = "";
            for (int i = 0; i < cmd.slots.size(); i++) {

                if (cmd.slots.get(i).contains("action")) {
                    act = cmd.words.get(i);
                }
                if (cmd.slots.get(i).contains("briefunction")) {
                    briefuc = cmd.words.get(i);
                }
            }
            mResultCommands.setCmdAndValueByAction_BerifFuction(act, briefuc);
        }
        return mResultCommands;
    }

    /**
     * ===================================================需要继承的  abstract 方法
     */

    /**
     * 设置自己应用的AppId
     * @param appid
     */

    public abstract void setAppID(StringBuffer appid);

    /**
     * 自己相关的初始化
     */
    public abstract void init();

    /**
     * ErrorMsg处理
     * @param msg
     */
    public abstract void showErrorMsg(String msg);

    /**
     * 将解析的结果作出处理，比如更新UI，发送串口。
     * @param commands
     */
    public abstract void parseControlCommand(ResultCommands commands);
    /**
     * 关闭服务
     */

    public abstract void finish();

}
