package com.jancar.settings.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.jancar.settings.SetDateTime;

public class SetDataTimeService extends Service {
    private final static String TAG = "com.jancar.settings.service.SetDateTimeService";

    @SuppressLint("LongLogTag")
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "OnBind-ok");
        return new MyBinder();
    }

    private class MyBinder extends SetDateTime.Stub{
        @SuppressLint("LongLogTag")
        @Override
        public int setTimeToSystem(long MilliSec) throws RemoteException{
            if( SystemClock.setCurrentTimeMillis(MilliSec)){
                Log.v(TAG, "SetTimeToSystem-ok");
                return 0;
            }
            else{
                Log.v(TAG, "SetTimeToSystem-fail");
                return -1;
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void setDateTime(int year, int month, int day, int hour,int minute) throws RemoteException {
            /********获得root权限********/
            File rootUser = new File("/system/xbin/ru");
            Process myProcess=null;
            DataOutputStream os = null;
            try{
                if(rootUser.exists()) {
                    myProcess=Runtime.getRuntime().exec(rootUser.getAbsolutePath());
                } else {
                    myProcess=Runtime.getRuntime().exec("su");
                }
                os = new DataOutputStream(myProcess.getOutputStream());
                os.writeBytes("chmod 666 /dev/alarm" + "\n");
                os.writeBytes("exit $?\n");
                try {
                    myProcess.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG, "getroot-fail");
            }finally {
                if(os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            /**********设置系统时间***********/
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month-1);
            c.set(Calendar.DAY_OF_MONTH, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            long when = c.getTimeInMillis();
            if (when / 1000 < Integer.MAX_VALUE) {
                if(SystemClock.setCurrentTimeMillis(when)){
                    Log.v(TAG, "setDateAndTime-ok");
                }else{
                    Log.v(TAG, "setDateAndTime-fail");
                }
            }
            long now = Calendar.getInstance().getTimeInMillis();
            if(now - when > 1000)
                Log.v(TAG, "setDateAndTime-fail");
        }
    }
}
