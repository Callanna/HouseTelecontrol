package com.github.callanna.metarialframe.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.callanna.metarialframe.R;


/**
 * Created by Callanna on 2015/12/17.
 */
public class MyToast extends Toast {

    private Context mContext;
    private TextView txtMsg;
    public MyToast(Context context) {
        super(context);
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
        txtMsg = (TextView)view.findViewById(R.id.toast_message);
        setView(view);
    }
    public void setMsg(String msg){
        txtMsg.setText(msg);
    }
    public void setMsg(int resId){
        setMsg(mContext.getString(resId));
    }
}
