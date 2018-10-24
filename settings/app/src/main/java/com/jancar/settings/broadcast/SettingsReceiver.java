package com.jancar.settings.broadcast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jancar.settings.suspension.OverlayMenuService;
import com.jancar.settings.suspension.utils.Contacts;
import com.orhanobut.hawk.Hawk;

public class SettingsReceiver extends BroadcastReceiver {
    private String TAG = SettingsReceiver.class.getSimpleName();
    private final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println("收到广播了");
         Log.e(TAG, "action==" + action);

        if (action.equals(BOOT_COMPLETE)) {
            Log.e(TAG, "");
            Log.w(TAG, "sdf");
            Intent service = new Intent();
            service.setClassName("com.jancar.settingss", "com.jancar.settings.service.SettingsUIService");
            context.startService(service);
            boolean isOpen = Hawk.get(Contacts.ISOPEN_OVERLAY, false);
            if (isOpen) {
                Intent services = new Intent();
                services.setClassName("com.jancar.settingss", "com.jancar.settings.suspension.OverlayMenuService");
                context.startService(services);
                // context.startService(new Intent(context, OverlayMenuService.class));
            }
            Log.w(TAG, "sdf" + isOpen);
        }

    }
}
