package com.github.callanna.housetelecontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;

import com.github.callanna.housetelecontrol.mediaData.GetMediaData;
import com.github.callanna.housetelecontrol.mediaData.Music;
import com.github.callanna.metarialframe.util.LogUtil;
import com.treadmill.runne.service.MusicControler;
import com.treadmill.runne.service.SongInfoListener;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by Callanna on 2016/1/10.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener {

    /**
     * 顺序播放
     */
    public static final int MODE_PLAY_ORDER = 0;
    /**
     * 单曲循环
     */
    public static final int MODE_SIGNLE_REPEAT = 1;
    /**
     * 随机播放
     */
    public static final int MODE_PLAY_RANDOM = 2;
    /**
     * 电台播放模式
     */
    public static final int MODE_RADIO = 3;
    /**
     * vitamio  媒体播放器
     */
    private static MediaPlayer mMediaPlayer;
    /**
     * 计时器，不断发送播放进度与状态
     */
    private CusCountdownTimer timer;
    /**
     * 当前在播放列表中的位置,与模式。
     */
    protected int nowPosition, playMode = 0;
    /**
     * 播放列表
     */
    private List<Music> list;
    /**
     * 播放文件路径
     */
    private String url;

    /**
     * 当前播放的音乐路径
     */
    private String nowUrl = "";

    private String radio_url = "http://hls.qd.qingting.fm/live/268.m3u8?bitrate=64";
    //=====================================================================================
    //====================================================================================
    @Override
    public void onDestroy() {
        cancelTimer();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicControlBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取音乐列表
        list = GetMediaData.GetMusicData(this);
        LogUtil.d("duanyl=============>" + list.size());
        // 初始化MediaPlayer
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer(this, true);
                mMediaPlayer.setOnInfoListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mp.reset();
                        return false;
                    }
                });

            }
        } catch (Exception e) {
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (list != null && list.size() > 0) {
            url = list.get(nowPosition).getUrl();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setBufferSize(1024 * 1024 * 3);
        mMediaPlayer.start();
        startTimer();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        url = "";
        playNext();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                // 开始缓存，暂停播放

                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                // 缓存完成，继续播放

                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                // 显示 下载速度
                break;
        }
        return true;
    }

    private void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CusCountdownTimer(mMediaPlayer.getDuration(), 1000);
        sendOnUpdateTime();
        timer.start();
    }

    private void sendOnUpdateTime() {
        try {
            if (mMediaPlayer.isPlaying()) {
                if (mInfoListener != null) {
                    try {
                        mInfoListener.onTimeChange(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新当前时间
     */
    class CusCountdownTimer extends CountDownTimer {

        public CusCountdownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendOnUpdateTime();
        }

        @Override
        public void onFinish() {

        }
    }

    private void playNext() {
        if (list.size() > 0) {
            if (playMode == MODE_PLAY_ORDER) {
                nowPosition++;
                if (nowPosition > list.size() - 1) {
                    nowPosition = 0;
                }
                play(list.get(nowPosition).getUrl().toString());
            }
            if (playMode == MODE_SIGNLE_REPEAT) {
                play(list.get(nowPosition).getUrl().toString());
            }
            if (playMode == MODE_PLAY_RANDOM) {
                Random random = new Random();
                nowPosition = random.nextInt(list.size() - 1);
                play(list.get(nowPosition).getUrl().toString());
            }
            if(playMode == MODE_RADIO){
                play(radio_url);
            }
        }
    }

    private void playPre() {
        if (list.size() > 0) {
            if (playMode == MODE_PLAY_ORDER) {
                nowPosition--;
                if (nowPosition < 0) {
                    nowPosition = list.size() - 1;
                }
                play(list.get(nowPosition).getUrl().toString());
            }
            if (playMode == MODE_SIGNLE_REPEAT) {
                play(list.get(nowPosition).getUrl().toString());
            }
            if (playMode == MODE_PLAY_RANDOM) {
                Random random = new Random();
                nowPosition = random.nextInt(list.size() - 1);
                play(list.get(nowPosition).getUrl().toString());
            }
            if(playMode == MODE_RADIO){
                play(radio_url);
            }
        }
    }

    private void play(String url) {
        nowUrl = url;
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.start();
            mMediaPlayer.prepareAsync();
            playStateChange(true);
            mInfoListener.onNowPositionChange(nowPosition);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        try {
            if (mMediaPlayer.isPlaying()) {
                cancelTimer();
                mMediaPlayer.pause();
                playStateChange(false);
            }
        } catch (Exception e) {
        }
    }

    private void reStart() {
        try {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                startTimer();
            }
        } catch (Exception e) {
        }
    }


    private void onSeekTo(long progress) {
        if (list.size() > 0) {
            url = list.get(nowPosition).getUrl();
            try {
                mMediaPlayer.seekTo(progress);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 播放状态变更通知UI界面
     *
     * @param state
     * @author DevinShine
     */
    private void playStateChange(boolean state) {
        if (mInfoListener != null)
            try {
                mInfoListener.onPlayStateChange(state);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }

    /**
     * 歌曲信息监听
     **/
    private SongInfoListener mInfoListener;

    private MusicControler.Stub musicControlBind = new MusicControler.Stub() {


        @Override
        public void onNext() throws RemoteException {
            playNext();
        }

        @Override
        public void onPreview() throws RemoteException {
            playPre();
        }

        @Override
        public void onPlay() throws RemoteException {
            try {
                if(playMode == MODE_RADIO){
                    play(radio_url);
                }else if (nowUrl.equals("")) {
                    play(list.get(nowPosition).getUrl());
                } else {
                    reStart();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPause() throws RemoteException {
            pause();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            try {
                return mMediaPlayer.isPlaying();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            playMode = mode;
        }

        @Override
        public void onSetSongInfoListener(SongInfoListener songInfoListener) throws RemoteException {
            mInfoListener = songInfoListener;
        }

        @Override
        public void seekTo(long time) throws RemoteException {
            onSeekTo(time);
        }
    };
}