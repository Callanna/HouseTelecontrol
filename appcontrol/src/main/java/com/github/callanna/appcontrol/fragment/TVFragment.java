package com.github.callanna.appcontrol.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.callanna.appcontrol.R;
import com.github.callanna.appcontrol.task.ControlTvTask;
import com.github.callanna.appcontrol.view.flippableStackView.FlippableStackView;
import com.github.callanna.appcontrol.view.flippableStackView.StackPageTransformer;
import com.github.callanna.appcontrol.view.flippableStackView.ValueInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2016/1/24.
 */
public class TVFragment extends  Fragment {
    private static final int NUMBER_OF_FRAGMENTS = 15;

    private FlippableStackView mFlippableStack;

    private ColorFragmentAdapter mPageAdapter;

    private List<Fragment> mViewPagerFragments;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tv, container, false);
        createViewPagerFragments();
        mPageAdapter = new ColorFragmentAdapter(getActivity().getSupportFragmentManager(), mViewPagerFragments);


        mFlippableStack = (FlippableStackView) v.findViewById(R.id.flippable_stack_view);
        mFlippableStack.initStack(4, StackPageTransformer.Orientation.VERTICAL );
        mFlippableStack.setAdapter(mPageAdapter);

        return v;
    }

    private void createViewPagerFragments() {
        mViewPagerFragments = new ArrayList<>();

        int startColor = getResources().getColor(R.color.emerald);
        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);

        int endColor = getResources().getColor(R.color.wisteria);
        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);

        ValueInterpolator interpolatorR = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endR, startR);
        ValueInterpolator interpolatorG = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endG, startG);
        ValueInterpolator interpolatorB = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endB, startB);

        ControlTvTask.getInstance(getActivity()).getState();
        String tag = "TV";
        int elect = (int) (5+Math.random()*5);
        for (int i = 0; i < NUMBER_OF_FRAGMENTS; ++i) {
            mViewPagerFragments.add(TabCardFragment.newInstance(Color.argb(255, (int) interpolatorR.map(i), (int) interpolatorG.map(i), (int) interpolatorB.map(i))
                                  ,tag,elect));
        }
    }

    private class ColorFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ColorFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;

        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
