package com.jancar.settings.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.format.Time;
import android.util.Log;

import com.jancar.settings.R;
import com.jancar.settings.contract.TimeZoneEntity;
import com.jancar.settings.listener.Contract.MainContractImpl;
import com.jancar.settings.listener.Contract.TimeContractImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.jancar.settings.util.Tool.getTime;

/**
 * Created by ouyan on 2018/9/4.
 */

public class TimeModel implements TimeContractImpl.Model {
    @Override
    public void onDestroy() {

    }

    public void getdata() {

    }

    @Override
    public List<TimeZoneEntity> getNameListData(Context mContext) {
        List<TimeZoneEntity> timeZoneList = new ArrayList<>();
        try {
//将上次的数据清空，方便重新搜索

//获取信息的方法
            Resources res = mContext.getResources();
            XmlResourceParser xrp = res.getXml(R.xml.timezones);
//判断是否已经到了文件的末尾
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String name = xrp.getName();
                    if (name.equals("timezone")) {
                        TimeZoneEntity timeZoneEntity = new TimeZoneEntity();
                        timeZoneEntity.setName(xrp.getAttributeValue(1));
                        timeZoneEntity.setId(xrp.getAttributeValue(0));
                        timeZoneEntity.setTime(getTime(xrp.getAttributeValue(0)));
                        timeZoneList.add(timeZoneEntity);

                    }
                }
//搜索过第一个信息后，接着搜索下一个
                xrp.next();
            }

        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeZoneList;
    }

    @Override
    public String getTimeZone(String id, List<TimeZoneEntity> timeZoneEntityList) {
        String timeZoneName = null;
        for (TimeZoneEntity mTimeZoneEntity : timeZoneEntityList) {
            if (mTimeZoneEntity.getId().equals(id)){
                timeZoneName=mTimeZoneEntity.getName();
            }
        }
        return timeZoneName;
    }


    public String converTime(String srcTime, TimeZone timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String convertTime;

        Date result_date;
        long result_time = 0;

        // 如果传入参数异常，使用本地时间
        if (null == srcTime) {
            result_time = System.currentTimeMillis();
        } else {
            // 将输入时间字串转换为UTC时间
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
                result_date = sdf.parse(srcTime);

                result_time = result_date.getTime();
            } catch (Exception e) {
                // 出现异常时，使用本地时间
                result_time = System.currentTimeMillis();
                dspFmt.setTimeZone(TimeZone.getDefault());
                convertTime = dspFmt.format(result_time);
                return convertTime;
            }
        }

        // 设定时区
        dspFmt.setTimeZone(timezone);
        convertTime = dspFmt.format(result_time);

        Log.e("current zone:", "id=" + sdf.getTimeZone().getID()
                + "  name=" + sdf.getTimeZone().getDisplayName());

        return convertTime;
    }
    // 国际化需求,要访问当地的时区和语言,作为参数上传服务器处理一些业务,Android手机中如果想以GMT形式(GMT+08:00)得到当前时区,如下的工具类做个记录:


    /**
     * 获取当前时区 * @return
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;
    }

    /**
     * 获取当前系统语言格式 * @param mContext * @return
     */
    public static String getCurrentLanguage(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String lc = language + "_" + country;
        return lc;
    }
}