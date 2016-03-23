/**********************************************************************
 * FILE：ScreenPower.java
 * PACKAGE：com.ebanswers.netkitchen.task
 * AUTHOR：YOLANDA
 * DATE：2015年1月29日下午7:47:47
 * Copyright © 56iq. All Rights Reserved
 *======================================================================
 * EDIT HISTORY
 *----------------------------------------------------------------------
 * |  DATE      | NAME       | REASON       | CHANGE REQ.
 *----------------------------------------------------------------------
 * | 2015年1月29日    | YOLANDA    | Created      |
 *
 * DESCRIPTION：create the File, and add the content.
 *
 ***********************************************************************/
package com.github.callanna.metarialframe.util;

import com.github.callanna.metarialframe.task.LiteAsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Project NetKitchen
 * @Class ScreenPower.java
 * @author YOLANDA
 * @Time 2015年1月29日 下午7:47:47
 */
public class ScreenPower {

	private static ScreenPower _ScreenPower;
	private ScreenPower(){}
	/**
	 * 实例
	 * @author YOLANDA
	 * @return
	 */
	public static synchronized ScreenPower getInstance(){
		if(_ScreenPower == null){
			_ScreenPower = new ScreenPower();
		}
		return _ScreenPower;
	}
	
	/**开关屏幕的命令**/
	private String cmd;
	
	/**
	 * 开屏
	 * @author YOLANDA
	 */
	public void on(){
		execute("echo 1 >/sys/class/iocontrol/iocontrol_point/attr/iobacklight");
	}
	
	/**
	 * 关屏
	 * @author YOLANDA
	 */
	public void off(){
		LogUtil.d("duanyl=======>:screenOnOff  ");
		execute("echo 0 >/sys/class/iocontrol/iocontrol_point/attr/iobacklight");
	}
	
	/**
	 * 执行
	 * @author YOLANDA
	 */
	private void execute(String cmd){
		this.cmd = cmd;
		LiteAsyncTask.executeAsync(screenOnOff);
	}
	
	/**
	 * 开关屏幕的线程
	 */
	private Runnable screenOnOff = new Runnable() {
		@Override
		public void run() {
			try {
				chmod();
			} catch (IOException e) {
//				e.printStackTrace();
			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
			//Dev.command_fGets(cmd);
		}
	};
	
	/**
	 * 提升权限
	 * @author YOLANDA
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void chmod() throws IOException, InterruptedException {
		Process shell = Runtime.getRuntime().exec("su", null, new File("/system/bin/"));
		OutputStream os = shell.getOutputStream();
		os.write(("chmod 777 /sys/class/iocontrol/iocontrol_point/attr/iobacklight").getBytes("ASCII"));
		os.flush();
		os.close();
		shell.waitFor();
	}

}
