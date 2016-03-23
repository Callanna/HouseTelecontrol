package com.github.callanna.iflylibaray.iflytask;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

/**
 * Created by duanyl on 2015/12/7.
 */
public class SemanticParser {
    private static final String TAG = "SemanticParser";
    private static SemanticParser instance;

    // 语义理解对象（语音到语义）。
    private SpeechUnderstander mSpeechUnderstander;

    private Context context;
    private speechUnderStanderCallback mCallback;


    private SemanticParser(final Context context){
        this.context = context;
        // 初始化对象
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(context, new InitListener(){

            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                }
            }
        });
        setParam();
    }

    public static SemanticParser getInstance(Context context){
        if(instance == null){
            instance = new SemanticParser(context);
        }
        return instance;
    }

    public void setParam(){
        // 设置语言
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域为普通话
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT,"mandarin");
        // 设置是否允许VAD，仅在允许VAD时，VAD_BOS, VAD_EOS才会起作用，且各监听 的音量变化回调
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_ENABLE,"1");
        // 设置语音前端点
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000" );
        // 设置语音后端点
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000" );
        // 设置标点符号
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,   "1" );
        // 设置音频保存路径
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/iflytek/wavaudio.pcm");
    }

    public void startListening(speechUnderStanderCallback callback){
        this.mCallback = callback;
        // 开始前检查状态
        if(mSpeechUnderstander.isUnderstanding()){
            mSpeechUnderstander.stopUnderstanding();
        }
         int ret = mSpeechUnderstander.startUnderstanding(mRecognizerListener);
            if(ret != 0){
               Log.d(TAG, "语义理解失败,错误码:" + ret);
            }else {
                Log.d(TAG, "请开始说话");
            }

    }

    public void stopListeneing(){
        if(mSpeechUnderstander != null){
            mSpeechUnderstander.stopUnderstanding();
        }
    }

    public void stopUnderstanding(){
        if(mSpeechUnderstander != null){
            mSpeechUnderstander.cancel();
        }
    }
    private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            //EventBus.getDefault().post(i,"TAG_RADIO_UNDERSTANDING");
        }

        @Override
        public void onBeginOfSpeech() {
            Log.i(TAG, "duanyl=========>开始说话");

        }

        @Override
        public void onEndOfSpeech() {
            Log.i(TAG, "duanyl=========>结束说话");
            //EventBus.getDefault().post("结束说话","TAG_RADIO_END_UNDERSTAND");
        }

        @Override
        public void onResult(UnderstanderResult understanderResult) {
            String text = understanderResult.getResultString();
            Log.d(TAG, "duanyl-========>UnderstanderResult:  " + text);
            mCallback.onResultData(text);
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.i(TAG, "duanyl=========>Error: " + speechError.toString());
            mCallback.onResultMsg(speechError.toString());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

   public interface speechUnderStanderCallback{
        void onResultData(String data);
        void onResultMsg(String msg);
    }
}
