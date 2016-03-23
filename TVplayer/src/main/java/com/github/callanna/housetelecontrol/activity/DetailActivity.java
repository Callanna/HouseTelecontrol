package com.github.callanna.housetelecontrol.activity;

import android.content.Intent;
import android.os.Bundle;

import com.github.callanna.housetelecontrol.Constants;
import com.github.callanna.housetelecontrol.TVAppliction;
import com.github.callanna.housetelecontrol.fragment.LocalMovieFragment;
import com.github.callanna.housetelecontrol.fragment.MusicFragment;
import com.github.callanna.housetelecontrol.fragment.RadioFragment;
import com.github.callanna.metarialframe.base.BaseSecondaryActivity;

import RxJava.RxBus;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Callanna on 2015/12/25.
 */

public class DetailActivity extends BaseSecondaryActivity{

    @Override
    protected void onSecondaryActivityCreated(Bundle saveInstanceState) {
        Intent intent = getIntent();
        String type =intent.getStringExtra("type");
        setContent(type);
        Observable<String> back = RxBus.get().register("Back", String.class);
        back.observeOn(AndroidSchedulers.mainThread()).subscribe(obserBack);
    }
    Observer<String> obserBack = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
            if(!TVAppliction.nowPage.equals(Constants.MainPage)) {
                finish();
            }
        }
    };
    private void setContent(String type) {
        switch(type){
            case Constants.MOVIE:
                replaceFragment(new LocalMovieFragment(),"");
                break;
            case Constants.MUSIC:
                replaceFragment(new MusicFragment(),"");
                break;
            case Constants.RADIO:
                replaceFragment(new RadioFragment(),"");
                break;
        }
    }

    @Override
    protected void onToolbarBackClicked() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    @Override
    protected boolean setToolbarAsActionbar() {
        return true;
    }


}
