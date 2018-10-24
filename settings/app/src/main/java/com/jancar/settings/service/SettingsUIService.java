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
        import android.util.Log;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import com.jancar.JancarManager;
        import com.jancar.common.ExtraData;
        import com.jancar.globallib.contentprovider.ContentData;
        import com.jancar.globallib.globaldatamanager.GlobaldataManager;
        import com.jancar.model.DisplayController;
        import com.jancar.prompt.PromptController;
        import com.jancar.settings.R;
        import com.jancar.settings.lib.SettingManager;
        import com.jancar.state.JacState;

        import java.util.HashMap;

        import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
        import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

public class SettingsUIService extends Service implements SeekBar.OnSeekBarChangeListener {
    private final String TAG= SettingsUIService.class.getSimpleName();
    private final String ACTION_SHOW_BRIGHT_ADJUST = "com.jancar.adjust.bright";
    private final String EXTRA_BRIGHT_ADJUST = "extra.bright.adjust";
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
    private ImageView brightImage,chromaImage,contrastImage;
    private SeekBar brightSeekbar,chromaSeekbar,contrastSeekbar;
    private TextView brightValue,chromaValue,contrastValue;
    private String currentType = "";
    private JancarManager jancarManager;
    private DisplayController displayController;
    private boolean isShow;

    @SuppressLint("HandlerLeak")
    private Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
        Log.e(TAG,"onCreate");
        settingManager = SettingManager.getSettingManager(this);
        jancarManager  = (JancarManager) getSystemService(JancarManager.JAC_SERVICE);
        displayController = new DisplayController();
        initView();
        jancarManager.registerJacStateListener(jacState.asBinder());
        jancarManager.registerPrompt(promptController.asBinder());
        Log.e(TAG,"onCreate end");
    }

    JacState jacState = new JacState(){
        @Override
        public void OnBackCar(boolean bState) {
            super.OnBackCar(bState);
            if(bState){
                displayController.nativeSetContrastAdjIndex(settingManager.getDisplayBackCarContrast());
                displayController.nativeSetBrightnessAdjIndex(settingManager.getDisplayBackCarBrightness());
                displayController.nativeSetSatAdjIndex(settingManager.getDisplayBackCarChroma());
            }else{
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
            String value = (String) map.get("show");
            Log.e(TAG,"show=="+map+"value=="+value);
            if(!isShow&&value.equals("DT_ADJUSTMENT")){
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
            Log.e(TAG,"hide=="+map+"value=="+value);
            if(isShow&&value.equals("DT_ADJUSTMENT")){
                UIHandler.removeMessages(MSG_CLOSE_VIEW);
                destroyView();
            }
        }

        @Override
        public void update(boolean bMaximize, HashMap<String, Object> map) {
            super.update(bMaximize, map);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        jancarManager.unregisterJacStateListener(jacState.asBinder());
        jancarManager.unregisterPrompt(promptController.asBinder());
    }

    private void initView(){
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.format = PixelFormat.RGBA_8888;

        mLayoutParams.flags = FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|FLAG_LAYOUT_INSET_DECOR ;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.width = 500;
        mLayoutParams.height = 316;
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
        chromaSeekbar.setOnSeekBarChangeListener(this);
        contrastSeekbar.setOnSeekBarChangeListener(this);

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
            Log.e(TAG,"settingManager.getDisplayBackCarBrightness()=="+settingManager.getDisplayBackCarBrightness());
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
        isShow = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW,DELAY_CLOSE_TIME);
        Log.e(TAG,"onProgressChanged progress=="+progress+seekBar.getId());
        switch (seekBar.getId()){
            case R.id.brightSeekbar:
                if(currentType==TYPE_BACKCAR){
                    displayController.nativeSetBrightnessAdjIndex(settingManager.getDisplayBackCarBrightness());
                    brightValue.setText(String.valueOf(progress));
                }
                break;
            case R.id.chromaSeekbar:
                if(currentType==TYPE_BACKCAR){
                    displayController.nativeSetSatAdjIndex(settingManager.getDisplayBackCarChroma());
                    chromaValue.setText(String.valueOf(progress));
                }
                break;
            case R.id.contrastSeekbar:
                if(currentType==TYPE_BACKCAR){
                    displayController.nativeSetContrastAdjIndex(settingManager.getDisplayBackCarContrast());
                    contrastValue.setText(String.valueOf(progress));
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW,DELAY_CLOSE_TIME);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        UIHandler.removeMessages(MSG_CLOSE_VIEW);
        UIHandler.sendEmptyMessageDelayed(MSG_CLOSE_VIEW,DELAY_CLOSE_TIME);
        int progress = seekBar.getProgress();
        Log.e(TAG,"onStopTrackingTouch progress=="+progress);
        switch (seekBar.getId()){
            case R.id.brightSeekbar:
                if(currentType.equals(TYPE_BACKCAR)){
                    Log.e(TAG,"progress=="+progress);
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
