package com.ancar.suspension;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;

/**
 * @anthor Tzq
 * @time 2018/10/9 9:18
 * @describe TODO
 */
public class SuspensionApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                .setStorage(HawkBuilder.newSqliteStorage(this))
                .setLogLevel(LogLevel.FULL)
                .build();
    }
}
