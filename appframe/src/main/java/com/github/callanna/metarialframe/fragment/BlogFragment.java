package com.github.callanna.metarialframe.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.base.BaseFragment;
import com.github.callanna.metarialframe.jsapi.JsApi;
import com.github.callanna.metarialframe.jsapi.JsObject;
import com.github.callanna.metarialframe.util.AppUtil;
import com.github.callanna.metarialframe.util.Constants;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.ToastUtil;
import com.github.callanna.metarialframe.view.widget.ProgressWebView;

import RxJava.RxBus;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Callanna on 2015/12/21.
 */
public class BlogFragment extends BaseFragment {
    /**
     * jsapi回调
     */
    private final int WHAT_JSAPI = 0x001;
    private ProgressWebView mWebView;

    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.fragment_blog);
        mWebView = (ProgressWebView)findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://smart.56iq.net/static/cookbook/#/index");
        //mWebView.loadUrl("https://www.baidu.com");
        JsApi jsApi = new JsApi();
        jsApi.setWhat(WHAT_JSAPI);
        mWebView.addJavascriptInterface(jsApi, "jsapi");
        Observable<JsObject> objsApi = RxBus.get().register(JsApi.JS_TAG, JsObject.class);
        objsApi.observeOn(AndroidSchedulers.mainThread()).subscribe(onJSApi);
    }

    Observer<JsObject> onJSApi = new Observer<JsObject>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(JsObject jsObject) {
            if (jsObject.cmd == JsObject.JsCommand.MESSAGE) {
                ToastUtil.show(context, jsObject.message);
            } else if (jsObject.cmd == JsObject.JsCommand.START_ZHANGCHU) {
                if (AppUtil.isInsatalled(context, Constants.ZHANGCHU_PACKAGE_NAME)) {
                    if (AppUtil.startAppByPackageName(getActivity(), Constants.ZHANGCHU_PACKAGE_NAME)) {

                    } else {
                        ToastUtil.show(getActivity(), "启动出错！" );
                    }
                } else {
                    ToastUtil.show(getActivity(),  "没有安装掌厨！");
                }
            }
        }
    };
}
