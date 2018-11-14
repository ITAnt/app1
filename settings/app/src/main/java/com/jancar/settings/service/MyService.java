package com.jancar.settings.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.jancar.JancarManager;

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
        jancarManager = (JancarManager) getSystemService(JancarManager.JAC_SERVICE);

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
      //  jancarManager.u(promptController.asBinder());
        Log.e(TAG, "onDestroy");
    }
}
