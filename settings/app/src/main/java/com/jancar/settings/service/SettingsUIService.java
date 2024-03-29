package com.jancar.settings.service;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.JancarManager;
import com.jancar.common.ExtraData;
import com.jancar.model.DisplayController;
import com.jancar.prompt.PromptController;
import com.jancar.settings.R;
import com.jancar.settings.lib.SettingManager;
import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.util.GPS;
import com.jancar.settings.util.NvRAMAgent;
import com.jancar.settings.view.activity.MainActivity;
import com.jancar.settings.view.activity.SettingsApplication;
import com.jancar.state.JacState;

import java.util.HashMap;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE;
import static android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
import static android.view.WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW;
import static android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static com.jancar.settings.suspension.utils.Contacts.CLASS_NAME;
import static com.jancar.settings.suspension.utils.Contacts.PACKAGE_NAME;
import static com.mediatek.aee.ExceptionLog.TAG;


public class SettingsUIService extends Service implements SeekBar.OnSeekBarChangeListener {
    private final String TAG = SettingsUIService.class.getSimpleName();
    private final String ACTION_SHOW_BRIGHT_ADJUST = "com.jancar.adjust.bright";
    private final String EXTRA_BRIGHT_ADJUST = "extra.bright.adjust";
    private final String NVRAMA = "android.jancar.settings.NvRAMA";
    private final String NVRAMAs = "android.jancar.settings.NvRAMA.read";
    private final String TYPE_BACKCAR = "type_backcar";
    private final String TYPE_VEDIO = "type_vedio";
    private final String TYPE_ALL = "type_all";
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View brightView;
    private final static int MSG_CLOSE_VIEW = 0;
    private final static long DELAY_CLOSE_TIME = 3000;
    private SettingManager settingManager;
    private int max = 9;
    private ImageView brightImage, chromaImage, contrastImage;
    private SeekBar brightSeekbar, chromaSeekbar, contrastSeekbar;
    private TextView brightValue, chromaValue, contrastValue;
    private String currentType = "";
    private JancarManager jancarManager;
    private DisplayController displayController;
    private boolean isShow;
    NetworkChangeReceiver networkChangeReceiver;
    IntentFilter intentFilter;
    @SuppressLint("HandlerLeak")
    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CLOSE_VIEW:
                    destroyView();
                    break;
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "startForegroundService==");
            startServiceForeground();
        }
        settingManager = SettingManager.getSettingManager(this);
        jancarManager = (JancarManager) getSystemService(JancarManager.JAC_SERVICE);
        displayController = new DisplayController();
        initView();
        jancarManager.registerJacStateListener(jacState.asBinder());
        jancarManager.registerPrompt(promptController.asBinder());
        //NvRAMA();
        Log.e(TAG, "onCreate end");
        Settings.Secure.setLocationProviderEnabled(getApplicationContext().getContentResolver(), LocationManager.GPS_PROVIDER, false);
        GPS gps = new GPS();
        gps.openGPSSettings(getApplicationContext(), 3);
        //动态接受网络变化的广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction(NVRAMA);
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startServiceForeground() {
        Log.e(TAG, "startServiceForeground==");
        // service的onCreate
        NotificationChannel channel = new NotificationChannel("im_channel_id", "System", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, "im_channel_id")
                .setSmallIcon(R.mipmap.ic_settings)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentText("BTUIService")  // the contents of the entry
                .build();

        startForeground(1, notification);

    }

    JacState jacState = new JacState() {
        @Override
        public void OnBackCar(boolean bState) {
            super.OnBackCar(bState);
            Log.e(TAG, "--" + bState);
            if (bState) {
                displayController.nativeSetContrastAdjIndex(settingManager.getDisplayBackCarContrast());
                displayController.nativeSetBrightnessAdjIndex(settingManager.getDisplayBackCarBrightness());
                displayController.nativeSetSatAdjIndex(settingManager.getDisplayBackCarChroma());
            } else {
                jancarManager.requestPrompt(PromptController.DisplayType.DT_ADJUSTMENT,
                        PromptController.DisplayParam.DP_HIDE, new ExtraData().put("hide", "" +
                                PromptController.DisplayType.DT_ADJUSTMENT));
                displayController.nativeSetContrastAdjIndex(settingManager.getDisplayContrast());
                displayController.nativeSetBrightnessAdjIndex(settingManager.getDisplayBrightness());
                displayController.nativeSetSatAdjIndex(settingManager.getDisplayChroma());
            }
        }
    };

    PromptController promptController = new PromptController(PromptController.DisplayType.DT_ADJUSTMENT) {
        @Override
        public void show(boolean bMaximize, HashMap<String, Object> map) {
            super.show(bMaximize, map);
            Log.e(TAG, "show");
            String value = (String) map.get("show");
            Log.e(TAG, "show==" + map + "value==" + value);
           /*
           // WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(dm);*/
            if (!isShow && value.equals("DT_ADJUSTMENT")) {
                initData(TYPE_BACKCAR);
                showView();
                currentType = TYPE_BACKCAR;
                isShow = true;
            }
        }

        @Override
        public void hide(HashMap<String, Object> map) {
            super.hide(map);
            String value = (String) map.get("hide");

            Log.e(TAG, "hide==" + map + "value==" + value);
            if (isShow && value.equals("DT_ADJUSTMENT")) {
                UIHandler.removeMessages(MSG_CLOSE_VIEW);
                destroyView();
            }
        }

        @Override
        public void update(boolean bMaximize, HashMap<String, Object> map) {
            super.update(bMaximize, map);
            String value = (String) map.get("update");
            Log.e(TAG, "update==" + map + "value==" + value);
        }
    };


    class NetworkChangeReceiver extends BroadcastReceiver {
        @SuppressLint("ShowToast")
        @Override
        public void onReceive(Context context, Intent intent) {
            String Type = intent.getStringExtra("Type");
            if (Type.equals("read")) {

                byte[] buff=  Read();
                for (byte butt:buff){
                    Log.w("SettingsUIService",butt+" butt");
                }
                Intent intents = new Intent(NVRAMAs);
                intents.putExtra("Data",buff);
                sendBroadcast(intents);
            } else {
                byte[] buff=new byte[64];
                byte[] buff1 = intent.getByteArrayExtra("Data");
                System.arraycopy(buff1,0,buff,0,64);
                for (byte butt:buff){
                    Log.w("SettingsUIService",butt+" butt");
                }
                if (buff1.length>64){
                    Toast.makeText(context," ",Toast.LENGTH_LONG);
                }
                writeFile(buff);
            }



        }
    }

    public byte[] Read() {
        IBinder binder = ServiceManager.getService("NvRAMAgent");
        NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
        byte[] buff = new byte[0];
        if (agent!=null){
            try {
                buff = agent.readFile(45);
                Log.w("NavigationSoftware", buff.toString() + " ");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


        return buff;
    }

    ;

    public int writeFile(byte[] buff) {
        IBinder binder = ServiceManager.getService("NvRAMAgent");
        NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
        int flag = 0;
        try {
            flag = agent.writeFile(45, buff);
            Log.w("NavigationSoftware", buff.toString() + " ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return flag;
    }

    ;

    public static boolean NvRAMA() {
        IBinder binder = ServiceManager.getService("NvRAMAgent");
        NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
        Log.w("NavigationSoftware", binder + " ");
        boolean isSuccess = false;
        try {
            byte[] buff = agent.readFile(45);
            int flag = agent.writeFile(45, buff);
            isSuccess = true;
            Log.w("NavigationSoftware", buff.toString() + " ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /* public static String getSerialNumber(){
         String serial = null;
         serial = SystemProperties.get("ro.serialno");

         return serial;
     }

     public static String getCustomerName(){
         return "JAC";
     }

     public static String getManufacturer(){
         String serial = null;
         serial = SystemProperties.get("ro.product.version");

         return serial.substring(6,9);
     }*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        jancarManager.unregisterJacStateListener(jacState.asBinder());
        jancarManager.unregisterPrompt(promptController.asBinder());
        unregisterReceiver(networkChangeReceiver);
        Log.e(TAG, "onDestroy");
    }

    private void initView() {

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.width = 500;
        mLayoutParams.height = 316;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.format = PixelFormat.RGBA_8888;

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mLayoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.STATUS_BAR_HIDDEN | View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.buttonBrightness = BRIGHTNESS_OVERRIDE_OFF;

        brightView = LayoutInflater.from(this).inflate(R.layout.bright_dialog, null);
        findView();
    }

    private void findView() {
        brightImage = brightView.findViewById(R.id.brightImage);
        chromaImage = brightView.findViewById(R.id.chromaImage);
        contrastImage = brightView.findViewById(R.id.contrastImage);

        brightSeekbar = brightView.findViewById(R.id.brightSeekbar);
        chromaSeekbar = brightView.findViewById(R.id.chromaSeekbar);
        contrastSeekbar = brightView.findViewById(R.id.contrastSeekbar);

        brightSeekbar.setMax(9);
        chromaSeekbar.setMax(9);
        contrastSeekbar.setMax(9);

        brightSeekbar.setOnSeekBarChangeListener(this);
        chromaSeekbar.setOnSeekBarChangeListener(this);
        contrastSeekbar.setOnSeekBarChangeListener(this);

        brightValue = brightView.findViewById(R.id.brightValue);
        chromaValue = brightView.findViewById(R.id.chromaValue);
        contrastValue = brightView.findViewById(R.id.contrastValue);
    }

    private void showView() {
        mWindowManager.addView(brightView, mLayoutParams);
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW, DELAY_CLOSE_TIME);
    }

    private void initData(String type) {
        if (type.equals(TYPE_BACKCAR)) {
            //brightImage.setImageResource();
            Log.e(TAG, "settingManager.getDisplayBackCarBrightness()==" + settingManager.getDisplayBackCarBrightness());
            brightSeekbar.setProgress(settingManager.getDisplayBackCarBrightness());
            brightValue.setText(String.valueOf(settingManager.getDisplayBackCarBrightness()));
            //brightImage.setImageResource();
            chromaSeekbar.setProgress(settingManager.getDisplayBackCarChroma());
            chromaValue.setText(String.valueOf(settingManager.getDisplayBackCarChroma()));

            //brightImage.setImageResource();
            contrastSeekbar.setProgress(settingManager.getDisplayBackCarContrast());
            contrastValue.setText(String.valueOf(settingManager.getDisplayBackCarContrast()));
        }
    }

    private void destroyView() {
        mWindowManager.removeViewImmediate(brightView);
        isShow = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW, DELAY_CLOSE_TIME);
        Log.e(TAG, "onProgressChanged progress==" + progress + seekBar.getId());
        switch (seekBar.getId()) {
            case R.id.brightSeekbar:
                if (currentType == TYPE_BACKCAR) {

                    displayController.nativeSetBrightnessAdjIndex((byte) progress);
                    brightValue.setText(String.valueOf(progress));
                }
                break;
            case R.id.chromaSeekbar:
                if (currentType == TYPE_BACKCAR) {
                    displayController.nativeSetSatAdjIndex((byte) progress);
                    chromaValue.setText(String.valueOf(progress));
                }
                break;
            case R.id.contrastSeekbar:
                if (currentType == TYPE_BACKCAR) {
                    displayController.nativeSetContrastAdjIndex((byte) progress);
                    contrastValue.setText(String.valueOf(progress));
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW, DELAY_CLOSE_TIME);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW, DELAY_CLOSE_TIME);
        int progress = seekBar.getProgress();
        Log.e(TAG, "onStopTrackingTouch progress==" + progress);
        switch (seekBar.getId()) {
            case R.id.brightSeekbar:
                if (currentType.equals(TYPE_BACKCAR)) {
                    Log.e(TAG, "progress==" + progress);
                    settingManager.setDisplayBackCarBrightness((byte) progress, true);
                } else if (currentType.equals(TYPE_VEDIO)) {
                } else {

                }
                break;
            case R.id.chromaSeekbar:
                if (currentType.equals(TYPE_BACKCAR)) {
                    settingManager.setDisplayBackCarChroma((byte) progress, true);
                } else if (currentType.equals(TYPE_VEDIO)) {

                } else {

                }
                break;
            case R.id.contrastSeekbar:
                if (currentType.equals(TYPE_BACKCAR)) {
                    settingManager.setDisplayBackCarContrast((byte) progress, true);
                } else if (currentType.equals(TYPE_VEDIO)) {

                } else {

                }
                break;
        }
    }
}
