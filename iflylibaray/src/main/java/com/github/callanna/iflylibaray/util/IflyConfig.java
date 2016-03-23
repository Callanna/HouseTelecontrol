package com.github.callanna.iflylibaray.util;

import android.content.Context;

import java.io.File;

/**
 * Created by Callanna on 2016/1/12.
 */
public class IflyConfig {
    /**
     * //唤醒识别匹配度
     */

    public static final int SCORE = 0;
    /**
     * //词法识别置信度
     */

    public static final int CONFIDENCE = 30;
    /**
     * ===================常量值
     */

    //=============================================
    /**
     * 公司根目录
     **/
    private String ROOT_PATH;
    /**
     * 科大讯飞相关文件根目录
     **/
    public String IFLY_PATH;
    /**
     * 构建语法目录
     */
    public  String GRAMMAR_PATH;
    /**
     * 初始化语法msc
     */
    public String IFLYGRAMMAR_MSC;
    /**
     * 语法文件格式
     */
    public final String GRAMMAR_TYPE_BNF = "bnf";
    /**
     * 语法ID
     */
    private final String KEY_GRAMMAR_ID = "grammar_id";
    /**
     * 是否开启语音控制
     */
    private  final String ENABLESPEECH = "enableSpeech";
    /**
     * 是否关闭识别
     */
    private final  String  EnabledReconginzer = "isEnabledReconginzer";
    /**
     * =================================================
     */

    private static IflyConfig instance;
    private static Context context;

    public static IflyConfig getInstance(Context ctx) {
        context = ctx;
        if (instance == null) {
            synchronized (IflyConfig.class) {
                if (instance == null) {
                    instance = new IflyConfig();
                }
            }
        }
        return instance;
    }
    private IflyConfig() {
        initDir();
    }
    /**
     * 初始化所有目录
     *
     * @author YOLANDA
     */
    public void initDir() {
        // 初始公司根目录
        ROOT_PATH = FileUtil.getRootPath() + File.separator + "53iq";
        FileUtil.initDirctory(ROOT_PATH);
        // 初始化科大讯飞根目录
        IFLY_PATH = ROOT_PATH + File.separator + "IflySpeech";
        FileUtil.initDirctory(IFLY_PATH);

        IFLYGRAMMAR_MSC = IFLY_PATH+File.separator+"msc";
        FileUtil.initDirctory(IFLYGRAMMAR_MSC);
        GRAMMAR_PATH = IFLYGRAMMAR_MSC+File.separator+ "test";
        FileUtil.initDirctory(GRAMMAR_PATH);
    }

    /**
     * ======================================SharePerference============
     */
    public void put(String key, Object value) {
        SPUtils.put(context, key, value);
    }

    public Object get(String key, Object defaultValue) {
        return SPUtils.get(context, key, defaultValue);
    }


    public void setGrammarID(String grammarID) {
        put(KEY_GRAMMAR_ID, grammarID);
    }

    public String getGrammarID(){
        return (String)get(KEY_GRAMMAR_ID,"cmd");
    }
    public void setEnableSpeech(boolean open){
        put(ENABLESPEECH, open);
    }
    public boolean getEnableSpeech() {
        return (boolean)get(ENABLESPEECH,true);
    }

    public void setEnabledReconginzer(boolean enabledReconginzer) {
        put(EnabledReconginzer,enabledReconginzer);
    }
    public boolean getEnabledReconginzer(){
        return (boolean)get(EnabledReconginzer,true);
    }
}
