package com.github.callanna.housetelecontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.github.callanna.housetelecontrol.Constants;
import com.github.callanna.housetelecontrol.R;
import com.github.callanna.housetelecontrol.TVAppliction;
import com.github.callanna.housetelecontrol.nanohttpd.TvControlServer;
import com.github.callanna.housetelecontrol.view.CeramicImageView;
import com.github.callanna.metarialframe.base.BaseActivity;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.NetUtil;
import com.github.callanna.metarialframe.util.ToastUtil;

import java.io.IOException;

import RxJava.RxBus;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseActivity {
    private TvControlServer server;
    private CeramicImageView cimv_recommand, cimv_music, cimv_movie, cimv_entertainment, cimv_radio, cimv_icloud;
    private TextView tv_recommand, tv_music, tv_movie, tv_entertainment, tv_radio, tv_icloud;
    private class BlockItem {
        public TextView main;
        public CeramicImageView top;
        public CeramicImageView bottom;
        public CeramicImageView left;
        public CeramicImageView right;
        public CeramicImageView center;
        public BlockItem(TextView main,CeramicImageView center, CeramicImageView top, CeramicImageView bottom, CeramicImageView left, CeramicImageView right) {
            this.main = main;
            this.center = center;
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
        }
        public void cleanAnim(){
            this.main.clearAnimation();
        }
        public void showBlock() {
            AnimationSet animationSet = new AnimationSet(true);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1.0f);
            ScaleAnimation animation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.setRepeatCount(1 );//设置重复次数
            animationSet.setFillAfter(true);//动画执行完后是否停留在执行完的状态
            animationSet.setStartOffset(200);//执行前的等待时间
            animationSet.setDuration(1000);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(animation);
            this.main.startAnimation(animationSet);
        }
        public void ClickOK(){
            this.center.performLongClick();
        }
    }

    private BlockItem nowBlock,block_recommand, block_music, block_movie, block_entertainment, block_radio, block_icloud;;

    @Override
    protected boolean setToolbarAsActionbar() {
        return true;
    }

    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        setMyContentView(R.layout.activity_main);
        String ip = NetUtil.getIpV4Address(this);
        TVAppliction.DeviceID = ip.substring(ip.lastIndexOf(".")+1);
        LogUtil.d("duanyl=========>tv deviceID :"+ip.substring(ip.lastIndexOf(".")+1));
        setToolbarTitle("设备号："+ip.substring(ip.lastIndexOf(".")+1),true);
        initBlock();
        registerRxBus();
        startServer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        TVAppliction.nowPage = Constants.MainPage;
    }

    private void initBlock() {
        cimv_recommand = ((CeramicImageView) findViewById(R.id.block_recommand));
        cimv_recommand.setOnLongClickListener(blockLongclicklistener);
        cimv_music = ((CeramicImageView) findViewById(R.id.block_music));
        cimv_music.setOnLongClickListener(blockLongclicklistener);
        cimv_movie = ((CeramicImageView) findViewById(R.id.block_movie));
        cimv_movie.setOnLongClickListener(blockLongclicklistener);
        cimv_entertainment = ((CeramicImageView) findViewById(R.id.block_entertainment));
        cimv_entertainment.setOnLongClickListener(blockLongclicklistener);
        cimv_radio = ((CeramicImageView) findViewById(R.id.block_radio));
        cimv_radio.setOnLongClickListener(blockLongclicklistener);
        cimv_icloud = ((CeramicImageView) findViewById(R.id.block_icould));
        cimv_icloud.setOnLongClickListener(blockLongclicklistener);
        block_recommand = new BlockItem((TextView)findViewById(R.id.txt_recommand),cimv_recommand,null,cimv_radio,cimv_movie,null);
        block_movie = new BlockItem((TextView)findViewById(R.id.txt_movie),cimv_movie,null,cimv_music,null,cimv_recommand);
        block_music = new BlockItem((TextView)findViewById(R.id.txt_music),cimv_music,cimv_movie,cimv_radio,cimv_entertainment,cimv_recommand);
        block_entertainment = new BlockItem((TextView)findViewById(R.id.txt_entertainment),cimv_entertainment,cimv_movie,cimv_icloud,null,cimv_music );
        block_radio = new BlockItem((TextView)findViewById(R.id.txt_radio),cimv_radio,cimv_recommand,null,cimv_icloud,null);
        block_icloud = new BlockItem((TextView)findViewById(R.id.txt_icloud),cimv_icloud,cimv_entertainment,null,null,cimv_radio);
        nowBlock = block_recommand;
        nowBlock.showBlock();
    }

    public View.OnLongClickListener blockLongclicklistener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            switch ((view.getId())) {
                case R.id.block_recommand:
                    ToastUtil.show(MainActivity.this, "正在实现对接。。。。。");
                    return false;
                case R.id.block_movie:
                    intent.putExtra("type", Constants.MOVIE);
                    break;
                case R.id.block_music:
                    intent.putExtra("type", Constants.MUSIC);
                    break;
                case R.id.block_radio:
                    intent.putExtra("type", Constants.RADIO);
                    break;
                case R.id.block_entertainment:
                    ToastUtil.show(MainActivity.this, "正在实现对接。。。。。");
                    return false;
                case R.id.block_icould:
                    ToastUtil.show(MainActivity.this, "正在实现对接。。。。。");
                    return false;
            }
            startActivity(intent);
            return false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

    private void startServer() {
        server = new TvControlServer(this);
        try {
            server.start();
        } catch (IOException ioe) {
            LogUtil.w("Httpd", "duanyl==========>The server could not start.");
        }
        LogUtil.w("Httpd", "duanyl========>Web server initialized.  IP : " + NetUtil.getIpV4Address(this));
    }

    public void registerRxBus(){
        Observable<String> top = RxBus.get().register("Top",String.class);
        top.observeOn(AndroidSchedulers.mainThread()).subscribe(obserTop);
        Observable<String> bottom = RxBus.get().register("Bottom",String.class);
        bottom.observeOn(AndroidSchedulers.mainThread()).subscribe(obserBottom);
        Observable<String> left = RxBus.get().register("Left",String.class);
        left.observeOn(AndroidSchedulers.mainThread()).subscribe(obserLeft);
        Observable<String> right = RxBus.get().register("Right",String.class);
        right.observeOn(AndroidSchedulers.mainThread()).subscribe(obserRight);
        Observable<String> OK = RxBus.get().register("OK",String.class);
        OK.observeOn(AndroidSchedulers.mainThread()).subscribe(obserOK);
        Observable<String> findDevice = RxBus.get().register("FindDevice",String.class);
        findDevice.observeOn(AndroidSchedulers.mainThread()).subscribe(obserFindDevice);

    }

    Observer<String> obserTop = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            nowBlock.cleanAnim();
            nowBlock.top.performClick();
            setNowBlock(nowBlock.top);
            nowBlock.showBlock();
        }
    };
    Observer<String> obserBottom = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            nowBlock.cleanAnim();
            nowBlock.bottom.performClick();
            setNowBlock(nowBlock.bottom);
            nowBlock.showBlock();
        }
    };
    Observer<String> obserLeft = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            nowBlock.cleanAnim();
            nowBlock.left.performClick();
            setNowBlock(nowBlock.left);
            nowBlock.showBlock();
        }
    };
    Observer<String> obserRight = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            nowBlock.cleanAnim();
                nowBlock.right.performClick();
                setNowBlock(nowBlock.right);
                nowBlock.showBlock();
        }
    };
    Observer<String> obserOK = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            if(TVAppliction.nowPage.equals(Constants.MainPage)) {
                nowBlock.ClickOK();
            }
        }
    };
    Observer<String> obserFindDevice = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            ToastUtil.show(context,"配对成功！");
        }
    };
    public void setNowBlock(CeramicImageView ceramicImageView){
        switch(ceramicImageView.getId()){
            case R.id.block_recommand:
                nowBlock = block_recommand;
                break;
            case R.id.block_movie:
                nowBlock = block_movie;
                break;
            case R.id.block_music:
                nowBlock = block_music;
                break;
            case R.id.block_radio:
                nowBlock = block_radio;
                break;
            case R.id.block_entertainment:
                nowBlock = block_entertainment;
                break;
            case R.id.block_icould:
                nowBlock = block_icloud;
                break;
        }
    }
}
