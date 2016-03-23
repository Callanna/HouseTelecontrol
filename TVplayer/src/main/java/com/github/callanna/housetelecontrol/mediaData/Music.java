package com.github.callanna.housetelecontrol.mediaData;

import android.graphics.Bitmap;

/**
 * Created by Callanna on 2016/1/10.
 */
public class Music {
    private long songid;
    //名称
    private String title;
    //歌手名
    private String singer;
    //专辑名
    private String album;

    private long albumid;

    private String url;

    private long size;

    private long time;

    private String name;

    Bitmap albumBitmap;

    private String suffix;
    private String displayName;

    public long getAlbumid() {
        return albumid;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setAlbumid(long albumid) {
        this.albumid = albumid;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return the singer
     */
    public String getSinger() {
        return singer;
    }
    /**
     * @param singer the singer to set
     */
    public void setSinger(String singer) {
        this.singer = singer;
    }
    /**
     * @return the album
     */
    public String getAlbum() {
        return album;
    }
    /**
     * @param album the album to set
     */
    public void setAlbum(String album) {
        this.album = album;
    }
    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }
    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }
    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the songid
     */
    public long getSongid() {
        return songid;
    }
    /**
     * @param songid the songid to set
     */
    public void setSongid(long songid) {
        this.songid = songid;
    }
    /**
     * @return the albumBitmap
     */
    public Bitmap getAlbumBitmap() {
        return albumBitmap;
    }
    /**
     * @param albumBitmap the albumBitmap to set
     */
    public void setAlbumBitmap(Bitmap albumBitmap) {
        this.albumBitmap = albumBitmap;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return this.displayName;
    }
}
