package com.jancar.radio.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.BaseManager;
import com.jancar.JancarServer;
import com.jancar.audio.AudioEffectParam;
import com.jancar.globallib.globaldatamanager.GlobaldataManager;
import com.jancar.key.KeyDef;
import com.jancar.key.keyFocuser;
import com.jancar.radio.BuildConfig;
import com.jancar.radio.R;
import com.jancar.radio.RadioManager;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.contract.RadioCacheUtil;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.entity.RadioStation;
import com.jancar.radio.listener.utils.ToastUtil;
import com.jancar.radio.notification.RadioNotification;
import com.jancar.radio.presenter.RadioPresenter;
import com.jancar.radio.widget.RuleView;
import com.jancar.utils.Logcat;
import com.ui.mvp.view.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.provider.Settings.Global.AUTO_TIME;
import static com.android.internal.app.IntentForwarderActivity.TAG;
import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_DOWN_LONG;
import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_UP;
import static com.jancar.key.KeyDef.KeyType.KEY_NEXT;
import static com.jancar.radio.listener.utils.RadioStationDaos.delete;
import static com.jancar.radio.listener.utils.RadioStationDaos.deleteAll;
import static com.jancar.radio.listener.utils.RadioStationDaos.deleteRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.queryFrequency;
import static com.jancar.radio.listener.utils.ToastUtil.ShowToast;
import static com.jancar.radio.listener.utils.ToastUtil.isRtl;
import static com.jancar.radio.view.activity.RdsActivity.BROADCAST_RDS;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by ouyan on 2018/9/19.
 */

