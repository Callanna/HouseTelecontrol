/**
 * *******************************************************************
 *
 * @AUTHOR：YOLANDA
 * @DATE：Apr 27, 201511:28:24 AM
 * @DESCRIPTION：create the File, and add the content.
 * ====================================================================
 * Copyright © 56iq. All Rights Reserved
 * *********************************************************************
 */
package com.github.callanna.metarialframe.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.github.callanna.metarialframe.Application;
import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.util.Constants;
import com.github.callanna.metarialframe.util.FileUtil;
import com.github.callanna.metarialframe.util.SdCardUtil;

import java.io.File;

/**
 * @author YOLANDA
 * @Time Apr 27, 2015 11:28:24 AM
 */
public class AppConfig {


    /**
     * instance
     */
    private static AppConfig _AppConfig;
    /**
     * local save
     */
    private SharedPreferences sharedPrefs;

    /**
     * 公司根目录
     **/
    private String BASEDIR;
    /**
     * 本应用目录
     **/
    public String MYAPPPATH;
    /**
     * 下载文件目录
     **/
    public String DOWNLOAD;
    /**
     * crash dir
     */
    public String CRASH;

    public String CACHE_DIR;
    /**
     * WEB缓存
     */
    public String CACHE_WEB_DIR;
    /**
     * web database
     */
    public String CACHE_WEB_DATABASE;
    /**
     * web定位
     */
    public String CACHE_WEB_LOCAL_DIR;
    /**
     * get instance
     *
     * @return
     * @author YOLANDA
     */
    public static AppConfig getInstance() {
        if (_AppConfig == null) {
            _AppConfig = new AppConfig();
        }
        return _AppConfig;
    }

    /**
     * 获取设备ID
     *
     * @return
     */
    public String getDeviceId() {
        return sharedPrefs.getString(Constants.Device_ID, "");
    }

    public void setDeviceId(String value) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.Device_ID, value);
        editor.commit();
    }

    public void setToken(String value) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.ACCESS_TOKEN, value);
        editor.commit();
    }

    public String getToken() {
        return sharedPrefs.getString(Constants.ACCESS_TOKEN, "");
    }


    private AppConfig() {
        sharedPrefs = Application.getInstance().getSharedPreferences("smart_freezer", Context.MODE_PRIVATE);
        // company dir
        BASEDIR = SdCardUtil.getRootPath() + File.separator + R.string.app_name;
        FileUtil.initDirctory(BASEDIR);
        // app dir
        MYAPPPATH = BASEDIR + File.separator + "SmartFreezer";
        FileUtil.initDirctory(MYAPPPATH);
        // download dir
        DOWNLOAD = MYAPPPATH + File.separator + "DownLoad";
        FileUtil.initDirctory(DOWNLOAD);
        // crash log dir
        CRASH = MYAPPPATH + File.separator + "Crash";
        FileUtil.initDirctory(CRASH);
        // 缓存
        CACHE_DIR = BASEDIR + File.separator + "Cache";
        FileUtil.initDirctory(CACHE_DIR);
        // web缓存
        CACHE_WEB_DIR = CACHE_DIR + File.separator + "WebCache";
        FileUtil.initDirctory(CACHE_DIR);
        // web local
        CACHE_WEB_LOCAL_DIR = CACHE_DIR + File.separator + "WebLocal";
        FileUtil.initDirctory(CACHE_WEB_LOCAL_DIR);
        // database
        CACHE_WEB_DATABASE = CACHE_DIR + File.separator + "WebDatabase";
        FileUtil.initDirctory(CACHE_WEB_DATABASE);
    }



    /**
     * 保存一个boolean
     *
     * @param key
     * @param value
     * @author YOLANDA
     */
    public void setBoolean(String key, boolean value) {
        Editor editor = sharedPrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPrefs.getBoolean(key, defaultValue);
    }

    /**
     * 保存mac地址
     *
     * @param macAddress
     */
    public void setMacAdderss(String macAddress) {
        // 获取mac 地址
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.MAC_ADDRESS, macAddress);
        editor.commit();
    }

    public String getMacAddress() {
        return sharedPrefs.getString(Constants.MAC_ADDRESS, "");
    }

    /**
     * 还原设置
     *
     * @author YOLANDA
     */
    public void clear() {
        Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
    }


    /**
     * 获取topic
     *
     * @return
     */
    public String getTopic() {
        return sharedPrefs.getString(Constants.TOPIC, "");
    }

    public void setTopic(String value) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.TOPIC, value);
        editor.commit();
    }
    /**
     * mqtt clientId
     *
     * @return
     */
    public String getClientId() {

        return sharedPrefs.getString(Constants.CLIENT_ID, "");
    }

    public void setClientId(String clientId) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.CLIENT_ID, clientId);
        editor.commit();
    }

    /**
     * 获取hostName
     *
     * @return
     */
    public String getHostName() {
        return sharedPrefs.getString(Constants.HOSTNAME, "tcp://61.129.70.112");
    }

    public void setHostName(String hostName) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.HOSTNAME, "tcp://" + hostName);
        editor.commit();
    }

    /**
     * 获取端口号
     *
     * @return
     */
    public String getHostPort() {
        return sharedPrefs.getString(Constants.HOSTPORT, "1884");
    }

    public void setHostPort(String hostPort) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.HOSTPORT, hostPort);
        editor.commit();
    }

    /**
     * 获取mqtt用户名
     *
     * @return
     */
    public String getUserName() {
        return sharedPrefs.getString(Constants.USER_NAME, "eb_sub_xingxing");
    }

    public void setUserName(String userName) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.USER_NAME, userName);
        editor.commit();
    }

    /**
     * 获取mqtt密码
     *
     * @return
     */
    public String getPasswd() {
        return sharedPrefs.getString(Constants.PASSWD, "53iq.com");
    }

    public void setPasswd(String passwd) {
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.PASSWD, passwd);
        editor.commit();
    }

}
