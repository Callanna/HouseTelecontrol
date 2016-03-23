package com.github.callanna.appcontrol.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.github.callanna.appcontrol.R;

/**
 * Created by Callanna on 2016/1/31.
 */
public class TogButton extends CompoundButton {
    private  Drawable mTrackDrawable;
    // Support track drawable instead of text
    private Drawable mTrackOnDrawable;
    private Drawable mTrackOffDrawable;
    private boolean isOn = false;
    public TogButton(Context context) {
        this(context, null);
    }

    public TogButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TogButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MySwitch_BUTTON, defStyleAttr, 0);

        // off-on 模式： 图片模式或文字模式，图片模式是用Track背景图片表示off-on的状态，文字模式是用文字来表示off-on状态。
        mTrackOnDrawable = a.getDrawable(R.styleable.MySwitch_BUTTON_trackOn);
        mTrackOffDrawable = a.getDrawable(R.styleable.MySwitch_BUTTON_trackOff);
        if (checkTrackOffOnDrawable()) {
            // 如果设定图片模式，则默认显示off状态
            mTrackDrawable = mTrackOffDrawable;
        } else {
            mTrackDrawable = a.getDrawable(R.styleable.MySwitch_BUTTON_trackOn);
        }

        // Refresh display with current params
        refreshDrawableState();
        setChecked(isChecked());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private boolean checkTrackOffOnDrawable() {
        return mTrackOnDrawable != null && mTrackOffDrawable != null;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setChecked(boolean checked) {
        if (checkTrackOffOnDrawable()) {
            mTrackDrawable = checked ? mTrackOnDrawable : mTrackOffDrawable;
            refreshDrawableState();
        }
        super.setChecked(checked);
       setBackground(mTrackDrawable);
    }
}
