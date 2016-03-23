package com.github.callanna.housetelecontrol.activity;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.callanna.housetelecontrol.Constants;
import com.github.callanna.housetelecontrol.R;
import com.github.callanna.housetelecontrol.TVAppliction;
import com.github.callanna.housetelecontrol.data.MediaItem;
import com.github.callanna.metarialframe.base.BaseActivity;
import com.github.callanna.metarialframe.util.Check;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.NetUtil;
import com.github.callanna.metarialframe.util.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import RxJava.RxBus;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.provider.MediaStore;
import io.vov.vitamio.widget.VideoView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Callanna on 2015/12/27.
 */
public class VideoPlayerAtviity extends BaseActivity{

    private final static String TAG = "VideoPlayer";

    private int position;

    private String radia = null;

    public static final String netACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private int mCurrentPosition = 0;

    private int fristBufferOk = -1;

    private VideoView mVideoView = null;

    private SeekBar mPlayerSeekBar = null;

    private TextView mEndTime = null;
    private TextView mCurrentTime = null;

    private TextView mLoadingRxBytesText = null;

    private TextView mLoadingVideoName = null;

    private TextView mLoadingBufferingText = null;

    private TextView mVideoNameText = null;

    private String mVideoName= null;

    private ImageView mBatteryState = null;

    private TextView mLastModify = null;
    private int currentVolume = 0;
    private Button mDiaplayMode = null;
    private Button mPrevButton = null;
    private Button mPlayOrPause = null;
    private Button mNextButton = null;
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private final static int TIME = 6868;
    private boolean isControllerShow = true;
    private boolean isPaused = false;
    private boolean isFullScreen = false;
    private boolean isSilent = false;

    private boolean isOnCompletion = false;

    private final static int SCREEN_FULL = 0;
    private final static int SCREEN_DEFAULT = 1;

    private final static int HIDE_CONTROLER = 1;

    private final static int PAUSE = 3;

    private final static int EXIT_TEXT = 5;
    private final static int PROGRESS_CHANGED = 0;

    private final static int BUFFER = 6;

    private final static int BUFFERING_TAG = 7;

    private final static int EXIT = 8;

    private final static int SET_PAUSE_BUTTON = 9;

    private final static int IS_PAUSE_BUTTON = 10;

    private final static int SEEK_BACKWARD = 11;

    private final static int SEEK_FORWARD = 12;

    private final static int REPLAY = 13;

    private final static int CHANGED_RXBYTES = 15;

    private Intent mIntent;

    private Uri uri;

    private Button mPlayerButtonBack = null;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private LinearLayout frame = null;
    private RelativeLayout mFrameLayout = null;

    private LinearLayout mPlayerLoading;

    private LinearLayout mVideoBuffer;

    private TextView mVideoBufferText = null;

    private boolean isLocal = false;

    private boolean isLoading = true;

    private int level = 0;

