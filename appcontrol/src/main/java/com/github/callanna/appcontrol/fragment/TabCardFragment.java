package com.github.callanna.appcontrol.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.callanna.appcontrol.R;
import com.github.callanna.appcontrol.activity.TVControlActivity;
import com.github.callanna.appcontrol.task.ControlTvTask;
import com.github.callanna.appcontrol.view.TogButton;

import RxJava.RxBus;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Callanna on 2016/1/24.
 */
public class TabCardFragment extends Fragment implements View.OnClickListener {

    private static final String EXTRA_COLOR = "TabCardFragment.EXTRA_COLOR";
    private static final String EXTRA_TAG = "TabCardFragment.EXTRA_TAG";
    private static final String EXTRA_STATE = "TabCardFragment.EXTRA_STATE";
    private static final String EXTRA_ELECT = "TabCardFragment.EXTRA_ELECT";
    public static final String TV_STATE_OFF = "设备在线：关";
    public static final String TV_STATE_ON = "设备在线：开";
    public static final String TV_STATE_ON_SENLICE = "设备在线：静音";
    public static final String TV_STATE_Offline = "设备不在线 ";
    public static String nowstate = TabCardFragment.TV_STATE_OFF;
    public static String nowDeviceId = "";
    FrameLayout mMainLayout;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.imv_tv)
    ImageView imvTv;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.btn_tv_switch)
    TogButton btnTvSwitch;
    @Bind(R.id.tv_electricity_consumption)
    TextView tvElectricityConsumption;
    @Bind(R.id.main_layout)
    FrameLayout mainLayout;
    String state = "";
    private boolean isOpen = true;

    public static TabCardFragment newInstance(int backgroundColor, String tag, int elect_con) {
        TabCardFragment fragment = new TabCardFragment();
        Bundle bd = new Bundle();
        bd.putString(EXTRA_TAG, tag);
        bd.putInt(EXTRA_ELECT, elect_con);
        bd.putInt(EXTRA_COLOR, backgroundColor);
        fragment.setArguments(bd);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabcard, container, false);
        ButterKnife.bind(this, v);
        Bundle bdl = getArguments();
        mMainLayout = (FrameLayout) v.findViewById(R.id.main_layout);
        LayerDrawable bgDrawable = (LayerDrawable) mMainLayout.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.background_shape);
        shape.setColor(bdl.getInt(EXTRA_COLOR));
        tvElectricityConsumption.setText(String.valueOf(bdl.getInt(EXTRA_ELECT)));
        setState();
        btnTvSwitch.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if (isOpen) {
                                                   ControlTvTask.getInstance(getActivity()).switchOff();
                                               } else {
                                                   ControlTvTask.getInstance(getActivity()).switchOn();
                                               }
                                               isOpen = !isOpen;
                                           }
                                       }

        );
        Observable<String> OBstate = RxBus.get().register("GetState",String.class);
        OBstate.observeOn(AndroidSchedulers.mainThread()).subscribe(observeState);
        v.setOnClickListener(this);

        return v;
    }
    Observer<String> observeState = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
          setState();
        }
    };
    private void setState() {
        state = nowstate;
        tvState.setText(state);
        tvName.setText("设别号：" +nowDeviceId  );
        if (state.equals(TabCardFragment.TV_STATE_ON) || state.equals(TabCardFragment.TV_STATE_ON_SENLICE)) {
            btnTvSwitch.setChecked(true);
            isOpen = true;
        }else {
            isOpen = false;
            btnTvSwitch.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        getActivity().startActivity(new Intent(getActivity(), TVControlActivity.class));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
