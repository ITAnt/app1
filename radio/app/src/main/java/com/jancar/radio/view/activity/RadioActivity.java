package com.jancar.radio.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.os.RemoteException;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.BaseManager;
import com.jancar.JancarServer;
import com.jancar.audio.AudioEffectParam;
import com.jancar.key.KeyDef;
import com.jancar.key.keyFocuser;
import com.jancar.radio.BuildConfig;
import com.jancar.radio.R;
import com.jancar.radio.RadioManager;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.contract.RadioCacheUtil;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.entity.RadioStation;
import com.jancar.radio.notification.RadioNotification;
import com.jancar.radio.presenter.RadioPresenter;
import com.jancar.radio.widget.RuleView;
import com.jancar.utils.Logcat;
import com.ui.mvp.view.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

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

import static com.android.internal.app.IntentForwarderActivity.TAG;
import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_DOWN_LONG;
import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_UP;
import static com.jancar.key.KeyDef.KeyType.KEY_NEXT;
import static com.jancar.radio.listener.utils.RadioStationDaos.delete;
import static com.jancar.radio.listener.utils.RadioStationDaos.deleteRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.queryFrequency;

/**
 * Created by ouyan on 2018/9/19.
 */

public class RadioActivity extends BaseActivity<RadioContract.Presenter, RadioContract.View> implements RadioContract.View, RadioManager.ConnectListener {
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
    @BindView(R.id.btn_right)
    ImageView rightBtn;
    @BindView(R.id.txt_unit)
    TextView unitTxt;
    @BindView(R.id.txt_st)
    TextView stTxt;
    @BindView(R.id.img_original)
    ImageView originalImg;
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
    public static final int PAGE_FM = 1;
    public static final int PAGE_AM = 2;
    List<RadioStation> radioStations;
    List<TextView> list = new ArrayList<>();
    List<RuleView> RuleView = new ArrayList<>();
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
    Map<String, Integer> stringIntegerMap = new HashMap<>();
    boolean isSwitc = false;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
            Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
            switch (msg.what) {
                case 0:
                    mRadioManager.setFreq(msg.arg1);
                    break;
                case 1:

                  /*  isSetting = true;*/
                    // getPresenter().Change(Band, mFreq, mLocation);
                    Band = msg.arg1;
                    VarietyBand();
                    SharedPreferences.Editor editors = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                    editors.putInt("Band", msg.arg1);
                    editors.commit();
                    // mRadioManager.setFreq(msg.arg1);
                    bandTxt.setText(mBandAF[msg.arg1]);
                    getPresenter().initText(msg.arg1, mLocation, first_run);
                    stTxt.setVisibility(View.GONE);
                    break;
                case 2:

                    // getPresenter().Change(Band, mFreq, mLocation);
                    Band = msg.arg1;
                    VarietyBand();
                    stTxt.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                    edito.putInt("Band", msg.arg1);
                    edito.commit();
                    bandTxt.setText(mBandAF[msg.arg1]);
                    getPresenter().initText(msg.arg1, mLocation, first_run);
                    s();
                    break;
                case 4:
                    if (Band >= 3) {
                        stTxt.setVisibility(View.GONE);
                    } else {
                        stTxt.setVisibility(View.VISIBLE);
                    }
                    bandTxt.setText(mBandAF[Band]);
                    getPresenter().initText(Band, mLocation, first_run);
                   /* s();*/
                    break;
                case 5:
                    isSwitch = false;
                    isSetting = false;
                    // swapBandImg.setEnabled(true);
                    if (!isMobile) {
                        final int freqStart = RadioWrapper.getFreqStart(mBand, mLocation);
                        final int freqStep = RadioWrapper.getFreqStep(mBand, mLocation);
                        if (freqStep > 0) {
                            mFMFreqSeekBar.setProgress((msg.arg1 - freqStart) / freqStep);
                        }
                    }

                   /* boolean b = mRadioManager.getStereo();
                    if (b) {
                        stTxt.setTextColor(Color.parseColor("#FF0B49E7"));
                    } else {
                        stTxt.setTextColor(Color.parseColor("#ffffff"));
                    }*/
                    updateNotification(msg.arg1);
                    // getPresenter().Change(msg.arg1, radioStations);
                    mFreq = msg.arg1;
                    SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                    editor.putInt("mFreq" + Band + mLocation, msg.arg1);
                    Log.w("RadioActivity_mFreq", msg.arg1 + "");
                    editor.commit();
                    channelTxt.setText(RadioWrapper.getFreqString(msg.arg1, mBand, mLocation));
                    break;
                case 6:
                    mRadioManager.setFreq(msg.arg1);
                    break;
                case 7:


                    break;
                case 8:
                    if (((Boolean) msg.obj)) {
                        stTxt.setTextColor(Color.parseColor("#FF0B49E7"));
                    } else {
                        stTxt.setTextColor(Color.parseColor("#ffffff"));
                    }
                  /*  tiems--;
                    if (tiems <= 0) {
                        tiems = 5;
                        if (scheduleds != null) {
                            scheduleds.shutdown();
                        }
                    }*/

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
            }
        }
    };
    private RadioAudioFocusChange mAudioFocusChange;
    ScheduledExecutorService scheduled = null;
    ScheduledExecutorService scheduleds = null;
    Runnable runnable = null;
    Runnable runnables = null;

    private class RadioAudioFocusChange implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logcat.d("onAudioFocusChange, focusChange = " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(mAudioFocusChange);
                mJancarManager.abandonKeyFocus(keyFocusListener);
                if (scheduleds != null) {
                    scheduleds.shutdown();

                }
            }
        }
    }

   /* public RadioActivity() {
        // mHandler = new SafeHandler(this);
        mAudioFocusChange = new RadioAudioFocusChange();
    }*/

    @Override
    protected void onPause() {
        super.onPause();
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
        unbinder = ButterKnife.bind(this);
        mAudioFocusChange = new RadioAudioFocusChange();
        initData();
        onDoIntent(getIntent(), false);
        s();
    }

    public void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//获取状态栏高度
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            View rectView = new View(this);
//绘制一个和状态栏一样高的矩形，并添加到视图中
            LinearLayout.LayoutParams params
                    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            ;
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

    @Override
    protected void onStart() {
        super.onStart();

    }

    @SuppressLint("WrongConstant")
    private void initData() {
        mJancarManager = (JancarServer) getSystemService("jancar_manager");
        mJancarManager.requestKeyFocus(keyFocusListener);
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        mLocation = RadioCacheUtil.getInstance().getLocation();
        mRadioManager = new RadioManager((Context) this, this, getPresenter().getRadioListener(), getPackageName());
        SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
        Band = read.getInt("Band", 0);
        //步骤2：获取文件中的值
        //    swapBandImg.setTag();
        bandTxt.setText(mBandAF[Band]);
        EventBus.getDefault().register(this);//订阅
        mFMFreqSeekBar.setOnSeekBarChangeListener(getPresenter());
        VarietyBand();
        getPresenter().initText(Band, mLocation, first_run);

        initReceiver();

    }

    private void initReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        this.registerReceiver(getPresenter().getmReceiver(), intentFilter);
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
                    //  gv_1.setValue(418f, 1812f, 1132f, 34, 5, true, false);
                    break;
            }
            // gv_1.setValue(430.2f, 1711.8f, 1166f, 30.6f, 5, false);
            // gv_1.setValue(439.2f, 1693.8f, 1135.4f, 30.6f, 5, false);
            mFMFreqSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                    layoutParams.setMargins(11, 0, 28, 30);
                    mFMFreqSeekBar.setLayoutParams(layoutParams);
                }
            });


            // gv_1.setValue(418f, 1812f, 1166f, 34, 5, true);
        } else {
            unitTxt.setText(R.string.unit_mhz);
            RuleView.get(3).setVisibility(View.VISIBLE);
            //  gv_1.setValue(84.6f, 109.8f, 97.8f, 0.6f, 5, false, false);
            switch (mLocation) {
                case 2:
                case 3:
                    mFMFreqSeekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                            layoutParams.setMargins(26, 0, 33, 30);
                            mFMFreqSeekBar.setLayoutParams(layoutParams);
                        }
                    });
                    break;
                default:
                    mFMFreqSeekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                            layoutParams.setMargins(26, 0, 30, 30);
                            mFMFreqSeekBar.setLayoutParams(layoutParams);
                        }
                    });
                    break;
            }
            this.mBand = 1;
        }
        if (isSwitc) {
            mRadioManager.setBand(mBand);
        }
    }

    int times = 10;

    public void s() {

    /*    runnables = new Runnable() {
            @Override
            public void run() {
                Boolean b = mRadioManager.getStereo();
                Message mMessage = new Message();
                mMessage.what = 8;
                mMessage.obj = b;
                handler.sendMessage(mMessage);
            }
        };
        if (scheduled != null) {
            if (scheduleds.isShutdown()) {
                scheduleds = new ScheduledThreadPoolExecutor(1);
                scheduleds.scheduleAtFixedRate(runnables, 0, 1, TimeUnit.SECONDS);
            } else {
                scheduleds.shutdown();
            }
        } else {
            scheduleds = new ScheduledThreadPoolExecutor(1);
            scheduleds.scheduleAtFixedRate(runnables, 0, 1, TimeUnit.SECONDS);
        }*/
        //初始化一个线程池大小为 1 的 ScheduledExecutorService


    }

    @Override
    public void initText(List<RadioStation> radioStations) {//
      /*  if (scheduleds!=null){
            scheduleds.shutdown();
            scheduleds=null;
        }*/

        this.radioStations = radioStations;
        if (radioStations.size() <= 0) {
            for (TextView mTextView : list) {
                mTextView.setText("");
                mTextView.setTag("1");
                mTextView.setTextColor(Color.parseColor("#ffffff"));
            }
            SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
            mFreq = read.getInt("mFreq" + Band + mLocation, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
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
        String sAgeFormat = getResources().getString(R.string.txt_channel);
        String sFinalAge = String.format(sAgeFormat, RadioWrapper.getFreqString(mFreq, mBand, mLocation));
        Log.d("sFinalAge", sFinalAge);
        channelTxt.setText(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
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
            SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
            Band = read.getInt("Band", 0);
            if (Band >= 3) {
                mBand = 0;
            } else {
                mBand = 1;
            }
            init();
            init();

            Log.w("mfreqq", "mFreq" + Band + mLocation + "_____" + mFreq);
        }
    }

    public void init() {
        SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
        mFreq = read.getInt("mFreq" + Band + mLocation, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
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
        mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mJancarManager.requestKeyFocus(keyFocusListener);
    }

    @Override
    public void abandonRadioFocus() {
        mAudioManager.abandonAudioFocus(mAudioFocusChange);
        mJancarManager.abandonKeyFocus(keyFocusListener);
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
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
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
                getPresenter().select(Band, mLocation, first_run);
                SharedPreferences sharedPreferenc = getActivity().getSharedPreferences("FirstRun", 0);
                sharedPreferenc.edit().putBoolean("Firsts", false).commit();

                if (!mNeedScanStop) {
                    isScanAll = true;
                    isShortSearch = true;
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

    @OnClick({R.id.txt_channel_list_one, R.id.txt_channel_list_two, R.id.txt_channel_list_three,
            R.id.txt_channel_list_four, R.id.txt_channel_list_fives, R.id.txt_channel_list_six,
            R.id.btn_left, R.id.btn_right, R.id.img_swap_band, R.id.img_search, R.id.img_st})
    public void OnCLick(View view) {
    /*    if (isSwitch){
            isSwitch=false;
            mRadioManager.setFreq(mFreq);
        }*/
        if (scheduleds != null) {
            scheduleds.shutdown();
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        if (mNeedScanStop) {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            mRadioManager.scanStop();
            //mNeedScanStop = false;
        }
        if (isShortSearch) {
            if (scheduled != null) {
                scheduled.shutdown();
            }
        }
        switch (view.getId()) {
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
                s();
                isShortSearch = false;
                getPresenter().select(0, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_two:
                s(); isShortSearch = false;
                getPresenter().select(1, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_three:
                s(); isShortSearch = false;
                getPresenter().select(2, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_four:
                s(); isShortSearch = false;
                getPresenter().select(3, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_fives:
                s(); isShortSearch = false;
                getPresenter().select(4, Band, mLocation, first_run);
                break;
            case R.id.txt_channel_list_six:
                s(); isShortSearch = false;
                getPresenter().select(5, Band, mLocation, first_run);
                break;
            case R.id.btn_left:
                s(); isShortSearch = false;
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
                s(); isShortSearch = false;
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
                //  getPresenter().Change(Band, mFreq, mLocation);
                if (Band == 2 || Band == 4) {
                    isSwitc = true;
                    if (Band == 2) {
                        SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
                        mFreq = read.getInt("mFreq" + 0 + mLocation, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
                        if (mFreq < RadioWrapper.getFreqStart(this.mBand, this.mLocation)) {
                            mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
                        }
                        mRadioManager.setFreq(mFreq);
                    } else if (Band == 4) {
                        SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
                        mFreq = read.getInt("mFreq" + 3 + mLocation, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
                        if (mFreq < RadioWrapper.getFreqStart(this.mBand, this.mLocation)) {
                            mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
                        }
                        mRadioManager.setFreq(mFreq);
                    }


                } else {
                    isSetting = true;
                }
                Band++;
                int band = Band % 5;
                Band = band;
                if (band >= 3) {
                    if (scheduleds != null) {
                        scheduleds.shutdown();
                    }
                } else {
                    s();
                }

                Log.w("RadioActivity", Band + "");
                setBand();
                VarietyBand();
                Log.w("RadioActivity", mBand + "");
                SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
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
                            mRadioManager.setFreq(radioStations.get(pd).getMFreq());
                            pd++;
                        } else {
                            pd = 0;
                            pds = pd;
                            if (mBand == 1) {
                                Band++;
                                if (Band < 3) {
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    VarietyBand();
                                    bandTxt.setText(mBandAF[Band]);
                                    RadioActivity.this.getPresenter().initText(Band, mLocation, false);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    mRadioManager.setFreq(radioStations.get(pd).getMFreq());
                                    list.get(radioStations.get(0).getPosition()).setTag("0");
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                } else {

                                    Band = 2;
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    mRadioManager.setFreq(radioStations.get(0).getMFreq());
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
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    VarietyBand();
                                    bandTxt.setText(mBandAF[Band]);
                                    RadioActivity.this.getPresenter().initText(Band, mLocation, false);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    mRadioManager.setFreq(radioStations.get(pd).getMFreq());
                                    list.get(radioStations.get(0).getPosition()).setTag("0");
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                } else {
                                    Band = 4;
                                    SharedPreferences.Editor edito = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                                    edito.putInt("Band", Band);
                                    edito.commit();
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    mRadioManager.setFreq(radioStations.get(0).getMFreq());
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
            runnable = new Runnable() {
                @Override
                public void run() {
                    time--;
                    Message msg = mHandler.obtainMessage(time);
                    mHandler.sendMessage(msg);
                    Message msgs = new Message();
                    m.sendMessage(msgs);
                }
            };
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
            scheduled.shutdown();
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
           /* final int freq =*/
      /*      if (mRadioManager != null) {
                mRadioManager.setFreq(freq);
            }*/
            isMobile = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在异步执行 拖动条停止监听
    public void onDataSynEvents(RadioPresenter.DataSynEvents event) {
        // final int freq = RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation);
        Message mMessage = new Message();
        mMessage.what = 6;
        mMessage.arg1 = RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation);

        s();
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
        // getPresenter().Change(event.mFreq, radioStations, event.mSignalStrength);
        //  getPresenter().Change(Band, event.mFreq ,mLocation);
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
          /*  if (this.mRadioManager != null) {
                Logcat.d(RadioWrapper.ScanAction.getName(this.mRadioManager.getScanAction()));
            }*/
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
                } else {
                    isTinTai = true;
                  /*  mFreq=ifreq;
                    mRadioManager.setFreq(ifreq);*/
                    mRadioManager.scanStop();
                }

            }

        }
    }

    private void addToScanAllResultList(int ifreq, int isignal) {
        final RadioStation radioStation = new RadioStation();
        radioStation.setMBand(this.mBand);
        radioStation.setMFreq(ifreq);
        radioStation.setLocation(mLocation);
        radioStation.setKind(isignal);
        radioStation.setRdsname(RadioWrapper.getFreqString(ifreq, mBand, mLocation));

/*
    */
        if (!this.mScanResultList.contains(radioStation)) {
            this.mScanResultList.add(radioStation);
        }
        //   mScanResultList.add(radioStation);
    }

    public void onScanEnd(final boolean bsave) {
        s();
        if (bsave) {
            if (this.mScanResultList != null && this.mScanResultList.size() > 0) {
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
                        radioStation.setMFreq(RadioWrapper.getFreqStart(1, mLocation));
                        radioStation.setSelect(false);
                        radioStation.setMBand(0);
                        radioStation.setPosition(i / 2);
                        radioStation.setName(RadioWrapper.getFreqString(RadioWrapper.getFreqStart(1, mLocation), 1, mLocation));
                        radioStation.setRdsname(RadioWrapper.getFreqString(RadioWrapper.getFreqStart(1, mLocation), 1, mLocation));
                        radioStation.setLocation(mLocation);
                        radioStation.setFrequency(Band);
                        mScanResultList.add(radioStation);
                    }
                }

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
                Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
                getPresenter().save(mScanResultList, mBand, Band, mLocation, first_run);
                this.mRadioManager.setFreq(mScanResultList.get(0).getMFreq());
                list.get(0).setTextColor(Color.parseColor("#FF0B49E7"));
            } else {
                if (isTermination) {
                    mFreq = mRadioManager.getFreq();
                } else {
                    mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
                    mRadioManager.setFreq(mFreq);
                }
                delete(mBand, mLocation);
                radioStations = getPresenter().queryFrequency(Band, mLocation);
                init(radioStations);
                Toast.makeText(this, R.string.search_no_result, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isTinTai) {
                SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                editor.putInt("mFreq" + Band + mLocation, mRadioManager.getFreq());
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
        /*delete(mBand, mLocation);*/
        radioStations = getPresenter().queryFrequency(Band, mLocation);
        init(radioStations);
        mNeedScanStop = true;
        mScanResultList.clear();

        // this.showNotification(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanEnd(final RadioWrapper.EventScanEnd event) {
        onScanEnd(event.mScanAll);
        if (event.mScanAll) {
            isTermination = false;
            //mFreq = mRadioManager.getFreq();
            // mRadioManager.setFreq(mScanResultList.get(0).getMFreq());
        }
        isTermination = false;
        mNeedScanStop = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //
    public void onScanAbort(final RadioWrapper.EventScanAbort event) {//
        if (event.mScanAll) {
            isTermination = true;

            // mRadioManager.setFreq(mScanResultList.get(0).getMFreq());
        }
        onScanEnd(event.mScanAll);
        mNeedScanStop = false;
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
        EventBus.getDefault().unregister(this);
        Logcat.d("mRadioManager = " + this.mRadioManager);
        mAudioManager.abandonAudioFocus(mAudioFocusChange);
        mJancarManager.abandonKeyFocus(keyFocusListener);
        if (scheduleds != null) {
            scheduleds.shutdown();
        }
        unregisterReceiver(getPresenter().getmReceiver());
        resetFreqStart();
        closeManager();
        SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
        editor.putInt("mFreq" + Band + mLocation, mFreq);
        editor.commit();
        //getPresenter().Change(radioStations);
        //  showNotification(false);
        ;
        //   AppManager.getAppManager().removeActivity(this);
    }

    private void resetFreqStart() {
        if (mRadioManager != null && mRadioManager.getScanAction() == 1) {
            RadioCacheUtil.getInstance().setLastFreq(mBand, RadioWrapper.getFreqStart(mBand, RadioCacheUtil.getInstance().getLocation()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRadioManager != null) {
            Logcat.d(" open");
            mRadioManager.open();
        }
        // showNotification(true);
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
                    this.showNotification(true);
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
        final int location = RadioCacheUtil.getInstance().getLocation();

        if (RadioWrapper.isFreqValid(mFreq, location)) {
            this.mInitFreq = mFreq;
            RadioCacheUtil.getInstance().setLastPage(getPresenter().getBandPage(this.mBand));
            RadioCacheUtil.getInstance().setLastFreq(this.mBand, this.mInitFreq);
        } else {
            final int lastPage = RadioCacheUtil.getInstance().getLastPage(1);
            if (-1 != mBand) {
                RadioCacheUtil.getInstance().setLastPage(getPresenter().getBandPage(this.mBand));
            } else {
                this.mBand = RadioCacheUtil.getInstance().getBand();
            }

            if (lastPage == PAGE_FM || PAGE_AM == lastPage) {
                this.mInitFreq = RadioCacheUtil.getInstance().getLastFreq(this.mBand);
            } else {
                this.mInitFreq = RadioCacheUtil.getInstance().getLastFavoritesFreq();
            }
        }
        if (this.mRadioManager != null) {
            this.mRadioManager.selectRdsTa(1 == RadioCacheUtil.getInstance().getTA());
            final RadioManager mRadioManager = this.mRadioManager;
            final int af = RadioCacheUtil.getInstance().getAF();
            boolean bAf = false;
            if (af != 0) {
                bAf = true;
            }
            mRadioManager.selectRdsAf(bAf);
            this.mRadioManager.setLocation(RadioCacheUtil.getInstance().getLocation());
            this.mRadioManager.setFreq(this.mInitFreq);
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
                            if (mNeedScanStop) {
                                // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                                mRadioManager.scanStop();
                                //mNeedScanStop = false;
                            }
                            if (isShortSearch) {
                                isShortSearch = false;
                                if (scheduled != null) {
                                    scheduled.shutdown();
                                }
                            }
                            favoritePrev();
                          /*  if (getFavStationVaild()) {

                            } else {
                                if (handler != null) {
                                    Message mMessage = new Message();
                                    mMessage.what = 10;
                                    handler.sendMessage(mMessage);
                                }
                                if (mRadioManager != null) {
                                    mRadioManager.step(-1);
                                }
                            }*/

                        }
                    }
                    break;
                case KEY_NEXT:
                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanDown();
                    } else {
                        if (keyAction == KEY_ACTION_UP) {
                            if (mNeedScanStop) {
                                // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                                mRadioManager.scanStop();
                                //mNeedScanStop = false;
                            }
                            if (isShortSearch) {
                                isShortSearch = false;
                                if (scheduled != null) {

                                    scheduled.shutdown();
                                }
                            }
                            favoriteNext();
/*
                            if (getFavStationVaild()) {

                            } else {
                                if (mRadioManager != null) {
                                    if (handler != null) {
                                        Message mMessage = new Message();
                                        mMessage.what = 10;
                                        handler.sendMessage(mMessage);
                                    }
                                    mRadioManager.step(1);
                                }
                            }*/
                        }
                    }
                    break;
                case KEY_AS:
                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanDown();
                    } else {

                        if (keyAction == KEY_ACTION_UP) {
                            if (scheduleds != null) {
                                scheduleds.shutdown();
                            }
                            if (mNeedScanStop) {
                                mRadioManager.scanStop();
                            }
                            if (isShortSearch) {
                                isShortSearch = false;
                                if (scheduled != null) {
                                    scheduled.shutdown();
                                }
                            }
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
                            if (scheduleds != null) {
                                scheduleds.shutdown();
                            }
                            if (mNeedScanStop) {
                                // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                                mRadioManager.scanStop();
                                //mNeedScanStop = false;
                            }
                            if (isShortSearch) {
                                isShortSearch = false;
                                if (scheduled != null) {
                                    scheduled.shutdown();
                                }
                            }
                            if (isAppOnForeground()) {
                                isSwitc = true;
                                isSwitch = true;
                                isSwitchs = false;
                                Message mMessage = new Message();
                                mMessage.what = 1;
                                mMessage.arg1 = 3;
                                handler.sendMessage(mMessage);
                              /*  if (!isSwitch) {
                                    isSwitc=true;
                                    isSwitch = true;
                                    isSwitchs = false;
                                    Message mMessage = new Message();
                                    mMessage.what = 1;
                                    mMessage.arg1 = 3;
                                    handler.sendMessage(mMessage);
                                    stringIntegerMap.clear();
                                } else {
                                    stringIntegerMap.put("scheduled", 1);
                                *//*isSwitchs=true;
                                SharedPreferences.Editor editors = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                                editors.putInt("isSwitch", 1);
                                editors.commit();*//*
                                }*/
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
                            if (scheduleds != null) {
                                scheduleds.shutdown();
                            }
                            if (mNeedScanStop) {
                                mRadioManager.scanStop();
                            }
                            if (isShortSearch) {
                                isShortSearch = false;
                                if (scheduled != null) {
                                    scheduled.shutdown();
                                }
                            }
                            if (isAppOnForeground()) {
                                isSwitch = true;
                                Message mMessage = new Message();
                                mMessage.what = 2;
                                mMessage.arg1 = 0;
                                handler.sendMessage(mMessage);
                                isSwitchs = false;
                                stringIntegerMap.clear();
                              /*  if (!isSwitch) {

                                } else {
                                    stringIntegerMap.put("scheduled", 2);
                                }*/
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
        onDoIntent(intent, false);
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
            }
            setMyFavoriteItem(d);
          /*  for (int i = 0; i < this.radioStations.size(); ++i) {
                if (this.radioStations.get(i).getMFreq() > this.mFreq) {
                    this.setMyFavoriteItem(i);
                    return;
                }
                if (i == -1 + this.radioStations.size()) {
                    this.setMyFavoriteItem(0);
                }
            }*/
        }
    }

    protected void setMyFavoriteItem(final int index) {
        this.mFreq = this.radioStations.get(index).getMFreq();
        Message mMessage = new Message();
        mMessage.what = 9;
        mMessage.arg1 = index;
        handler.sendMessage(mMessage);
        if (this.mRadioManager != null) {
            this.mRadioManager.setFreq(this.mFreq);
        }
  /*      this.updateRDS();
        this.updateUI();*/
    }

    protected void showNotification(final boolean bshow) {
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
                    SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);

                    int mFreq = read.getInt("mFreq" + Band + mLocation, RadioWrapper.getFreqStart(this.mBand, this.mLocation));
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

    protected void updateNotification(int mFreq) {
        if (mRadioNotification != null) {
            mRadioNotification.update(mFreq,
                    mBand, RadioCacheUtil.getInstance().getLocation(), Band);
        }
    }


}
