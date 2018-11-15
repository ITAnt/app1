package com.jancar.settings.view.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.jancar.bluetooth.lib.BluetoothSettingManager;
import com.jancar.settings.lib.SettingManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by ouyan on 2018/9/25.
 */

public class SettingsApplication extends Application {
    private RefWatcher refWatcher;

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        SettingsApplication leakApplication = (SettingsApplication) context.getApplicationContext();
        return leakApplication.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = setupLeakCanary();
        SettingManager.getSettingManager(this);
        BluetoothSettingManager.getBluetoothSettingManager(this);

//        Hawk.init(this)
//                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
//                .setStorage(HawkBuilder.newSqliteStorage(this))
//                .setLogLevel(LogLevel.FULL)
//                .build();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        SettingManager.releaseSettingManager();
        BluetoothSettingManager.getBluetoothSettingManager(this).release();
    }
}