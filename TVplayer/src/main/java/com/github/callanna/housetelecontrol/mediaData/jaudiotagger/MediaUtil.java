package com.github.callanna.housetelecontrol.mediaData.jaudiotagger;

import android.content.Context;

import com.github.callanna.housetelecontrol.mediaData.Music;
import com.github.callanna.metarialframe.util.SdCardUtil;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2016/1/11.
 * 扫描MP3文件
 */
public class MediaUtil {

    private static MediaUtil instance = null;

    private static Context context;
    private MediaUtil(Context context){
        this.context = context;
    }
    public static MediaUtil getInstance(Context context){
        if(instance == null){
            instance = new MediaUtil(context);
        }
        return instance;
    }

    /**
     * 扫描歌曲，从手机文件夹里面进行递归扫描
     */
    public void scannerMusic() {
        List<StorageInfo> list = StorageListUtil
                .listAvaliableStorage(context);
        for (int i = 0; i < list.size(); i++) {
            StorageInfo storageInfo = list.get(i);
            scannerLocalMP3File(storageInfo.path, ".mp3", true);
        }
    }

    public   List<Music> getMp3File(){
        return scannerLocalMP3File(SdCardUtil.getRootPath()+File.separator+"Music",".mp3",true);
    }

    public List<String> getMp3Lrc(){
        return scannerMp3Lrc(SdCardUtil.getRootPath()+File.separator+"Music");
    }





    public static List<String> scannerMp3Lrc(String path){

        List<String> listlrc=  new ArrayList<String>() ;
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isFile()) {
                    if (f.getPath().endsWith(".lrc")) // 判断扩展名
                    {
                        if (!f.exists()) {
                            continue;
                        }
                      listlrc.add(f.getPath());
                    }
                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
                {
                    scannerMp3Lrc(f.getPath());
                }
            }
        }
        return listlrc;
    }
    /**
     *
     * @param Path
     *            搜索目录
     * @param Extension
     *            扩展名
     * @param IsIterative
     *            是否进入子文件夹
     */
    public static List<Music> scannerLocalMP3File(String Path, String Extension,  boolean IsIterative) {

        List<Music> listmusic = new ArrayList<>();

        File[] files = new File(Path).listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isFile()) {
                    if (f.getPath().endsWith(Extension)) // 判断扩展名
                    {
                        if (!f.exists()) {
                            continue;
                        }
                        // 将扫描到的数据保存到播放列表
                        Music songInfo =  getSongInfoByFile(f
                                .getPath());
                         listmusic.add(songInfo);
                    }
                    if (!IsIterative)
                        break;
                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
                {
                    scannerLocalMP3File(f.getPath(), Extension, IsIterative);
                }
            }
        }
        return listmusic;
    }

    /**
     * 通过文件获取mp3的相关数据信息
     *
     * @param filePath
     * @return
     */

    public static Music getSongInfoByFile(String filePath) {
        File sourceFile = new File(filePath);
        if (!sourceFile.exists())
            return null;
        Music songInfo = null;
        try {
            MP3File mp3file = new MP3File(sourceFile);
            MP3AudioHeader header = mp3file.getMP3AudioHeader();
            if (header == null)
                return null;
            songInfo = new Music();
            // 歌曲时长
            String durationStr = header.getTrackLengthAsString();
            long duration = getTrackLength(durationStr);
            // 文件名

            String displayName = sourceFile.getName();
            if (displayName.contains(".mp3")) {
                String[] displayNameArr = displayName.split(".mp3");
                displayName = displayNameArr[0].trim();
            }
            String artist = "张韶涵";
            String title = "";
            if (displayName.contains("-")) {
                String[] titleArr = displayName.split("-");
                artist = titleArr[0].trim();
                title = titleArr[1].trim();
            } else {
                title = displayName;
            }
            songInfo.setName(sourceFile.getName());
            songInfo.setSongid(Long.parseLong(IDGenerate.getId()));
            songInfo.setDisplayName(displayName);
            songInfo.setSinger(artist);
            songInfo.setTitle(title);
            songInfo.setTime(duration);
            songInfo.setSize(sourceFile.length());
            songInfo.setUrl(filePath);
            songInfo.setAlbum("");


            mp3file = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return songInfo;

    }

    /**
     * 获取歌曲长度
     *
     * @param trackLengthAsString
     * @return
     */
    private static long getTrackLength(String trackLengthAsString) {

        if (trackLengthAsString.contains(":")) {
            String temp[] = trackLengthAsString.split(":");
            if (temp.length == 2) {
                int m = Integer.parseInt(temp[0]);// 分
                int s = Integer.parseInt(temp[1]);// 秒
                int currTime = (m * 60 + s) * 1000;
                return currTime;
            }
        }
        return 0;
    }




}
