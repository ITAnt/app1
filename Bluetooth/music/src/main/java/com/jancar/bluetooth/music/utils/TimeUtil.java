package com.jancar.bluetooth.music.utils;

/**
 * @anthor Tzq
 * @time 2018/9/4 18:19
 * @describe 时间工具类
 */
public class TimeUtil {
    public static String millSeconds2readableTime(int millseconds) {

        StringBuffer dateBf = new StringBuffer();
        int totalSeconds = millseconds / 1000;

        // HOUR_OF_DAY:24Hour
        int hour = (totalSeconds / 60) / 60;
        if (hour <= 9) {
            dateBf.append("0").append(hour + ":");
        } else {
            dateBf.append(hour + ":");
        }
        // Minute
        int minute = (totalSeconds / 60) % 60;
        if (minute <= 9) {
            dateBf.append("0").append(minute + ":");
        } else {
            dateBf.append(minute + ":");
        }
        // Seconds
        int second = totalSeconds % 60;
        if (second <= 9) {
            dateBf.append("0").append(second);
        } else {
            dateBf.append(second);
        }
        return dateBf.toString();
    }

    public static String updataCallTime(int mCallTime) {
        String time;
        if (mCallTime < 0) {
            return null;
        }
        int hour = mCallTime / 3600;
        int min = (mCallTime - hour * 3600) / 60;
        int seconds = mCallTime % 60;
        if (hour < 10) {
            time = "0";
            time += Integer.toString(hour);
        } else {
            time = Integer.toString(hour);
        }
        time += ":";
        if (min < 10) {
            time += "0";
            time += Integer.toString(min);
        } else {
            time += Integer.toString(min);
        }
        time += ":";
        if (seconds < 10) {
            time += "0";
            time += Integer.toString(seconds);
        } else {
            time += Integer.toString(seconds);
        }
        return time;
    }
}
