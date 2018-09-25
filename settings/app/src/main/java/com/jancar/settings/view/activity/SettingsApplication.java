package com.jancar.settings.view.activity;

import android.app.Application;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.settings.lib.SettingManager;

/**
 * Created by ouyan on 2018/9/25.
 */

public class SettingsApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SettingManager.getSettingManager(this);
        BluetoothManager.getBluetoothManagerInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        BluetoothManager.getBluetoothManagerInstance(this).release();
        SettingManager.releaseSettingManager();
    }
}