    private boolean mIsLive = false;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    private GestureDetector mGestureDetector;
    /** 最大声音 */
    private int mMaxVolume;
    /** 当前声音 */
    private int mVolume = -1;
    /** 当前亮度 */
    private float mBrightness = -1f;

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra("level", 0);

        }
    };

    private void setBattery(int level) {
        if (level <= 0) {
            mBatteryState.setBackgroundResource(R.drawable.ic_battery_0);
        } else if (0 < level && level <= 10) {
            mBatteryState.setBackgroundResource(R.drawable.ic_battery_10);
        } else if (10 < level && level <= 20) {
            mBatteryState.setBackgroundResource(R.drawable.ic_battery_20);
        } else if (20 < level && level <= 40) {
            mBatteryState.setBackgroundResource(R.drawable.ic_battery_40);
        } else if (40 < level && level <= 60) {
            mBatteryState.setBackgroundResource(R.drawable.ic_battery_60);
        } else if (60 < level && level <= 80) {
            mBatteryState.setBackgroundResource(R.drawable.ic_battery_80);
        } else if (80 < level && level <= 100) {
            mBatteryState.setBackgroundResource(R.drawable.ic_battery_100);
        }

    }

    private SharedPreferences preference = null;
    private int histroyPosition = 0;
    private String histroyUri = null;
    private String[] netUris = null;
    private String[] loacaUris = null;
    private boolean isTrue = false;
    private boolean isAutoNext = false;
    private ArrayList<MediaItem> mCurrentPlayList;
    private MediaItem mMediaItem = null;
    private MediaStore.Audio.Media mMedia = null;

    private boolean checkVitamioLibs = false;

    private NetCheckReceiver mCheckReceiver;
    private boolean isNetAvailable = true;

    @Override
    protected boolean setToolbarAsActionbar() {
        return false;
    }

    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {

        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;
        checkVitamioLibs = true;
        LogUtil.d(TAG, " ---onCreate()--");
        registerRxbus();
        initWindow();

        initData();

        initView();

        mIntent = getIntent();

        getPlayData();
        setMediaName();
        getScreenSize();
        initVideoView();
        initVolumeandBright();
        startPlay();
    }

    private void initVolumeandBright() {
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        // ~~~ 绑定数据
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
    }
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /** 双击 */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        /** 滑动 */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth / 5.0)// 左边滑动
                onBrightnessSlide((mOldY - y) / windowHeight);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }
    /** 手势结束 */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }
    /** 定时隐藏 */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
        }
    };
    private void playNextReset(){
        fristBufferOk = -1;
        isOnCompletion = false;
        isBuffering = true;
        isBack = false;
        isError = false;
        isLoading = true;
        mCurrentPosition = 0;
        mPlayerSeekBar.setProgress(mCurrentPosition);
    }
    private void playNextNetVideo(){
        if(mMedia != null){
            playNextReset();
            hideController();
            if (mVideoBuffer != null)
                mVideoBuffer.setVisibility(View.GONE);
            if (mPlayerLoading != null) {
                mPlayerLoading.setVisibility(View.VISIBLE);
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessage(PAUSE);
            }
            setMediaName();
            getNextUri();
            setMediaName();
            setButtonState();
        }
    }

    private void playNextVideo(){

        playNextReset();
        hideController();
        if (mVideoBuffer != null)
            mVideoBuffer.setVisibility(View.GONE);
        if (mPlayerLoading != null) {
            mPlayerLoading.setVisibility(View.VISIBLE);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(PAUSE);
        }

        LogUtil.d(TAG, " ---getPlayData()--");

        getNextVideoUri();

        setButtonState();
        setMediaName();
        startPlay();

    }


    /**
     * 注册检查网络变化*
     */
    private void regListenerNet() {
        mCheckReceiver = new NetCheckReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(netACTION);
        this.registerReceiver(mCheckReceiver, intentfilter);
    }

    /**
     * 取消注册检查网络变化*
     */
    private void unregisterListenerNet() {
        if (mCheckReceiver != null) {
            unregisterReceiver(mCheckReceiver);

        }

    }

    private void checkNetworkInfo() {
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();

        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
           ToastUtil.show(VideoPlayerAtviity.this, getString(R.string.net_3g));
            isNetAvailable = true;
            return;
        }

        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            isNetAvailable = true;
            return;
        }
        isNetAvailable = false;
        // 添加对本地文件的判断，播放本地文件时不应提示网络中断
        if (uri != null && isHttp && fristBufferOk == 0) {
            ToastUtil.show(VideoPlayerAtviity.this, getString(R.string.net_outage_tip));
        }

    }

    class NetCheckReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(netACTION)) {
                LogUtil.e(TAG, "ACTION:" + intent.getAction());
                checkNetworkInfo();
            }

        }
    }

    private void initVideoView() {

        mVideoView.setOnSeekCompleteListener(new io.vov.vitamio.MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer arg0) {
                // TODO Auto-generated method stub
                if (mVideoBuffer != null) {
                    LogUtil.d("duanyl=========>onSeekComplete");
                    mVideoBuffer.setVisibility(View.GONE);
                }
            }
        });

        LogUtil.e(TAG, " ---initVideoView()--");
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {

                LogUtil.e(TAG, " ---出错了Error: " + what + "," + extra);
                isError = true;

                if (isReplay) {
                    return true;
                }
                if (fristBufferOk == 0 && mCurrentPosition > 1000 && !isReplay) {
                    isReplay = true;
                    retryDialog();
                    return true;
                }

                return true;

            }

        });


        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                switch (what) {

                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (mVideoBuffer != null) {
                            mVideoBuffer.setVisibility(View.VISIBLE);
                        }

                        break;

                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        if (mVideoBuffer != null) {
                            mVideoBuffer.setVisibility(View.GONE);
                        }

                        break;

                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:


                        break;

                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        break;

                    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        break;

                    case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        LogUtil.e(TAG,
                                "--MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK--");
                        break;

                }

                return true;
            }
        });

        mPlayerSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekbar,
                                                  int progress, boolean fromUser) {
                        if (fromUser) {
                            mVideoView.seekTo(progress);
                            cancelDelayHide();
                        }
                    }

                    public void onStartTrackingTouch(SeekBar arg0) {
                        cancelDelayHide();
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (uri == null && !isPaused) {
                            isBuffering = false;
                            mHandler.sendEmptyMessageDelayed(BUFFERING_TAG,
                                    1000);
                        }
                        mHandler.sendEmptyMessage(SET_PAUSE_BUTTON);
                        hideControllerDelay();
                    }
                });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer arg0) {

                mPlayerLoading.setVisibility(View.VISIBLE);

                isControllerShow = false;

                isBuffering = true;

                setVideoScale(SCREEN_DEFAULT);

                if (!isLoading) {
                    hideController();
                }

                int i = (int) mVideoView.getDuration();
                mPlayerSeekBar.setMax(i);
                mEndTime.setText(stringForTime(i));

                if(fristBufferOk == 0&&mCurrentPosition>1000){
                    mVideoView.seekTo(mCurrentPosition);
                }
                mVideoView.start();
                mPlayerLoading.setVisibility(View.GONE);
                mVideoBuffer.setVisibility(View.GONE);
                fristBufferOk = 0;
                replayNum = 0;
                LogUtil.e(TAG, " ---播放成功了: -----");
                isLoading = false;

                isOnCompletion = false;
                isError = false;
                mHandler.sendEmptyMessage(SET_PAUSE_BUTTON);
                if(isCheckUriBym3u8){
                    showController() ;
                }
                cancelDelayHide();
                hideControllerDelay();
                mHandler.removeMessages(PROGRESS_CHANGED);
                mHandler.sendEmptyMessage(PROGRESS_CHANGED);
                mHandler.removeMessages(BUFFER);
                mHandler.sendEmptyMessage(BUFFER);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer arg0) {
                LogUtil.i(TAG, "onCompletion()================="
                        + isOnCompletion);
                if (!isOnCompletion) {
                    isOnCompletion = true;
                    LogUtil.i(TAG, "onCompletion()========Completion");
                    isBuffering = false;

                            if (isCheckUriBym3u8&&mIsLive) {

                                if (replayNum <= 3 && fristBufferOk == 0) {
                                    if (mHandler != null) {
                                        mHandler.removeMessages(REPLAY);
                                        mHandler.sendEmptyMessage(REPLAY);
                                    } else {
                                        replay();
                                    }
                                    replayNum++;
                                    return;
                                }
                                if (isReplay) {
                                    return;
                                }
                                // 在出错之前从新更新一遍用户存储播放历史的方法
                                // updateByHashid();
                                if (fristBufferOk == 0 && !isReplay) {
                                    isReplay = true;
                                    retryDialog();
                                    return;
                                }

                            } else {
                                if (!isAutoNext) {
                                    if (mCurrentPlayList != null
                                            && mCurrentPlayList.size() > 1) {

                                        int n = mCurrentPlayList.size();
                                        if (++position < n) {

                                            if(mMedia != null){
                                                playNextNetVideo();
                                            }else{
                                                playNextVideo();
                                            }
                                        } else {
                                            --position;
                                            if(mMedia != null){
                                                playNextNetVideo();
                                            }else{
                                                playNextVideo();
                                            }

                                        }

                                    } else {
                                        if (isHttp) {
                                            if (mVideoView != null) {
                                                mExitHandler
                                                        .sendEmptyMessage(EXIT);
                                            }

                                        } else {
                                            if (mVideoView != null) {
                                                mExitHandler
                                                        .sendEmptyMessage(EXIT);
                                            }

                                        }

                                    }
                                } else {
                                    mExitHandler.sendEmptyMessage(EXIT);
                                }
                            }

                        }
            }
        });
    }

    private void getPlayData() {
        LogUtil.d(TAG, " ---getPlayData()--");
        if (mIntent != null) {
            mCurrentPlayList = (ArrayList<MediaItem>) mIntent
                    .getSerializableExtra("playlist");
            position = mIntent.getIntExtra("position", 0);
            LogUtil.d("duanyl============> position"+position);
            getUri();
        }
        setButtonState();

    }



    private void getUri() {
        if(mIntent != null){
            String strLocaluri = null;

            if (strLocaluri == null && mCurrentPlayList != null) {
                mMediaItem = mCurrentPlayList.get(position);
                strLocaluri = mMediaItem.getUrl();
                mIsLive = mMediaItem.isLive();
                if(Check.isEmpty(strLocaluri)){
                    strLocaluri = mMediaItem.getSourceUrl();
                }
            }
            if (strLocaluri != null) {
                uri = Uri.parse(strLocaluri);
                if (mCurrentPlayList != null
                        && mCurrentPlayList.size() > 1) {
                    mVideoName = mMediaItem.getTitle();
                } else {
                    if (mMediaItem != null) {
                        mVideoName = mMediaItem.getTitle();
                    }
                }
            }
            LogUtil.d("duanyl============>uri "+uri);
        }

    }

    private void getNextVideoUri() {
        if(mIntent != null){
            String strLocaluri = null;
            if (strLocaluri == null && mCurrentPlayList != null) {
                mMediaItem = mCurrentPlayList.get(position);
                strLocaluri = mMediaItem.getUrl();
                mIsLive = mMediaItem.isLive();
                if(Check.isEmpty(strLocaluri)){
                    strLocaluri = mMediaItem.getSourceUrl();
                }
            }

            if (strLocaluri != null) {
                uri = Uri.parse(strLocaluri);
                if (mCurrentPlayList != null
                        && mCurrentPlayList.size() > 1) {
                    mVideoName = mMediaItem.getTitle();
                } else {
                    if (mMediaItem != null) {
                        mVideoName = mMediaItem.getTitle();
                    }
                }
            }

            if (uri != null) {
                String content = uri.toString().replace("?", "yangguangfu");
                if (content != null)
                    netUris = content.split("yangguangfu");
            }
        }

    }

    private void setMediaName(){

        mLoadingVideoName.setText(mVideoName);
        mVideoNameText.setText(mVideoName);
        boolean isHistory  = false;

        if(mCurrentPosition>0&&isHistory){
            mLoadingBufferingText.setText("跳转至上次观看位置...");
        }else{
            mLoadingBufferingText.setText("加载视频中，请稍候...");
        }
    }
    private void getNextUri() {
        String strLocaluri = null;
        if (strLocaluri == null && mCurrentPlayList != null) {
            mMediaItem = mCurrentPlayList.get(position);
            strLocaluri = mMediaItem.getSourceUrl();
            mIsLive = mMediaItem.isLive();
            if(Check.isEmpty(strLocaluri)){
                strLocaluri = mMediaItem.getUrl();
            }
        }

    }


    private void setButtonState() {
        if (mCurrentPlayList != null && mCurrentPlayList.size() == 1) {
            setNextEnabled(false);
            setPrevEnabled(false);

        } else if (mCurrentPlayList != null && mCurrentPlayList.size() > 1) {
            if (position == 0) {
                setPrevEnabled(false);
                setNextEnabled(true);
            } else if (position == (mCurrentPlayList.size() - 1)) {
                setPrevEnabled(true);
                setNextEnabled(false);
            } else {
                setPrevEnabled(true);
                setNextEnabled(true);
            }

        } else {
            setPlaySeekBarEnabled(true);
            setNextEnabled(true);
            setPrevEnabled(true);

        }
    }



    private void initData() {
        LogUtil.e(TAG, " ---initData()--");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        currentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        preference = PreferenceManager.getDefaultSharedPreferences(this);

        LogUtil.v(TAG, getIntent().toString());

        registerReceiver(batteryReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        regListenerNet();
        isOnCompletion = false;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    private void initWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_videoplayer);
    }

    private Builder dialogBuilder;
    private boolean isReplay = false;

    private void retryDialog() {
        dialogBuilder = getBuilderInstance();
        if (dialogBuilder != null) {
            dialogBuilder.setTitle(R.string.tips);
            if (!isNetAvailable) {
                dialogBuilder.setMessage(R.string.playretry_neterror);
            } else {
                dialogBuilder.setMessage(R.string.playretry);
            }
            dialogBuilder.setPositiveButton(R.string.retry,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mHandler != null) {
                                mHandler.removeMessages(REPLAY);
                                mHandler.sendEmptyMessage(REPLAY);
                            } else {
                                replay();
                            }
                        }
                    });
            dialogBuilder.setNegativeButton(R.string.player_exit,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!isBack) {
                                isBack = true;
                                mExitHandler.removeMessages(EXIT_TEXT);
                                mExitHandler.sendEmptyMessage(EXIT_TEXT);
                            }
                            if (dialog != null) {
                                dialog.dismiss();
                                dialog = null;
                            }
                        }
                    });
            dialogBuilder.create().show();
        }
    }

    public Builder getBuilderInstance() {
        if (dialogBuilder == null) {
            dialogBuilder = new Builder(VideoPlayerAtviity.this);
        }
        return dialogBuilder;
    }

    private int errorType = 0;

    public void setPauseButtonImage() {
        if (mVideoView != null) {
            LogUtil.i(TAG, "setPauseButtonImage()=============");
            try {
                if (mVideoView.isPlaying()) {
                    mPlayOrPause.setBackgroundResource(R.drawable.btn_pause);
                } else {
                    mPlayOrPause.setBackgroundResource(R.drawable.btn_play);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private RelativeLayout mPlayerLayout = null;

    private void initView() {
        LogUtil.e(TAG, " ---initView()--");
        mPlayerLayout = (RelativeLayout) findViewById(R.id.playe_layout);

        frame = (LinearLayout) findViewById(R.id.frame);

        mFrameLayout = (RelativeLayout) findViewById(R.id.mFrameLayout);

        mPlayerLoading = (LinearLayout) findViewById(R.id.player_loading);

        mVideoBuffer = (LinearLayout) findViewById(R.id.video_buffer);

        mVideoBufferText = (TextView) findViewById(R.id.mediacontrolle_buffer_info_text);

        mVideoView = (VideoView) findViewById(R.id.video_view);

        mLoadingRxBytesText = (TextView) findViewById(R.id.loading_rxBytes_text);

        mLoadingVideoName = (TextView) findViewById(R.id.loading_video_name);

        mLoadingBufferingText = (TextView) findViewById(R.id.loading_text);

        mVideoNameText = (TextView) findViewById(R.id.video_name);

        mBatteryState = (ImageView) findViewById(R.id.battery_state);

        mLastModify = (TextView) findViewById(R.id.last_modify);

        mPlayerButtonBack = (Button) findViewById(R.id.btn_exit);

        mPlayerSeekBar = (SeekBar) findViewById(R.id.PlaybackProgressBar);

        mCurrentTime = (TextView) findViewById(R.id.current_time);

        mEndTime = (TextView) findViewById(R.id.total_time);

        mDiaplayMode = (Button) findViewById(R.id.diaplay_mode);

        mPrevButton = (Button) findViewById(R.id.btn_back);

        mPlayOrPause = (Button) findViewById(R.id.btn_play_pause);

        mNextButton = (Button) findViewById(R.id.btn_forward);

        mPlayerSeekBar.setThumbOffset(13);
        mPlayerSeekBar.setMax(100);
        mPlayerSeekBar.setSecondaryProgress(0);
        mPlayerButtonBack.setOnClickListener(mListener);

        mPlayOrPause.setOnClickListener(mListener);

        mPrevButton.setOnClickListener(mListener);
        mNextButton.setOnClickListener(mListener);
        mDiaplayMode.setOnClickListener(mListener);
        hideController();
        mHandler.sendEmptyMessage(CHANGED_RXBYTES);

    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_exit:
                    isBuffering = false;
                    if (!isBack) {
                        isBack = true;
                        mExitHandler.sendEmptyMessage(EXIT_TEXT);
                    }
                    break;
                case R.id.btn_back:
                    if (mCurrentPlayList != null && mCurrentPlayList.size() > 1) {
                        isBack = false;
                        int n = mCurrentPlayList.size();
                        if (--position >= 0 && position < n) {

                            if (mVideoView != null) {
                                hideController();
                            }
                            if(mMedia != null){
                                playNextNetVideo();
                            }else{
                                playNextVideo();
                            }

                        } else {
                            position = 0;
                            if (position >= 0 && position < n) {
                                if (mVideoView != null) {
                                    hideController();
                                }
                                if(mMedia != null){
                                    playNextNetVideo();
                                }else{
                                    playNextVideo();
                                }
                            }

                        }

                    } else {
                        mHandler.sendEmptyMessage(SEEK_BACKWARD);
                    }

                    break;

                case R.id.btn_play_pause:
                    mHandler.sendEmptyMessage(IS_PAUSE_BUTTON);
                    break;
                case R.id.btn_forward:
                    if (mCurrentPlayList != null && mCurrentPlayList.size() > 1) {
                        isBack = false;

                        int n = mCurrentPlayList.size();
                        if (++position < n && position >= 0) {
                            if (mVideoView != null) {
                                hideController();
                            }
                            if(mMedia != null){
                                playNextNetVideo();
                            }else{
                                playNextVideo();
//							Utils.startSystemPlayer(VideoPlayer.this,
//									mCurrentPlayList, position);
//							mExitHandler.sendEmptyMessage(EXIT);
                            }
                        } else {
                            if (position > 0) {
                                --position;
                            }
                            if (position >= 0 && position < n) {
                                if (mVideoView != null) {
                                    hideController();
                                }
                                if(mMedia != null){
                                    playNextNetVideo();
                                }else{
                                    playNextVideo();
//								Utils.startSystemPlayer(VideoPlayer.this,
//										mCurrentPlayList, position);
//								mExitHandler.sendEmptyMessage(EXIT);
                                }

                            }

                        }

                    } else {
                        mHandler.sendEmptyMessage(SEEK_FORWARD);
                    }
                    break;
                case R.id.diaplay_mode:
                    if (isFullScreen) {
                        setVideoScale(SCREEN_DEFAULT);
                    } else {
                        setVideoScale(SCREEN_FULL);
                    }

                    break;

            }

        }
    };



    private boolean isCick;
    private Dialog dialog;
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    private void startPlay() {
        if (uri != null && mVideoView != null) {
            if (mVideoBuffer != null) {
                mVideoBuffer.setVisibility(View.GONE);
            }
            LogUtil.e(TAG, "playUri ===111--" + String.valueOf(uri));
            mVideoView.setVideoURI(uri);
            mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);

        } else {
            isBack = true;
            mExitHandler.sendEmptyMessage(EXIT);
        }
    }

    public void setPlaySeekBarEnabled(boolean enabled) {
        if (mPlayerSeekBar != null) {
            mPlayerSeekBar.setEnabled(enabled && mPlayerSeekBar != null);

        }
    }

    public void setNextEnabled(boolean enabled) {
        if (mNextButton != null) {
            mNextButton.setEnabled(enabled && mListener != null);
            if (enabled) {

                if (mCurrentPlayList != null && mCurrentPlayList.size() > 0) {

                    mNextButton.setBackgroundDrawable(VideoPlayerAtviity.this
                            .getResources().getDrawable(
                                    R.drawable.btn_forward));

                } else {
                    mNextButton.setBackgroundDrawable(VideoPlayerAtviity.this
                            .getResources().getDrawable(
                                    R.drawable.btn_forward_one));
                }

                // mNextButton.setBackgroundDrawable(VideoPlayer.this.getResources().getDrawable(R.drawable.videonextbtn_bg));
            } else {
                if (mCurrentPlayList != null && mCurrentPlayList.size() > 0) {
                    if (position == (mCurrentPlayList.size() - 1)) {
                        mNextButton.setBackgroundDrawable(VideoPlayerAtviity.this
                                .getResources().getDrawable(
                                        R.drawable.video_next_btn_bg));
                    }
                } else {
                    mNextButton.setBackgroundDrawable(VideoPlayerAtviity.this
                            .getResources().getDrawable(
                                    R.drawable.btn_forward_one_huise));
                }

            }
        }
    }

    public void setPrevEnabled(boolean enabled) {

        if (mPrevButton != null) {
            mPrevButton.setEnabled(enabled);
            if (enabled) {

                if (mCurrentPlayList != null && mCurrentPlayList.size() > 0) {

                    mPrevButton.setBackgroundDrawable(VideoPlayerAtviity.this
                            .getResources()
                            .getDrawable(R.drawable.btn_back));

                } else {
                    mPrevButton.setBackgroundDrawable(VideoPlayerAtviity.this
                            .getResources()
                            .getDrawable(R.drawable.btn_back_one));
                }

            } else {
                if (mCurrentPlayList != null && mCurrentPlayList.size() > 0) {
                    if (position == 0) {
                        mPrevButton.setBackgroundDrawable(VideoPlayerAtviity.this
                                .getResources().getDrawable(
                                        R.drawable.video_pre_gray));
                    }
                } else {
                    mPrevButton.setBackgroundDrawable(VideoPlayerAtviity.this
                            .getResources().getDrawable(
                                    R.drawable.btn_back_one_huise));
                }

            }
        }

    }

    public void setPlayOrPauseEnabled(boolean enabled) {
        if (mPlayOrPause != null) {
            mPlayOrPause.setEnabled(enabled && mPlayOrPause != null);

            if (enabled) {
                // mPlayOrPause.setBackgroundDrawable(VideoPlayer.this.getResources().getDrawable(R.drawable.videonextbtn_bg));
            } else {
                if (!isPaused) {
                    mPlayOrPause
                            .setBackgroundResource(R.drawable.video_puase_gray);

                } else {
                    mPlayOrPause.setBackgroundResource(R.drawable.btn_play);
                }

            }
        }
    }

    private void setVideoScale(int flag) {
        switch (flag) {
            case SCREEN_FULL:
                mDiaplayMode.setBackgroundResource(R.drawable.btn_original_size);
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ZOOM, 0);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                isFullScreen = true;
                break;

            case SCREEN_DEFAULT:

                mDiaplayMode.setBackgroundResource(R.drawable.btn_full_screen);

                int videoWidth = mVideoView.getVideoWidth();
                int videoHeight = mVideoView.getVideoHeight();
                int mWidth = screenWidth;
                int mHeight = screenHeight - 25;

                if (videoWidth > 0 && videoHeight > 0) {
                    if (videoWidth * mHeight > mWidth * videoHeight) {

                        mHeight = mWidth * videoHeight / videoWidth;
                    } else if (videoWidth * mHeight < mWidth * videoHeight) {

                        mWidth = mHeight * videoWidth / videoHeight;
                    } else {

                    }
                }
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                isFullScreen = false;
                break;
        }
    }

    private void hideControllerDelay() {
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
    }


    private void hideController() {
        LogUtil.e(TAG, " ---hideController()--");

        if (isLoading && isBuffering) {
            frame.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.GONE);
        } else if (!isLoading && isBuffering) {
            frame.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.GONE);
        }

        isControllerShow = false;
    }

    private void cancelDelayHide() {
        mHandler.removeMessages(HIDE_CONTROLER);
    }

    private void showController() {
        LogUtil.e(TAG, " ---showController()--");
        if (!isLoading && isBuffering) {
            frame.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.VISIBLE);
        }

        isControllerShow = true;

    }

    private boolean isBuffering = false;
    private boolean isHttp = false;
    private boolean isCheckUriBym3u8 = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case PROGRESS_CHANGED:

                    if (mVideoView == null||isBack) {
                        return;
                    }

                    int i = (int) mVideoView.getCurrentPosition();

                    if (isPaused || !isHttp) {
                        if (mVideoBuffer != null) {
                            mVideoBuffer.setVisibility(View.GONE);

                        }
                    }

                    if (i > 1000)
                        mCurrentPosition = i;
                    Calendar calendar = Calendar.getInstance();
                    String hourStr = null;
                    String minuteStr = null;
                    String timeStr = null;
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    if (hour == 0) {
                        hourStr = "00";
                    } else if (0 < hour && hour < 10) {
                        hourStr = "0" + hour;
                    } else {
                        hourStr = String.valueOf(hour);
                    }

                    if (minute == 0) {
                        minuteStr = "00";
                    } else if (0 < minute && minute < 10) {
                        minuteStr = "0" + minute;
                    } else {
                        minuteStr = String.valueOf(minute);
                    }

                    if (second == 0) {
                        timeStr = "00";
                    } else if (0 < second && second < 10) {
                        timeStr = "0" + second;
                    } else {
                        timeStr = String.valueOf(second);
                    }
                    String time = hourStr + ":" + minuteStr + ":" + timeStr;
                    mLastModify.setText(time);

                    mPlayerSeekBar.setProgress(mCurrentPosition);
                    setBattery(level);
                    mCurrentTime.setText(stringForTime(i));
                    if (!isOnCompletion && !isLoading) {
                        SharedPreferences.Editor editor = preference.edit();
                        if (editor != null) {

                            if (mCurrentPosition > 0 && uri != null) {
                                editor.putInt("CurrentPosition", mCurrentPosition);
                                if (uri != null) {
                                    editor.putString("histroyUri", uri.toString());
                                }

                            }
                            editor.commit();
                        }

                    }
                    if (!isBack && !isError) {
                        mHandler.removeMessages(PROGRESS_CHANGED);
                        mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
                    }

                    break;

                case HIDE_CONTROLER:
                    hideController();
                    break;
                case BUFFERING_TAG:
                    isBuffering = true;
                    break;
                case PAUSE:
                    if (mVideoView != null) {
                        mVideoView.pause();
                    }
                    break;

                case SET_PAUSE_BUTTON:
                    setPauseButtonImage();
                    break;

                case IS_PAUSE_BUTTON:
                    if (isPaused) {
                        mVideoView.start();
                        mPlayOrPause.setBackgroundResource(R.drawable.btn_pause);
                        isBuffering = true;
                        cancelDelayHide();
                        hideControllerDelay();
                    } else {
                        mVideoView.pause();
                        mPlayOrPause.setBackgroundResource(R.drawable.btn_play);
                        cancelDelayHide();
                        showController();
                        isBuffering = false;

                    }

                    isPaused = !isPaused;
                    break;

                case SEEK_BACKWARD:
                    if (mVideoView != null) {
                        int pos = (int) mVideoView.getCurrentPosition();
                        Integer times = 10;
                        String key_2 = "10";
                        if (preference != null) {
                            if (key_2 != null) {
                                times = Integer.valueOf(key_2);
                            }

                        }
                        pos -= (times * 1000);
                        mVideoView.seekTo(pos);
                    }
                    cancelDelayHide();
                    hideControllerDelay();
                    break;

                case SEEK_FORWARD:
                    if (mVideoView != null) {
                        int pos = (int) mVideoView.getCurrentPosition();
                        Integer times = 10;
                        String key_2 = "10";
                        if (preference != null) {
                            if (key_2 != null) {
                                times = Integer.valueOf(key_2);
                            }

                        }

                        pos += (times * 1000);
                        mVideoView.seekTo(pos);
                    }
                    cancelDelayHide();
                    hideControllerDelay();
                    break;
                case REPLAY:
                    replay();
                    break;
                case CHANGED_RXBYTES:
                    String countCurRate = NetUtil.countCurRate();
                    if (mLoadingRxBytesText != null && isHttp) {
                        mLoadingRxBytesText.setText(countCurRate);
                    } else {
                        mLoadingRxBytesText.setVisibility(View.GONE);
                    }

                    if (mVideoBufferText != null && isHttp) {

                        mVideoBufferText.setText(countCurRate);
                    } else {
                        mVideoBufferText.setVisibility(View.GONE);
                    }

                    if (!isBack ) {
                        mHandler.removeMessages(CHANGED_RXBYTES);
                        mHandler.sendEmptyMessageDelayed(CHANGED_RXBYTES, 1000);
                    }
                    break;

            }

            super.handleMessage(msg);
        }
    };

    Handler mExitHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EXIT_TEXT:
                    if (isBack) {
                        mLoadingBufferingText.setText(VideoPlayerAtviity.this
                                .getBaseContext().getResources()
                                .getString(R.string.exit_player_text));
                        if (mVideoBuffer != null) {
                            mVideoBuffer.setVisibility(View.GONE);
                        }
                    }
                    mExitHandler.sendEmptyMessage(EXIT);
                    break;

                case EXIT:
                    exit();
                    break;

            }
        }
    };

    private int replayNum = 0;

    private void replay() {

        LogUtil.e(TAG, "replay()--------------");

        isBack = false;
        isError = false;
        isReplay = false;
        isOnCompletion = false;
        isBuffering = true;
        isLoading = true;
        hideController();
        if (mVideoBuffer != null)
            mVideoBuffer.setVisibility(View.GONE);
        if (mPlayerLoading != null) {
            mPlayerLoading.setVisibility(View.VISIBLE);
        }
        startPlay();

    }

    private int mAudioDisplayRange;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mAudioDisplayRange == 0)
            mAudioDisplayRange = Math.min(getWindowManager()
                    .getDefaultDisplay().getWidth(), getWindowManager()
                    .getDefaultDisplay().getHeight());

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                endGesture();

                    if (!isControllerShow) {
                        isControllerShow = false;
                        showController();
                        cancelDelayHide();
                        hideControllerDelay();
                    } else {
                        isControllerShow = true;
                        hideController();
                        cancelDelayHide();
                    }

                break;
        }
        return  super.onTouchEvent(event);

    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getScreenSize();
        if (isControllerShow) {
            hideController();
            showController();
            cancelDelayHide();
            hideControllerDelay();
        }

        super.onConfigurationChanged(newConfig);
    }

    private void getScreenSize() {
        LogUtil.e(TAG, " ---getScreenSize()--");
        Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();

    }

    private AlertDialog alertDialog = null;
    AlertDialog.Builder aler = null;


    private void setErrorTyp(int errorType) {
        switch (errorType) {

            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                aler.setMessage("抱歉，该视频无法拖动！");
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN:

                aler.setMessage("抱歉，播放出错了!");

                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                aler.setMessage("抱歉，解码时出现");
                break;
            default:
                aler.setMessage("抱歉，该视频无法播放！");
                break;
        }
    }




    private void exit() {

        if(mVideoView != null){
            if(isBack){
                mVideoView.pause();
            }else{
                mVideoView.pause();
            }
        }
        finish();
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }


    private boolean isError = false;

    @Override
    protected void onPause() {

        if (mHandler != null && radia == null) {
            mHandler.sendEmptyMessage(PAUSE);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        isBack = false;
        LogUtil.v(TAG, "onResume()");
        if (mVideoView != null && mVideoView.isPlaying()) {
            showController();
            cancelDelayHide();
            hideControllerDelay();
        } else if (!isCheckUriBym3u8 && mVideoView != null) {
            if (mCurrentPosition > 1000) {
                mVideoView.start();
            }

            showController();
            cancelDelayHide();
            hideControllerDelay();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (checkVitamioLibs) {
            unregisterReceiver(batteryReceiver);
            unregisterListenerNet();
            mHandler.removeMessages(PROGRESS_CHANGED);
            mHandler.removeMessages(HIDE_CONTROLER);
        }

        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        LogUtil.v(TAG, " onRestart()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        LogUtil.v(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
//		if (mVideoView != null) {
//			mVideoView.stop();
//		}
        LogUtil.e(TAG, "onStop()");
    }

    private boolean isBack = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!isBack) {
                isBack = true;
                mExitHandler.removeMessages(EXIT_TEXT);
                mExitHandler.sendEmptyMessage(EXIT_TEXT);

            }
            return true;

        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (currentVolume >= 0) {
                currentVolume--;
            }
            onVolumeSlide(currentVolume/mVolume);

            return super.onKeyDown(keyCode, event);

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            if (currentVolume < mVolume) {
                currentVolume++;
            }

            onVolumeSlide(currentVolume /mVolume);

            return super.onKeyDown(keyCode, event);

        }
        return false;
    }

    public void registerRxbus(){
        Observable<String> switchOn =  RxBus.get().register("SwitchON",String.class);
        switchOn.observeOn(AndroidSchedulers.mainThread()).subscribe(vobserSwitch);
        Observable<String> switchOff =  RxBus.get().register("SwitchOFF",String.class);
        switchOff.observeOn(AndroidSchedulers.mainThread()).subscribe(vobserSwitch);
        Observable<String> progPre = RxBus.get().register("ProgramPre",String.class);
        progPre.observeOn(AndroidSchedulers.mainThread()).subscribe(obserProgPre);
        Observable<String> progNext = RxBus.get().register("ProgramNext",String.class);
        progNext.observeOn(AndroidSchedulers.mainThread()).subscribe(obserProgNext);
        Observable<String> menu = RxBus.get().register("Menu",String.class);
        menu.observeOn(AndroidSchedulers.mainThread()).subscribe(obserMenu);
        Observable<String> back = RxBus.get().register("Back",String.class);
        back.observeOn(AndroidSchedulers.mainThread()).subscribe(obserBack);
    }
    Observer<String> vobserSwitch  = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(String s) {
            LogUtil.d("duanyl=========>v  Srceen");
            mHandler.sendEmptyMessage(IS_PAUSE_BUTTON);
        }
    };
    Observer<String> obserProgPre = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
             mPrevButton.performClick();
        }
    };
    Observer<String> obserProgNext = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
            mNextButton.performClick();
        }
    };
    Observer<String> obserMenu = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
            if(isControllerShow){
                hideController();
            }else{
                showController();
            }
        }
    };
    Observer<String> obserBack = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
            if(TVAppliction.nowPage.equals(Constants.MoviePage)) {
                exit();
            }
        }
    };
}
