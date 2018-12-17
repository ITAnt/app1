package settings.jancar.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private static String TAG = TestMainActivity.class.getSimpleName();
    private static String CLASS_NAME = "com.jancar.settings.service.SettingsUIService";
    private static String PACKAGE_NAME = "com.jancar.settingss";
    private static String GET_SERIALNUMBER = "NvRAMA";
    private static String READ = "read";
    private static String RYPE = "Type";
    private static String WRITE = "write";
    private static String DATA = "Data";
    private static String fileLid = "fileLid";
    NetworkChangeReceiver networkChangeReceiver;
    IntentFilter intentFilter;
    private final String NVRAMAs = "android.jancar.settings.NvRAMA.read";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intents = new Intent("android.jancar.settings.NvRAMA");
        intents.putExtra("Type","read");
        sendBroadcast(intents);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.jancar.settings.NvRAMA.read");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);



    //   getThreeMethod(this,CLASS_NAME);
    }
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] buff = intent.getByteArrayExtra("Data");
            for (byte butt:buff){

                Log.w(TAG,butt+" butt");
            }
            Intent intents = new Intent("android.jancar.settings.NvRAMA");
            intents.putExtra("Type","write");
            intents.putExtra("Data",buff);
            sendBroadcast(intents);
        }
    }

    public static void getThreeMethod(Context context, String clsName) {
        try {
            Context c = context.createPackageContext(PACKAGE_NAME, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

            Class clazz = c.getClassLoader().loadClass(clsName);
            Object obj =    clazz. newInstance();
            Method getSerialNumber = clazz.getMethod(GET_SERIALNUMBER);
            boolean serial = (boolean) getSerialNumber.invoke(clazz);//机器唯一标识码
            android.util.Log.e(TAG, "serial: " + serial);

        } catch (Throwable e) {
            android.util.Log.e(TAG, "e: " + e);
            e.printStackTrace();
        }
    }
}
