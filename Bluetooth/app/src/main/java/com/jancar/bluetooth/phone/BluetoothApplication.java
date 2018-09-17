package com.jancar.bluetooth.phone;

import android.app.Application;

import com.jancar.bluetooth.lib.BluetoothManager;

/**
 * @anthor Tzq
 * @time 2018/8/27 11:33
 * @describe application
 */
public class BluetoothApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager.getBluetoothManagerInstance(this);
    }

    @Override
    public void onTerminate() {
        BluetoothManager.getBluetoothManagerInstance(this).release();
        super.onTerminate();
    }
}
