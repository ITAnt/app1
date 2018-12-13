package com.jancar.radio.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jancar.globallib.globaldatamanager.GlobaldataManager;
import com.jancar.key.KeyDef;
import com.jancar.media.JacMediaSession;
import com.jancar.radio.R;
import com.jancar.radio.RadioManager;
import com.jancar.radio.RadioWrapper;

import com.jancar.radio.contract.RadioCacheUtil;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.entity.Collection;
import com.jancar.radio.entity.RadioStation;
import com.jancar.radio.notification.RadioNotification;
import com.jancar.radio.presenter.RadioPresenter;
import com.jancar.radio.widget.RuleView;
import com.jancar.utils.Logcat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static com.android.internal.app.IntentForwarderActivity.TAG;
import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_DOWN_LONG;
import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_UP;
import static com.jancar.radio.listener.utils.RadioStationDaos.deleteAll;
import static com.jancar.radio.notification.uite.CONTEXT;
import static com.jancar.radio.notification.uite.INITIAL;

@SuppressLint("Registered")
public abstract class BasesActivity extends com.ui.mvp.view.BaseActivity<RadioContract.Presenter, RadioContract.View> implements RadioContract.View, RadioManager.ConnectListener{
    public String BROADCAST_ACTION = "RadioActivity";
    Unbinder unbinder;
    @BindView(R.id.FMFreqSeekBar)
    public    SeekBar mFMFreqSeekBar;
    @BindView(R.id.img_swap_band)
    public   ImageView swapBandImg;
    @BindView(R.id.img_search)
    public   ImageView searchImg;
    @BindView(R.id.txt_band)
    TextView bandTxt;
    @BindView(R.id.btn_left)
    public   ImageView leftBtn;
    @BindView(R.id.img_dnr)
    public   ImageView img_dnr;
    @BindView(R.id.btn_right)
    public  ImageView rightBtn;
    @BindView(R.id.txt_unit)
    public TextView unitTxt;
    @BindView(R.id.txt_st)
    public  TextView stTxt;
    @BindView(R.id.txt_ta)
    public  TextView taTxt;
    @BindView(R.id.txt_tas)
    public TextView taTxts;
    @BindView(R.id.txt_af)
    public  TextView afTxt;
    @BindView(R.id.txt_name)
    public TextView txt_name;
    @BindView(R.id.txt_rt)
    public TextView txt_rt;
    @BindView(R.id.img_st)
    public  ImageView img_st;
    @BindView(R.id.Rds_llayout)
    LinearLayout rds_Llayout;
    @BindView(R.id.radio_llayout)
    LinearLayout radio_Llayout;


    int Band;
    public RadioManager mRadioManager;
    public AudioManager mAudioManager = null;
    public int mBand;
    public int mLocation;
    public int mFreq;
    public int mInitFreq = 0;
    public boolean mNeedScanStop;
    public boolean isTermination;
    public boolean isTinTai = false;
    public boolean isMobile;
    public boolean isChange = false;
    public static final int PAGE_FM = 1;
    public static final int PAGE_AM = 2;
    public List<RadioStation> radioStations;
    public List<Collection> collectionsStations;

    public List<com.jancar.radio.widget.RuleView> RuleView;
    public int mBandAF[] = new int[]{R.string.fm1, R.string.fm2, R.string.fm3, R.string.am1, R.string.am2, R.string.am3};
    //protected RadioNotification mRadioNotification;
    public static final int FM_FREQ_CRITICAL = 10000;
    public static final int SCAN_FREQ_LIST_DISPLAY_MAX = 15;
    public ArrayList<RadioStation> mScanResultList;
    public boolean isScanAll = false;
    public boolean isSetting = false;
    public JacMediaSession mjacMediaSession;
    public boolean isShortSearch = false;
    public boolean isSwitch = false;
    public boolean isSwitchs = false;
    public RadioNotification mRadioNotification;
    public  int tiems = 5;
    public  RdsFragment rdsFragment;
    public Map<String, Integer> stringIntegerMap;
    public boolean isSwitc = false;
    public static final String PROPERTY_GIS_COMPENNSATE = "persist.jancar.GisCompensate";
    public  GlobaldataManager manager;
    public String mPs;
    public int mPty;
    public  ScheduledExecutorService scheduled = null;
    public Handler handler ;
    public  BasesActivity(){
    }

