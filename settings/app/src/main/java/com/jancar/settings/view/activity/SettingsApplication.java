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
        SettingManager.getSettingManager(this);
        BluetoothSettingManager.getBluetoothSettingManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SettingManager.releaseSettingManager();
        BluetoothSettingManager.getBluetoothSettingManager(this).release();
    }
}