package com.github.callanna.metarialframe.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.callanna.metarialframe.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2015/12/21.
 */
public class TabFragmentAdapter  extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles= new ArrayList<>();

    public TabFragmentAdapter(FragmentManager fm){
        super(fm);
    }
    public TabFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }
    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mTitles.add(title);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    public void addFragment(Fragment baseFragment) {
        mFragments.add(baseFragment);
        mTitles.add(baseFragment.getTag());
    }
}
