package com.jancar.settings.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import java.util.Iterator;

import static android.provider.Settings.Global.AUTO_TIME;
import static com.jancar.settings.util.Tool.setAutoTime;
import static com.jancar.settings.view.fragment.TimeFragment.AUTO_TIME_GPS;

public class GPS {
    private LocationManager locationManager = null;
    private Context context;
    private boolean isTest;


    public void openGPSSettings(Context context,int value) {
        isTest = true;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
        toggleGps();
       @SuppressLint("MissingPermission")
       boolean b = locationManager.addGpsStatusListener(listener);
        Log.i("ygl", "openGPSSettings==" + b);
    }

    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i("ygl", "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i("ygl", "卫星状态改变");
                    //获取当前状态
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    setAutoTime(context, false,1);
                    ContentResolver cv = context.getContentResolver();
                    int autoTime = 0;
                    try {
                        autoTime = Settings.Global.getInt(context.getContentResolver(), AUTO_TIME_GPS);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (autoTime==0){
                        try {
                            autoTime = Settings.Global.getInt(context.getContentResolver(), AUTO_TIME);
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (count >=3) {


                        if (autoTime!=0){
                            Settings.Global.putInt(cv,
                                    AUTO_TIME_GPS, 1); //1：设置为On； 0：设置为Off
                            releaseGps();

                         /*   Settings.Secure.setLocationProviderEnabled(context. getContentResolver(), LocationManager.GPS_PROVIDER, false );
                            Settings.Secure.setLocationProviderEnabled(context. getContentResolver(), LocationManager.GPS_PROVIDER, true );
              */          }

                    }else {
                        if (autoTime!=0){
                            Settings.Global.putInt(cv,
                                    AUTO_TIME, 1); //1：设置为On； 0：设置为Off

                         /*   Settings.Secure.setLocationProviderEnabled(context. getContentResolver(), LocationManager.GPS_PROVIDER, false );
                            Settings.Secure.setLocationProviderEnabled(context. getContentResolver(), LocationManager.GPS_PROVIDER, true );
              */          }
                        //
                    }
                    Log.i("ygl", "搜索到：" + count + "颗卫星");
                    //Toast.makeText(context,"搜索到：" + count + "颗卫星",0).show();
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i("ygl", "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:

                    Settings.Secure.setLocationProviderEnabled(context. getContentResolver(), LocationManager.GPS_PROVIDER, true );
                    //  Settings.Secure.setLocationProviderEnabled(context. getContentResolver(), LocationManager.GPS_PROVIDER, true );
                    Log.i("ygl", "定位结束");
                    break;
                default:
                    Log.i("ygl", "GpsStatus default");
                    break;
            }
        }

        ;
    };


    public boolean getGpsState() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void toggleGps() {
        ContentResolver resolver = context.getContentResolver();
        boolean enabled = getGpsState();
        String value = LocationManager.PROVIDERS_CHANGED_ACTION;
        if (!enabled) {
            value = "+" + LocationManager.GPS_PROVIDER;
        }
        Settings.Secure.putString(resolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED, value);
    }

    public void releaseGps(){
      //  mHandler.removeMessages(1);
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_STOPPED);
        locationManager.removeGpsStatusListener(listener);
    }
}
