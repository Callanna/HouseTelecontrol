package com.github.callanna.iflylibaray.util;

import android.content.Context;
import android.os.Environment;

import com.iflytek.cloud.util.ResourceUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Callanna on 2016/1/12.
 */
public class FileUtil {

    /**
     * 得到SD卡根目录
     * @author YOLANDA
     * @return
     */
    public static File getRootPath(){
        File path = null;
        if (FileUtil.sdCardIsAvailable()) {
            path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        } else {
            path = Environment.getDataDirectory();
        }
        return path;
    }

    /**
     * SD卡是否可用
     * @author YOLANDA
     * @return
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            if (sd.canWrite())
                return true;
            else
                return false;
        } else
            return false;
    }

    /** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**  删除方法  删除某个文件夹下的文件，包括文件夹*/
    public static void deleteDirectorys(File file) {
        if (file.exists() ){
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteDirectorys(f);
                }
                file.delete();
            }
        }
    }


    /**
     * 创建一个文件夹
     * @author YOLANDA
     * @param path
     * @return
     */
    public static boolean initDirctory(String path) {
        boolean result = false;
        File file = new File(path);
        if (!file.exists()) {
            result = file.mkdir();
        } else if (!file.isDirectory()) {
            file.delete();
            result = file.mkdir();
        }else if(file.exists()){
            result = true;
        }
        return result;
    }
    /**
     * 读取asset目录下文件内容
     *
     * @return content
     */
    public static String readFile(String file,Context context) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = context.getApplicationContext().getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);
            result = new String(buf, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取识别资源路径
    public static String getResourcePath(Context mContext) {
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext.getApplicationContext(), ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        return tempBuffer.toString();
    }
}
