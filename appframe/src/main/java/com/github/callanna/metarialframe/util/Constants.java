package com.github.callanna.metarialframe.util;

/**
 * Created by Callanna on 2015/12/17.
 */
public class Constants {

    public static final String CHAR_SET_UTF8 = "UTF-8";

    public static String Device_ID = "DEVICE_ID";
    public static String FactroyID = "";
    public static String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String MAC_ADDRESS ="MAC_ADDRESS" ;
    //获取Token 参数
    public static String APP_ID = "534Glh2zxyyfvSiThU";
    public static String APP_SECRET = "p5lwRGEgUqH1BHgHf1XAn2vRlUamrvqf";
    public static String GRANT_TYPE = "client_credential";
    //============Mqtt param
    public static final String TOPIC = "TOPIC";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String HOSTNAME = "HOSTNAME";
    public static final String HOSTPORT = "HOSTPORT";
    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWD = "PASSWD";

    /**
     * =========mqtt===url======
     **/
    //register Device
    public static final String  URL_GET_DEVICEID = "http://smart.56iq.net:8002/device";
    //access_token
    public static final String URL_GET_TOKEN = "http://api.53iq.com/token";
    // URL-topic
    public static final String URL_MQTT = "https://api.53iq.com/message";


    //获取天气的接口
    public static final String WEATHRER_URL = "http://api.53iq.com/component/weather";
    public static final String ZHANGCHU_PACKAGE_NAME = "com.gold.palm.kitchen";

    public static final String URL_TVSERVER = "http://%s:8885/";
    public static final String URL_TVSERVER_FIND_DEVICE = "api/tvserver/findDevice";
    public static final String URL_TVSERVER_SWITCH_ON = "api/tvserver/switch/on";
    public static final String URL_TVSERVER_SWITCH_OFF = "api/tvserver/switch/off";
    public static final String URL_TVSERVER_SILENCE  = "api/tvserver/silence";
    public static final String URL_TVSERVER_RING  = "api/tvserver/ring";
    public static final String URL_TVSERVER_SOUND_DOWN  = "api/tvserver/sound/down";
    public static final String URL_TVSERVER_SOUND_UP  = "api/tvserver/sound/add";
    public static final String URL_TVSERVER_PROGRAM_PRE  = "api/tvserver/program/pre";
    public static final String URL_TVSERVER_PROGRAM_NEXT  = "api/tvserver/program/next";
    public static final String URL_TVSERVER_MENU = "api/tvserver/menu";
    public static final String URL_TVSERVER_BACK = "api/tvserver/back";
    public static final String URL_TVSERVER_OK = "api/tvserver/OK";
    public static final String URL_TVSERVER_TOP = "api/tvserver/top";
    public static final String URL_TVSERVER_BOTTOM = "api/tvserver/bottom";
    public static final String URL_TVSERVER_LEFT = "api/tvserver/left";
    public static final String URL_TVSERVER_RIGHT = "api/tvserver/right";
    public static final String URL_TVSERVER_STATE = "api/tvserver/getState";
}
