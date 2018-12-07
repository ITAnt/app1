package com.jancar.settings.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.jancar.JancarManager;

import com.jancar.settings.R;
import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.util.GPS;

import static com.jancar.settings.suspension.utils.Contacts.CLASS_NAME;
import static com.jancar.settings.suspension.utils.Contacts.PACKAGE_NAME;
import static com.mediatek.aee.ExceptionLog.TAG;

public class MyService extends Service {
    public MyService() {
    }
    private JancarManager jancarManager;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "startForegroundService==");
            startServiceForeground();
        }
        jancarManager = (JancarManager) getSystemService(JancarManager.JAC_SERVICE);

    }
    /**
     * 适配8.0 开启服务 (context.startForegroundService(intent);)
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startServiceForeground() {
        Log.e(TAG, "startServiceForeground==");
        // service的onCreate
        NotificationChannel channel = new NotificationChannel("im_channel_id", "System", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, "im_channel_id")
                .setSmallIcon(R.mipmap.ic_settings)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentText("BTUIService")  // the contents of the entry
                .build();

        startForeground(1, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand = " + intent);
        if (intent != null) {
            String PackageName = intent.getStringExtra(PACKAGE_NAME);
            String ClassName = intent.getStringExtra(CLASS_NAME);
            if (PackageName != null && ClassName != null) {
                jancarManager.registerPage(Contacts.GPS, PackageName, ClassName, false, false);
                Log.w("SettingsUIService", PackageName);
                Log.w("SettingsUIService", ClassName);
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        jancarManager=null;

        //  jancarManager.u(promptController.asBinder());
        Log.e(TAG, "onDestroy");
    }
}
