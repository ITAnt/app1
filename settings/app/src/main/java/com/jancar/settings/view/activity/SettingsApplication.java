package com.jancar.settings.view.activity;

import android.app.Application;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothSettingManager;
import com.jancar.settings.lib.SettingManager;

/**
 * Created by ouyan on 2018/9/25.
 */

public class SettingsApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothSettingManager.getBluetoothSettingManager(this);
        SettingManager.getSettingManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
       BluetoothSettingManager.getBluetoothSettingManager(this).release();
        SettingManager.releaseSettingManager();
    }
}