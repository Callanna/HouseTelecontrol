package com.github.callanna.metarialframe.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.github.callanna.metarialframe.config.AppConfig;
import com.github.callanna.metarialframe.util.Constants;


public class ProgressWebView extends WebView {
    private WebViewProgressBar progressBar;
    private Handler handler;
    private WebView _this;
    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressBar = new WebViewProgressBar(context);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setVisibility(GONE);
        addView(progressBar);
        handler = new Handler();
        _this = this;
        initAttribute();
        setWebChromeClient(new WebChromeClient(context, progressBar));
        setWebViewClient(new WebViewClient(context));

    }

    @SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
    @SuppressWarnings("deprecation")
    private void initAttribute() {
        WebSettings webSettings = getSettings();
        webSettings.setDefaultTextEncodingName(Constants.CHAR_SET_UTF8);
        webSettings.setJavaScriptEnabled(true);// 支持JS
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 白边
        // 支持缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 定位
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(AppConfig.getInstance().CACHE_WEB_LOCAL_DIR);
        // 允许
        webSettings.setUseWideViewPort(true);// 是否使用宽视图，根据html的meta
        webSettings.setLoadWithOverviewMode(true);// 是否缩放到屏幕大小，如果getUserWideViewPort为true，它默认为false
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);// window.open()不能用
        webSettings.setAllowFileAccess(true);// 允许访问文件
        // 不允许缩放
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webSettings.setDisplayZoomControls(false);
        }
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        // 自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 获得焦点时设置节点
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        // 关闭硬件加速，会产生Bitmap，所以要重写onMeasure
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Joe打开硬件加速
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}