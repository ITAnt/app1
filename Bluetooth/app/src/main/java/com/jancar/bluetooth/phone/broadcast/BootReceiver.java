package com.jancar.bluetooth.phone.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @anthor Tzq
 * @time 2018/11/20 12:29
 * @describe TODO
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BOOT_COMPLETE)) {
            Log.e(TAG, "BOOT_COMPLETE==");
            Intent service = new Intent();
            service.setClassName("com.jancar.bluetooth.phone", "com.jancar.bluetooth.phone.view.BTUIService");
            context.startService(service);
        }
    }
}
