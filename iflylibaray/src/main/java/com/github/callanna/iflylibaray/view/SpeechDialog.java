package com.github.callanna.iflylibaray.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.callanna.iflylibaray.R;
import com.github.callanna.iflylibaray.util.DensityUtils;
import com.github.callanna.iflylibaray.util.ScreenUtils;

/**
 * Created by Administrator on 2015/12/4.
 */
public class SpeechDialog extends RelativeLayout {

    private WindowManager.LayoutParams windowManagerParams;
    private WindowManager wm;
    private TextView speech_tips;

    private View speechWave;
    private boolean isShown;

    public SpeechDialog(Context context) {
        this(context, null);
    }

    public SpeechDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeechDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManagerParams = new WindowManager.LayoutParams();
        View.inflate(context, R.layout.popup_speech_dialog, this);
        speech_tips = (TextView) findViewById(R.id.speech_tips);
        speechWave = findViewById(R.id.wave);
    }

    public void setTip(String text) {
        speech_tips.setText(text);
    }

    public void volumChanged(int value) {
        final int VTAG = 0xFF00AA01;
        Integer rawHeight = (Integer) speechWave.getTag(VTAG);
        if (rawHeight == null) {
            rawHeight = speechWave.getLayoutParams().height;
            speechWave.setTag(VTAG, rawHeight);
        }
        LayoutParams params = (LayoutParams) speechWave.getLayoutParams();
        params.height = (int) (rawHeight * (value + 10) * 0.05);
        params.height = Math.max(params.height, speechWave.getMeasuredWidth());
        speechWave.setLayoutParams(params);
    }

    public void show() {
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        // windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 注意，flag的值可以为： LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
		 * LayoutParams.FLAG_NOT_FOCUSABLE 不可聚焦 LayoutParams.FLAG_NOT_TOUCHABLE
		 * 不可触摸
		 */

        //什么是gravity属性呢？简单地说，就是窗口如何停靠。  当设置了 Gravity.LEFT 或 Gravity.RIGHT 之后，x值就表示到特定边的距离
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
//        // 以屏幕左上角为原点，设置x、y初始值，使按钮定位在右下角
        int width = DensityUtils.dp2px(getContext(), 180);
        int height = DensityUtils.dp2px(getContext(), 140);
        windowManagerParams.x = (ScreenUtils.getScreenWidth(getContext()) - width) / 2;
        windowManagerParams.y = (ScreenUtils.getScreenHeight(getContext()) - height) / 2;
        // 设置悬浮窗口长宽数据
        windowManagerParams.width = width;
        windowManagerParams.height = height;
        // 显示悬浮View
        wm.addView(this, windowManagerParams);
        isShown = true;
    }

    public void hide() {
        if (isShown) {
            wm.removeView(this);
            isShown = false;
        }
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

}
