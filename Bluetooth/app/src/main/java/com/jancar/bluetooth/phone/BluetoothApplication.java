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
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.lang.reflect.Field;

/**
 * @anthor Tzq
 * @time 2018/8/27 11:33
 * @describe application
 */
public class BluetoothApplication extends Application {
    private static final String TAG = "BluetoothApplication";
    private RefWatcher refWatcher;

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        BluetoothApplication leakApplication = (BluetoothApplication) context.getApplicationContext();
        return leakApplication.refWatcher;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate===");
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
//        BlockCanary.install(this, new BlockCanaryContext()).start();
        refWatcher = setupLeakCanary();
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
        Log.e(TAG, "attachBaseContext===");
        MultiDex.install(this);
    }

}
