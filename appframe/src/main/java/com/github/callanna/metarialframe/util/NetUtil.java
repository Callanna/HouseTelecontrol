package com.github.callanna.metarialframe.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * Created by Callanna on 2015/12/17.
 */
public class NetUtil {
    private static final String TAG = NetUtil.class.getSimpleName();

    public enum NetType {
        None(1),
        Mobile(2),
        Wifi(4),
        Other(8);

        NetType(int value) {
            this.value = value;
        }

        public int value;
    }

    public enum NetWorkType {
        UnKnown(-1),
        Wifi(1),
        Net2G(2),
        Net3G(3),
        Net4G(4);

        NetWorkType(int value) {
            this.value = value;
        }

        public int value;
    }

    /**
     * 获取ConnectivityManager
     */
    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 获取ConnectivityManager
     */
    public static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 判断网络连接是否有效（此时可传输数据）。
     *
     * @return boolean 不管wifi，还是mobile net，只有当前在连接状态（可有效传输数据）才返回true,反之false。
     */
    public static boolean isConnected(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    /**
     * 判断有无网络正在连接中（查找网络、校验、获取IP等）。
     *
     * @return boolean 不管wifi，还是mobile net，只有当前在连接状态（可有效传输数据）才返回true,反之false。
     */
    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo[] nets = getConnectivityManager(context).getAllNetworkInfo();
        if (nets != null) {
            for (NetworkInfo net : nets) {
                if (net.isConnectedOrConnecting()) { return true; }
            }
        }
        return false;
    }

    public static NetType getConnectedType(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        if (net != null) {
            switch (net.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetType.Wifi;
                case ConnectivityManager.TYPE_MOBILE:
                    return NetType.Mobile;
                default:
                    return NetType.Other;
            }
        }
        return NetType.None;
    }

    /**
     * 是否存在有效的WIFI连接
     */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == ConnectivityManager.TYPE_WIFI && net.isConnected();
    }

    /**
     * 是否存在有效的移动连接
     *
     * @return boolean
     */
    public static boolean isMobileConnected(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == ConnectivityManager.TYPE_MOBILE && net.isConnected();
    }

    /**
     * 检测网络是否为可用状态
     */
    public static boolean isAvailable(Context context) {
        return isWifiAvailable(context) || (isMobileAvailable(context) && isMobileEnabled(context));
    }

    /**
     * 判断是否有可用状态的Wifi，以下情况返回false：
     * 1. 设备wifi开关关掉;
     * 2. 已经打开飞行模式；
     * 3. 设备所在区域没有信号覆盖；
     * 4. 设备在漫游区域，且关闭了网络漫游。
     *
     * @return boolean wifi为可用状态（不一定成功连接，即Connected）即返回ture
     */
    public static boolean isWifiAvailable(Context context) {
        NetworkInfo[] nets = getConnectivityManager(context).getAllNetworkInfo();
        if (nets != null) {
            for (NetworkInfo net : nets) {
                if (net.getType() == ConnectivityManager.TYPE_WIFI) { return net.isAvailable(); }
            }
        }
        return false;
    }

    /**
     * 判断有无可用状态的移动网络，注意关掉设备移动网络直接不影响此函数。
     * 也就是即使关掉移动网络，那么移动网络也可能是可用的(彩信等服务)，即返回true。
     * 以下情况它是不可用的，将返回false：
     * 1. 设备打开飞行模式；
     * 2. 设备所在区域没有信号覆盖；
     * 3. 设备在漫游区域，且关闭了网络漫游。
     *
     * @return boolean
     */
    public static boolean isMobileAvailable(Context context) {
        NetworkInfo[] nets = getConnectivityManager(context).getAllNetworkInfo();
        if (nets != null) {
            for (NetworkInfo net : nets) {
                if (net.getType() == ConnectivityManager.TYPE_MOBILE) { return net.isAvailable(); }
            }
        }
        return false;
    }

    /**
     * 设备是否打开移动网络开关
     *
     * @return boolean 打开移动网络返回true，反之false
     */
    public static boolean isMobileEnabled(Context context) {
        try {
            Method getMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            getMobileDataEnabledMethod.setAccessible(true);
            return (Boolean) getMobileDataEnabledMethod.invoke(getConnectivityManager(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 反射失败，默认开启
        return true;
    }

    /**
     * 打印当前各种网络状态
     *
     * @return boolean
     */
    public static boolean printNetworkInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo in = connectivity.getActiveNetworkInfo();
            LogUtil.i(TAG, "-------------$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$-------------");
            LogUtil.i(TAG, "getActiveNetworkInfo: " + in);
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    // if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]isAvailable : " + info[i].isAvailable());
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]isConnected : " + info[i].isConnected());
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]isConnectedOrConnecting : " + info[i].isConnectedOrConnecting());
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]: " + info[i]);
                    // }
                }
                LogUtil.i(TAG, "\n");
            } else {
                LogUtil.i(TAG, "getAllNetworkInfo is null");
            }
        }
        return false;
    }

    /**
     * get connected network type by {@link ConnectivityManager}
     *
     * such as WIFI, MOBILE, ETHERNET, BLUETOOTH, etc.
     *
     * @return {@link ConnectivityManager#TYPE_WIFI}, {@link ConnectivityManager#TYPE_MOBILE},
     * {@link ConnectivityManager#TYPE_ETHERNET}...
     */
    public static int getConnectedTypeINT(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        if (net != null) {
            if (LogUtil.isPrint) {
                LogUtil.i(TAG, "NetworkInfo: " + net.toString());
            }
            return net.getType();
        }
        return -1;
    }

    /**
     * get network type by {@link TelephonyManager}
     *

     */
    public static int getTelNetworkTypeINT(Context context) {
        return getTelephonyManager(context).getNetworkType();
    }

    public static NetWorkType getNetworkType(Context context) {
        int type = getConnectedTypeINT(context);
        switch (type) {
            case ConnectivityManager.TYPE_WIFI:
                return NetWorkType.Wifi;
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_DUN:
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
                int teleType = getTelephonyManager(context).getNetworkType();
                switch (teleType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetWorkType.Net2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetWorkType.Net3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetWorkType.Net4G;
                    default:
                        return NetWorkType.UnKnown;
                }
            default:
                return NetWorkType.UnKnown;
        }
    }


    public static String   getMacAddress(Context context){
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getMacAddress().toString();
    }

    public static String getIpV4Address(Context context){
        //获取wifi服务
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        return intToIp(ipAddress);
    }
    public static String intToIp(int i) {
        return (i & 0xFF ) + "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ( i >> 24 & 0xFF) ;
    }


