package com.jancar.bluetooth.phone.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.widget.ConnectDialog;

/**
 * @anthor Tzq
 * @time 2018/10/12 11:29
 * @describe 蓝牙状态全局广播
 */
public class BTConnStatusReceiver extends BroadcastReceiver {
    ConnectDialog connectDialog;
    BluetoothManager bluetoothManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(context.getApplicationContext());
        boolean connect = bluetoothManager.isConnect();
        if (!connect) {
//            showDialog(context);

        }
    }

    private void showDialog(final Context context) {
        if (connectDialog == null) {
            connectDialog = new ConnectDialog(context.getApplicationContext(), R.style.AlertDialogCustom);
        }
        connectDialog.setCanelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDialog.dismiss();
            }
        });
        connectDialog.go2SettingOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.jancar.settingss", "com.jancar.settings.view.activity.MainActivity");
                intent.putExtra("position", 1);
                context.startActivity(intent);
                connectDialog.dismiss();
            }
        });
        connectDialog.show();
    }




}
