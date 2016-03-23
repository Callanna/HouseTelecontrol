package com.github.callanna.appcontrol.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.callanna.appcontrol.R;
import com.github.callanna.appcontrol.popupwindows.KeyBroadPopupWindow;
import com.github.callanna.appcontrol.task.ControlTvTask;
import com.github.callanna.appcontrol.view.NumericKeyboard;
import com.github.callanna.metarialframe.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Callanna on 2016/1/24.
 */
public class TVControlActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private boolean isSilence = true;
    private boolean isOpen = true;
    @Bind(R.id.layout_switch)
    LinearLayout layoutSwitch;
    @Bind(R.id.layout_menu)
    LinearLayout layoutMenu;
    @Bind(R.id.layout_setting)
    LinearLayout layoutSetting;
    @Bind(R.id.layout_senlice)
    LinearLayout layoutSenlice;
    @Bind(R.id.layout_keybroad)
    LinearLayout layoutKeybroad;
    @Bind(R.id.layout_back)
    LinearLayout layoutBack;
    @Bind(R.id.program_add)
    Button programAdd;
    @Bind(R.id.program_down)
    Button programDown;
    @Bind(R.id.voice_add)
    Button voiceAdd;
    @Bind(R.id.voice_down)
    Button voiceDown;
    @Bind(R.id.imageButton_top)
    ImageButton imageButtonTop;
    @Bind(R.id.imageButton_right)
    ImageButton imageButtonRight;
    @Bind(R.id.imageButton_left)
    ImageButton imageButtonLeft;
    @Bind(R.id.imageButton_button)
    ImageButton imageButtonBottom;
    @Bind(R.id.imageButton_main)
    Button imageButtonMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvcontrol);
        ButterKnife.bind(this);
        context = this;
        layoutBack.setOnClickListener(this);
        layoutKeybroad.setOnClickListener(this);
        layoutMenu.setOnClickListener(this);
        layoutSenlice.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutSwitch.setOnClickListener(this);
        programAdd.setOnClickListener(this);
        programDown.setOnClickListener(this);
        voiceAdd.setOnClickListener(this);
        voiceDown.setOnClickListener(this);
        imageButtonBottom.setOnClickListener(this);
        imageButtonLeft.setOnClickListener(this);
        imageButtonMain.setOnClickListener(this);
        imageButtonRight.setOnClickListener(this);
        imageButtonTop.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.layout_back:
                ControlTvTask.getInstance(context).back();
                break;
            case R.id.layout_keybroad:
                showKeyBroad_popupWindows();
                break;
            case R.id.layout_setting:
                showSetting_popupwindows();
                break;
            case R.id.layout_menu:
                ControlTvTask.getInstance(context).tvmenu();
                break;
            case R.id.layout_senlice:
                if(isSilence) {
                    ControlTvTask.getInstance(context).ring();
                }else{
                     ControlTvTask.getInstance(context).silence();
                 }
                    isSilence = !isSilence;
                break;
            case R.id.layout_switch:
                if(isOpen) {
                    ControlTvTask.getInstance(context).switchOff();
                }else{
                    ControlTvTask.getInstance(context).switchOn();
                }
                isOpen = !isOpen;
                break;
            case R.id.program_add:
                ControlTvTask.getInstance(context).programPre();
                break;
            case R.id.program_down:
                ControlTvTask.getInstance(context).programNext();
                break;
            case R.id.voice_add:
                ControlTvTask.getInstance(context).volumeAdd();
                break;
            case R.id.voice_down:
                ControlTvTask.getInstance(context).volumeDown();
                break;
            case R.id.imageButton_button:
                ControlTvTask.getInstance(context).tvBottom();
                break;
            case R.id.imageButton_left:
                ControlTvTask.getInstance(context).tvRight();
                break;
            case R.id.imageButton_right:
                ControlTvTask.getInstance(context).tvLeft();
                break;
            case R.id.imageButton_top:
                ControlTvTask.getInstance(context).tvTop();
                break;
            case R.id.imageButton_main:
                ControlTvTask.getInstance(context).tvOK();
                break;

        }
    }

    KeyBroadPopupWindow keyBroadPopupWindow;
    NumericKeyboard.OnNumberClick numberClick = new NumericKeyboard.OnNumberClick() {
        @Override
        public void onNumberReturn(int number) {
            LogUtil.d("duanyl===============>number:"+number);
        }
    };

    private void showKeyBroad_popupWindows() {
        keyBroadPopupWindow = new KeyBroadPopupWindow(TVControlActivity.this,numberClick);
        keyBroadPopupWindow.showAtLocation(TVControlActivity.this.findViewById(R.id.layout_tvmain), Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);

    }
    private void showSetting_popupwindows() {

    }

}
