package com.github.callanna.housetelecontrol.mediaData.jaudiotagger;

/**
 * Created by Callanna on 2016/1/11.
 */
public class StorageInfo {
    public String path;
    public String state;
    public boolean isRemoveable;

    public StorageInfo(String path) {
        this.path = path;
    }

    public boolean isMounted() {
        return "mounted".equals(state);
    }
}
