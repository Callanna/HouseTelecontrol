/**
 * *******************************************************************
 *
 * @AUTHOR：YOLANDA
 * @DATE：May 11, 201510:23:05 AM
 * @DESCRIPTION：create the File, and add the content.
 * ====================================================================
 * Copyright © 56iq. All Rights Reserved
 * *********************************************************************
 */
package com.github.callanna.metarialframe.jsapi;

import java.io.Serializable;

/**
 * @author YOLANDA
 * @Time May 11, 2015 10:23:05 AM
 */
public class JsObject implements Serializable {

	/**
	 * @author YOLANDA
	 * @Time May 11, 2015 10:25:12 AM
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 执行命令
	 */
	public JsCommand cmd;
	/**
	 * 接受sing
	 */
	public int what;
	/**
	 * 消息
	 */
	public String message;

	/**
	 * js 命令
	 * 
	 * @author YOLANDA
	 * @Time May 11, 2015 10:27:33 AM
	 */
	public enum JsCommand {
		/**
		 * toast
		 */
		MESSAGE,
		/**
		 * 调用掌厨APP
		 */
		START_ZHANGCHU
	}
}
