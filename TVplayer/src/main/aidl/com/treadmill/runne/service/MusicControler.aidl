// MusicControler.aidl
package com.treadmill.runne.service;

import  com.treadmill.runne.service.SongInfoListener;

interface MusicControler {
        void onNext();
        void onPreview();
    	void onPlay();
    	void onPause();
    	boolean isPlaying();
    	void setPlayMode(int mode);
    	void onSetSongInfoListener(SongInfoListener songInfoListener);
    	void seekTo(long time);
}
