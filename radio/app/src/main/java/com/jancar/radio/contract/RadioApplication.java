package com.jancar.radio.contract;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.jancar.radio.greendao.DaoMaster;
import com.jancar.radio.greendao.DaoSession;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class RadioApplication extends Application {
    public static boolean InitDone;
    public static int PlayState;
    public static boolean RDSIsON;
    public static boolean RadioIsRun;
    protected static int ThemeType;
    public static boolean isRadioBeforeTA;
    public static boolean isThereRDSByDisplay;
    private static RadioApplication pThis;
    private static DaoSession daoSession;
    private RefWatcher refWatcher;
    static {
        RadioApplication.PlayState = 0;
        RadioApplication.RadioIsRun = false;
        RadioApplication.isRadioBeforeTA = false;
        RadioApplication.isThereRDSByDisplay = true;
        RadioApplication.InitDone = true;
        RadioApplication.RDSIsON = false;
        RadioApplication.ThemeType = 1;
        RadioApplication.pThis = null;
    }
    
    public static RadioApplication getApplication() {
        return RadioApplication.pThis;
    }
    
    public void onCreate() {
        super.onCreate();
        refWatcher = setupLeakCanary();
        RadioApplication.pThis = this;
        setupDatabase();
    }
    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }
    private void setupDatabase() {
        //创建数据库shop.db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "shop.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
