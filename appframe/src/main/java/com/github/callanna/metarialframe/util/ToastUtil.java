package com.github.callanna.metarialframe.util;

import android.content.Context;
import android.view.Gravity;

import com.github.callanna.metarialframe.view.MyToast;


/**
 * Created by Callanna on 2015/12/17.
 */
public class ToastUtil {

    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;

    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;

    public static void show(Context context, int resId){
        show(context, resId, LENGTH_SHORT);
    }


    public static void show(Context context, CharSequence text){
        show(context, text, LENGTH_SHORT);
    }


    public static void show(Context context, int resId, int duration){
        show(context, context.getText(resId), duration);
    }


    public static void show(Context context, CharSequence text, int duration){
        MyToast mToast = new MyToast(context);
        mToast.setDuration(duration);
        mToast.setMsg(text.toString());
        mToast.show();
    }

    public static void showInCenter(Context context, CharSequence text, int duration){
        MyToast mToast = new MyToast(context);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.CENTER,50,50) ;
        mToast.setMsg(text.toString());
        mToast.show();
    }
}
