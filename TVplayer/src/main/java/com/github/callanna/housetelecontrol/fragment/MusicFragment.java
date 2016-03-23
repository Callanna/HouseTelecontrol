package com.github.callanna.housetelecontrol.fragment;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.callanna.housetelecontrol.Constants;
import com.github.callanna.housetelecontrol.R;
import com.github.callanna.housetelecontrol.TVAppliction;
import com.github.callanna.housetelecontrol.mediaData.GetMediaData;
import com.github.callanna.housetelecontrol.mediaData.Music;
import com.github.callanna.housetelecontrol.mediaData.jaudiotagger.MediaUtil;
import com.github.callanna.housetelecontrol.mediaData.lrc.LrcRead;
import com.github.callanna.housetelecontrol.mediaData.lrc.LyricContent;
import com.github.callanna.housetelecontrol.service.MusicPlayerService;
import com.github.callanna.housetelecontrol.view.LRC.LyricView;
import com.github.callanna.housetelecontrol.view.LrcSeekBar;
import com.github.callanna.metarialframe.base.BaseFragment;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.ToastUtil;
import com.github.callanna.metarialframe.view.VerticalSeekBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.treadmill.runne.service.MusicControler;
import com.treadmill.runne.service.SongInfoListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import RxJava.RxBus;
import butterknife.Bind;
import io.vov.vitamio.Vitamio;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Callanna on 2016/1/10.
 */
public class MusicFragment extends BaseFragment implements View.OnClickListener {

    private List<Music> list;
    private List<LyricContent> LyricList = new ArrayList<LyricContent>();
    private List<String> lrcname = new ArrayList<>();
    private LrcRead mLrcRead;
    int play_state, pesition, duration, current;
    int controlmode = 0;
    private int index = 0;
    private int CurrentTime = 0;
    private int CountTime = 0;
    //pager has lrc layout
    LyricView lyricview;
    TextView nolrc;

