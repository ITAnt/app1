package com.jancar.settings.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SettingsReceiver extends BroadcastReceiver{
    private String TAG = SettingsReceiver.class.getSimpleName();
    private final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"action=="+action);
        if(action.equals(BOOT_COMPLETE)){
            Intent service = new Intent();
            service.setClassName("com.jancar.settingss","com.jancar.settings.service.SettingsUIService");
            context.startService(service);
        }
    }
}
