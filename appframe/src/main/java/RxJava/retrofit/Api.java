package RxJava.retrofit;

import android.content.Context;
import android.util.Pair;

import com.github.callanna.metarialframe.Application;
import com.github.callanna.metarialframe.config.AppConfig;
import com.github.callanna.metarialframe.data.Weather;
import com.github.callanna.metarialframe.util.Constants;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.OSUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import RxJava.RxBus;
import RxJava.converter.ToStringConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Callanna on 2015/12/18.
 * Base url :
 *
 */
public class Api {
    private static Api instance;

    private ApiUrl apiUrl;
    private Context context;

    private Api(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.53iq.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        apiUrl = retrofit.create(ApiUrl.class);
    }

    public static Api getInstance(Context context) {
        if (instance == null) {
            instance = new Api(context);
        }
        return instance;
    }

    /**
     * Rxjava 变换两次 先获得Token,在获得DeviceID，最后获得MqttParam.
     */
    public void registerMqtt() {
        apiUrl.getToken(Constants.APP_ID, Constants.APP_SECRET, Constants.GRANT_TYPE)
                .flatMap(new Func1<String, Observable<Pair<String, String>>>() {
                    @Override
                    public Observable<Pair<String, String>> call(String stringObservable) {
                        return getDeviceID(stringObservable);
                    }
                })
                .flatMap(new Func1<Pair<String, String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(Pair<String, String> stringStringPair) {
                        return getMqttParam(stringStringPair);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                               @Override
                               public void onCompleted() {
                                   LogUtil.d("duanyl============>onCompleted: Mqqt Connect get Success");
                                   RxBus.get().post("startMqtt","get mqtt param ,we can start Mqtt ervice");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   LogUtil.d("duanyl============>error" + e.toString());

                               }

                               @Override
                               public void onNext(String s) {
                                   LogUtil.d("duanyl============> Completing");
                                   JSONObject jsonObject = null;
                                   try {
                                       jsonObject = new JSONObject(s).getJSONObject("data");
                                       AppConfig.getInstance().setTopic(jsonObject.getString("topic"));
                                       AppConfig.getInstance().setClientId(jsonObject.getString("client_id"));
                                       AppConfig.getInstance().setHostName(jsonObject.getString("host"));
                                       AppConfig.getInstance().setHostPort(jsonObject.getString("port"));
                                       AppConfig.getInstance().setUserName(jsonObject.getString("user"));
                                       AppConfig.getInstance().setPasswd(jsonObject.getString("pwd"));
                                       // 开启MqTT服务
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }

                               }
                           }

                );

    }

    public void getWeather(){
        apiUrl.getToken(Constants.APP_ID, Constants.APP_SECRET, Constants.GRANT_TYPE)
                .flatMap(new Func1<String, Observable< String >>() {
                    @Override
                    public Observable<String> call(String stringObservable) {
                        return getWeatherString(stringObservable) ;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String s) {
                        Gson gson = new Gson();
                        Weather weather = gson.fromJson(s,Weather.class);
                        Application.getInstance().setWeather(weather);
                        LogUtil.d("duanyl=============>"+weather.code);

                    }
                });

    }
    private Observable<Pair<String, String>> getDeviceID(String stringObservable) {
        Observable<String> device = apiUrl.getDevice(
                AppConfig.getInstance().getMacAddress(), OSUtil.GetCPUSerial(),
                OSUtil.getAndroidId(context), android.os.Build.SERIAL);

        return Observable.zip(Observable.just(stringObservable), device, new Func2<String, String, Pair<String, String>>() {
            @Override
            public Pair<String, String> call(String s, String s2) {
                String token = "", device_id = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    token = jsonObject.getJSONObject("data").getString("access_token");
                    JSONObject jsonObject2 = new JSONObject(s2);
                    device_id = jsonObject2.getJSONObject("data").getString("device_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new Pair<String, String>(token, device_id);
            }
        });
    }

    private Observable<String> getMqttParam(Pair<String, String> stringStringPair) {
        Observable<String> param = apiUrl.getPrameters(stringStringPair.first, stringStringPair.second);
        return param;
    }

    private Observable<String> getWeatherString(String json){
        JSONObject jsonObject = null;
        Observable<String> weather = null;
        try {
            jsonObject = new JSONObject(json);
            String token = jsonObject.getJSONObject("data").getString("access_token");
             weather = apiUrl.getWeather(token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }
}
