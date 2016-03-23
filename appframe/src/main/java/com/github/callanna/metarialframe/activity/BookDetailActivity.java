package com.github.callanna.metarialframe.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.callanna.metarialframe.base.BaseCollaActivity;
import com.github.callanna.metarialframe.fragment.TabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2015/12/21.
 */
public class BookDetailActivity extends BaseCollaActivity{
    @Override
    protected void onBaseCollaActivityCreated(Bundle savedInstanceState) {
        List<Fragment> listtabs = new ArrayList<>();
        List<String> listtabTitles = new ArrayList<>();
        for(int i = 0;i < 3;i ++){
            Fragment f1 = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content", "TAB "+ i);
            f1.setArguments(bundle);
            listtabs.add(f1);
            listtabTitles.add("TAB "+i);
        }
        setTabPager(listtabs,listtabTitles);
        setTitle("Book");
    }

    @Override
    protected boolean needTabPager() {
        return true;
    }
}
