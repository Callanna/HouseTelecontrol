package com.github.callanna.metarialframe.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.github.callanna.metarialframe.Application;
import com.github.callanna.metarialframe.config.AppConfig;
import com.github.callanna.metarialframe.data.Weather;
import com.github.callanna.metarialframe.util.Constants;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.OSUtil;
import com.google.gson.Gson;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.BitmapRequest;
import com.litesuits.http.request.FileRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.content.FileBody;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.request.content.multi.MultipartBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by Callanna on 2015/12/17.
 */
public class LiteHttpTask {
    private static LiteHttpTask instance;
    private LiteHttp liteHttp;
    private Context context;
    private LiteHttpTask(Context content){
        this.context = content;
        HttpConfig config = new HttpConfig(content);
        config.setDebugged(true).setDetectNetwork(true).setUserAgent("Mozilla/5.0(...)").setTimeOut(10000,10000);
        liteHttp = LiteHttp.newApacheHttpClient(config);
    }

    public static LiteHttpTask getInstance(Context context){
        if (instance == null){
            instance = new LiteHttpTask(context) ;
        }
        return instance;
    }

    //==========================一行代码======Method GET==========
    public Object getData(String url,Object object){
        return liteHttp.get(url, object.getClass());
    }

    public String performSync(String url){
       return liteHttp.perform(new StringRequest(url));
    }

    public  void performAsync(String url){
          liteHttp.executeAsync(new StringRequest(url));
    }

    public Bitmap getBitmap(String picUrl){
        return liteHttp.perform(new BitmapRequest(picUrl));
    }
    public void getBitmap(String picUrl,String savetoPath){
          liteHttp.executeAsync(new BitmapRequest(picUrl, savetoPath));
    }
    public void getFile(String url,String saveTopath){
        liteHttp.executeAsync(new FileRequest(url, saveTopath));
    }

    //=============HttpListen 回调========Method GET===

    /**
     * 同步 获取 String，在 UI线程 处理结果或异常
     * @param url
     * @param httpListener
     */
    public void getDataStringSync(String url,HttpListener<String> httpListener){
          liteHttp.execute(new StringRequest(url).setHttpListener(httpListener));
    }

    public void getDataStringAsync(String url,HttpListener<String> httpListener){
          liteHttp.executeAsync(new StringRequest(url).setHttpListener(httpListener));
    }

    public void getBitmap(String picUrl,String savetoPath,HttpListener<Bitmap> httpListener){
        liteHttp.executeAsync(new BitmapRequest(picUrl,savetoPath).setHttpListener(httpListener));
    }
    public void getFile(String url,String saveTopath,HttpListener<File> httpListener){
        liteHttp.executeAsync(new FileRequest(url,saveTopath).setHttpListener(httpListener));

    }

    public void uploadFile(String path,String uploadUrl,HttpListener<String> uploadListener){
        StringRequest upload = new StringRequest(uploadUrl)
                .setMethod(HttpMethods.Post)
                .setHttpListener(uploadListener)
                .setHttpBody(new FileBody(new File(path)));
    }

    public void postDataHttpBody(String url,LinkedList<NameValuePair> pList,HttpListener listener){
        StringRequest postRequest = new StringRequest(url);
        postRequest.setMethod(HttpMethods.Post).setHttpListener(listener);
        postRequest.setHttpBody(new UrlEncodedFormBody(pList));
        liteHttp.executeAsync(postRequest);
    }
    public void postDataFileAsync(String url,String path,HttpListener listener){
        StringRequest postRequest = new StringRequest(url);
        postRequest.setMethod(HttpMethods.Post).setHttpListener(listener);
        postRequest.setHttpBody(new FileBody(new File(path)));
        liteHttp.executeAsync(postRequest);
    }
    public void postDataMultipart(String url,MultipartBody body,HttpListener listener){
        StringRequest postRequest = new StringRequest(url);
        postRequest.setMethod(HttpMethods.Post).setHttpListener(listener);
        postRequest.setHttpBody(body);
        liteHttp.executeAsync(postRequest);
    }
    public void getTokenAndWeather(){
        LinkedList<NameValuePair> pList = new LinkedList<>();
        pList.add(new NameValuePair("appid",Constants.APP_ID));
        pList.add(new NameValuePair("secret", Constants.APP_SECRET));
        pList.add(new NameValuePair("grant_type", Constants.GRANT_TYPE));

        postDataHttpBody(Constants.URL_GET_TOKEN, pList, new HttpListener<String>() {
            @Override
            public void onSuccess(String s, Response<String> response) {
                super.onSuccess(s, response);
                try {
                    JSONObject jsonObject = new JSONObject(response.getResult()).getJSONObject("data");
                    String token = jsonObject.getString("access_token");
                    LogUtil.d("duanyl===========>get access_token  :" + token);
                    AppConfig.getInstance().setToken(token);
                    getWeather();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getWeather() {
         String url=  Constants.WEATHRER_URL + "?access_token=" + AppConfig.getInstance().getToken();
         getDataStringAsync(url, new HttpListener<String>() {
             @Override
             public void onSuccess(String s, Response<String> response) {
                 super.onSuccess(s, response);
                 Gson gson = new Gson();
                 Weather weather = gson.fromJson(response.getResult(), Weather.class);
                 Application.getInstance().setWeather(weather);
                 LogUtil.d("duanyl========>" + weather.code);
             }
         });
    }


    public void getDeviceIDAndMqttParam(){
        LinkedList<NameValuePair> pList = new LinkedList<>();
        pList.add(new NameValuePair("mac",AppConfig.getInstance().getMacAddress()));
        pList.add(new NameValuePair("cpu_id", OSUtil.GetCPUSerial()));
        pList.add(new NameValuePair("android_id", OSUtil.getAndroidId(context)));
        pList.add(new NameValuePair("serial", Build.SERIAL));
        postDataHttpBody(Constants.URL_GET_DEVICEID, pList, new HttpListener() {
            @Override
            public void onSuccess(Object o, Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.getResult().toString()).getJSONObject("data");
                    String device_id = jsonObject.getString("device_id");
                    LogUtil.d("duanyl===========>get device_id  :" + device_id);
                    AppConfig.getInstance().setDeviceId(device_id);
                    getMqttParam();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        })
;    }

    private void getMqttParam() {
        LinkedList<NameValuePair> pList = new LinkedList<>();
        pList.add(new NameValuePair("id",AppConfig.getInstance().getDeviceId()));
        pList.add(new NameValuePair("access_token",AppConfig.getInstance().getToken()));
        postDataHttpBody(Constants.URL_MQTT, pList, new HttpListener<String>() {
            @Override
            public void onSuccess(String s, Response<String> response) {
                super.onSuccess(s, response);
                JSONObject jsonObject = null;
                try {
                    LogUtil.d("duanyl=========>" + response.getResult());
                    jsonObject = new JSONObject(response.getResult().toString()).getJSONObject("data");
                    AppConfig.getInstance().setTopic(jsonObject.getString("topic"));
                    AppConfig.getInstance().setClientId(jsonObject.getString("client_id"));
                    AppConfig.getInstance().setHostName(jsonObject.getString("host"));
                    AppConfig.getInstance().setHostPort(jsonObject.getString("port"));
                    AppConfig.getInstance().setUserName(jsonObject.getString("user"));
                    AppConfig.getInstance().setPasswd(jsonObject.getString("pwd"));
                    LogUtil.d("duanyl=========> Topic:" + AppConfig.getInstance().getTopic());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
