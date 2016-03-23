package com.github.callanna.iflylibaray.iflytask;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.github.callanna.iflylibaray.data.IFlyJsonResult;
import com.github.callanna.iflylibaray.util.IflyConfig;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Callanna on 2015/12/1.
 */
public class WakeUpReconginzer {
    // Log标签
    private static final String TAG = "WakeUpRecognizer";

    // 上下文
    private Context mContext;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;

    private static WakeUpReconginzer instance;

    private WakeUpCallback wakeUpCallback;

    private WakeUpReconginzer(Context context) {
        // 初始化唤醒对象
        mContext = context;
        mIvw = VoiceWakeuper.createWakeuper(context, null);
        setParam();
    }

    /**
     * 参数设置
     */
    public void setParam() {
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 设置识别引擎
            mIvw.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:-20;1:-20");
            // 设置唤醒+识别模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置返回结果格式
            mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
        }
    }

    public static WakeUpReconginzer getInstance(Context context) {
        if (instance == null) {
            instance = new WakeUpReconginzer(context);
        }
        return instance;
    }

    public void startWakeUp(WakeUpCallback listener) {
        // 非空判断，防止因空指针使程序崩溃
        this.wakeUpCallback = listener;
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.startListening(mWakeuperListener);
        }
    }

    public void stopWakeUp() {
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null && mIvw.isListening()) {
            mIvw.stopListening();
        }
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        @Override
        public void onResult(WakeuperResult result) {
            try {
                JSONObject object = new JSONObject(result.getResultString());
                if (Integer.parseInt(object.getString("score")) > IflyConfig.SCORE) {
                    Log.d(TAG, "duanyl==================>已被唤醒，正在识别。。。");
                    IFlySpeechRecognizer.getInstance(mContext).startListening(new IFlySpeechRecognizer.KwqRecognizerCallBack() {
                        @Override
                        public void resultData(IFlyJsonResult jsoncmd) {
                            wakeUpCallback.resultData(jsoncmd);
                        }

                        @Override
                        public void resultMsg(String msg) {
                            wakeUpCallback.resultMsg(msg);
                        }

                        @Override
                        public void endSpeech() {
                            wakeUpCallback.restartWakeup();
                        }
                    });
                } else {
                    wakeUpCallback.restartWakeup();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError error) {
            wakeUpCallback.resultMsg("" + error);
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "duanyl==================>开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
        }

        @Override
        public void onVolumeChanged(int i) {
        }
    };

    public interface WakeUpCallback {
        void resultData(IFlyJsonResult result);

        void resultMsg(String msg);

        void restartWakeup();
    }
}
