package com.github.callanna.iflylibaray.iflytask;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.callanna.iflylibaray.util.IflyConfig;
import com.github.callanna.iflylibaray.util.FileUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * 构建离线命令词语法
 *
 * @author duanyl
 */
public class BuildLocalGrammar {
    // TAG
    protected static final String TAG = "InitLocalGrammar";
    // 上下文
    private Context mContext;
    // 语音识别对象
    private SpeechRecognizer mAsr;
    //实例
    private static BuildLocalGrammar instance;

    /**
     * 单例模式
     * @param context
     * @return
     */

    public static BuildLocalGrammar getInstance(Context context) {
        if (instance == null) {
            instance = new BuildLocalGrammar(context);
        }
        return instance;
    }

    private BuildLocalGrammar(Context context) {
        mContext = context;
        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Log.d(TAG, "duanyl=========>error code:" + code);
                }
            }
        });
    }

    /**
     * 构建语法
     *
     * @return
     */
    public void buildLocalGrammar(String filename) {
        Log.d(TAG,"duanyl=======localGrsmmar" + IflyConfig.getInstance(mContext).GRAMMAR_PATH);
        try {
            String mLocalGrammar = FileUtil.readFile(filename, mContext);//读词法文件
            String txt_command = new String(mLocalGrammar);
            Log.d(TAG, "duanyl========>txt_command:  " + txt_command);
            mAsr.setParameter(SpeechConstant.PARAMS, null);
            // 设置文本编码格式
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            // 设置引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置语法构建路径
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, IflyConfig.getInstance(mContext).GRAMMAR_PATH);
            // 设置资源路径
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, FileUtil.getResourcePath(mContext));
            // 构建语法
            int ret = mAsr.buildGrammar(IflyConfig.getInstance(mContext).GRAMMAR_TYPE_BNF, txt_command, new GrammarListener() {

                @Override
                public void onBuildFinish(String grammarId, SpeechError error) {
                    Log.d(TAG, "duanyl========>grammarId:" + grammarId);

                    if (error == null) {
                        Log.d(TAG, "duanyl=======>语法构建成功");
                        if (!TextUtils.isEmpty(grammarId)) {
                            IflyConfig.getInstance(mContext).setGrammarID(grammarId);//记录GarmmarID
                        }
                    } else {
                        Log.d(TAG, "duanyl=======>语法构建失败,错误码:" + error.getErrorCode());
                    }
                }
            });
            if (ret != ErrorCode.SUCCESS) {
                Log.d(TAG, "duanyl=======>语法构建失败,错误码:" + ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
