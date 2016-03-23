/**********************************************************************
 * @AUTHOR：YOLANDA
 * @DATE：Jun 4, 201512:03:36 PM
 * @DESCRIPTION：create the File, and add the content.
 * ====================================================================
 * Copyright © 56iq. All Rights Reserved
 ***********************************************************************/
package com.github.callanna.metarialframe.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebStorage;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.dialog.MaterialDialog;

/**
 * @author YOLANDA
 * @Time Jun 4, 2015 12:03:36 PM
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class WebChromeClient extends android.webkit.WebChromeClient {
	private WebViewProgressBar progressBar;
	/**
	 * context
	 */
	private Context mContext;
	
	public WebChromeClient(Context context,WebViewProgressBar progressBar) {
		this.mContext = context;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {

		if(newProgress == 100){
			progressBar.setProgress(100);
			new Handler().postDelayed(new Runnable(){

				@Override
				public void run() {

				}
			},200);
		}else if(progressBar.getVisibility() == View.GONE){
			progressBar.setVisibility(View.VISIBLE);
		}
		if(newProgress < 5){
			newProgress = 5;
		}
		progressBar.setProgress(newProgress);
		super.onProgressChanged(view, newProgress);
	}
	/**
	 * 定位
	 */
	private MaterialDialog mLocationDialog;

	/**
	 * 定位
	 */
	@Override
	public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
		if(mLocationDialog == null) {
			mLocationDialog= new MaterialDialog(mContext);
			mLocationDialog.setCanceledOnTouchOutside(false);
			mLocationDialog.setMessage(R.string.web_location_please);
			mLocationDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mLocationDialog.dismiss();
					callback.invoke(origin, false, false);
				}
			});
			mLocationDialog.setPositiveButton(R.string.allow, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mLocationDialog.dismiss();
					callback.invoke(origin, true, true);
				}
			});
		}
		mLocationDialog.show();
	}

	/**
	 * 隐藏前一个定位申请
	 * @author YOLANDA
	 */
	@Override
	public void onGeolocationPermissionsHidePrompt() {
		if(mLocationDialog != null && mLocationDialog.isShowing()) {
			mLocationDialog.dismiss();
		}
	}

	/**
	 * 确认
	 * @author YOLANDA
	 */
	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		final MaterialDialog confirmDialog = new MaterialDialog(mContext);
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				result.cancel();
				confirmDialog.dismiss();
			}
		});
		confirmDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				result.confirm();
				confirmDialog.dismiss();
			}
		});
		confirmDialog.setMessage(message);
		confirmDialog.show();
		return true;
	}

	/**
	 * web 输入框
	 * @author YOLANDA
	 * @return
	 */
	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
		final MaterialDialog defineDialog = new MaterialDialog(mContext);
		if(!TextUtils.isEmpty(message)) {
			defineDialog.setTitle(message);
		}
		defineDialog.setCanceledOnTouchOutside(false);
		defineDialog.setContentView(R.layout.dialog_webprompt);
		final EditText edtContent = (EditText) defineDialog.findViewById(R.id.edt_web_prompt);
		if(!TextUtils.isEmpty(defaultValue)) {
			edtContent.setText(defaultValue);
		}
		defineDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				result.cancel();
				defineDialog.dismiss();
			}
		});
		defineDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				defineDialog.dismiss();
				String userContent = edtContent.getText().toString();
				result.confirm(userContent);
			}
		});
		defineDialog.show();
		return true;
	}

	/**
	 * 通知
	 * @author YOLANDA
	 * @return
	 */
	@Override
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		final MaterialDialog jsAlertDialog = new MaterialDialog(mContext);
		jsAlertDialog.setCanceledOnTouchOutside(false);
		jsAlertDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				result.confirm();
				jsAlertDialog.dismiss();
			}
		});
		jsAlertDialog.setMessage(message);
		jsAlertDialog.show();
		return true;
	}

	/**
	 * 扩展缓存
	 * @author YOLANDA
	 */
	@Override
	public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
		//		super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);// WebView now uses the HTML5 / JavaScript Quota Management API
		quotaUpdater.updateQuota(requiredStorage * 2);
	}

	@Override
	public Bitmap getDefaultVideoPoster() {
		return super.getDefaultVideoPoster();
	}

	@Override
	public View getVideoLoadingProgressView() {
		return super.getVideoLoadingProgressView();
	}

	@Override
	public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
		return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
	}

	@Override
	public void getVisitedHistory(ValueCallback<String[]> callback) {
		super.getVisitedHistory(callback);
	}

	@Override
	public void onPermissionRequest(PermissionRequest request) {
		super.onPermissionRequest(request);
	}

	@Override
	public void onPermissionRequestCanceled(PermissionRequest request) {
		super.onPermissionRequestCanceled(request);
	}

	@Override
	public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
		super.onReceivedTouchIconUrl(view, url, precomposed);
	}

	@Override
	public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
		super.onShowCustomView(view, requestedOrientation, callback);
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		super.onShowCustomView(view, callback);
	}

	@Override
	public void onCloseWindow(WebView window) {
		super.onCloseWindow(window);
	}

	@Override
	public void onHideCustomView() {
		super.onHideCustomView();
	}

	@Override
	public void onConsoleMessage(String message, int lineNumber, String sourceID) {
		super.onConsoleMessage(message, lineNumber, sourceID);
	}

	@Override
	public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
		return super.onConsoleMessage(consoleMessage);
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
		return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
	}

	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
		return super.onJsBeforeUnload(view, url, message, result);
	}

	@Override
	public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
		super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
		//WebView now uses the HTML5 / JavaScript Quota Management API.
	}

	@Override
	public void onRequestFocus(WebView view) {
		super.onRequestFocus(view);
	}


	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		super.onReceivedIcon(view, icon);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		super.onReceivedTitle(view, title);
	}

	@Override
	public boolean onJsTimeout() {
		return super.onJsTimeout();
	}

}
