/**********************************************************************
 * @AUTHOR：YOLANDA
 * @DATE：Jun 4, 201512:05:38 PM
 * @DESCRIPTION：create the File, and add the content.
 * ====================================================================
 * Copyright © 56iq. All Rights Reserved
 ***********************************************************************/
package com.github.callanna.metarialframe.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.EditText;

import com.github.callanna.metarialframe.Application;
import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.dialog.MaterialDialog;
import com.github.callanna.metarialframe.util.Constants;
import com.github.callanna.metarialframe.util.NetUtil;

/**
 * @author YOLANDA
 * @Time Jun 4, 2015 12:05:38 PM
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class WebViewClient extends android.webkit.WebViewClient {

	private Context mContext;
	
	public WebViewClient(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 加载URL
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (URLUtil.isValidUrl(url)) {
			view.loadUrl(url);
		} else {
			try {
				Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Application.getInstance().startActivity(in);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	 /**
     * 服务端加载客户端资源
     */
    private String assetsKey = "android-assets";

    /** leve 21 - **/
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse response = super.shouldInterceptRequest(view, url);
        if (url != null && url.contains(assetsKey)) {
            int startPosition = url.indexOf(assetsKey) + assetsKey.length() + 1;
            String assetPath = url.substring(startPosition, url.length());
            if(assetPath.contains("?")) {
                String temp = assetPath.substring(assetPath.lastIndexOf("?"), assetPath.length());
                assetPath = assetPath.replace(temp, "");
            }
            try {
                String mimeType = MimeTypeMap.getFileExtensionFromUrl(url);
                if (mimeType.endsWith("js")) {
                    mimeType = "application/javascript";
                } else {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeType);// png/jpg/css
                }
                response = new WebResourceResponse(mimeType, Constants.CHAR_SET_UTF8, mContext.getAssets().open(assetPath));
            } catch (Throwable e) {
            }
        }
        return response;
    }

    /** leve 21 + **/
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        WebResourceResponse response = super.shouldInterceptRequest(view, request);
        String url = request.getUrl().toString();
        if (url != null && url.contains(assetsKey)) {
            int startPosition = url.indexOf(assetsKey) + assetsKey.length() + 1;
            String assetPath = url.substring(startPosition, url.length());
            if(assetPath.contains("?")) {
                String temp = assetPath.substring(assetPath.lastIndexOf("?"), assetPath.length());
                assetPath = assetPath.replace(temp, "");
            }
            try {
                String mimeType = MimeTypeMap.getFileExtensionFromUrl(url);
                if (mimeType.endsWith("js")) {
                    mimeType = "application/javascript";
                } else {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeType);
                }
                response = new WebResourceResponse(mimeType, Constants.CHAR_SET_UTF8, Application.getInstance().getAssets().open(assetPath));
            } catch (Throwable e) {
            }
        }
        return response;
    }

	@Override
	public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, String host, String realm) {
		final MaterialDialog defineDialog = new MaterialDialog(mContext);
        defineDialog.setCanceledOnTouchOutside(false);
        defineDialog.setTitle(host + mContext.getText(R.string.web_auth_title));
        defineDialog.setContentView(R.layout.dialog_webauth);
        final EditText edtName = (EditText) defineDialog.findViewById(R.id.edt_web_auth_name);
        final EditText edtPass = (EditText) defineDialog.findViewById(R.id.edt_web_auth_pass);
        defineDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				defineDialog.dismiss();
				handler.cancel();
			}
		});
        defineDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				defineDialog.dismiss();
				String name = edtName.getText().toString();
				String pass = edtPass.getText().toString();
				handler.proceed(name, pass);
			}
		});
        defineDialog.show();
	}
	
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//		super.onReceivedError(view, errorCode, description, failingUrl);
		if (NetUtil.isAvailable(mContext)) {
			view.loadUrl("file:///android_asset/web/app_load_error.html");
		} else {
			view.loadUrl("file:///android_asset/web/app_net_error.html");
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
	}
	
	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		return super.shouldOverrideKeyEvent(view, event);
	}
	
	@Override
	public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
		super.doUpdateVisitedHistory(view, url, isReload);
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		super.onLoadResource(view, url);
	}
	
	@Override
	public void onFormResubmission(WebView view, Message dontResend, Message resend) {
		super.onFormResubmission(view, dontResend, resend);
	}
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
	}
	
	@Override
	public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
		super.onReceivedClientCertRequest(view, request);
	}
	
	@Override
	public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
		super.onReceivedLoginRequest(view, realm, account, args);
	}
	
	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		super.onReceivedSslError(view, handler, error);
	}
	
	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		super.onScaleChanged(view, oldScale, newScale);
	}
	
	@Override
	public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
		super.onTooManyRedirects(view, cancelMsg, continueMsg);
	}
	
	@Override
	public void onUnhandledInputEvent(WebView view, InputEvent event) {
		super.onUnhandledInputEvent(view, event);
	}
	
	@Override
	public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
		super.onUnhandledKeyEvent(view, event);
	}
}
