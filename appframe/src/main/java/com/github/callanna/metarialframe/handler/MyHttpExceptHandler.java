package com.github.callanna.metarialframe.handler;

import android.content.Context;

import com.github.callanna.metarialframe.util.LogUtil;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.exception.ClientException;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpServerException;
import com.litesuits.http.exception.NetException;
import com.litesuits.http.exception.ServerException;
import com.litesuits.http.exception.handler.HttpExceptionHandler;
import com.litesuits.http.utils.HttpUtil;

/**
 * Created by Callanna on 2015/12/17.
 */
public class MyHttpExceptHandler extends HttpExceptionHandler {
    private static final String TAG = "LiteHttpTask";
    private Context mContext;
    public MyHttpExceptHandler(Context context) {
        this.mContext = context;
    }
    @Override
    protected void onClientException(HttpClientException e, ClientException type) {
        switch (e.getExceptionType()) {
            case UrlIsNull:
                LogUtil.e(TAG, "duanyl========>Url is null");
                break;
            case ContextNeeded:
                LogUtil.e(TAG,"duanyl========>Context Needed");
                break;
            case PermissionDenied:
                LogUtil.e(TAG,"duanyl========>Permission Denied");
                break;
            case SomeOtherException:
                break;
        }
        HttpUtil.showTips(mContext, "LiteHttp2.0", "Client Exception:\n" + e.toString());
        mContext = null;
    }
    @Override
    protected void onNetException(HttpNetException e, NetException type) {
        switch (e.getExceptionType()) {
            case NetworkNotAvilable:
                LogUtil.e(TAG,"duanyl========>Network Not Avilable");
                break;
            case NetworkUnstable:
                LogUtil.e(TAG,"duanyl========>Network Unstable");
                break;
            case NetworkDisabled:
                LogUtil.e(TAG,"duanyl========>Network  Disabled");
                break;
            default:
                break;
        }
        HttpUtil.showTips(mContext, "LiteHttp2.0", "Network Exception:\n" + e.toString());
        mContext = null;
    }
    @Override
    protected void onServerException(HttpServerException e, ServerException type,
                                     HttpStatus status) {
        switch (e.getExceptionType()) {
            case ServerInnerError:
                // status code 5XX error
                break;
            case ServerRejectClient:
                // status code 4XX error
                break;
            case RedirectTooMuch:
                break;
            default:
                break;
        }
        HttpUtil.showTips(mContext, "LiteHttp2.0", "Server Exception:\n" + e.toString());
        mContext = null;
    }
}

