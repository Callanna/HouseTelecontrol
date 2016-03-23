// SongInfoListener.aidl
package com.treadmill.runne.service;

interface SongInfoListener {
  	void onPlayStateChange(boolean state);
  	void onNowPositionChange(int position);
  	void onTimeChange(long currentTime, long totalTime);
}
