package com.github.callanna.appcontrol.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.callanna.appcontrol.R;
import com.github.callanna.appcontrol.TVControlAppliction;
import com.github.callanna.appcontrol.fragment.TVFragment;
import com.github.callanna.appcontrol.task.ControlTvTask;
import com.github.callanna.appcontrol.view.guillotine.animation.GuillotineAnimation;
import com.github.callanna.metarialframe.dialog.MaterialDialog;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.NetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;
    private android.support.v4.app.FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    @Bind(R.id.content_hamburger)
    ImageView contentHamburger;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.btn_finddevice)
    Button btn_findDevice;
    @Bind(R.id.root)
    FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new TVFragment(), "").commit();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .build();

    }

    @OnClick(R.id.btn_finddevice)
    public void clivkButton(View view){
        LogUtil.d("duanyl===========>TVControlAppliction  speechService");
        if( TVControlAppliction.getInstance().speechService!=null) {
            TVControlAppliction.getInstance().speechService.startReconginzer();
        }
        final MaterialDialog deviceDialog = new MaterialDialog(MainActivity.this);
        deviceDialog.setCanceledOnTouchOutside(false);
        deviceDialog.setTitle(getResources().getString(R.string.enterDeviceid));
        final EditText ed_device =new EditText(this);
        ed_device.setTextColor(Color.BLACK);
        deviceDialog.setContentView(ed_device);

        deviceDialog.setNegativeButton(com.github.callanna.metarialframe.R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceDialog.dismiss();
            }
        });
        deviceDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = NetUtil.getIpV4Address(MainActivity.this);
                ControlTvTask.getInstance(MainActivity.this).findDevice(ip.substring(0, ip.lastIndexOf(".") + 1) + ed_device.getText().toString().trim());
                deviceDialog.dismiss();
            }
        });
        deviceDialog.show();
       }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TVControlAppliction.getInstance().speechService.stopReconginzer();
        TVControlAppliction.getInstance().speechService.closeService();
    }
}
