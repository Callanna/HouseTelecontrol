package com.github.callanna.metarialframe.base;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2015/7/16.
 */
public class ActivityTaskStack {

    private static ArrayList<BaseActivity> mActivityList = new ArrayList<>();

    public static void add(BaseActivity activity) {
        mActivityList.add(activity);
    }

    public static void remove(BaseActivity activity) {
        mActivityList.remove(activity);
    }

    public static void exit() {
        for (BaseActivity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        mActivityList.clear();
        System.exit(0);
    }
}
