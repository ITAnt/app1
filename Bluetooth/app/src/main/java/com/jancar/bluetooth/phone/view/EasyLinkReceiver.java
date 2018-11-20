package com.jancar.bluetooth.phone.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @anthor Tzq
 * @time 2018/9/27 15:23
 * @describe TODO
 */
public class EasyLinkReceiver extends BroadcastReceiver {
    private static final String TAG = "EasyLinkReceiver";
    private static final String EASY_GET_FOUS = "net.easyconn.a2dp.acquire";
    private static final String EASY_LOSS_FOUS = "net.easyconn.a2dp.release";
    private static final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";
    private static final String BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";
    private static final String ACTION_BT_STATE_CHANGE = "com.jancar.bt.state.change";
    private static final String PREBOOT_IPO = "android.intent.action.ACTION_PREBOOT_IPO";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothRequestFocus bluetoothRequestFocus = BluetoothRequestFocus.getBluetoothRequestFocusStance(context);
        Log.e(TAG, "action==" + action);
        if (action.equals(BECOMING_NOISY)) {
            Intent service = new Intent();
            service.setClassName("com.jancar.bluetooth.phone", "com.jancar.bluetooth.phone.view.BTUIService");
            context.startService(service);

        } else if (action.equals(EASY_LOSS_FOUS)) {
            //BluetoothManager.getBluetoothManagerInstance(context).setPlayerState(false);
//            bluetoothRequestFocus.releaseAudioFocus();

        } else if (action.equals(EASY_GET_FOUS)) {
            //BluetoothManager.getBluetoothManagerInstance(context).setPlayerState(true);
            if (!bluetoothRequestFocus.isNeedGainFocus()) {
                bluetoothRequestFocus.requestAudioFocus();
            }
            if (!bluetoothRequestFocus.isBTConnect()) {
//                Toast.makeText(context, "连接蓝牙之后才能在车机上听到声音", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
