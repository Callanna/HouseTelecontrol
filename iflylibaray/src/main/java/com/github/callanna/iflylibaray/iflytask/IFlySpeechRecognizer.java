package com.github.callanna.iflylibaray.iflytask;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.github.callanna.iflylibaray.data.IFlyJsonResult;
import com.github.callanna.iflylibaray.util.IflyConfig;
import com.github.callanna.iflylibaray.util.FileUtil;
import com.github.callanna.iflylibaray.view.SpeechDialog;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * 命令词识别
 *
 * @author duanyl
 */
public class IFlySpeechRecognizer {
    //TAG
    private static final String TAG = "IFlySpeechRecognizer";
    // 上下文
    private Context mContext;
    // 语音识别对象
    private SpeechRecognizer mAsr;

    //单例实例
    private static IFlySpeechRecognizer instance;

    //识别回调
    private KwqRecognizerCallBack callback;

    //语音控制显示Dialog,关闭Dialog，显示音量大小变化
    private SpeechDialog speechDialog;

    //单列模式
    public static IFlySpeechRecognizer getInstance(Context context) {
        if (instance == null) {
            instance = new IFlySpeechRecognizer(context);
        }
        return instance;
    }

    private IFlySpeechRecognizer(Context context) {
        mContext = context;
        initParams();
    }

    public void initParams() {
        if (mAsr != null) {
            mAsr.destroy();
            mAsr = null;
        }
        speechDialog = new SpeechDialog(mContext.getApplicationContext());
        mAsr = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    Log.d(TAG, "duanyl======>SpeechRecognizer error code = " + code);
                }
            }
        });
        // 设置参数
        setParam();
    }

    /**
     * 参数设置
     */
    private void setParam() {
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置识别引擎 本地引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // // 设置本地识别资源
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, FileUtil.getResourcePath(mContext));
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, IflyConfig.getInstance(mContext).GRAMMAR_PATH);
        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置本地识别使用语法id
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, IflyConfig.getInstance(mContext).getGrammarID());
        // 设置识别的门限值
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        // 设置是否允许VAD，仅在允许VAD时，VAD_BOS, VAD_EOS才会起作用，且各监听 的音量变化回调
        mAsr.setParameter(SpeechConstant.VAD_ENABLE, "1");
        // 设置前端点检测；静音超时时间，即用户多长时间不说话则当做超时处理；
        mAsr.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置后断点检测；后端点静音检测时间，即用户停止说话多长时间内即认为不再输入,自动停止录音；
        mAsr.setParameter(SpeechConstant.VAD_EOS, "1000");
    }

    /**
     * 开始识别
     */
    public void startListening(final KwqRecognizerCallBack callback) {
        this.callback = callback;
        int ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.i(TAG, "duanyl=========>识别失败,错误码: " + ret);
        }
    }

    /**
     * stop recongnizer
     */
    public void stopListening() {
        if (mAsr != null) {
            mAsr.stopListening();
            mAsr.cancel();
        }
    }

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            Log.d(TAG, "duanyl========>RecognizerResult:" + result.toString());
            IFlyJsonResult jocmd = parseGrammarResult(result.getResultString());
            callback.resultData(jocmd);
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "duanyl========>RecognizerResult:end");
            callback.endSpeech();
            if (IflyConfig.getInstance(mContext).getEnabledReconginzer()) {
                speechDialog.hide();
            }
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            if (IflyConfig.getInstance(mContext).getEnabledReconginzer()) {
                speechDialog.volumChanged(i);
            }
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "duanyl========>RecognizerResult:start");
            if (IflyConfig.getInstance(mContext).getEnabledReconginzer()) {
                speechDialog.show();
            }
        }

        @Override
        public void onError(SpeechError error) {
            callback.endSpeech();
            if(IflyConfig.getInstance(mContext).getEnabledReconginzer()) {
                speechDialog.hide();
            }
            Log.i(TAG, "error = " + error.getErrorCode());
            switch (error.getErrorCode()) {
                case 20004://ERROR_INSUFFICIENT_PERMISSIONS		应用程序授权不足
                    callback.resultMsg("应用程序授权不足");
                case 20005://无匹配结果
                case 20010://无匹配的识别结果
                    callback.resultMsg("无匹配结果，请重试！");
                    break;
                case 20006:
                    callback.resultMsg("录音失败,请检查录音设备！");
                    break;
                case 20007:
                    callback.resultMsg("未检测到语音");
                    break;
                case 20008:
                    callback.resultMsg("音频输入超时");
                    break;
                case 20009:
                    callback.resultMsg("麦克初始化错误");
                    break;
                case 20016:
                    callback.resultMsg("存储空间不足");
                    break;
                case 22003:
                    callback.resultMsg("本地引擎内部错误");
                    initParams();
                    break;
                case 21003:
                    callback.resultMsg("初始化失败");
                    initParams();
                    break;
                case 20999:
                    callback.resultMsg("未知错误");
                    break;
                case 20025://ERROR_PERMISSION_DENIED	 引擎授权错误
                case 23001://ERROR_ASR_BUILD_GRAMMER，序列号错误，没有正确授权，
                case 11201://未经授权的语音应用
                case 11207://未经授权的语音服务,装机量限制
                case 11208:
                    callback.resultMsg("禁止使用,语音服务未经授权，请联系厂商");
                    break;
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

    };

    //解析语音识别后的JSON数据
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

    // 回调
    public interface KwqRecognizerCallBack {
        void resultData(IFlyJsonResult jsoncmd);

        void resultMsg(String msg);

        void endSpeech();
    }

}
