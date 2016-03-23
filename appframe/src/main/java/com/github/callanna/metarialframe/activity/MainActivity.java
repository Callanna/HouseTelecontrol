package com.github.callanna.metarialframe.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.adapter.DrawerAdapter;
import com.github.callanna.metarialframe.adapter.TabFragmentAdapter;
import com.github.callanna.metarialframe.base.BaseActivity;
import com.github.callanna.metarialframe.fragment.TabFragment;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.ToastUtil;
import com.github.callanna.metarialframe.view.ScrimInsetsRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String MENU_ONE = "menu one";
    private static final String MENU_TWO = "menu two";
    private static final String MENU_THREE = "menu three";
    private static final String MENU_FOUR = "menu four";
    private static final String MENU_FIVE = "menu five";
    private static final String  TAG = "MainActivity";
    private ScrimInsetsRelativeLayout mDrawerLinear;
    private ListView mDrawerList;
    private View mDrawerHeaderView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private DrawerAdapter adapter;
    private ArrayList<String> menulist = new ArrayList<>();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected boolean setToolbarAsActionbar() {
        return true;
    }

    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        setMyContentView(R.layout.activity_main_demo);
        setTitle("Material");
        initActionMenu();
        initTab();
        initDarwer();
    }

    private void initTab() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        List<String> tabList = new ArrayList<>();
        tabList.add("Tab1");
        tabList.add("Tab2");
        tabList.add("Tab3");
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//支持水平滑动
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(2)));

        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < tabList.size(); i++) {
            Fragment f1 = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content", ""+tabList.get(i));
            f1.setArguments(bundle);
            fragmentList.add(f1);
        }

        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), fragmentList, tabList);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);//给Tabs设置适配器
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private void initActionMenu() {
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.action_setting1) {
                    ToastUtil.show(MainActivity.this, "duanyl=======>action_settings");

                } else if (i == R.id.action_setting2) {
                    ToastUtil.show(MainActivity.this, "duanyl=======>action_settings");

                } else if (i == R.id.action_setting3) {
                    ToastUtil.show(MainActivity.this, "duanyl=======>action_settings");

                } else if (i == R.id.action_share) {
                    ToastUtil.show(MainActivity.this, "duanyl=======>action_share");

                } else {
                }
                return true;
            }
        });
    }

    private void initDarwer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerLinear = (ScrimInsetsRelativeLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.menu_drawer);
        initDarwerHead();

        View appbutton = findViewById(R.id.appbutton);
        appbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Open another activity
                LogUtil.d("duanyl============>open about");
                adapter.toggleChecked(false);
                mDrawerLayout.closeDrawer(mDrawerLinear);
            }
        });

        menulist.add(MENU_ONE);
        menulist.add(MENU_TWO);
        menulist.add(MENU_THREE);
        menulist.add(MENU_FOUR);
        menulist.add(MENU_FIVE);
        adapter = new DrawerAdapter(this, menulist,MainActivity.this);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setDivider(null);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, getToolbar(), R.string.open, R.string.close){
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };


        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

    }

    private void initDarwerHead() {
        mDrawerHeaderView = getLayoutInflater().inflate(R.layout.drawerheader,null);
        mDrawerList.addHeaderView(mDrawerHeaderView);
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectMenu(i);
        }

    }

    public void selectMenu(int position) {
        //TODO start other fragment
        LogUtil.d("duanyl==========>start other fragment :" + menulist.get(position));
        adapter.toggleChecked(position);
        mDrawerLayout.closeDrawer(mDrawerLinear);
    }
}
