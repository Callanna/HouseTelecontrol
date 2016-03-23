package com.github.callanna.iflylibaray.iflytask;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.github.callanna.iflylibaray.data.IFlyJsonResult;
import com.github.callanna.iflylibaray.util.IflyConfig;
import com.github.callanna.iflylibaray.util.FileUtil;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class WakeUpSpeechReconginzer {
    // Log标签
    private static final String TAG = "WakeUpCommandRecognizer";
    // 上下文
    private Context mContext;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    private WakeUpRecognizerCallBack wakeupListener;
    private static WakeUpSpeechReconginzer instance;

    private WakeUpSpeechReconginzer(Context context) {
        // 初始化唤醒对象
        mContext = context;
        mIvw = VoiceWakeuper.createWakeuper(context, null);
        setParam();
    }

    public static WakeUpSpeechReconginzer getInstance(Context context) {
        if (instance == null) {
            synchronized (WakeUpSpeechReconginzer.class) {
                instance = new WakeUpSpeechReconginzer(context);
            }
        }
        return instance;
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
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "oneshot");
            // 设置返回结果格式
            mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
            // 设置本地识别资源
            mIvw.setParameter(ResourceUtil.ASR_RES_PATH, FileUtil.getResourcePath(mContext));
            // 设置语法构建路径
            mIvw.setParameter(ResourceUtil.GRM_BUILD_PATH, IflyConfig.getInstance(mContext).GRAMMAR_PATH);
            // 设置本地识别使用语法id
            mIvw.setParameter(SpeechConstant.LOCAL_GRAMMAR, IflyConfig.getInstance(mContext).getGrammarID());

        }


    }

    public void startWakeUpListening(WakeUpRecognizerCallBack callback) {
        this.wakeupListener = callback;
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.startListening(mWakeuperListener);
        }
    }

    public void stopWakeUp() {
        if (mIvw != null) {
            Log.d(TAG, "duanyl============>stopWakeUp");
            mIvw.stopListening();
        }
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {
                JSONObject object = new JSONObject(result.getResultString());
                if (Integer.parseInt(object.getString("score")) > 10) {
                    Log.d(TAG, "duanyl==================>已被唤醒，正在识别。。。");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError error) {
            wakeupListener.resultMsg("" + error);
            //发生错误后，重新唤醒
            wakeupListener.restartWake();
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "duanyl==================>开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            Log.d(TAG, "eventType:" + eventType + "arg1:" + isLast + "arg2:" + arg2);
            // 识别结果
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                RecognizerResult result = ((RecognizerResult) obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                Log.d(TAG, "duanyl==================>" + result.getResultString());
                IFlyJsonResult jocmd = parseGrammarResult(result.getResultString());
                wakeupListener.resultData(jocmd);
                wakeupListener.restartWake();
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }

    };

    public IFlyJsonResult parseGrammarResult(String json) {
        IFlyJsonResult mIFlyJsonResult = new IFlyJsonResult();
        try {
            Log.d(TAG, "duanyl==========>get  data " + json);

            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            Log.d(TAG, "duanyl==============sc==>" + joResult.getString("sc"));
            mIFlyJsonResult.setConfidence(joResult.getString("sc"));
            Log.d(TAG,
                    "duanyl===============confidence=>"
                            + mIFlyJsonResult.getConfidence());
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                mIFlyJsonResult.addSlots(words.getJSONObject(i).getString(
                        "slot"));
                JSONObject obj = items.getJSONObject(0);//曲该词槽的第一项
                mIFlyJsonResult.addWords(obj.getString("w"));
            }
            for (int h = 0; h < mIFlyJsonResult.slots.size(); h++) {
                Log.d(TAG,
                        "duanyl=========>slot " + mIFlyJsonResult.slots.get(h)
                                + "cword " + mIFlyJsonResult.words.get(h));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mIFlyJsonResult.setConfidence("0");
        }
        return mIFlyJsonResult;
    }

    public interface WakeUpRecognizerCallBack {
        void resultData(IFlyJsonResult jsoncmd);

        void resultMsg(String msg);

        void restartWake();
    }
}
