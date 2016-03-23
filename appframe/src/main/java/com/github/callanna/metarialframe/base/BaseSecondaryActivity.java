package com.github.callanna.metarialframe.base;

import android.os.Bundle;
import android.view.View;

import com.github.callanna.metarialframe.R;


public abstract class BaseSecondaryActivity extends BaseActivity {
    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        getToolbar().setNavigationIcon(R.mipmap.ic_action_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarBackClicked();
            }
        });
        onSecondaryActivityCreated(savedInstanceState);
    }

    protected abstract void onSecondaryActivityCreated(Bundle saveInstanceState);

    /**
     * Toobar返回键点击事件
     */
    protected abstract void onToolbarBackClicked();
}