package com.github.callanna.housetelecontrol.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.github.callanna.housetelecontrol.Constants;
import com.github.callanna.housetelecontrol.R;
import com.github.callanna.housetelecontrol.TVAppliction;
import com.github.callanna.housetelecontrol.service.MusicPlayerService;
import com.github.callanna.metarialframe.base.BaseFragment;
import com.treadmill.runne.service.MusicControler;
import com.treadmill.runne.service.SongInfoListener;
import java.io.IOException;
import RxJava.RxBus;
import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Callanna on 2016/1/23.
 */
public class RadioFragment extends BaseFragment {
    @Bind(R.id.imv_mic_wave)
    RelativeLayout imvMicWave;
    @Bind(R.id.mbtn_switch)
    ImageButton mbtnSwitch;
    @Bind(R.id.layout_switch)
    RelativeLayout layoutSwitch;
    private MediaRecorder mediaRecorder;
    private Drawable[]progressImg=new Drawable[7];//显示录音振幅图片缓存
    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.fragment_radio);
        bindService();
        initMediaRecord();
        initDataImage();

        Observable<String> OK = RxBus.get().register("OK",String.class);
        OK.observeOn(AndroidSchedulers.mainThread()).subscribe(obserOK);
        TVAppliction.nowPage = Constants.RadioPage;

    }
    Observer<String> obserOK = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            if(TVAppliction.nowPage.equals(Constants.RadioPage) ) {
                mbtnSwitch.performClick();
            }
        }
    };
    private void initDataImage() {
        //初始化振幅图片
        progressImg[0]=context.getResources().getDrawable(R.mipmap.mic_1);
        progressImg[1]=context.getResources().getDrawable(R.mipmap.mic_2);
        progressImg[2]=context.getResources().getDrawable(R.mipmap.mic_3);
        progressImg[3]=context.getResources().getDrawable(R.mipmap.mic_4);
        progressImg[4]=context.getResources().getDrawable(R.mipmap.mic_5);
        progressImg[5]=context.getResources().getDrawable(R.mipmap.mic_6);
        progressImg[6]=context.getResources().getDrawable(R.mipmap.mic_7);
    }

    private void initMediaRecord() {
        mediaRecorder=new MediaRecorder();//创建录音对象
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);//从麦克风源进行录音
    }
    private final Handler mHandler=new Handler();
    private Runnable mUpdateMicStatusTimer=new Runnable() {
        /**
         * 分贝的计算公式K=20lg(Vo/Vi) Vo当前的振幅值,Vi基准值为600
         */
        private int BASE=500;
        private int RATIO=5;
        private int postDelayed=200;
        @Override
        public void run() {
            int ratio=mediaRecorder.getMaxAmplitude()/BASE;
            int db=(int)(20*Math.log10(Math.abs(ratio)));
            int value=db/RATIO;
            if(value<0) {
                value=0;
            }else if(value >  6){
                value = 6;
            }
            layoutSwitch.setBackgroundDrawable(progressImg[value]);
        }
    };
    private boolean isPlay = false;

    @OnClick(R.id.mbtn_switch)
    public void btn_switch(View view) {
        try {
            if (!isPlay) {
                isPlay = true;
                musicControler.setPlayMode(MusicPlayerService.MODE_RADIO);
                musicControler.onPlay();
                mbtnSwitch.setImageResource(R.mipmap.ic_kai);
                layoutSwitch.setBackgroundResource(R.mipmap.ic_zhongxin_an);
                mediaRecorder.prepare();
                mediaRecorder.start();
            } else {
                isPlay = false;
                musicControler.onPause();
                mbtnSwitch.setImageResource(R.mipmap.ic_guan);
                layoutSwitch.setBackgroundResource(R.mipmap.ic_zhongxin_buanxia);
                mediaRecorder.stop();
                mediaRecorder.release();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SongInfoListener songInfoListener = new SongInfoListener() {
        @Override
        public IBinder asBinder() {
            return null;
        }

        @Override
        public void onPlayStateChange(boolean state) throws RemoteException {

        }

        @Override
        public void onNowPositionChange(int position) throws RemoteException {
        }

        @Override
        public void onTimeChange(long currentTime, long totalTime) throws RemoteException {

        }
    };
    /**
     * 音乐控制器
     **/
    private MusicControler musicControler;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                musicControler = MusicControler.Stub.asInterface(service);
                if (musicControler != null) {
                    musicControler.onSetSongInfoListener(songInfoListener);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private void bindService() {
        context.bindService(new Intent(context, MusicPlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        context.unbindService(serviceConnection);
        serviceConnection = null;
    }
}
