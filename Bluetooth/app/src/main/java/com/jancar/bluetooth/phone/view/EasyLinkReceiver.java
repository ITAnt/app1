package com.jancar.bluetooth.phone.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @anthor Tzq
 * @time 2018/9/27 15:23
 * @describe TODO
 */
public class EasyLinkReceiver extends BroadcastReceiver {
    private String TAG = EasyLinkReceiver.class.getSimpleName();
    private final String EASY_GET_FOUS = "net.easyconn.a2dp.acquire";
    private final String EASY_LOSS_FOUS = "net.easyconn.a2dp.release";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothRequestFocus bluetoothRequestFocus = BluetoothRequestFocus.getBluetoothRequestFocusStance(context);
        Log.e(TAG, "action==" + action);
        if (action.equals(EASY_GET_FOUS)) {
            //BluetoothManager.getBluetoothManagerInstance(context).setPlayerState(true);
            if (!bluetoothRequestFocus.isNeedGainFocus()) {
                bluetoothRequestFocus.requestAudioFocus();
            }
            if (!bluetoothRequestFocus.isBTConnect()) {
//                Toast.makeText(context, "连接蓝牙之后才能在车机上听到声音", Toast.LENGTH_SHORT).show();
            }
        } else if (action.equals(EASY_LOSS_FOUS)) {
            //BluetoothManager.getBluetoothManagerInstance(context).setPlayerState(false);
            bluetoothRequestFocus.releaseAudioFocus();
        }
    }
}
