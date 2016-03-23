package com.github.callanna.metarialframe.base;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.OSUtil;
import com.github.callanna.metarialframe.util.ScreenPower;

import RxJava.RxBus;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Description  框架基础Activity类
 * Created by chenqiao on 2015/7/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final int TV_STATE_ON = 1;
    public static final int TV_STATE_OFF = 2;
    public static final int TV_STATE_ON_SENLICE = 3;
    protected AppCompatActivity context;
    /**
     * 替代Actionbar的Toolbar
     */
    private Toolbar mToolbar;

    private TextView mTitleTv;

    protected FragmentManager fragmentManager;
    /**
     * FragmentTransaction，you should use
     * {@link android.support.v4.app.Fragment} replace
     * <code>R.id.layout_content_root</code>
     **/
    protected FragmentTransaction fragmentTransction;
    /**
     * Toolbar之下的layout
     */
    private FrameLayout mContentLayout;

    /**
     * 是否注册了EventBus，true时会在onDestroy()中自动注销
     */

    private boolean isSupportActionbar = false;
    /**
     * 遮罩层
     */
    private RelativeLayout mShadeLayout;
    public static int nowTvState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        mContentLayout = (FrameLayout) findViewById(R.id.rootlayout_baseactivity);
        context = this;
        ActivityTaskStack.add(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleTv = (TextView) findViewById(R.id.toolbarTitle);

        if (setToolbarAsActionbar()) {
            isSupportActionbar = true;
            setSupportActionBar(mToolbar);
        } else {
            isSupportActionbar = false;
        }

        setTitle("");
        fragmentManager = getSupportFragmentManager();
        onBaseActivityCreated(savedInstanceState);
        nowTvState = TV_STATE_ON;
        initShadeLayout();//在baseActivity 里初始化，保证所有界面都可以关屏

        Observable<String> switchOn =  RxBus.get().register("SwitchON",String.class);
        switchOn.observeOn(AndroidSchedulers.mainThread()).subscribe(obserSwitchOn);

        Observable<String> switchOff =  RxBus.get().register("SwitchOFF",String.class);
        switchOff.observeOn(AndroidSchedulers.mainThread()).subscribe(obserSwitchOff);
    }

    Observer<String> obserSwitchOn = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(String s) {
            LogUtil.d("duanyl=========>showSrceen");
           showSrceen();
        }
    };
    Observer<String> obserSwitchOff = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(String s) {
            LogUtil.d("duanyl=========>closeSrceen");
            closeSrceen();
        }
    };
    /**
     * 初始化遮罩层
     */
    /**
     * 遮罩层布局的上一次点击时间戳
     */
    long prevTime = -1;
    private void initShadeLayout() {
        if (mShadeLayout == null) {
            LogUtil.d("duanyl=========>initShadeLayout");
            mShadeLayout = new RelativeLayout(context);
            mShadeLayout.setVisibility(View.GONE);
            mShadeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            mShadeLayout.setBackgroundColor(Color.BLACK);
            mShadeLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    long nowTime = System.currentTimeMillis();
                    if (nowTime - prevTime < 300) {
                        mShadeLayout.setVisibility(View.GONE);
                        prevTime = -1;
                        ScreenPower.getInstance().on();

                    }
                    prevTime = nowTime;
                }
            });
            ((ViewGroup) getActivityContentView()).addView(mShadeLayout);
            // resetTime("reset");
        }
    }

    private View getActivityContentView() {
        Window window = context.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏，因为仅仅mshadeLayout会有状态栏。
        return window.getDecorView().findViewById(android.R.id.content);
    }

    public void showSrceen(){
        nowTvState = TV_STATE_ON;
        mShadeLayout.setVisibility(View.GONE);
        ScreenPower.getInstance().on();
    }
    public void closeSrceen() {
        nowTvState = TV_STATE_OFF;
        ScreenPower.getInstance().off();
        mShadeLayout.setVisibility(View.VISIBLE);
    }
    /**
     * 设定是否将Toolbar作为Actionbar使用，将会
     * 影响Toolbar的菜单的使用方法。
     *
     * @return true则使用{@link #onCreateMyToolbarMenu()}进行菜单创建
     */
     protected abstract boolean setToolbarAsActionbar();
     public void uesCustomToolBar(){
         mToolbar.setVisibility(View.GONE);
     }
    /**
     * 设置Toolbar标题
     *
     * @param title 标题
     * @param isCenter 是否居中
     */
    public void setToolbarTitle(String title, boolean isCenter) {
        if (isSupportActionbar) {
            if (!isCenter) {
                getSupportActionBar().setTitle(title);
            } else {
                mTitleTv.setText(title);
            }
        } else {
            if (!isCenter) {
                mToolbar.setTitle(title);
            } else {
                mTitleTv.setText(title);
            }
        }
    }

    /**
     * 设置Toolbar标题
     *
     * @param resid    标题资源
     * @param isCenter 是否居中
     */
    public void setToolbarTitle(int resid, boolean isCenter) {
        if (isSupportActionbar) {
            if (!isCenter) {
                getSupportActionBar().setTitle(resid);
            } else {
                mTitleTv.setText(resid);
            }
        } else {
            if (!isCenter) {
                mToolbar.setTitle(resid);
            } else {
                mTitleTv.setText(resid);
            }
        }
    }

    /**
     * 设置居中标题的颜色
     *
     * @param color 颜色
     */
    public final void setCenterTitleColor(int color) {
        if (mTitleTv != null) {
            mTitleTv.setTextColor(color);
        }
    }

    /**
     * 设置居中标题的字体大小
     *
     * @param size 大小
     */
    public final void setCenterTitleSize(float size) {
        if (mTitleTv != null) {
            mTitleTv.setTextSize(size);
        }
    }

    /**
     * 代替onCreate的入口类
     *
     * @param savedInstanceState
     */
    protected abstract void onBaseActivityCreated(Bundle savedInstanceState);



    /**
     * 重写onDestroy，如果注册了EventBus，则需要注销
     */
    @Override
    protected void onDestroy() {

        ActivityTaskStack.remove(this);
        super.onDestroy();
    }

    /**
     * 获取内容布局id
     *
     * @return 根布局Id
     */
    public final int getRootFrameLayoutId() {
        return R.id.rootlayout_baseactivity;
    }

    /**
     * 获取Toolbar下的根布局
     *
     * @return 根布局
     */
    public final FrameLayout getRootFrameLayout() {
        return mContentLayout;
    }

    /**
     * 设置Activity的中心内容
     *
     * @param layoutResID 资源Id
     */
    protected final void setMyContentView(int layoutResID) {
        if (mContentLayout != null) {
            LayoutInflater.from(this).inflate(layoutResID, mContentLayout, true);
        }
    }

    protected final void setMyContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
    }

    protected final View findViewByTag(Object tag) {
        return mContentLayout.findViewWithTag(tag);
    }



    /**
     * 替换Activity的内容
     *
     * @param fragment
     * @param isBackStack
     */
    protected void replaceFragment(BaseFragment fragment, String isBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(isBackStack)) {
            fragmentTransaction.replace(R.id.rootlayout_baseactivity, fragment);
        } else {
            fragmentTransaction.replace(R.id.rootlayout_baseactivity, fragment, isBackStack);
            fragmentTransaction.addToBackStack(isBackStack);
        }
        OSUtil.closeInputMethod(context);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Toolbar相关
     */
    public final Toolbar getToolbar() {
        if (mToolbar != null) {
            return mToolbar;
        } else {
            return null;
        }
    }

    public final void hideToolbar() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public final void showToolbar() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * handler message what 关屏
     */
    private static final int CLOSE_SRCEEN = 0x000a;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        OSUtil.closeInputMethod(context);
        LogUtil.d("duanyl==========>dispatchTouchEvent  base");
        //if (Application.getInstance().ISAutoCloseSrceen) {
           // resetTime();
        //}
        return super.dispatchTouchEvent(ev);
    }

    private void resetTime() {
        // TODO Auto-generated method stub
        //如果无操作，重置时间
        myHandler.removeMessages(CLOSE_SRCEEN);//從消息隊列中移除
        Message msg = myHandler.obtainMessage(CLOSE_SRCEEN);
        myHandler.sendMessageDelayed(msg, 1000 * 180* 1);//無操作dd分钟后進入屏保
    }


    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLOSE_SRCEEN:
                    closeSrceen();
                    break;
            }
        }
    };

    ////**
    /**
     * =================================================RxBus================
     */

}