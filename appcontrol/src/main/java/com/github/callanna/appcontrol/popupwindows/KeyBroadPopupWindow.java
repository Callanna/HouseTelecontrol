package com.github.callanna.appcontrol.popupwindows;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.github.callanna.appcontrol.R;
import com.github.callanna.appcontrol.view.NumericKeyboard;

/**
 * Created by Callanna on 2016/2/12.
 */
public class KeyBroadPopupWindow extends PopupWindow {
    private View contentView;
    private NumericKeyboard numericKeyboard;
    public KeyBroadPopupWindow(Activity context,NumericKeyboard.OnNumberClick numberClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.keybroad_popupwindow, null);
        numericKeyboard = (NumericKeyboard) contentView.findViewById(R.id.number_keyboard);
        numericKeyboard.setOnNumberClick(numberClick);
        //设置KeyBroadPopupWindow的View
        this.setContentView(contentView);
        //设置KeyBroadPopupWindow弹出窗体的宽
        this.setWidth( LayoutParams.MATCH_PARENT);
        //设置KeyBroadPopupWindow弹出窗体的高
        this.setHeight( LayoutParams.WRAP_CONTENT);
        //设置KeyBroadPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置KeyBroadPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置KeyBroadPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        contentView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = contentView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()== MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }
}
