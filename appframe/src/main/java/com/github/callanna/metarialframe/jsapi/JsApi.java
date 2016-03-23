/**
 * *******************************************************************
 *
 * @AUTHOR：YOLANDA
 * @DATE：May 10, 20158:24:25 PM
 * @DESCRIPTION：create the File, and add the content.
 * ====================================================================
 * Copyright © 56iq. All Rights Reserved
 * *********************************************************************
 */
package com.github.callanna.metarialframe.jsapi;

import android.webkit.JavascriptInterface;

import com.github.callanna.metarialframe.config.AppConfig;

import RxJava.RxBus;

/**
 * @author YOLANDA
 * @Time May 10, 2015 8:24:25 PM
 */
public class JsApi {

	/**
	 * jstag
	 */
	public static final String JS_TAG = "RECIPER_JS_TAG";
	/**
	 * 回调sign
	 */
	private int what;

	/**
	 * @param what
	 *            set your tag
	 * @author YOLANDA
	 */
	public void setWhat(int what) {
		this.what = what;
	}

	/**
	 * 得到终端playerid
	 * 
	 * @author YOLANDA
	 * @return
	 */
	@JavascriptInterface
	public String getPlayerId() {
		return AppConfig.getInstance().getDeviceId();
	}

	/**
	 * web 显示一个toast
	 * 
	 * @param message
	 *            show toast text
	 * @author YOLANDA
	 */
	@JavascriptInterface
	public void message(String message) {
		JsObject object = new JsObject();
		object.cmd = JsObject.JsCommand.MESSAGE;
		object.message = message;
		notifyUI(object);
	}

	/**
	 * 调用掌厨APP
	 * 
	 * @description
	 * @author Joe
	 */
	@JavascriptInterface
	public void startZhangChuApp() {
		JsObject object = new JsObject();
		object.cmd = JsObject.JsCommand.START_ZHANGCHU;
		notifyUI(object);
	}

	/**
	 * 发送一个通知
	 * 
	 * @param object
	 *            data
	 * @author YOLANDA
	 */
	private void notifyUI(JsObject object) {
		object.what = this.what;
		RxBus.get().post( JS_TAG,object);
	}
}
