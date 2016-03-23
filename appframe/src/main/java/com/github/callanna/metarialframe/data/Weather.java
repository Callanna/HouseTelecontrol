package com.github.callanna.metarialframe.data;

/**
 * Created by Callanna on 2015/12/18.
 */
public class Weather {
    public DataEntity data;
    public int code;
    public static class DataEntity {
        public Pm25Entity Pm25;

        public RealtmpEntity realtmp;

        public WeatherInfoEntity weatherInfo;

        public String status;

        private class Pm25Entity {
            public String value;
            public String uptime;
        }

        private class RealtmpEntity {
            public String value;
            public String uptime;
        }

        private class WeatherInfoEntity {
            public String weather3;
            public String temp3;
            public String weather1;
            public String weather2;
            public String temp2;
            public String date_y;
            public String temp1;
            public String city;
            public String index_d;
            public String week;
        }
    }
}
