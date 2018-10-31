package com.jancar.bluetooth.phone;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.ViewConfiguration;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.util.FlyLog;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;

import java.lang.reflect.Field;

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
        /**
         * 设置Marquee不显示省略号
         */
        try {
            ViewConfiguration configuration = ViewConfiguration.get(getApplicationContext());
            Class claz = configuration.getClass();
            Field field = claz.getDeclaredField("mFadingMarqueeEnabled");
            field.setAccessible(true);
            field.set(configuration, true);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        Hawk.init(this)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                .setStorage(HawkBuilder.newSqliteStorage(this))
                .setLogLevel(LogLevel.FULL)
                .build();
    }

    @Override
    public void onTerminate() {
        Log.e("BluetoothApplication", "onTerminate===");
        BluetoothManager.getBluetoothManagerInstance(this).release();
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("BluetoothApplication", "onLowMemory===");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
