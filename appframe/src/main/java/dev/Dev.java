package dev;

import java.io.FileDescriptor;

public class Dev {

	public Dev() {
		super();
	}
	
	public static native int openDev(String usbDevicePath);
	
	public static native int writeDev(int fd,byte[] buffer,int len);
	
	public static native byte[] readDev(int fd);
	
	public static native int open_SPDev(String spDevPath,int iSpeed,int iFlags);
	
	public static native void closeFd(int paramFd);
	
	public static native int deviceChomd(String cmd);
	
	public static native String command(String cmd);
	
	public static native String sysCmd(String cd,String path);
	
	public static native String dev_fRead();
	
	public static native int dev_fClose();
	
	public static native String command_fGets(String cmd);
	
    public static native String command_Test(String path,String cmd);
    
    public static native void powerReboot(String reason);
    
    public static native FileDescriptor getFileDescriptor(int paramFd);
    
	static {
		System.loadLibrary("Dev");
	}
}