    Thread runnable = null;
    public class RadioAudioFocusChange implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logcat.d("onAudioFocusChange, focusChange = " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(mAudioFocusChange);
                mjacMediaSession.setActive(true);
            } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {

            } else if (focusChange == AUDIOFOCUS_GAIN) {
                //  mRadioManager.scanStop();
                // mRadioManager = new RadioManager(RadioActivity.this, RadioActivity.this, getPresenter().getRadioListener(), getPackageName());
            }
        }
    }

    public RadioAudioFocusChange mAudioFocusChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        initStatusBar();
        handler=getHandler();
        stringIntegerMap = new HashMap<>();
        unbinder = ButterKnife.bind(this);
        mAudioFocusChange = new RadioAudioFocusChange();
        manager = new GlobaldataManager(getApplicationContext(), null, 0);
        mRadioManager = new RadioManager(this, this, getPresenter().getRadioListener(), getPackageName());
        Band = getBandEditor();
        if (Band >= 3) {
            stTxt.setVisibility(View.INVISIBLE);
        } else {
            stTxt.setVisibility(View.VISIBLE);
        }
        rdsFragment = new RdsFragment();
        initData();
        onDoIntent(getIntent(), false);
        bandTxt.setText(mBandAF[Band]);
        Message data = new Message();
        data.what = CONTEXT;
        data.obj = this;
        rdsFragment.getData(data);
        getFragmentManager().beginTransaction()
                .replace(R.id.Rds_llayout, rdsFragment)
                .commit();
    }

     abstract void initData();

    abstract Handler getHandler();

    public void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//获取状态栏高度
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            View rectView = new View(this);
//绘制一个和状态栏一样高的矩形，并添加到视图中
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            rectView.setLayoutParams(params);
//设置状态栏颜色
            rectView.setBackgroundColor(Color.parseColor("#000000"));
