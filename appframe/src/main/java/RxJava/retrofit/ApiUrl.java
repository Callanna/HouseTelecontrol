package RxJava.retrofit;

import java.util.Map;

import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Callanna on 2015/12/16.
 */
public interface ApiUrl {
    @FormUrlEncoded
    @POST("token")
    Observable<String> getToken(@Field("appid") String apppid, @Field("secret") String secret, @Field("grant_type") String type);
    @FormUrlEncoded
    @POST("http://smart.56iq.net:8002/device")
    Observable<String> getDevice(@Field("mac") String mac, @Field("cpu_id") String cpu_id, @Field("android_id") String android_id, @Field("serail") String serial);
    @FormUrlEncoded
    @POST("message")
    Observable<String> getPrameters(@Field("access_token") String token, @Field("id") String id);
    @GET("component/weather")
    Observable<String> getWeather(@Query("access_token") String token);
    @FormUrlEncoded
    @POST("token")
    Observable<String> getToken(@FieldMap Map<String, String> param);
    @FormUrlEncoded
    @POST("http://smart.56iq.net:8002/device")
    Observable<String> getDevice(@FieldMap Map<String, String> param);
    @FormUrlEncoded
    @POST("message")
    Observable<String> getPrameters(@FieldMap Map<String, String> param);
}
