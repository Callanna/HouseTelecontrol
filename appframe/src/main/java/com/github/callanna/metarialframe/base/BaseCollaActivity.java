package com.github.callanna.metarialframe.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.adapter.TabFragmentAdapter;
import com.github.callanna.metarialframe.util.LogUtil;
import java.util.List;

/**
 * Created by Callanna on 2015/12/21.
 */
public abstract class BaseCollaActivity extends BaseActivity {
    private ViewPager mViewPager;
    private ImageView ivImage;
    @Override
    protected boolean setToolbarAsActionbar() {
        uesCustomToolBar();
        return true;
    }

    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);
        mViewPager = (ViewPager) findViewById(R.id.viewpager2);
        onBaseCollaActivityCreated(savedInstanceState);
    }

    protected abstract void onBaseCollaActivityCreated(Bundle savedInstanceState);


    public void setToolbarImageBg(String uri){
//        Glide.with(ivImage.getContext())
//               .load(uri)
//                .fitCenter()
//                .into(ivImage);
    }

    public void setToolbarImageBg(int resid){
       ivImage.setBackgroundResource(resid);
    }

    protected abstract boolean needTabPager();
    protected  void setTabPager(List<Fragment> tabs,List<String> tabTitles){
        if(needTabPager()){
            TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager());
            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            if(tabs.size() == tabTitles.size()) {
                for (int i = 0; i < tabs.size(); i++) {
                    adapter.addFragment(tabs.get(i));
                    tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(i)));
                }
            }else{
                LogUtil.d("duanyl=========>error param");
            }
            mViewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(mViewPager);
        }else{
            findViewById(R.id.collatabs).setVisibility(View.GONE);
        }

    }

}
