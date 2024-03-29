package com.jancar.settings.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.jancar.settings.service.MyService;
import com.jancar.settings.suspension.entry.ShowAndHideEntry;
import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.util.SPUtil;

import org.greenrobot.eventbus.EventBus;

public class SettingsReceiver extends BroadcastReceiver {
    private String TAG = SettingsReceiver.class.getSimpleName();
    private final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println("收到广播了");
        Log.e(TAG, "action==" + action);
        Log.e(TAG, "收到广播了");

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||  android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            Log.e(TAG, "action=" + intent.getAction());
            Intent serviced = new Intent();
            serviced.setClassName("com.jancar.settingss", "com.jancar.settings.MyService");
         /*   context.startService(serviced);*/
            Log.w(TAG, "serviced");
            Intent service = new Intent();
            service.setClassName("com.jancar.settingss", "com.jancar.settings.service.SettingsUIService");

            //================悬浮按钮服务 start
            boolean isOpen = SPUtil.getBoolean(context, Contacts.ISOPEN_OVERLAY, false);
            Log.e(TAG, "isOpen==" + isOpen);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(TAG, "startForegroundService==");

                context.startForegroundService(serviced);
                context.startForegroundService(service);
            } else {
                context.startService(service);
                context.startService(serviced);
                Log.e(TAG, "startService==");
            }
            if (isOpen) {
                Log.e(TAG, "services==");
                Intent services = new Intent();
                services.setClassName("com.jancar.settingss", "com.jancar.settings.suspension.OverlayMenuService");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.e(TAG, "startForegroundService==");

                    context.startForegroundService(services);
                } else {
                    context.startService(services);
                    Log.e(TAG, "startService==");
                }
            }
            //================悬浮按钮服务 end
            SharedPreferences sharedPreferences = context.getSharedPreferences("FirstRun", 0);
            Boolean first_run = sharedPreferences.getBoolean("First", true);
            if (first_run) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                sharedPreferences.edit().putBoolean("First", false).commit();
            }
            Log.w(TAG, "sdf" + isOpen);
        }

    }
}