    @Nullable @Bind(R.id.layout_musicshow)
    RelativeLayout layout_musicshow;
    @Bind(R.id.tv_song_name)
    TextView tv_song_name;
    @Bind(R.id.tv_singer)
    TextView tv_singer;
    RoundedImageView img_song_ablum;
    ImageView imv_pre;
    ImageView imv_next;
    @Bind(R.id.layout_btn_mode)
    RelativeLayout layout_btn_mode;
    @Bind(R.id.layout_btn_control)
    RelativeLayout layout_btn_control;
    @Bind(R.id.layout_btn_vocie)
    RelativeLayout layout_btn_voice;
    @Bind(R.id.btn_mode)
    Button btn_mode;
    @Bind(R.id.btn_control)
    Button btn_control;
    @Bind(R.id.btn_vocie)
    Button btn_vocie;
    @Bind(R.id.seekbar)
    LrcSeekBar seekbar;
    @Bind(R.id.seekbar_voice)
    VerticalSeekBar seekbar_voice;
    @Bind(R.id.no_sound)
    ImageButton mBtn_noSound;
    @Bind(R.id.layout_music_voice)
    LinearLayout layout_music_voice;
    private AudioManager audioManager;
    @Bind(R.id.tv_time)
      TextView tv_time;
    @Bind(R.id.tv_time_progress)
    TextView tv_time_progress;
      ViewPager viewPager;

    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.fragment_music);

        initView();

        initVitamio();

        voiceCtrl();

        bindService();

        TVAppliction.nowPage = Constants.MusicPage;
        Observable<String> OK = RxBus.get().register("OK",String.class);
        OK.observeOn(AndroidSchedulers.mainThread()).subscribe(obserOK);

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
            if(TVAppliction.nowPage.equals(Constants.MusicPage) ) {
                layout_btn_control.performClick();
            }
        }
    };


    private void initVitamio() {
        boolean isok = false;
        if (!Vitamio.isInitialized(getContext())) {
            isok = Vitamio.initialize(context, context.getResources().getIdentifier("libarm", "raw", context.getPackageName()));
        }
        if (!isok) {
            return;
        }
    }

    private void initView() {
        list = GetMediaData.GetMusicData(context);
        lrcname = MediaUtil.getInstance(context).getMp3Lrc();
        mLrcRead = new LrcRead();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ArrayList<View> viewlist = new ArrayList<>();
        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_music_nolrc, null);
        imv_next = (ImageView) view1.findViewById(R.id.imv_next);
        imv_pre = (ImageView) view1.findViewById(R.id.imv_pre);
        img_song_ablum = (RoundedImageView) view1.findViewById(R.id.img_song_ablum);
        img_song_ablum.setImageResource(R.mipmap.music_imagetest);
        View view2 = LayoutInflater.from(context).inflate(R.layout.layout_music_haslrc, null);
        lyricview = (LyricView) view2.findViewById(R.id.lrcview2);
        nolrc = (TextView) view2.findViewById(R.id.nolrctextview);
        viewlist.add(view1);
        viewlist.add(view2);
        viewPager.setAdapter(new MyPagerAdapter(viewlist));
        viewPager.setCurrentItem(0);

        layout_btn_control.setOnClickListener(this);
        layout_btn_voice.setOnClickListener(this);
        layout_btn_mode.setOnClickListener(this);
        mBtn_noSound.setOnClickListener(this);
        imv_next.setOnClickListener(this);
        imv_pre.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LogUtil.d("duanyl==============>seekbar.getProgress " + seekbar.getProgress());
                try {
                    musicControler.seekTo(seekBar.getProgress());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });

    }

    private boolean isShowSound = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_btn_control:
                try {
                    if (musicControler.isPlaying()) {
                        musicControler.onPause();
                        btn_control.setBackgroundResource(R.mipmap.music_btn_play);
                        img_song_ablum.clearAnimation();
                    } else {
                        musicControler.onPlay();
                        btn_control.setBackgroundResource(R.mipmap.music_btn_pause);
                        Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.music_ablum_rotate);
                        LinearInterpolator lin = new LinearInterpolator();
                        operatingAnim.setInterpolator(lin);
                        img_song_ablum.startAnimation(operatingAnim);

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.no_sound:
                seekbar_voice.setProgress(0);
                break;
            case R.id.layout_btn_vocie:
                if (!isShowSound) {
                    isShowSound = true;
                    layout_music_voice.setVisibility(View.VISIBLE);
                } else {
                    isShowSound = false;
                    layout_music_voice.setVisibility(View.GONE);
                }
                break;
            case R.id.layout_btn_mode:

                if (controlmode < 3) {
                    controlmode++;
                } else {
                    controlmode = 0;
                }
                try {
                    musicControler.setPlayMode(controlmode);
                    if (controlmode == MusicPlayerService.MODE_PLAY_ORDER) {
                        ToastUtil.show(context, "顺序播放");
                        btn_mode.setBackgroundResource(R.mipmap.ic_music_order_mode);
                    } else if (controlmode == MusicPlayerService.MODE_SIGNLE_REPEAT) {
                        ToastUtil.show(context, "单曲循环");
                        btn_mode.setBackgroundResource(R.mipmap.ic_music_signle_mode);
                    } else if (controlmode == MusicPlayerService.MODE_PLAY_RANDOM) {
                        ToastUtil.show(context, "随机播放");
                        btn_mode.setBackgroundResource(R.mipmap.ic_music_random_mode);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imv_pre:
                try {
                    musicControler.onPreview();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imv_next:
                try {
                    musicControler.onNext();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private SongInfoListener songInfoListener = new SongInfoListener() {
        @Override
        public IBinder asBinder() {
            return null;
        }

        @Override
        public void onPlayStateChange(boolean state) throws RemoteException {
            if (!state) {
                btn_control.setBackgroundResource(R.mipmap.music_btn_play);
                img_song_ablum.clearAnimation();

            } else {
                btn_control.setBackgroundResource(R.mipmap.music_btn_pause);
                Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.music_ablum_rotate);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                img_song_ablum.startAnimation(operatingAnim);
            }
        }

        @Override
        public void onNowPositionChange(int position) throws RemoteException {
            duration = (int) list.get(position).getTime();
            tv_time.setText(GetFormatTime(duration).toString());
            CountTime = duration;
            seekbar.setMax(duration);
            String lrcurl2 = list.get(position).getName().toString().substring(0, list.get(position).getName().toString().length() - 4);
            String slrcname = "";
            for (int i = 0; i < lrcname.size(); i++) {
                if (lrcname.get(i).toString().contains(lrcurl2)) {
                    slrcname = lrcname.get(i).toString();
                }
            }
            tv_song_name.setText(lrcurl2);
//            if (list.get(position).getAlbumBitmap() != null) {
//                Drawable ablum = new BitmapDrawable(list.get(position).getAlbumBitmap());
//                Drawable drawable = new BitmapDrawable(blurBitmap(list.get(position).getAlbumBitmap()));
//                img_song_ablum.setImageBitmap(list.get(position).getAlbumBitmap());
//                layout_musicshow.setBackground(drawable);
//            } else {
//                img_song_ablum.setBackgroundResource(R.mipmap.music_imagetest);
//                layout_musicshow.setBackgroundResource(R.mipmap.music_blurbg);
//            }
            tv_singer.setText(list.get(position).getSinger());
            try {
                mLrcRead.Read(slrcname);
                LogUtil.d("duanyl=============>lrc  " + slrcname);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LyricList = mLrcRead.GetLyricContent();
            lyricview.setSentenceEntities(LyricList);
            mHandler.post(mRunnable);
        }

        @Override
        public void onTimeChange(long currentTime, long totalTime) throws RemoteException {
            tv_time_progress.setText(GetFormatTime((int) currentTime));
            seekbar.setProgress((int) currentTime);
            CurrentTime = (int) currentTime;
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

    private static String GetFormatTime(int time) {
        SimpleDateFormat sim = new SimpleDateFormat("mm:ss");
        return sim.format(time);
    }

    public void voiceCtrl() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int M = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar_voice.setMax(M);
        int C = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekbar_voice.setProgress(C);
        seekbar_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int a = seekBar.getProgress();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, a, 0);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    /**
     * 更新歌词
     */
    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        public void run() {

            lyricview.SetIndex(Index());

            lyricview.invalidate();
            nolrc.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(mRunnable, 50);
        }
    };

    public int Index() {
        if (CurrentTime < CountTime) {
            for (int i = 0; i < LyricList.size(); i++) {
                if (i < LyricList.size() - 1) {
                    if (CurrentTime < LyricList.get(i).getLyricTime() && i == 0) {
                        index = i;
                    }
                    if (CurrentTime > LyricList.get(i).getLyricTime()
                            && CurrentTime < LyricList.get(i + 1)
                            .getLyricTime()) {
                        index = i;
                    }
                }
                if (i == LyricList.size() - 1
                        && CurrentTime > LyricList.get(i).getLyricTime()) {
                    index = i;
                }
            }
        }

        return index;
    }

    /**
     * ======================ViewPager      adapter
     */

    public class MyPagerAdapter extends PagerAdapter {
        List<View> list = new ArrayList<>();

        public MyPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager pViewPager = ((ViewPager) container);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ViewPager pViewPager = ((ViewPager) arg0);
            pViewPager.addView(list.get(arg1));
            return list.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    //高斯模糊背景
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap blurBitmap(Bitmap bitmap) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context);

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        //recycle the original bitmap
        bitmap.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;
    }

}
