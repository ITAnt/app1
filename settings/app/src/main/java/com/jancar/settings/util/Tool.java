package com.jancar.settings.util;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.format.Time;
import android.view.Window;
import android.view.WindowManager;

import java.util.TimeZone;

import static android.provider.Settings.Global.AUTO_TIME;
import static android.provider.Settings.System.TIME_12_24;

/**
 * Created by ouyan on 2018/9/6.
 */

public class Tool {
    public static void setDialogParam(Dialog dialog, int width, int height) {

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.width = width; //
        lp.height = height; //
        dialogWindow.setAttributes(lp);

    }




    //通过时区的id获得当时的时间
    public static String getTime(String id) {
        TimeZone tz = TimeZone.getTimeZone(id);
        String s = tz.getDisplayName(false, TimeZone.SHORT);
//String s = "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT)
//+ " Timezon id :: " + tz.getID();
        Time time = new Time(tz.getID());
        time.setToNow();

        int year = time.year;
        int month = time.month;
        int day = time.monthDay;
        int minute = time.minute;
        int hour = time.hour;
        int sec = time.second;
        String srcTIme = year +
                "年 " + (month + 1) +
                "月 " + day +
                "日 " + hour +
                "时 " + minute +
                "分 " + sec +
                "秒";

        return tz.getDisplayName(false, TimeZone.SHORT);
    }

    public static void set24Hour(Context mContext, String value) {
        ContentResolver cv = mContext.getContentResolver();
        //    android.provider.Settings.System.putString(cv, Settings.Global.AUTO_TIME, value);
        Settings.System.putString(cv,
                TIME_12_24,
                value);

   /*     Settings.Global.putInt(cv, Settings.Global.AUTO_TIME,
                value);*/
    }

    public static void setAutoTime(Context mContext, boolean isAutoTime) {
        ContentResolver cv = mContext.getContentResolver();
    /*    int autoTime = 0;
        try {
            autoTime = Settings.Global.getInt(cv, AUTO_TIME);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }*/
        Settings.Global.putInt(cv,
                Settings.Global.AUTO_TIME, !isAutoTime ? 1 : 0); //1：设置为On； 0：设置为Off

   /*     Settings.Global.putInt(cv, Settings.Global.AUTO_TIME,
                value);*/
    }

    // 需要添加权限<uses-permission android:name="android.permission.SET_TIME_ZONE" />
    public static void setChinaTimeZone(Context context, String id) {
       /* TimeZone.getTimeZone("GMT+8");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));*/
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone(id);// Asia/Taipei//GMT+08:00
    }
}