//添加矩形View到布局中
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.addView(rectView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).
                    getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }
    public void setBand() {
        if (Band >= 3) {
            this.mBand = 0;
        } else {
            this.mBand = 1;
        }
    }

    @Override
    public void onServiceConnected() {
        Logcat.d("get Current Thread  = " + Thread.currentThread().getName());
        initCurFreq();
    }
    protected void initCurFreq() {
        final int location = RadioCacheUtil.getInstance(getApplicationContext()).getLocation();
        mFreq = getmFreq();
        if (RadioWrapper.isFreqValid(mFreq, location)) {
            this.mInitFreq = mFreq;
            RadioCacheUtil.getInstance(getApplicationContext()).setLastPage(getPresenter().getBandPage(this.mBand));
            RadioCacheUtil.getInstance(getApplicationContext()).setLastFreq(this.mBand, this.mInitFreq);
        } else {
            final int lastPage = RadioCacheUtil.getInstance(getApplicationContext()).getLastPage(1);
            if (-1 != mBand) {
                RadioCacheUtil.getInstance(getApplicationContext()).setLastPage(getPresenter().getBandPage(this.mBand));
            } else {
                this.mBand = RadioCacheUtil.getInstance(getApplicationContext()).getBand();
            }

            if (lastPage == PAGE_FM || PAGE_AM == lastPage) {
                this.mInitFreq = RadioCacheUtil.getInstance(getApplicationContext()).getLastFreq(this.mBand);
            } else {
                this.mInitFreq = RadioCacheUtil.getInstance(getApplicationContext()).getLastFavoritesFreq();
            }
        }
        if (this.mRadioManager != null) {
            this.mRadioManager.selectRdsTa(1 == RadioCacheUtil.getInstance(getApplicationContext()).getTA());
            final RadioManager mRadioManager = this.mRadioManager;
            final int af = RadioCacheUtil.getInstance(getApplicationContext()).getAF();
            boolean bAf = false;
            if (af != 0) {
                bAf = true;
            }
            mRadioManager.selectRdsAf(bAf);
            this.mRadioManager.setLocation(RadioCacheUtil.getInstance(getApplicationContext()).getLocation());
            Handler(0, this.mInitFreq);
            //  this.mRadioManager.setFreq();
            this.mRadioManager.open();
        }
    }
    public void Handler(int what, int arg1) {
        Message mMessage = new Message();
        mMessage.what = what;
        mMessage.arg1 = arg1;
        handler.sendMessage(mMessage);
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public RadioManager getRadioManager() {
        return mRadioManager;
    }

    @Override
    public void init(List<RadioStation> radioStations) {
        boolean icFreq = false;
        for (int i = 0; i < radioStations.size(); i++) {
            RadioStation mRadioStation = radioStations.get(i);
            //  list.get(mRadioStation.getPosition()).setText(mRadioStation.getRdsname());
            if (mRadioStation.getSelect()) {
                icFreq = true;
                mFreq = mRadioStation.getMFreq();
                //  list.get(mRadioStation.getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                // list.get(mRadioStation.getPosition()).setTag("2");
            }

        }
        if (!icFreq) {

            Band = getBandEditor();
            if (Band >= 3) {
                mBand = 0;
            } else {
                mBand = 1;
            }
            init();
            Log.w("mfreqq", "mFreq" + Band + "_____" + mFreq);
        }
    }
    public void init() {

        mFreq = getmFreq();
        if (mFreq < RadioWrapper.getFreqStart(this.mBand, this.mLocation)) {
            mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
        }
        if (mFreq > RadioWrapper.getFreqEnd(this.mBand, this.mLocation)) {
            mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
        }

    }

    @Override
    public void initSelect(List<RadioStation> radioStations, RadioStation mRadioStation) {
        Message mMessage = new Message();
        mMessage.what = 6;
        mMessage.arg1 = mRadioStation.getMFreq();
        handler.sendMessage(mMessage);
        init(radioStations);
    }

    @Override
    public void initSelect(List<RadioStation> radioStations) {
        init(radioStations);
    }

    @Override
    public void requestRadioFocus() {
        Log.w("Radio", "requestRadioFocus");
        if (mRadioManager != null) {

            setMediaInfo(mRadioManager.getFreq());
        }
        mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN);

        SystemProperties.set(PROPERTY_GIS_COMPENNSATE, "1");
    }

    @Override
    public void abandonRadioFocus() {
        mAudioManager.abandonAudioFocus(mAudioFocusChange);

        SystemProperties.set(PROPERTY_GIS_COMPENNSATE, "0");
    }
    public void setMediaInfo(int mFreq) {
        Bundle mBundle = new Bundle();
        mBundle.putInt("Freq", mFreq);
        mBundle.putInt("mLocation", mLocation);
        mBundle.putInt("Band", Band);
        mBundle.putInt("mBand", mBand);
        mBundle.putString("name", RadioWrapper.getFreqString(mFreq, mBand, mLocation));
        mjacMediaSession.notifyCustomEvent(BROADCAST_ACTION, mBundle);
        Log.w(BROADCAST_ACTION, "Freq" + mFreq + " mLocation" +mLocation+ " Band" + Band + " mBand" + mBand + " name" + RadioWrapper.getFreqString(mFreq, mBand, mLocation));
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("RadioAcitivity", "onPause");
        isSwitch = false;
        scanStop();
        shutdown();
    }
    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onStereo(int i, boolean b) {
        Message mMessage = new Message();
        mMessage.what = 8;
        mMessage.obj = b;
        handler.sendMessage(mMessage);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在异步执行 拖动条正在拖动监听
    public void onDataSynEvent(RadioPresenter.DataSynEvent event) {
        if (event.bChanged && mNeedScanStop) {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            mRadioManager.scanStop();
        }
        if (event.bChanged) {
            Handler(6, RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation));
            isMobile = true;
        }

    }
    @Subscribe(threadMode = ThreadMode.ASYNC) //在异步执行 拖动条停止监听
    public void onDataSynEvents(RadioPresenter.DataSynEvents event) {

        Handler(6, RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation));
        isMobile = false;

    }


    @Override
    public void setCollection(List<Collection> collections) {

    }

    @Override
    public RadioContract.Presenter createPresenter() {
        return new RadioPresenter();
    }

    @Override
    public RadioContract.View getUiImplement() {
        return this;
    }

    public void onDoIntent(final Intent intent, boolean bfrist) {

        try {
            String strMsg = null;
            if (intent != null) {
                strMsg = intent.getStringExtra("device");
                if (strMsg != null) {
                    Logcat.d("onDoIntent -> " + strMsg);
                    if (strMsg.equals("fm")) {
                        isSwitch = false;
                        Handler(2, 0);
                        stringIntegerMap.clear();
                    } else if (strMsg.equals("am")) {
                        isSwitch = false;
                        Handler(1, 3);
                        stringIntegerMap.clear();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    //方控下曲
    public void favoritePrev() {
        // this.mNeedPopUp = true;
        if (this.mRadioManager != null && this.mRadioManager.getScanAction() != 0) {
            this.mRadioManager.scanStop();
        }
        if (this.radioStations.size() > 0) {
            boolean select = false;
            int d = 0;
            for (int i = 0; i < this.radioStations.size(); i++) {
                if (radioStations.get(i).getSelect()) {
                    select = true;
                    d = i;
                }
            }
            if (select) {
                d++;
            }
            if (d > 5) {
                d = 0;
                Band++;
                if (mBand == 0) {
                    if (Band > 4) {
                        Band = 3;
                    }
                } else {
                    if (Band > 2) {
                        Band = 0;
                    }
                }
                Handler(12, Band);
            }
            setMyFavoriteItem(d);
        }
    }

    //方控上曲
    public void favoriteNext() {
        if (this.mRadioManager != null && this.mRadioManager.getScanAction() != 0) {
            this.mRadioManager.scanStop();
        }
        if (this.radioStations.size() > 0) {
            boolean select = false;
            int d = 0;
            for (int i = 0; i < this.radioStations.size(); i++) {
                if (radioStations.get(i).getSelect()) {
                    select = true;
                    d = i;
                }
            }
            if (select) {
                d--;
            }
            if (d < 0) {
                d = 5;
                Band--;
                if (mBand == 0) {
                    if (Band < 3) {
                        Band = 4;
                    }
                } else {
                    if (Band < 0) {
                        Band = 2;
                    }
                }
                Handler(12, Band);
            }
            setMyFavoriteItem(d);
        }
    }

    public void setMyFavoriteItem(final int index) {
        this.mFreq = this.radioStations.get(index).getMFreq();
        Handler(9, index);
        if (this.mRadioManager != null) {
            Handler(0, this.mFreq);
            //this.mRadioManager.setFreq(this.mFreq);
        }

    }

    public void updateNotification(int mFreq) {
        if (mRadioNotification != null) {
            mRadioNotification.update(mFreq,
                    mBand, RadioCacheUtil.getInstance(getApplicationContext()).getLocation(), Band);
        }
    }

    public void selectRdsPty(int Pty) {

        mRadioManager.selectRdsPty(Pty);
        Log.w(TAG, Pty + "");
    }

    public void selectRdsTa(boolean RdsTa) {
        //boolean RdsTa = intent.getBooleanExtra("RdsTa", false);
        mRadioManager.selectRdsTa(RdsTa);
        if (RdsTa) {
            taTxt.setVisibility(View.VISIBLE);
        } else {
            scanStop();
            taTxt.setVisibility(View.INVISIBLE);
        }
        Log.w(TAG, RdsTa + "");
    }

    public void selectRdsAf(boolean RdsAf) {
        //boolean RdsAf = intent.getBooleanExtra("RdsAf", false);
        mRadioManager.selectRdsAf(RdsAf);
        Log.w(TAG, RdsAf + "");
        if (RdsAf) {
            afTxt.setVisibility(View.VISIBLE);
        } else {
            afTxt.setVisibility(View.INVISIBLE);
        }
    }

    //显示
    public void hideRds(int i) {
        rds_Llayout.setVisibility(i == 0 ? View.INVISIBLE : View.VISIBLE);
        radio_Llayout.setVisibility(i == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("按下了back键   onKeyDown()");
            if (rds_Llayout.getVisibility() == View.VISIBLE) {
                hideRds(0);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //判断是否关闭搜索
    public void scanStop() {
        if (mNeedScanStop) {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            mRadioManager.scanStop();
        }
    }

    //判断是否电台预览
    public void shutdown() {
        if (isShortSearch) {
            if (scheduled != null) {
                scheduled.shutdown();
            }
        }

    }

    public void setBandEditor(int band) {
        SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
        edito.putInt("Band", band);
        edito.commit();
    }

    public int getBandEditor() {
        SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);

        return read.getInt("Band", 0);
    }

    public int getmFreq() {
        SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);
        return read.getInt("mFreq" + Band, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
    }

    public void setmFreq(int band, int mFreq) {
        SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
        edito.putInt("mFreq" + band, mFreq);
        edito.commit();
    }

    public static List removeDuplicate(ArrayList<RadioStation> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getMFreq() == (list.get(i).getMFreq())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }
    public void KEY_AS() {
        isChange = true;
        if (handler != null) {
            Handler(10, 0);
        }
        SharedPreferences sharedPreferenc = getActivity().getSharedPreferences("FirstRun", 0);
        sharedPreferenc.edit().putBoolean("Firsts", false).commit();
        if (!mNeedScanStop) {
            isScanAll = true;
            if (this.mRadioManager != null) {
                this.mRadioManager.scanAll();
            }
        }
    }

    public void KEY_AM() {
        isChange = true;
        if (isAppOnForeground()) {
            isSwitc = true;
            isSwitch = true;
            isSwitchs = false;
            isSetting = true;
            Handler(1, 3);
        } else {
            Message mMessage = new Message();
            mMessage.what = 11;
            mMessage.obj = "am";
            handler.sendMessage(mMessage);

        }
    }

    public void KEY_FM() {
        isChange = true;
        if (isAppOnForeground()) {
            isSwitc = true;
            isSwitch = true;
            isSetting = true;

            Handler(2, 0);
            isSwitchs = false;
            stringIntegerMap.clear();
        } else {
            Message mMessage = new Message();
            mMessage.what = 11;
            mMessage.obj = "fm";
            handler.sendMessage(mMessage);
        }
    }
}
