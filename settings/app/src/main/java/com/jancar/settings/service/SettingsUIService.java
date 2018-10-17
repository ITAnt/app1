package com.jancar.settings.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jancar.model.DisplayController;
import com.jancar.settings.R;
import com.jancar.settings.lib.SettingManager;
import com.jancar.settings.util.GPS;

public class SettingsUIService extends Service implements SeekBar.OnSeekBarChangeListener {
    private final String ACTION_SHOW_BRIGHT_ADJUST = "com.jancar.adjust.bright";
    private final String EXTRA_BRIGHT_ADJUST = "extra.bright.adjust";
    private final String TYPE_BACKCAR = "type_backcar";
    private final String TYPE_VEDIO = "type_vedio";
    private final String TYPE_ALL = "type_all";
    private IntentFilter intentFilter;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View brightView;
    private final static int MSG_CLOSE_VIEW = 0;
    private final static long DELAY_CLOSE_TIME = 3000;
    private SettingManager settingManager;
    private int max = 9;
    private ImageView brightImage,chromaImage,contrastImage;
    private SeekBar brightSeekbar,chromaSeekbar,contrastSeekbar;
    private TextView brightValue,chromaValue,contrastValue;
    private DisplayController displayController;
    private String currentType = "";

    @SuppressLint("HandlerLeak")
    private Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CLOSE_VIEW:
                    destroyView();
                    if(displayController!=null){
                        displayController.nativeSetBrightnessAdjIndex(settingManager.getDayBrightness());
                        displayController.nativeSetContrastAdjIndex(settingManager.getDisplayContrast());
                        displayController.nativeSetSatAdjIndex(settingManager.getDisplayChroma());
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SHOW_BRIGHT_ADJUST);
        settingManager = SettingManager.getSettingManager(this);
        displayController = new DisplayController();
        registerReceiver(broadcastReceiver,intentFilter);
        initView();

        GPS gps=new GPS();
        gps.openGPSSettings(getApplicationContext(),3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String extra = intent.getStringExtra(EXTRA_BRIGHT_ADJUST);
            if(action.equals(ACTION_SHOW_BRIGHT_ADJUST)){
                if(extra.equals(TYPE_ALL)){

                }else if(extra.equals(TYPE_BACKCAR)){
                    currentType = TYPE_BACKCAR;
                    showView();
                    initData(TYPE_BACKCAR);
                }else if(extra.equals(TYPE_VEDIO)){
                    currentType = TYPE_VEDIO;
                    initData(TYPE_VEDIO);
                }
            }
        }
    };

    private void initView(){
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.width = 400;
        mLayoutParams.height = 200;
        brightView = LayoutInflater.from(this).inflate(R.layout.bright_dialog, null);
        findView();
    }

    private void findView(){
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

        brightValue = brightView.findViewById(R.id.brightValue);
        chromaValue = brightView.findViewById(R.id.chromaValue);
        contrastValue = brightView.findViewById(R.id.contrastValue);
    }

    private void showView(){
        mWindowManager.addView(brightView,mLayoutParams);
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW,DELAY_CLOSE_TIME);
    }

    private void initData(String type){
        if(type.equals(TYPE_BACKCAR)){
            //brightImage.setImageResource();
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

    private void destroyView(){
        mWindowManager.removeViewImmediate(brightView);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()){
            case R.id.brightSeekbar:
                displayController.nativeSetBrightnessAdjIndex(settingManager.getDayBrightness());
                break;
            case R.id.chromaSeekbar:
                displayController.nativeSetSatAdjIndex(settingManager.getDisplayChroma());
                break;
            case R.id.contrastSeekbar:
                displayController.nativeSetContrastAdjIndex(settingManager.getDisplayContrast());
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        switch (seekBar.getId()){
            case R.id.brightSeekbar:
                if(currentType.equals(TYPE_BACKCAR)){
                    settingManager.setDisplayBackCarBrightness((byte) progress,true);
                }else if(currentType.equals(TYPE_VEDIO)){
                }else{

                }
                break;
            case R.id.chromaSeekbar:
                if(currentType.equals(TYPE_BACKCAR)){
                    settingManager.setDisplayBackCarChroma((byte) progress,true);
                }else if(currentType.equals(TYPE_VEDIO)){

                }else{

                }
                break;
            case R.id.contrastSeekbar:
                if(currentType.equals(TYPE_BACKCAR)){
                    settingManager.setDisplayBackCarContrast((byte) progress,true);
                }else if(currentType.equals(TYPE_VEDIO)){

                }else{

                }
                break;
        }
    }
}
