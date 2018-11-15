package com.jancar.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.util.SPUtil;

public class RadioReceiver extends BroadcastReceiver {
    private String TAG = RadioReceiver.class.getSimpleName();
    private final String BOOT_COMPLETE = "com.jancar.services.ready";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println("收到广播了");
        Log.e(TAG, "action==" + action);

        if (action.equals(BOOT_COMPLETE)) {

        }

    }
}
