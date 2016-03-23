package com.github.callanna.metarialframe.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;

import com.github.callanna.metarialframe.Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Created by Callanna on 2015/12/17.
 */
public class FileUtil {

    /**
     * 文件或者文件夹是否存在
     *
     * @param filePath
     * @return
     * @author YOLANDA
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 删除文件夹和里边的所有
     *
     * @param folderPath
     * @author YOLANDA
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File f = new File(filePath);
            f.delete(); // 删除空文件夹
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path
     * @return
     * @author YOLANDA
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile() && !temp.toString().contains("appconfig")) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 文件复制
     *
     * @param srcFile  原路径
     * @param destFile 目标路径
     * @return
     * @author YOLANDA
     */
    public static boolean copy(String srcFile, String destFile) {
        try {
            FileInputStream in = new FileInputStream(srcFile);
            FileOutputStream out = new FileOutputStream(destFile);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 复制整个文件夹内
     *
     * @param oldPath String 原文件路径如：c:/fqf
     * @param newPath String 复制后路径如：f:/fqf/ff
     * @return boolean
     * @author YOLANDA
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }
    }

    /**
     * 重命名文件
     *
     * @param resFilePath
     * @param newFilePath
     * @return
     * @author YOLANDA
     */
    public static boolean renameFile(String resFilePath, String newFilePath) {
        File resFile = new File(resFilePath);
        File newFile = new File(newFilePath);
        return resFile.renameTo(newFile);
    }

    /**
     * 获取文件夹大小
     *
     * @param path
     * @return
     * @author YOLANDA
     */
    public static long getFileAllSize(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getFileAllSize(f.getPath());
                return size;
            } else {
                long size = file.length();
                return size;
            }
        } else {
            return 0;
        }
    }

    /**
     * 创建一个文件夹
     *
     * @param path
     * @return
     * @author YOLANDA
     */
    public static boolean initDirctory(String path) {
        boolean result = false;
        File file = new File(path);
        if (!file.exists()) {
            result = file.mkdir();
        } else if (!file.isDirectory()) {
            file.delete();
            result = file.mkdir();
        } else if (file.exists()) {
            result = true;
        }
        return result;
    }

    /**
     * 复制文件
     *
     * @param from
     * @param to
     * @throws IOException
     * @author YOLANDA
     */
    public static void copyFile(File from, File to) throws IOException {
        if (!from.exists()) {
            throw new IOException("The source file not exist: " + from.getAbsolutePath());
        }
        FileInputStream fis = new FileInputStream(from);
        try {
            copyFile(fis, to);
        } finally {
            fis.close();
        }
    }

    /**
     * 复制文件
     *
     * @param from
     * @param to
     * @return
     * @throws IOException
     * @author YOLANDA
     */
    public static long copyFile(InputStream from, File to) throws IOException {
        long totalBytes = 0;
        FileOutputStream fos = new FileOutputStream(to);
        try {
            byte[] data = new byte[1024];
            int len;
            while ((len = from.read(data)) > -1) {
                fos.write(data, 0, len);
                totalBytes += len;
            }
            fos.flush();
        } finally {
            fos.close();
        }
        return totalBytes;
    }

    /**
     * 用UTF8保存一个文件
     *
     * @param path    文件完成路径
     * @param content 要写入的内容
     * @throws Exception
     * @author YOLANDA
     */
    public static void saveFileUTF8(String path, String content, Boolean append) throws Exception {
        FileOutputStream fos = new FileOutputStream(path, append);
        Writer out = new OutputStreamWriter(fos, Constants.CHAR_SET_UTF8);
        out.write(content);
        out.flush();
        out.close();
        fos.flush();
        fos.close();
    }

    /**
     * 用UTF8读取一个文件
     *
     * @param path 文件完整路径
     * @return
     * @author YOLANDA
     */
    public static String getFileUTF8(String path) {
        String result = "";
        InputStream fin = null;
        try {
            fin = new FileInputStream(path);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            fin.close();
            result = new String(buffer, Constants.CHAR_SET_UTF8);
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        return result;
    }

    /**
     * 得到一个文件Intent
     *
     * @param path
     * @param mimetype
     * @return
     * @author YOLANDA
     */
    public static Intent getFileIntent(String path, String mimetype) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), mimetype);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
    /**
     * 将src/mian下的assets文件夹的文件复制到SD上，因为assets下文件有大小限制
     *
     * @param assetsfilename
     * @return
     */
    public static File getAssetFileToSD(String assetsfilename, String path) {
        AssetManager asset = Application.getInstance().getAssets();

        File file = new File(path);
        try {

            InputStream is = asset.open(assetsfilename);
            LogUtil.d("duanyl=====================>+1111" + assetsfilename);
            FileOutputStream fos = new FileOutputStream(file);
            LogUtil.d("duanyl=====================>+1111");
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
            file = new File(path);
            LogUtil.d("duanyl=====================>+1111");
            return file;

        } catch (IOException e) {
            e.printStackTrace();
            return file;
        }

    }
    public static String getFromAssets(String fileName,Context context){
        String Result="";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            while((line = bufReader.readLine()) != null)
                Result += line;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

}
