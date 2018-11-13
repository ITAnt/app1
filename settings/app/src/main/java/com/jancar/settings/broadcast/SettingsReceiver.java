package com.jancar.settings.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.util.SPUtil;

public class SettingsReceiver extends BroadcastReceiver {
    private String TAG = SettingsReceiver.class.getSimpleName();
    private final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println("收到广播了");
        Log.e(TAG, "action==" + action);

        if (action.equals(BOOT_COMPLETE)) {

            Intent serviced = new Intent();
            serviced.setClassName("com.jancar.settingss", "com.jancar.settings.MyService");
            context.startService(serviced);
            Log.w(TAG,"serviced");
            Intent service = new Intent();
            service.setClassName("com.jancar.settingss", "com.jancar.settings.service.SettingsUIService");
            context.startService(service);
//            boolean isOpen = Hawk.get(Contacts.ISOPEN_OVERLAY, false);
            boolean isOpen = SPUtil.getBoolean(context, Contacts.ISOPEN_OVERLAY, false);
            if (isOpen) {
                Intent services = new Intent();
                services.setClassName("com.jancar.settingss", "com.jancar.settings.suspension.OverlayMenuService");
                context.startService(services);
            }
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