public class RadioActivity extends BaseActivity<RadioContract.Presenter, RadioContract.View> implements RadioContract.View, RadioManager.ConnectListener {
    public static final String BROADCAST_ACTION = "RadioActivity.MyBroadcastReceiver";
    @BindView(R.id.txt_channel)
    TextView channelTxt;
    Unbinder unbinder;
    @BindView(R.id.gv_1_one)
    RuleView gv_1_one;
    @BindView(R.id.gv_1_two)
    RuleView gv_1_two;
    @BindView(R.id.gv_1_three)
    RuleView gv_1_three;
    @BindView(R.id.gv_1_four)
    RuleView gv_1_four;
    @BindView(R.id.txt_channel_list_one)
    TextView channelListOneTxt;
    @BindView(R.id.txt_channel_list_two)
    TextView channelListTwoTxt;
    @BindView(R.id.txt_channel_list_three)
    TextView channelListThreeTxt;
    @BindView(R.id.txt_channel_list_four)
    TextView channelListFourTxt;
    @BindView(R.id.txt_channel_list_fives)
    TextView channelListFivesTxt;
    @BindView(R.id.txt_channel_list_six)
    TextView channelListSixTxt;
    @BindView(R.id.FMFreqSeekBar)
    SeekBar mFMFreqSeekBar;
    @BindView(R.id.img_swap_band)
    ImageView swapBandImg;
    @BindView(R.id.img_search)
    ImageView searchImg;
    @BindView(R.id.txt_band)
    TextView bandTxt;
    @BindView(R.id.btn_left)
    ImageView leftBtn;
    @BindView(R.id.img_dnr)
    ImageView img_dnr;
    @BindView(R.id.btn_right)
    ImageView rightBtn;
    @BindView(R.id.txt_unit)
    TextView unitTxt;
    @BindView(R.id.txt_st)
    TextView stTxt;
    @BindView(R.id.txt_ta)
    TextView taTxt;
    @BindView(R.id.txt_af)
    TextView afTxt;
    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.txt_rt)
    TextView txt_rt;
    @BindView(R.id.img_original)
    ImageView originalImg;
    @BindView(R.id.RelativeLayout_left)
    RelativeLayout FrameLayout_left;
    @BindView(R.id.RelativeLayout_right)
    RelativeLayout FrameLayout_right;
    int Band;
    protected RadioManager mRadioManager;
    AudioManager mAudioManager = null;
    protected int mBand;
    protected int mLocation;
    int mFreq;
    protected int mInitFreq = 0;
    protected boolean mNeedScanStop;
    protected boolean isTermination;
    protected boolean isTinTai = false;
    protected boolean isMobile;
    private boolean isChange = false;
    public static final int PAGE_FM = 1;
    public static final int PAGE_AM = 2;
    List<RadioStation> radioStations;
    List<TextView> list = new ArrayList<>();
    List<RuleView> RuleView;
    int mBandAF[] = new int[]{R.string.fm1, R.string.fm2, R.string.fm3, R.string.am1, R.string.am2, R.string.am3};
    //protected RadioNotification mRadioNotification;
    public static final int FM_FREQ_CRITICAL = 10000;
    public static final int SCAN_FREQ_LIST_DISPLAY_MAX = 15;
    private ArrayList<RadioStation> mScanResultList;
    private boolean isScanAll = false;
    public boolean isSetting = false;
    private JancarServer mJancarManager = null;
    boolean isShortSearch = false;
    public boolean isSwitch = false;
    public boolean isSwitchs = false;
    protected RadioNotification mRadioNotification;
    int tiems = 5;
    Map<String, Integer> stringIntegerMap;
    boolean isSwitc = false;
    private static final String PROPERTY_GIS_COMPENNSATE = "persist.jancar.GisCompensate";
    GlobaldataManager manager;
    MyBroadcastReceiver myBroadcastReceiver;
    private String mPs;
    private int mPty;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
            Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
            switch (msg.what) {
                case 0:
                    Log.w("Freq",msg.arg1+"");
                    if (msg.arg1 < RadioWrapper.getFreqStart(mBand, mLocation)) {
                        msg.arg1 = RadioWrapper.getFreqStart(mBand, mLocation);
                    }
                    if (msg.arg1 > RadioWrapper.getFreqEnd(mBand, mLocation)) {
                        msg.arg1 = RadioWrapper.getFreqStart(mBand, mLocation);
                    }
                   // if (msg.arg1)
                    mRadioManager.setFreq(msg.arg1);
                    break;
                case 1:
                    isSetting = true;
                    Band = msg.arg1;
                    VarietyBand();
                    SharedPreferences.Editor editors = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                    editors.putInt("Band", msg.arg1);
                    editors.commit();
                    bandTxt.setText(mBandAF[msg.arg1]);
                    getPresenter().initText(msg.arg1, mLocation, first_run);
                    stTxt.setVisibility(View.GONE);
                    break;
                case 2:
                    isSetting = true;
                    // getPresenter().Change(Band, mFreq, mLocation);
                    Band = msg.arg1;
                    VarietyBand();
                    stTxt.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                    edito.putInt("Band", msg.arg1);
                    edito.commit();
                    bandTxt.setText(mBandAF[msg.arg1]);
                    getPresenter().initText(msg.arg1, mLocation, first_run);

                    break;
                case 4:
                    if (Band >= 3) {
                        stTxt.setVisibility(View.GONE);
                    } else {
                        stTxt.setVisibility(View.VISIBLE);
                    }
                    // swapBandImg.setEnabled(true);
                    bandTxt.setText(mBandAF[Band]);
                    getPresenter().initText(Band, mLocation, first_run);
                    /* s();*/
                    break;
                case 5:
                    isSwitch = false;
                    isSetting = false;
                    //swapBandImg.setEnabled(true);
                    if (!isMobile) {
                        final int freqStart = RadioWrapper.getFreqStart(mBand, mLocation);
                        final int freqStep = RadioWrapper.getFreqStep(mBand, mLocation);
                        if (freqStep > 0) {
                            mFMFreqSeekBar.setProgress((msg.arg1 - freqStart) / freqStep);
                        }
                    }


                    updateNotification(msg.arg1);
                    // getPresenter().Change(msg.arg1, radioStations);
                    mFreq = msg.arg1;
                    boolean a = true;

                    if (mFreq < RadioWrapper.getFreqStart(mBand, mLocation)) {
                        mFreq = RadioWrapper.getFreqStart(mBand, mLocation);
                        a = false;
                    }
                    if (mFreq > RadioWrapper.getFreqEnd(mBand, mLocation)) {
                        mFreq = RadioWrapper.getFreqStart(mBand, mLocation);
                        a = false;
                    }
                    if (a) {
                        SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                        editor.putInt("mFreq" + Band, msg.arg1);
                        Log.w("RadioActivity_mFreq", msg.arg1 + "");
                        editor.commit();
                    } else {
                        SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);
                        mFreq = read.getInt("mFreq" + Band, RadioWrapper.getFreqStart(mBand, mLocation));
                        //mRadioManager.setFreq(mFreq);
                        Message mMessage = new Message();
                        mMessage.what = 0;
                        mMessage.arg1 =mFreq;
                        handler.sendMessage(mMessage);
                    }
                    if (!isChange) {
                        channelTxt.setText(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
                    }
                    isChange = false;
                  /*  if(ToastUtil.isForeground(RadioActivity.this,"RdsActivity")) {

                    }*/
                    Log.w("RdsActivity",mFreq+"");
                   Intent intents = new Intent();
                    intents.setAction(BROADCAST_RDS);
                    intents.putExtra("Type", 4);
                    intents.putExtra("ifreq", mFreq);
                    intents.putExtra("mLocation", mLocation);
                    intents.putExtra("mBand", mBand);
                    sendBroadcast(intents);
                    break;
                case 6:
                    Message mMessage = new Message();
                    mMessage.what = 0;
                    mMessage.arg1 = msg.arg1;
                    handler.sendMessage(mMessage);
                    break;
                case 7:


                    break;
                case 8:
                    if (((Boolean) msg.obj)) {
                        stTxt.setTextColor(Color.parseColor("#FF0B49E7"));
                    } else {
                        stTxt.setTextColor(Color.parseColor("#ffffff"));
                    }
                    Log.w("Stereo", ((Boolean) msg.obj) + "");
                    break;
                case 9:

                    getPresenter().select(msg.arg1, Band, mLocation, first_run);
                    break;
                case 10:
                    getPresenter().select(Band, mLocation, first_run);
                    break;
                case 11:
                    getActivity().finish();
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName("com.jancar.radios", "com.jancar.radio.view.activity.RadioActivity");
                    intent.putExtra("device", msg.obj.toString());
                    startActivity(intent);
                    Log.w("wssss", "asfadfa");
                    break;
                case 12:
                    SharedPreferences.Editor s = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                    s.putInt("Band", msg.arg1);
                    s.commit();
                    bandTxt.setText(mBandAF[msg.arg1]);
                    RadioActivity.this.getPresenter().initText(msg.arg1, mLocation, false);
                    break;
                case 30:
                    FrameLayout.LayoutParams layoutParam = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                    layoutParam.setMargins(11, 0, 28, 30);
                    mFMFreqSeekBar.setLayoutParams(layoutParam);
                    break;
                case 31:
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                    layoutParams.setMargins(26, 0, 33, 30);
                    mFMFreqSeekBar.setLayoutParams(layoutParams);
                    break;
                case 32:
                    FrameLayout.LayoutParams layout = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                    layout.setMargins(26, 0, 30, 30);
                    mFMFreqSeekBar.setLayoutParams(layout);
                    break;
            }
        }
    };
    private RadioAudioFocusChange mAudioFocusChange;
    ScheduledExecutorService scheduled = null;
    ScheduledExecutorService scheduleds = null;
    Thread runnable = null;
    Runnable runnables = null;
    Boolean isST;
    private class RadioAudioFocusChange implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logcat.d("onAudioFocusChange, focusChange = " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(mAudioFocusChange);
                mJancarManager.abandonKeyFocus(keyFocusListener.asBinder());
                if (scheduleds != null) {
                    scheduleds.shutdown();
                }
            } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {

            } else if (focusChange == AUDIOFOCUS_GAIN) {
                //  mRadioManager.scanStop();
                // mRadioManager = new RadioManager(RadioActivity.this, RadioActivity.this, getPresenter().getRadioListener(), getPackageName());
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("RadioAcitivity", "onPause");
        isSwitch = false;
        if (mNeedScanStop) {
            mRadioManager.scanStop();
        }
        if (isShortSearch) {
            isShortSearch = false;
            if (scheduled != null) {
                scheduled.shutdown();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        initStatusBar();
        stringIntegerMap = new HashMap<>();
        unbinder = ButterKnife.bind(this);
        mAudioFocusChange = new RadioAudioFocusChange();
        manager = new GlobaldataManager(getApplicationContext(), null, 0);
        mRadioManager = new RadioManager(this, this, getPresenter().getRadioListener(), getPackageName());
        SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);
        Band = read.getInt("Band", 0);
        if (Band >= 3) {
            stTxt.setVisibility(View.GONE);
        } else {
            stTxt.setVisibility(View.VISIBLE);
        }
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
        initData();
        onDoIntent(getIntent(), false);
        bandTxt.setText(mBandAF[Band]);
    }

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

    @SuppressLint("WrongConstant")
    private void initData() {

        RuleView = new ArrayList<>();
        radioStations = new ArrayList<>();
        Log.w("RadioAcitivity", "onCreate");
       mJancarManager = (JancarServer) getSystemService("jancar_manager");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mScanResultList = new ArrayList<>();
        list.add(channelListOneTxt);
        list.add(channelListTwoTxt);
        list.add(channelListThreeTxt);
        list.add(channelListFourTxt);
        list.add(channelListFivesTxt);
        list.add(channelListSixTxt);
        RuleView.add(gv_1_one);
        RuleView.add(gv_1_two);
        RuleView.add(gv_1_three);
        RuleView.add(gv_1_four);
        EventBus.getDefault().register(this);//订阅
        mLocation = RadioCacheUtil.getInstance(getApplicationContext()).getLocation();
        if (manager.getRadioLocal() == RadioCacheUtil.getInstance(getApplicationContext()).getLocation()) {
            mRadioManager.setLocation(mLocation);
            SharedPreferences sharedPrefere = getActivity().getSharedPreferences("FirstRun", 0);
            sharedPrefere.edit().putBoolean("Firsts", true).apply();
            isSetting = sharedPrefere.getBoolean("Firsts", true);
            bandTxt.setText(mBandAF[Band]);
            mFMFreqSeekBar.setOnSeekBarChangeListener(getPresenter());
            VarietyBand();
            getPresenter().initText(Band, mLocation, isSetting);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.w("RadioAcitivity", "onStart");

        if (manager.getRadioLocal() != RadioCacheUtil.getInstance(getApplicationContext()).getLocation()) {
            Band = 0;
            SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
            edito.putInt("Band", Band);
            edito.commit();
            SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_PRIVATE).edit();
            editor.putInt("mFreq" + 3, RadioWrapper.getFreqStart(0, mLocation));
            editor.putInt("mFreq" + 4, RadioWrapper.getFreqStart(0, mLocation));
            editor.putInt("mFreq" + 0, RadioWrapper.getFreqStart(1, mLocation));
            editor.putInt("mFreq" + 1, RadioWrapper.getFreqStart(1, mLocation));
            editor.putInt("mFreq" + 2, RadioWrapper.getFreqStart(1, mLocation));
            editor.commit();
            mLocation = manager.getRadioLocal();
            deleteAll();
            RadioCacheUtil.getInstance(getApplicationContext()).setLocation(mLocation);
            mFreq = RadioWrapper.getFreqStart(mBand, mLocation);
            mRadioManager.setLocation(mLocation);
            SharedPreferences sharedPrefere = getActivity().getSharedPreferences("FirstRun", 0);
            sharedPrefere.edit().putBoolean("Firsts", true).apply();
            isSetting = true;
            bandTxt.setText(mBandAF[Band]);
            mFMFreqSeekBar.setOnSeekBarChangeListener(getPresenter());
            VarietyBand();
            getPresenter().initText(Band, mLocation, true);
        }

    }




    public void setBand() {
        if (Band >= 3) {
            this.mBand = 0;
        } else {
            this.mBand = 1;
        }
    }

    public void VarietyBand() {
        for (RuleView mRuleView : RuleView) {
            mRuleView.setVisibility(View.GONE);
        }
        if (Band >= 3) {
            img_dnr.setBackgroundResource(R.mipmap.btn_radio_rds_d);
            txt_rt.setVisibility(View.GONE);
            stTxt.setVisibility(View.GONE);
            taTxt.setVisibility(View.GONE);
            afTxt.setVisibility(View.GONE);
            txt_name.setVisibility(View.GONE);
            this.mBand = 0;
            unitTxt.setText(R.string.unit_khz);
            switch (mLocation) {
                case 0:
                    RuleView.get(0).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    RuleView.get(1).setVisibility(View.VISIBLE);
                    break;
                default:
                    RuleView.get(2).setVisibility(View.VISIBLE);
                    break;
            }
            handler.sendEmptyMessageDelayed(30, 500);
        } else {
          /*  img_dnr.setBackgroundResource(R.drawable.bg_radio_rds_n);
            stTxt.setVisibility(View.VISIBLE);
            txt_name.setVisibility(View.VISIBLE);
            txt_rt.setVisibility(View.GONE);
            int mTA = RadioCacheUtil.getInstance(getApplicationContext()).getTA();
            int mAF = RadioCacheUtil.getInstance(getApplicationContext()).getAF();
            if (mAF == 1) {
                afTxt.setVisibility(View.VISIBLE);
            } else {
                afTxt.setVisibility(View.GONE);
            }
            if (mTA == 1) {
                taTxt.setVisibility(View.VISIBLE);
            } else {
                taTxt.setVisibility(View.GONE);
            }*/
            img_dnr.setBackgroundResource(R.mipmap.btn_radio_rds_d);
            txt_rt.setVisibility(View.GONE);
            stTxt.setVisibility(View.GONE);
            taTxt.setVisibility(View.GONE);
            afTxt.setVisibility(View.GONE);
            txt_name.setVisibility(View.GONE);
            unitTxt.setText(R.string.unit_mhz);
            RuleView.get(3).setVisibility(View.VISIBLE);
            switch (mLocation) {
                case 2:
                case 3:

                    handler.sendEmptyMessageDelayed(31, 500);
                    break;
                default:
                    handler.sendEmptyMessageDelayed(32, 500);
                    break;
            }
            this.mBand = 1;
        }
        if (isSwitc) {
            // mRadioManager.setBand(mBand);
        }
    }

    int times = 10;

    public void s() {
        if (scheduleds != null) {
            scheduleds.shutdown();
        }
        if (isShortSearch) {
            if (scheduled != null) {
                scheduled.shutdown();
            }
        }

    }

    @Override
    public void initText(List<RadioStation> radioStations) {
        this.radioStations.clear();
        this.radioStations = radioStations;
        if (radioStations.size() <= 0) {
            for (TextView mTextView : list) {
                mTextView.setText("");
                mTextView.setTag("1");
                mTextView.setTextColor(Color.parseColor("#ffffff"));
            }
            SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);
            mFreq = read.getInt("mFreq" + Band, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
        } else {
            init(radioStations);
        }
        this.mNeedScanStop = false;
        this.isMobile = false;
        final int freqStart = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
        final int freqEnd = RadioWrapper.getFreqEnd(this.mBand, this.mLocation);
        final int freqStep = RadioWrapper.getFreqStep(this.mBand, this.mLocation);
        final int max = (freqEnd - freqStart) / freqStep;
        if (freqStep > 0) {
            mFMFreqSeekBar.setMax(max);
        }
        if (freqStep > 0) {
            mFMFreqSeekBar.setProgress((this.mFreq - freqStart) / freqStep);
        }
       // channelTxt.setText(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
        if (isChange) {

            channelTxt.setText(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
        }
        if (isSetting) {
            Message mMessage = new Message();
            mMessage.what = 0;
            mMessage.arg1 = mFreq;
            handler.sendMessage(mMessage);
        }
    }

    @Override
    public void init(List<RadioStation> radioStations) {
        for (TextView mTextView : list) {
            mTextView.setText("");
            mTextView.setTag("1");
            mTextView.setTextColor(Color.parseColor("#ffffff"));
        }
        boolean icFreq = false;
        for (int i = 0; i < radioStations.size(); i++) {
            RadioStation mRadioStation = radioStations.get(i);
            list.get(mRadioStation.getPosition()).setText(mRadioStation.getRdsname());
            if (mRadioStation.getSelect()) {
                icFreq = true;
                mFreq = mRadioStation.getMFreq();
                list.get(mRadioStation.getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                list.get(mRadioStation.getPosition()).setTag("2");
            }

        }
        if (!icFreq) {
            SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);
            Band = read.getInt("Band", 0);
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
        SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);
        mFreq = read.getInt("mFreq" + Band, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
        if (mFreq < RadioWrapper.getFreqStart(this.mBand, this.mLocation)) {
            mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
        }
        if (mFreq > RadioWrapper.getFreqEnd(this.mBand, this.mLocation)) {
            mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
        }

    }

    @Override
    public void initSelect(List<RadioStation> radioStations, RadioStation radioStation) {
        //  mRadioManager.setFreq(radioStation.getMFreq());
        Message mMessage = new Message();
        mMessage.what = 6;
        mMessage.arg1 = radioStation.getMFreq();
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

        mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN);
       mJancarManager.requestKeyFocus(keyFocusListener.asBinder());
       SystemProperties.set(PROPERTY_GIS_COMPENNSATE, "1");
    }

    @Override
    public void abandonRadioFocus() {
        mAudioManager.abandonAudioFocus(mAudioFocusChange);
        mJancarManager.abandonKeyFocus(keyFocusListener.asBinder());
       SystemProperties.set(PROPERTY_GIS_COMPENNSATE, "0");
        if (scheduleds != null) {
            scheduleds.shutdown();

        }
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


    public void addRadioStation(int position, int size, RadioStation mRadioStation) {

        RadioStation mRadioStationd = null;
        for (RadioStation mRadioStations : radioStations) {
            if (mRadioStations.getPosition() == position) {
                mRadioStationd = mRadioStations;
            }
        }
        mRadioStation.setPosition(position);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        getPresenter().Replace(mRadioStation, mRadioStationd, first_run);
    }

    @OnLongClick({R.id.txt_channel_list_one, R.id.txt_channel_list_two, R.id.txt_channel_list_three,
            R.id.txt_channel_list_four, R.id.txt_channel_list_fives, R.id.txt_channel_list_six,
            R.id.btn_left, R.id.btn_right, R.id.img_search})
    public boolean onLongClick(View v) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        if (!mNeedScanStop) {
            mRadioManager.scanStop();
        }
        if (isShortSearch) {
            isShortSearch = false;
            if (scheduled != null) {
                scheduled.shutdown();
            }
        }
        RadioStation mRadioStation = new RadioStation();
        mRadioStation.setRdsname(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
        mRadioStation.setSelect(true);
        mRadioStation.setMFreq(mFreq);
        mRadioStation.setMBand(mBand);
        mRadioStation.setLocation(mLocation);
        mRadioStation.setFrequency(Band);
        switch (v.getId()) {
            case R.id.txt_channel_list_one:
                addRadioStation(0, 1, mRadioStation);

                break;
            case R.id.txt_channel_list_two:
                addRadioStation(1, 2, mRadioStation);

                break;
            case R.id.txt_channel_list_three:
                addRadioStation(2, 3, mRadioStation);

                break;
            case R.id.txt_channel_list_four:
                addRadioStation(3, 4, mRadioStation);

                break;
            case R.id.txt_channel_list_fives:
                addRadioStation(4, 5, mRadioStation);

                break;
            case R.id.txt_channel_list_six:
                addRadioStation(5, 6, mRadioStation);
                break;
            case R.id.btn_left:

                getPresenter().select(Band, mLocation, first_run);
                isScanAll = false;
                SharedPreferences sharedPrefere = getActivity().getSharedPreferences("FirstRun", 0);
                sharedPrefere.edit().putBoolean("Firsts", false).commit();

                if (this.mRadioManager != null) {
                    this.mRadioManager.scanDown(this.mFreq);
                }
                break;
            case R.id.btn_right:
                getPresenter().select(Band, mLocation, first_run);
                SharedPreferences sharedPreferencesdd = getActivity().getSharedPreferences("FirstRun", 0);
                sharedPreferencesdd.edit().putBoolean("Firsts", false).commit();

                isScanAll = false;
                if (this.mRadioManager != null) {
                    this.mRadioManager.scanUp(this.mFreq);
                }
                break;
            case R.id.img_search:
                if (mBand == 0) {
                    Band = 3;
                } else {
                    Band = 0;
                }
                SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                edito.putInt("Band", Band);
                edito.commit();
                bandTxt.setText(mBandAF[Band]);
                getPresenter().select(Band, mLocation, first_run);
                SharedPreferences sharedPreferenc = getActivity().getSharedPreferences("FirstRun", 0);
                sharedPreferenc.edit().putBoolean("Firsts", false).commit();
                if (!mNeedScanStop) {
                    isScanAll = true;
                    isShortSearch=true;
                    if (this.mRadioManager != null) {
                        this.mRadioManager.scanAll();

                    }
                }
                break;
        }
        return true;
    }

    int time;
    int pd = 0;
    int pds = 0;

    public void scanStop() {
        if (mNeedScanStop) {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            mRadioManager.scanStop();
        }
    }

    @OnClick({R.id.txt_channel_list_one, R.id.txt_channel_list_two, R.id.txt_channel_list_three,
            R.id.txt_channel_list_four, R.id.txt_channel_list_fives, R.id.txt_channel_list_six,
            R.id.btn_left, R.id.btn_right, R.id.img_swap_band, R.id.img_search, R.id.img_st, R.id.img_dnr})
    public void OnCLick(View view) {
        scanStop();
        s();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        switch (view.getId()) {
            case R.id.img_dnr:
                if (mBand==0){
                    return;
                }
              /*  Intent intent = new Intent(this, RdsActivity.class);
                intent.putExtra("Freq", mFreq);
                intent.putExtra("mLocation", mLocation);
                intent.putExtra("mBand", mBand);
                intent.putExtra("PS", mRadioManager.getPSText(mFreq));
                intent.putExtra("PTY", mPty);
                startActivity(intent);*/
               /* */
                break;
            case R.id.img_st:
                if (mBand == 0) {
                    return;
                }
                if (stTxt.getVisibility() == View.GONE) {
                    stTxt.setVisibility(View.VISIBLE);
                } else {
                    stTxt.setVisibility(View.GONE);
                }

                break;
            case R.id.txt_channel_list_one:
                isShortSearch = false;
                getPresenter().select(0, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_two:
                isShortSearch = false;
                getPresenter().select(1, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_three:
                isShortSearch = false;
                getPresenter().select(2, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_four:
                isShortSearch = false;
                getPresenter().select(3, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_fives:
                isShortSearch = false;
                getPresenter().select(4, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_six:
                isShortSearch = false;
                getPresenter().select(5, Band, mLocation, first_run);
                break;
            case R.id.btn_left:
                isShortSearch = false;
                isMobile = false;
                getPresenter().select(Band, mLocation, first_run);
                if (this.mRadioManager != null) {
                    this.mRadioManager.step(-1);
                }
                break;
            case R.id.img_search:
                Brows();
                //scheduled.
                break;
            case R.id.btn_right:
                isShortSearch = false;
                getPresenter().select(Band, mLocation, first_run);
                isMobile = false;
                if (this.mRadioManager != null) {
                    this.mRadioManager.step(1);
                }
                break;
            case R.id.img_swap_band:
                if (scheduleds != null) {
                    scheduleds.shutdown();
                }
                isShortSearch = false;
                isSetting = true;
                Band++;
                int band = Band % 5;
                Band = band;
                if (Band==3||Band==0){
                    isChange=true;
                }
                Log.w("RadioActivity", Band + "");
                setBand();
                VarietyBand();
                Log.w("RadioActivity", mBand + "");
                SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                edito.putInt("Band", Band);
                edito.commit();
                Message mMessage = new Message();
                mMessage.what = 4;
                handler.sendMessage(mMessage);
                break;
        }
        mNeedScanStop = false;
    }


    private void Brows() {
        pd = 0;
        if (!isShortSearch) {
            isShortSearch = true;
            @SuppressLint("HandlerLeak") final Handler m = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (list.get(pds).getTag().equals("1")) {
                        Log.w("PDs", "1");
                        list.get(radioStations.get(pds).getPosition()).setTag("0");
                        list.get(radioStations.get(pds).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                    } else {
                        Log.w("PDs", "0");
                        list.get(radioStations.get(pds).getPosition()).setTag("1");
                        list.get(radioStations.get(pds).getPosition()).setTextColor(Color.parseColor("#ffffff"));

                    }
                }
            };
            @SuppressLint("HandlerLeak") final Handler mHandler = new Handler() {
                public void handleMessage(Message msg) {

                    if (msg.what <= 0) {
                        time = 10;
                        if (pd < 6) {
                            for (TextView m : list) {
                                m.setTextColor(Color.parseColor("#ffffff"));
                                m.setTag("1");
                            }
                            pds = pd;
                            list.get(radioStations.get(pd).getPosition()).setTag("0");
                            list.get(radioStations.get(pd).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                            Message mMessage = new Message();
                            mMessage.what = 0;
                            mMessage.arg1 = radioStations.get(pd).getMFreq();
                            handler.sendMessage(mMessage);
                            //mRadioManager.setFreq(radioStations.get(pd).getMFreq());
                            pd++;
                        } else {
                            pd = 0;
                            pds = pd;
                            if (mBand == 1) {
                                Band++;
                                if (Band < 3) {
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    VarietyBand();
                                    bandTxt.setText(mBandAF[Band]);
                                    RadioActivity.this.getPresenter().initText(Band, mLocation, false);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    Message mMessage = new Message();
                                    mMessage.what = 0;
                                    mMessage.arg1 =radioStations.get(pd).getMFreq();
                                    handler.sendMessage(mMessage);
                                    //mRadioManager.setFreq(radioStations.get(pd).getMFreq());
                                    list.get(radioStations.get(0).getPosition()).setTag("0");
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                } else {

                                    Band = 2;
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    Message mMessage = new Message();
                                    mMessage.what = 0;
                                    mMessage.arg1 =radioStations.get(0).getMFreq();
                                    handler.sendMessage(mMessage);
                                   // mRadioManager.setFreq();
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                    isShortSearch = false;
                                    scheduled.shutdown();
                                    if (!scheduled.isShutdown()) {
                                        scheduled.shutdown();
                                    }
                                }
                            } else {
                                Band++;
                                Log.w("pdsaf______", pd + "");
                                Log.w("pdsaf______", Band + "");
                                if (Band < 5) {
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    VarietyBand();
                                    bandTxt.setText(mBandAF[Band]);
                                    RadioActivity.this.getPresenter().initText(Band, mLocation, false);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    Message mMessage = new Message();
                                    mMessage.what = 0;
                                    mMessage.arg1 =radioStations.get(pd).getMFreq();
                                    handler.sendMessage(mMessage);

                                    list.get(radioStations.get(0).getPosition()).setTag("0");
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                } else {
                                    Band = 4;
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    Message mMessage = new Message();
                                    mMessage.what = 0;
                                    mMessage.arg1 =radioStations.get(0).getMFreq();
                                    handler.sendMessage(mMessage);
                                    //mRadioManager.setFreq();
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                    isShortSearch = false;
                                    scheduled.shutdown();
                                    if (!scheduled.isShutdown()) {
                                        scheduled.shutdown();
                                    }
                                }
                            }

                        }
                    }

                }
            };
            runnable = new Thread() {
                @Override
                public void run() {
                    time--;
                    Message msg = mHandler.obtainMessage(time);
                    mHandler.sendMessage(msg);
                    Message msgs = new Message();
                    m.sendMessage(msgs);
                }
            };
            runnable.setName("OuYang");
            time = 1;
            if (scheduled != null) {
                if (scheduled.isShutdown()) {
                    scheduled = new ScheduledThreadPoolExecutor(1);
                    scheduled.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
                } else {
                    scheduled.shutdown();
                }
            } else {
                scheduled = new ScheduledThreadPoolExecutor(1);
                scheduled.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
            }
        } else {

            isShortSearch = false;
            if (scheduled!=null){

                scheduled.shutdown();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在异步执行 拖动条正在拖动监听
    public void onDataSynEvent(RadioPresenter.DataSynEvent event) {
        if (event.bChanged && mNeedScanStop) {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            mRadioManager.scanStop();
        }
        if (event.bChanged) {
            Message mMessage = new Message();
            mMessage.what = 6;
            mMessage.arg1 = RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation);
            handler.sendMessage(mMessage);
            isMobile = true;
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在异步执行 拖动条停止监听
    public void onDataSynEvents(RadioPresenter.DataSynEvents event) {
        Message mMessage = new Message();
        mMessage.what = 6;
        mMessage.arg1 = RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation);
        handler.sendMessage(mMessage);
        isMobile = false;

    }

    //拖动条开始监听
    @Override
    public void onStartTrackingTouch() {
        if (scheduled != null) {
            isShortSearch = false;
            scheduled.shutdown();
        }
        if (scheduleds != null) {

            scheduleds.shutdown();
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        getPresenter().select(Band, mLocation, first_run);

    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在ui线程执行
    public void getDefault(final RadioWrapper.EventFreqChanged event) {
        if (isSwitch) {
            isSwitch = false;
            if (stringIntegerMap.size() > 0) {
                isSwitch = true;
                Message mMessage = new Message();
                Log.w("isSwitchs", stringIntegerMap.get("scheduled") + "");
                mMessage.what = stringIntegerMap.get("scheduled");
                if (stringIntegerMap.get("scheduled") == 2) {
                    mMessage.arg1 = 0;
                } else {
                    mMessage.arg1 = 3;
                }
                handler.sendMessage(mMessage);
                stringIntegerMap.clear();
            }
        } else {
            Log.w("isSwitchs___dd", stringIntegerMap.get("scheduled") + "");
            Message mMessage = new Message();
            mMessage.what = 5;
            mMessage.arg1 = event.mFreq;
            handler.sendMessage(mMessage);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanResult(final RadioWrapper.EventScanResult event) {
        onScanResult(event.mFreq, event.mSignalStrength);
        Logcat.d("RadioListener, freq = " + event.mFreq + ", signal = " + event.mSignalStrength);
        /*mFreq = event.mFreq;*/
        final int freqStart = RadioWrapper.getFreqStart(mBand, mLocation);
        final int freqStep = RadioWrapper.getFreqStep(mBand, mLocation);
        if (freqStep > 0) {
            mFMFreqSeekBar.setProgress((event.mFreq - freqStart) / freqStep);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String sAgeFormat = getResources().getString(R.string.txt_channel);
                String sFinalAge = String.format(sAgeFormat, RadioWrapper.getFreqString(event.mFreq, mBand, mLocation));
                Log.d("sFinalAge", sFinalAge);
                channelTxt.setText(RadioWrapper.getFreqString(event.mFreq, mBand, mLocation));
            }
        });
    }

    public void onScanResult(int ifreq, int isignal) {
        if (this.mNeedScanStop) {
            Logcat.d("freq = " + ifreq + ", signal = " + isignal);

            if (this.mBand == 1) {
                if (ifreq < FM_FREQ_CRITICAL) {
                    return;
                }
            } else if (ifreq > FM_FREQ_CRITICAL) {
                return;
            }
            if (isignal > 0) {
                if (isScanAll) {
                    this.addToScanAllResultList(ifreq, isignal);
                   // this.addToScanAllResultList(ifreq, isignal);
                } else {
                    isTinTai = true;
                  /*  mFreq=ifreq;
                    mRadioManager.setFreq(ifreq);*/
                    mRadioManager.scanStop();
                }

            }

         if(ToastUtil.isForeground(RadioActivity.this,"RdsActivity")) {
                Log.w("RdsActivity",ifreq+"");
             Intent intent = new Intent();
             intent.setAction(BROADCAST_RDS);
             intent.putExtra("Type", 0);
             intent.putExtra("ifreq", ifreq);
             sendBroadcast(intent);
            }
        /*    */
        }
    }

    @SuppressLint("LongLogTag")
    private void addToScanAllResultList(int ifreq, int isignal) {
        final RadioStation radioStation = new RadioStation();
        radioStation.setMBand(this.mBand);
        radioStation.setMFreq(ifreq);
        radioStation.setLocation(mLocation);
        radioStation.setKind(isignal);
        radioStation.setRdsname(RadioWrapper.getFreqString(ifreq, mBand, mLocation));
        //  radioStation.setPosition(mScanResultList.size()+1 / 6);
        if (!mScanResultList.contains(radioStation)) {
            Log.w("RadioActivity_RadioStations", ifreq + "---" + isignal);
            this.mScanResultList.add(radioStation);
        }
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

    public void onScanEnd(final boolean bsave) {
        if (bsave) {
            isShortSearch=false;
            if (this.mScanResultList != null && this.mScanResultList.size() > 0) {
                removeDuplicate(mScanResultList);
                /*  if (mScanResultList.size())*/
                if (mBand == 1) {
                    for (int i = mScanResultList.size() - 1; i < 18; i++) {
                        RadioStation radioStation = new RadioStation();
                        radioStation.setMFreq(RadioWrapper.getFreqStart(1, mLocation));
                        radioStation.setSelect(false);
                        radioStation.setMBand(1);
                        radioStation.setPosition(i / 2);
                        radioStation.setName(RadioWrapper.getFreqString(RadioWrapper.getFreqStart(1, mLocation), 1, mLocation));
                        radioStation.setRdsname(RadioWrapper.getFreqString(RadioWrapper.getFreqStart(1, mLocation), 1, mLocation));
                        radioStation.setLocation(mLocation);
                        radioStation.setFrequency(Band);
                        mScanResultList.add(radioStation);
                    }
                } else {
                    for (int i = mScanResultList.size() - 1; i < 12; i++) {
                        RadioStation radioStation = new RadioStation();
                        radioStation.setMFreq(RadioWrapper.getFreqStart(0, mLocation));
                        radioStation.setSelect(false);
                        radioStation.setMBand(0);
                        radioStation.setPosition(i / 2);
                        radioStation.setName(RadioWrapper.getFreqString(RadioWrapper.getFreqStart(0, mLocation), 0, mLocation));
                        radioStation.setRdsname(RadioWrapper.getFreqString(RadioWrapper.getFreqStart(0, mLocation), 0, mLocation));
                        radioStation.setLocation(mLocation);
                        radioStation.setFrequency(Band);
                        mScanResultList.add(radioStation);
                    }
                }

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
                Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
                getPresenter().save(mScanResultList, mBand, Band, mLocation, first_run);
                Message mMessage = new Message();
                mMessage.what = 0;
                mMessage.arg1 =mScanResultList.get(0).getMFreq();
                handler.sendMessage(mMessage);
                //this.mRadioManager.setFreq();
                list.get(0).setTextColor(Color.parseColor("#FF0B49E7"));
            } else {
                if (isTermination) {
                    mFreq = mRadioManager.getFreq();
                } else {
                    mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
                    isChange=false;
                    Message mMessage = new Message();
                    mMessage.what = 0;
                    mMessage.arg1 =mFreq;
                    handler.sendMessage(mMessage);
                  //  mRadioManager.setFreq();
                }
                delete(mBand, mLocation);
                radioStations = getPresenter().queryFrequency(Band, mLocation);
                init(radioStations);
                ShowToast(this, getString(R.string.search_no_result));
                //  Toast.makeText(this, R.string.search_no_result, Toast.LENGTH_SHORT).show();
            }
            if (mBand == 0) {
                SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                editor.putInt("mFreq" + 3, 0);
                editor.putInt("mFreq" + 4, 0);
                editor.commit();
            } else {
                SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                editor.putInt("mFreq" + 0, 0);
                editor.putInt("mFreq" + 1, 0);
                editor.putInt("mFreq" + 2, 0);
                editor.commit();
            }

        } else {
            if (isTinTai) {
                SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_PRIVATE).edit();
                editor.putInt("mFreq" + Band, mRadioManager.getFreq());
                Log.w("RadioActivity_mFreq", mRadioManager.getFreq() + "");
                editor.commit();
                mFreq = mRadioManager.getFreq();
                //  mRadioManager.setFreq(ifreq);
                isTinTai = false;
            }
        }
    }

    //搜索开始
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanStart(final RadioWrapper.EventScanStart event) {
        if (scheduleds != null) {
            scheduleds.shutdown();
        }
        radioStations = getPresenter().queryFrequency(Band, mLocation);
        init(radioStations);
        mNeedScanStop = true;
        mScanResultList.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanEnd(final RadioWrapper.EventScanEnd event) {
        onScanEnd(event.mScanAll);

        if (event.mScanAll) {
            isTermination = false;
        }
        isTermination = false;
        mNeedScanStop = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //
    public void onScanAbort(final RadioWrapper.EventScanAbort event) {//
        isShortSearch=true;
        if (event.mScanAll) {
            isTermination = true;

            // mRadioManager.setFreq(mScanResultList.get(0).getMFreq());
        }
        onScanEnd(event.mScanAll);
        mNeedScanStop = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRdsMaskChanged(RadioWrapper.EventRdsMask event) {
        Logcat.d("RadioListener, pi = " + event.mPI + ", freq = " + event.mFreq + ", pty = " + event.mPTY + ", tp = " + event.mTP + ", ta = " + event.mTA);
        onRdsMaskChanged(event.mPI, event.mFreq, event.mPTY, event.mTP, event.mTA);
    }

    private void onRdsMaskChanged(int mPI, int mFreq, int mPTY, int mTP, int mTA) {
        Log.w("RadioListener", mTP + "; mfreq"+mFreq+"; pty"+mPTY+" ; mtp "+mTP+" ;mta"+mTA);
        if (mTP == 1) {
            txt_rt.setVisibility(View.VISIBLE);
        } else {
            txt_rt.setVisibility(View.GONE);
        }
        if (mTA == 1) {
            taTxt.setVisibility(View.VISIBLE);
        } else {
            taTxt.setVisibility(View.GONE);
        }
        RadioCacheUtil.getInstance(getApplicationContext()).setTA(mTP);
        //RadioCacheUtil.getInstance(getApplicationContext()).setAF(mTA);
        mPty = mPTY;
       /* if(ToastUtil.isForeground(RadioActivity.this,"RdsActivity")) {
            Intent intent = new Intent();
            intent.setAction(BROADCAST_RDS);
            intent.putExtra("Type", 1);
            intent.putExtra("mPty", mPTY);
            intent.putExtra("mTP", mTP);
            intent.putExtra("mTA", mTA);
            sendBroadcast(intent);
        }*/
        /**/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRdsPsChanged(RadioWrapper.EventRdsPs event) {
        Log.w("RadioListener", event.mText);
        //Logcat.d("RadioListener, pi = " + event.mPI + ", freq = " + event.mFreq + ", ps = " + event.mText);
        // Logcat.d("RadioListener, pi = " + event.mPI + ", freq = " + event.mFreq + ", ps = " + event.mText);
        onRdsPsChanged(event.mPI, event.mFreq, event.mText);
    }

    private void onRdsPsChanged(int mPI, int mFreq, String mText) {
        txt_name.setText(mText);
       /* if(ToastUtil.isForeground(RadioActivity.this,"RdsActivity")) {
            Intent intent = new Intent();
            intent.setAction(BROADCAST_RDS);
            intent.putExtra("Type", 2);
            intent.putExtra("ps", mText);
            sendBroadcast(intent);
        }*/

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRdsRtChanged(RadioWrapper.EventRdsRt event) {
        Log.w("RadioListener", event.mText);
        // Logcat.d("RadioListener, pi = " + event.mPI + ", freq = " + event.mFreq + ", rt = " + event.mText);
        onRdsRtChanged(event.mPI, event.mFreq, event.mText);
    }

    private void onRdsRtChanged(int mPI, int mFreq, String mText) {
     /*   if(ToastUtil.isForeground(RadioActivity.this,"RdsActivity")) {
            Intent intent = new Intent();
            intent.setAction(BROADCAST_RDS);
            intent.putExtra("Type", 3);
            intent.putExtra("Rt", mText);
            sendBroadcast(intent);
        }*/
      /*  */
        //   Logcat.d("RadioListener, pi = " + event.mPI + ", freq = " + event.mFreq + ", rt = " + event.mText);
    }

    @Override
    public RadioContract.Presenter createPresenter() {
        return new RadioPresenter();
    }


    @Override
    public RadioContract.View getUiImplement() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        unregisterReceiver(myBroadcastReceiver);
        Log.w("RadioAcitivity", "onDestroy");
        EventBus.getDefault().unregister(this);
        Logcat.d("mRadioManager = " + this.mRadioManager);
        mAudioManager.abandonAudioFocus(mAudioFocusChange);
        mJancarManager.abandonKeyFocus(keyFocusListener.asBinder());
        if (scheduleds != null) {
            scheduleds.shutdown();
        }
        manager = null;
        keyFocusListener = null;
      mJancarManager = null;
        resetFreqStart();
        closeManager();
        SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_PRIVATE).edit();
        editor.putInt("mFreq" + Band, mFreq);
        editor.commit();
       SystemProperties.set(PROPERTY_GIS_COMPENNSATE, "0");
        radioStations.clear();
        list.clear();
        RuleView.clear();
        stringIntegerMap.clear();
        radioStations = null;
        list = null;

        RuleView = null;
        stringIntegerMap = null;
    }

    private void resetFreqStart() {
        if (mRadioManager != null && mRadioManager.getScanAction() == 1) {
            RadioCacheUtil.getInstance(getApplicationContext()).setLastFreq(mBand, RadioWrapper.getFreqStart(mBand, RadioCacheUtil.getInstance(getApplicationContext()).getLocation()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRadioManager != null) {
            Logcat.d(" open");
            mRadioManager.open();
        }
    }

    private void closeManager() {
        if (mRadioManager != null) {
            mRadioManager.close();
            mRadioManager.disconnect();
            mRadioManager = null;
        }
    }

    @Override
    public void onServiceConnected() {
        Logcat.d("get Current Thread  = " + Thread.currentThread().getName());
        initCurFreq();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onControlEvent(RadioWrapper.EventControl event) {
        if (getPresenter().getRadioListener() != null) {
            switch (event.mAction) {
                case RadioWrapper.EventControl.Action.QUIT_APP:
                    Logcat.d("RadioListener, quitApp");
                    //   this.showNotification(false);

                    break;
                case RadioWrapper.EventControl.Action.RESUME:
                    Logcat.d("RadioListener, resume");
                    //  this.showNotification(true);
                    if (this.mRadioManager != null) {
                        this.mRadioManager.open();
                    }
                   // this.showNotification(true);
                    break;
                case RadioWrapper.EventControl.Action.STOP:
                    Logcat.d("RadioListener, stop");
                    //  this.showNotification(false);
                    break;
                case RadioWrapper.EventControl.Action.NEXT:
                    if (this.mRadioManager != null) {
                        this.mRadioManager.step(1);
                    }
                    break;
                case RadioWrapper.EventControl.Action.PREV:
                    if (this.mRadioManager != null) {
                        this.mRadioManager.step(-1);
                    }
                default:
                    Logcat.d("Unknown action " + event.mAction);
                    break;
            }
        }
    }

    protected void initCurFreq() {
        final int location = RadioCacheUtil.getInstance(getApplicationContext()).getLocation();

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
            Message mMessage = new Message();
            mMessage.what = 0;
            mMessage.arg1 =this.mInitFreq;
            handler.sendMessage(mMessage);
          //  this.mRadioManager.setFreq();
            this.mRadioManager.open();
        }
        // this.showNotification(true);
    }

    int anInt;

    @Override
    public void onServiceDisconnected() {
        Logcat.d("get Current Thread  = sd" + Thread.currentThread().getName());
    }


    @Override
    public RadioManager getRadioManager() {
        return mRadioManager;
    }

    private keyFocuser keyFocusListener = new keyFocuser() {

        @Override
        public int getDescriptor() throws RemoteException {
            return super.getDescriptor();
        }

        @Override
        public boolean OnKeyEvent(int key, int state) {

            boolean bRet = true;
            KeyDef.KeyType keyType = KeyDef.KeyType.nativeToType(key);
            KeyDef.KeyAction keyAction = KeyDef.KeyAction.nativeToType(state);

            switch (keyType) {
                case KEY_PREV:
                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanUp();
                    } else {
                        if (keyAction == KEY_ACTION_UP) {
                            scanStop();
                            s();
                            favoriteNext();
                        }
                    }
                    break;
                case KEY_NEXT:
                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanDown();
                    } else {
                        if (keyAction == KEY_ACTION_UP) {
                            scanStop();
                            s();

                            favoritePrev();
                        }
                    }
                    break;
                case KEY_AS:
                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanDown();
                    } else {

                        if (keyAction == KEY_ACTION_UP) {

                            scanStop();
                            s();
                            isChange=true;
                            if (handler != null) {
                                Message mMessage = new Message();
                                mMessage.what = 10;
                                handler.sendMessage(mMessage);
                            }
                            SharedPreferences sharedPreferenc = getActivity().getSharedPreferences("FirstRun", 0);
                            sharedPreferenc.edit().putBoolean("Firsts", false).commit();
                            if (!mNeedScanStop) {
                                isScanAll = true;
                                if (RadioActivity.this.mRadioManager != null) {
                                    RadioActivity.this.mRadioManager.scanAll();
                                }
                            }
                        }
                    }
                    break;
                case KEY_AM:

                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanDown();
                    } else {

                        if (keyAction == KEY_ACTION_UP) {
                            scanStop();
                            s();
                            isChange=true;
                            if (isAppOnForeground()) {
                                isSwitc = true;
                                isSwitch = true;
                                isSwitchs = false;
                                isSetting = true;
                                Message mMessage = new Message();
                                mMessage.what = 1;
                                mMessage.arg1 = 3;
                                handler.sendMessage(mMessage);
                            } else {
                                Message mMessage = new Message();
                                mMessage.what = 11;
                                mMessage.obj = "am";
                                handler.sendMessage(mMessage);

                            }

                        }
                    }
                    break;
                case KEY_FM:
                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanDown();
                    } else {

                        if (keyAction == KEY_ACTION_UP) {
                            scanStop();
                            s();
                            if (isAppOnForeground()) {
                                isSwitc = true;
                                isSwitch = true;
                                isSetting = true;
                                Message mMessage = new Message();
                                mMessage.what = 2;
                                mMessage.arg1 = 0;
                                handler.sendMessage(mMessage);
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
                    break;
                case KEY_SCAN:

                    break;
                default:
                    bRet = false;
                    break;
            }
            return bRet;
        }


        @Override
        public void OnKeyFocusChange(int ifocus) {
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w("RadioAcitivity", "onNewIntent");
        onDoIntent(intent, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // outState.putInt("A",a);
        Log.w("RadioAcitivity", "onSaveInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.w("RadioAcitivity", "onRestoreInstanceState");
    }

    private void onDoIntent(final Intent intent, boolean bfrist) {

        try {
            String strMsg = null;
            if (intent != null) {
                strMsg = intent.getStringExtra("device");
                if (strMsg != null) {
                    Logcat.d("onDoIntent -> " + strMsg);
                    if (strMsg.equals("fm")) {

                        Message mMessage = new Message();
                        mMessage.what = 2;
                        mMessage.arg1 = 0;
                        isSwitch = false;
                        handler.sendMessage(mMessage);
                        stringIntegerMap.clear();
                    } else if (strMsg.equals("am")) {
                        isSwitch = false;
                        Message mMessage = new Message();
                        mMessage.what = 1;
                        mMessage.arg1 = 3;
                        handler.sendMessage(mMessage);
                        stringIntegerMap.clear();
                    } else if (strMsg.equals("band")) {

                    } else if (strMsg.equals("freq")) {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAppOnForeground() {
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


    public boolean getFavStationVaild() {
        boolean bRet = false;
        if (radioStations != null) {
            bRet = radioStations.size() > 0 ? true : false;
        }
        if (radioStations.size() > 3) {

            if (radioStations.get(0).getMFreq() == radioStations.get(1).getMFreq()) {
                bRet = false;
            }
        }
        return bRet;
    }

    private void favoritePrev() {
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
                Message mMessage = new Message();
                mMessage.what = 12;
                mMessage.arg1 = Band;
                handler.sendMessage(mMessage);
            }
            setMyFavoriteItem(d);
        }
    }

    public void favoriteNext() {
       /* Logcat.d("mFavStationList size = " + this.mFavStationList.size());
        this.mNeedPopUp = true;*/
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
                Message mMessage = new Message();
                mMessage.what = 12;
                mMessage.arg1 = Band;
                handler.sendMessage(mMessage);
            }
            setMyFavoriteItem(d);
        }
    }

    protected void setMyFavoriteItem(final int index) {
        this.mFreq = this.radioStations.get(index).getMFreq();
        Message mMessage = new Message();
        mMessage.what = 9;
        mMessage.arg1 = index;
        handler.sendMessage(mMessage);
        if (this.mRadioManager != null) {
            Message mMessages = new Message();
            mMessages.what = 0;
            mMessages.arg1 =this.mFreq;
            handler.sendMessage(mMessages);
            //this.mRadioManager.setFreq(this.mFreq);
        }

    }

   /* protected void showNotification(final boolean bshow) {
        Logcat.d("show = " + bshow);
        if (bshow) {
            if (this.mRadioNotification == null) {
                (this.mRadioNotification = new RadioNotification()).onCreate(this, new RadioNotification.NotificationCallback() {
                    @Override
                    public void onExit() {
                        RadioActivity.this.finish();
                        //  AppManager.getAppManager().finishAllActivity();
                    }

                    @Override
                    public void onNext() {
                        RadioActivity.this.getPresenter().getRadioListener().next();
                    }

                    @Override
                    public void onPrev() {
                        RadioActivity.this.getPresenter().getRadioListener().prev();
                    }
                });
            }

            if (this.mRadioNotification != null) {
                this.mRadioNotification.show();
                if (this.mRadioManager == null || this.mRadioManager.getScanAction() == 0) {
                    SharedPreferences read = getSharedPreferences("Radio", MODE_PRIVATE);

                    int mFreq = read.getInt("mFreq" + Band, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
                    this.updateNotification(mFreq);
                    return;
                }
                this.mRadioNotification.updateScan();
            }
        } else if (this.mRadioNotification != null) {
            this.mRadioNotification.hide();
            this.mRadioNotification.onDestroy();
            this.mRadioNotification = null;
        }
    }
*/
    protected void updateNotification(int mFreq) {
        if (mRadioNotification != null) {
            mRadioNotification.update(mFreq,
                    mBand, RadioCacheUtil.getInstance(getApplicationContext()).getLocation(), Band);
        }
    }

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int Type = intent.getIntExtra("Type", 0);
        switch (Type) {
            case 1:
                int Pty = intent.getIntExtra("Pty", 0);
                mRadioManager.selectRdsPty(Pty);
                Log.w(TAG, Pty + "");
                break;
            case 2:
                boolean RdsTa = intent.getBooleanExtra("RdsTa", false);
                mRadioManager.selectRdsTa(RdsTa);
                if (RdsTa) {
                    SharedPreferences sharedPreferencesdd = getActivity().getSharedPreferences("FirstRun", 0);
                    sharedPreferencesdd.edit().putBoolean("Firsts", false).commit();

                    isScanAll = false;
                    if (mRadioManager != null) {
                        mRadioManager.scanAll();
                    }
                    taTxt.setVisibility(View.VISIBLE);
                } else {
                    scanStop();
                    taTxt.setVisibility(View.GONE);
                }
                Log.w(TAG, RdsTa + "");
                break;
            case 3:
                boolean RdsAf = intent.getBooleanExtra("RdsAf", false);
                mRadioManager.selectRdsAf(RdsAf);
                Log.w(TAG, RdsAf + "");
                if (RdsAf) {
                    afTxt.setVisibility(View.VISIBLE);
                } else {
                    afTxt.setVisibility(View.GONE);
                }
                break;
        }
    }
}
}