//=======================================================  流量统计工具类======
    private static int UNSUPPORTED = -1;
    /**
     * 首次接受到的字节数
     */
    private static long mPreRxBytes = UNSUPPORTED;

    /**
     * 计算当前网速
     */
    public static String countCurRate() {
        String curRate = "-1B/s";
        try {
            // 当前接受到的字节总数
            long curRxBytes = TrafficStats.getTotalRxBytes();
            // LogUtil.v(TAG, "接收到的总字节数"+curRxBytes);
            // 这次接受到的字节数
            if (mPreRxBytes == UNSUPPORTED) {
                mPreRxBytes = curRxBytes;
            }
            long curRateBytes = curRxBytes - mPreRxBytes;
            // 更新上一次接收到的字节总数
            mPreRxBytes = curRxBytes;
            if (curRateBytes < 1024) {
                // LogUtil.v(TAG, "当前网速" + curRateBytes + "B/s");
                curRate = curRateBytes + "B/s";
            }
            if (curRateBytes >= 1024 && curRateBytes < 1024 * 1024) {
                // LogUtil.v(TAG, "当前网速" + curRateBytes / 1024 + "K/s");
                curRate = curRateBytes / 1024 + "K/s";
            }
            if (curRateBytes >= 1024 * 1024) {
                // LogUtil.v(TAG, "当前网速" + curRateBytes / (1024 * 1024) +
                // "M/s");
                curRate = curRateBytes / (1024 * 1024) + "M/s";
            }

        } catch (NoClassDefFoundError e) {
            mPreRxBytes = UNSUPPORTED;
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            mPreRxBytes = UNSUPPORTED;
            e.printStackTrace();
            return "";
        }
        return curRate;
    }

    /**
     * 计算当前的网速
     *
     * @param startPos
     * @param endtime
     * @return
     */
    public static long getAverageRateSpeed(long startPos, long endtime) {
        long averageRateSpeed;
        try {
            // 当前接受到的字节总数
            long curRateBytes = TrafficStats.getTotalRxBytes() - startPos;
            double time = ((double) System.currentTimeMillis() - (double) endtime);
            averageRateSpeed = (int) ((double) curRateBytes / time);
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return averageRateSpeed;
    }

}
