package com.jancar.bluetooth.phone.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.util.Constants;

/**
 * @anthor Tzq
 * @time 2018/9/27 15:23
 * @describe TODO
 */
public class EasyLinkReceiver extends BroadcastReceiver {
    private static final String TAG = "EasyLinkReceiver";
    private static final String EASY_GET_FOUS = "net.easyconn.a2dp.acquire";//获取蓝牙声道（进入投屏时）
    private static final String EASY_LOSS_FOUS = "net.easyconn.a2dp.release";//释放蓝牙声道（退出 投屏时
    private static final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";
    private static final String BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";
    private static final String ACTION_BT_STATE_CHANGE = "com.jancar.bt.state.change";
    private static final String PREBOOT_IPO = "android.intent.action.ACTION_PREBOOT_IPO";
    private static final String EASY_BLUETHOTH_CONNECTED = "net.easyconn.bt.connected";//蓝牙连接
    private static final String EASY_BLUETHOTH_DISCONNECTED = "net.easyconn.bt.notconnected ";//蓝牙断开
    private static final String EASY_EXIT_ANDRID = "net.easyconn.android.mirror.out";//退出 Android 投屏
    private static final String EASY_EXIT_ANDROID_BACK = "net.easyconn.screen.pause";//退出Android投屏，进入后台
    private static final String EASY_EXIT_IPHONE = "net.easyconn.iphone.inner.mirror.out";//退出iphone投屏

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothRequestFocus bluetoothRequestFocus = BluetoothRequestFocus.getBluetoothRequestFocusStance(context);
        Log.e(TAG, "action==" + action);
        if (action.equals(BECOMING_NOISY) || action.equals(BOOT_COMPLETE)) {
            Intent service = new Intent();
            service.setClassName(Constants.BTUISERVICE_PACKAGENAME, Constants.BTUISERVICE_CLASSNAME);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(TAG, "startForegroundService==");
                context.startForegroundService(service);
            } else {
                Log.e(TAG, "startService==");
                context.startService(service);
            }
//            context.startService(service);

        } else if (action.equals(EASY_LOSS_FOUS)) {
            //BluetoothManager.getBluetoothManagerInstance(context).setPlayerState(false);
//            bluetoothRequestFocus.releaseAudioFocus();

        } else if (action.equals(EASY_GET_FOUS)) {
//                BluetoothManager.getBluetoothManagerInstance(context).setPlayerState(true);
            if (!bluetoothRequestFocus.isNeedGainFocus()) {
                if (!bluetoothRequestFocus.getPlayStatus()) {
                    bluetoothRequestFocus.setBTPlayStatus(true);
                }
                bluetoothRequestFocus.requestAudioFocus();
            }
            if (!bluetoothRequestFocus.isBTConnect()) {
//                Toast.makeText(context, "连接蓝牙之后才能在车机上听到声音", Toast.LENGTH_SHORT).show();
            }
        } else if (action.equals(EASY_BLUETHOTH_CONNECTED)) {
            //蓝牙连接
        } else if (action.equals(EASY_BLUETHOTH_DISCONNECTED)) {
            //蓝牙断开
        } else if (action.equals(EASY_EXIT_ANDRID)) {
            //退出安卓投屏
            bluetoothRequestFocus.setBTPlayStatus(false);
            bluetoothRequestFocus.releaseAudioFocus();
        } else if (action.equals(EASY_EXIT_IPHONE)) {
            //iphone退出投屏
            bluetoothRequestFocus.setBTPlayStatus(false);
            bluetoothRequestFocus.releaseAudioFocus();
        } else if (action.equals(EASY_EXIT_ANDROID_BACK)) {
            //退出Android投屏，进入后台
            bluetoothRequestFocus.setBTPlayStatus(false);
            bluetoothRequestFocus.releaseAudioFocus();
        }
    }
}
