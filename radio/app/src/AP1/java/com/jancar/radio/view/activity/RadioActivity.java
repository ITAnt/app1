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
import android.view.KeyEvent;
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
import com.jancar.media.JacMediaSession;
import com.jancar.radio.BuildConfig;
import com.jancar.radio.R;
import com.jancar.radio.RadioManager;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.contract.RadioCacheUtil;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.entity.Collection;
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
import static com.jancar.radio.notification.uite.CONTEXT;
import static com.jancar.radio.notification.uite.INITIAL;
import static com.jancar.radio.notification.uite.PSTXT;
import static com.jancar.radio.notification.uite.RTTXT;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by ouyan on 2018/9/19.
 */

public class RadioActivity extends BasesActivity{
    public static final String BROADCAST_ACTION = "RadioActivity";
    @BindView(R.id.txt_channel)
    TextView channelTxt;

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
    List<TextView> list = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
            Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
            switch (msg.what) {
                case 0:
                    Log.w("Freq", msg.arg1 + "");
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
                    setBandEditor(msg.arg1);
                    bandTxt.setText(mBandAF[msg.arg1]);
                    getPresenter().initText(msg.arg1, mLocation, first_run);
                    stTxt.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    isSetting = true;
                    Band = msg.arg1;
                    VarietyBand();
                    stTxt.setVisibility(View.VISIBLE);
                    setBandEditor(msg.arg1);
                    bandTxt.setText(mBandAF[msg.arg1]);
                    getPresenter().initText(msg.arg1, mLocation, first_run);

                    break;
                case 4:
                    if (Band >= 3) {
                        stTxt.setVisibility(View.INVISIBLE);
                    } else {
                        stTxt.setVisibility(View.VISIBLE);
                    }
                    // swapBandImg.setEnabled(true);
                    bandTxt.setText(mBandAF[Band]);
                    getPresenter().initText(Band, mLocation, first_run);
                    break;
                case 5:
                    if (manager.getRadioLocal() != RadioCacheUtil.getInstance(getApplicationContext()).getLocation()) {
                        mLocation = manager.getRadioLocal();
                        RadioCacheUtil.getInstance(getApplicationContext()).setLocation(mLocation);
                        mRadioManager.setLocation(mLocation);
                        final int freqStart = RadioWrapper.getFreqStart(mBand, mLocation);
                        final int freqEnd = RadioWrapper.getFreqEnd(mBand, mLocation);
                        final int freqStep = RadioWrapper.getFreqStep(mBand, mLocation);
                        final int max = (freqEnd - freqStart) / freqStep;
                        if (freqStep > 0) {
                            mFMFreqSeekBar.setMax(max);
                        }
                    }
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
                        setmFreq(Band, msg.arg1);
                        Log.w("RadioActivity_mFreq", msg.arg1 + "");

                    } else {
                        mFreq = getmFreq();
                        Handler(0, mFreq);
                    }
                    if (!isChange) {


                        if (mFreq < RadioWrapper.getFreqStart(mBand, mLocation)) {
                            mFreq = RadioWrapper.getFreqStart(mBand, mLocation);
                        }
                        if (mFreq > RadioWrapper.getFreqEnd(mBand, mLocation)) {
                            mFreq= RadioWrapper.getFreqStart(mBand, mLocation);
                        }
                        channelTxt.setText(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
                    }

                    Message data = new Message();
                    data.what = PSTXT;
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("Freq", mFreq);
                    data.setData(mBundle);
                    rdsFragment.getData(data);
                    isChange = false;
                    setMediaInfo(mFreq);
                    break;
                case 6:

                    Handler(0,  msg.arg1);
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
                    setBandEditor(msg.arg1);
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

    @SuppressLint("WrongConstant")
    public void initData() {
        RuleView = new ArrayList<>();
        radioStations = new ArrayList<>();
        Log.w("RadioAcitivity", "onCreate");
        // mJancarManager = (JancarServer) getSystemService("jancar_manager");
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
        mLocation = RadioCacheUtil.getInstance(getApplicationContext()).getLocation();
        Log.w("RadioAcitivity", mLocation + "");
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
  public   Handler getHandler() {
        return handler;
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.w("RadioAcitivity", "onStart");
        Log.w("RadioAcitivity", manager.getRadioLocal() + "");

        if (manager.getRadioLocal() != RadioCacheUtil.getInstance(getApplicationContext()).getLocation()) {
            Band = 0;
            setBandEditor(Band);
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
        Message data = new Message();
        data.what = INITIAL;
        Bundle mBundle = new Bundle();
        mBundle.putInt("Freq", mFreq);
        mBundle.putInt("mLocation", mLocation);
        mBundle.putInt("mBand", mBand);
        mBundle.putString("PS", mRadioManager.getPSText(mFreq));
        mBundle.putInt("PTY", mPty);
        data.setData(mBundle);
        rdsFragment.getData(data);
    }

    public void VarietyBand() {
        for (RuleView mRuleView : RuleView) {
            mRuleView.setVisibility(View.GONE);
        }
        if (Band >= 3) {
            img_dnr.setBackgroundResource(R.mipmap.btn_radio_rds_d);
            txt_rt.setVisibility(View.INVISIBLE);
            stTxt.setVisibility(View.INVISIBLE);
            taTxt.setVisibility(View.INVISIBLE);
            afTxt.setVisibility(View.INVISIBLE);
            txt_name.setVisibility(View.INVISIBLE);
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
            txt_rt.setVisibility(View.INVISIBLE);
            int mTA = RadioCacheUtil.getInstance(getApplicationContext()).getTA();
            int mAF = RadioCacheUtil.getInstance(getApplicationContext()).getAF();
            if (mAF == 1) {
                afTxt.setVisibility(View.VISIBLE);
            } else {
                afTxt.setVisibility(View.INVISIBLE);
            }
            if (mTA == 1) {
                taTxt.setVisibility(View.VISIBLE);
            } else {
                taTxt.setVisibility(View.INVISIBLE);
            }*/
            img_dnr.setBackgroundResource(R.mipmap.btn_radio_rds_d);
            txt_rt.setVisibility(View.INVISIBLE);
            stTxt.setVisibility(View.INVISIBLE);
            taTxt.setVisibility(View.INVISIBLE);
            afTxt.setVisibility(View.INVISIBLE);
            txt_name.setVisibility(View.INVISIBLE);
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

            mFreq = getmFreq();
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


            if (mFreq < RadioWrapper.getFreqStart(mBand, mLocation)) {
                mFreq = RadioWrapper.getFreqStart(mBand, mLocation);
            }
            if (mFreq > RadioWrapper.getFreqEnd(mBand, mLocation)) {
                mFreq= RadioWrapper.getFreqStart(mBand, mLocation);
            }
            channelTxt.setText(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
        }
        if (isSetting) {

            Handler(0,  mFreq);
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


/*    public void addRadioStation(int position, int size, RadioStation mRadioStation) {

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
    }*/

    @OnLongClick({R.id.txt_channel_list_one, R.id.txt_channel_list_two, R.id.txt_channel_list_three,
            R.id.txt_channel_list_four, R.id.txt_channel_list_fives, R.id.txt_channel_list_six,
            R.id.btn_left, R.id.btn_right, R.id.img_search})
    public boolean onLongClick(View v) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        scanStop();
        shutdown();
        RadioStation mRadioStation = new RadioStation();
        mRadioStation.setRdsname(RadioWrapper.getFreqString(mFreq, mBand, mLocation));
        mRadioStation.setSelect(true);
        mRadioStation.setMFreq(mFreq);
        mRadioStation.setMBand(mBand);
        mRadioStation.setLocation(mLocation);
        mRadioStation.setFrequency(Band);
        switch (v.getId()) {
            case R.id.txt_channel_list_one:
                getPresenter().addRadioStation(0, mRadioStation, radioStations);
                break;
            case R.id.txt_channel_list_two:
                getPresenter().addRadioStation(1, mRadioStation, radioStations);
                break;
            case R.id.txt_channel_list_three:

                getPresenter().addRadioStation(2, mRadioStation, radioStations);
                break;
            case R.id.txt_channel_list_four:
                getPresenter().addRadioStation(3, mRadioStation, radioStations);
                break;
            case R.id.txt_channel_list_fives:
                getPresenter().addRadioStation(4, mRadioStation, radioStations);
                break;
            case R.id.txt_channel_list_six:
                getPresenter().addRadioStation(5, mRadioStation, radioStations);
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
                setBandEditor(Band);
                bandTxt.setText(mBandAF[Band]);
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
            R.id.btn_left, R.id.btn_right, R.id.img_swap_band, R.id.img_search, R.id.img_st, R.id.img_dnr})
    public void OnCLick(View view) {
        scanStop();
        shutdown();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        switch (view.getId()) {
            case R.id.img_dnr:
                if (mBand == 0) {
                    return;
                }
                //hideRds(1);
                break;
            case R.id.img_st:
                if (mBand == 0) {
                    return;
                }
                if (stTxt.getVisibility() == View.INVISIBLE) {
                    stTxt.setVisibility(View.VISIBLE);
                } else {
                    stTxt.setVisibility(View.INVISIBLE);
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
                isShortSearch = false;
                isSetting = true;
                Band++;
                int band = Band % 5;
                Band = band;
                if (Band == 3 || Band == 0) {
                    isChange = true;
                }
                Log.w("RadioActivity", Band + "");
                setBand();
                VarietyBand();
                Log.w("RadioActivity", mBand + "");
                setBandEditor(Band);

                Handler(4,  0);
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

                            Handler(0,  radioStations.get(pd).getMFreq());
                            //mRadioManager.setFreq(radioStations.get(pd).getMFreq());
                            pd++;
                        } else {
                            pd = 0;
                            pds = pd;
                            if (mBand == 1) {
                                Band++;
                                if (Band < 3) {
                                    setBandEditor(Band);
                                    VarietyBand();
                                    bandTxt.setText(mBandAF[Band]);
                                    RadioActivity.this.getPresenter().initText(Band, mLocation, false);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }

                                    Handler(0,  radioStations.get(pd).getMFreq());
                                    //mRadioManager.setFreq(radioStations.get(pd).getMFreq());
                                    list.get(radioStations.get(0).getPosition()).setTag("0");
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                } else {

                                    Band = 2;
                                    setBandEditor(Band);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }

                                    Handler(0,   radioStations.get(0).getMFreq());
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
                                    setBandEditor(Band);
                                    VarietyBand();
                                    bandTxt.setText(mBandAF[Band]);
                                    RadioActivity.this.getPresenter().initText(Band, mLocation, false);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }
                                    Handler(0,   radioStations.get(pd).getMFreq());
                                    list.get(radioStations.get(0).getPosition()).setTag("0");
                                    list.get(radioStations.get(0).getPosition()).setTextColor(Color.parseColor("#FF0B49E7"));
                                } else {
                                    Band = 4;
                                    setBandEditor(Band);
                                    for (TextView m : list) {
                                        m.setTextColor(Color.parseColor("#ffffff"));
                                        m.setTag("1");
                                    }

                                    Handler(0,   radioStations.get(0).getMFreq());
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
            if (scheduled != null) {

                scheduled.shutdown();
            }
        }
    }


    //拖动条开始监听
    @Override
    public void onStartTrackingTouch() {
        if (scheduled != null) {
            isShortSearch = false;
            scheduled.shutdown();
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Firsts", true);
        getPresenter().select(Band, mLocation, first_run);

    }

    @Override
    public void setCollection(List<Collection> collections) {

    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在ui线程执行
    public void getDefault(final RadioWrapper.EventFreqChanged event) {
        if (isSwitch) {

            isSwitch = false;
            if (stringIntegerMap.size() > 0) {
                isSwitch = true;
               int arg1;
                if (stringIntegerMap.get("scheduled") == 2) {
                    arg1= 0;
                } else {
                    arg1= 3;
                }
                Handler( stringIntegerMap.get("scheduled"), arg1);

                stringIntegerMap.clear();
            }
            isChange = false;
            setMediaInfo(event.mFreq);
        } else {
            Log.w("isSwitchs___dd", stringIntegerMap.get("scheduled") + "");

            Handler( 5,  event.mFreq);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)//扫描中
    public void onScanResult(final RadioWrapper.EventScanResult event) {
        onScanResult(event.mFreq, event.mSignalStrength);
        Logcat.d("RadioListener, freq = " + event.mFreq + ", signal = " + event.mSignalStrength);
        /*mFreq = event.mFreq;*/
        setMediaInfo(event.mFreq);
        final int freqStart = RadioWrapper.getFreqStart(mBand, mLocation);
        final int freqStep = RadioWrapper.getFreqStep(mBand, mLocation);
        if (freqStep > 0) {
            mFMFreqSeekBar.setProgress((event.mFreq - freqStart) / freqStep);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (event.mFreq < RadioWrapper.getFreqStart(mBand, mLocation)) {
                    event.mFreq = RadioWrapper.getFreqStart(mBand, mLocation);
                }
                if (event.mFreq > RadioWrapper.getFreqEnd(mBand, mLocation)) {
                    event.mFreq= RadioWrapper.getFreqStart(mBand, mLocation);
                }
                channelTxt.setText(RadioWrapper.getFreqString(event.mFreq, mBand, mLocation));
                Log.d("sFinalAge",   event.mFreq+"");
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
                } else {
                    isTinTai = true;
                    mRadioManager.scanStop();
                }

            }
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
        if (!mScanResultList.contains(radioStation)) {
            Log.w("RadioActivity_RadioStations", ifreq + "---" + isignal);
            this.mScanResultList.add(radioStation);
        }
    }


    //扫描结束，添加数据
    public void onScanEnd(final boolean bsave) {
        if (bsave) {
            isShortSearch = false;
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

                Handler( 0,  mScanResultList.get(0).getMFreq());
                //this.mRadioManager.setFreq();
                list.get(0).setTextColor(Color.parseColor("#FF0B49E7"));
            } else {
                if (isTermination) {
                    mFreq = mRadioManager.getFreq();
                } else {
                    mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
                    isChange = false;

                    Handler( 0, mFreq);
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
        radioStations = getPresenter().queryFrequency(Band, mLocation);
        init(radioStations);
        mNeedScanStop = true;
        mScanResultList.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//扫描结束
    public void onScanEnd(final RadioWrapper.EventScanEnd event) {
        onScanEnd(event.mScanAll);

        if (event.mScanAll) {
            isTermination = false;
        }
        isTermination = false;
        mNeedScanStop = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //扫描终止
    public void onScanAbort(final RadioWrapper.EventScanAbort event) {//
        isShortSearch = true;
        if (event.mScanAll) {
            isTermination = true;
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
        Log.w("RadioListener", mTP + "; mfreq" + mFreq + "; pty" + mPTY + " ; mtp " + mTP + " ;mta" + mTA);

        if (mTA == 1) {
            taTxts.setVisibility(View.VISIBLE);
        } else {
            taTxts.setVisibility(View.GONE);
        }
        Message data = new Message();
        data.what = INITIAL;
        Bundle mBundle = new Bundle();
        mBundle.putInt("Freq", mFreq);
        mBundle.putInt("mLocation", mLocation);
        mBundle.putInt("mBand", mBand);
        mBundle.putString("PS", mRadioManager.getPSText(mFreq));
        mBundle.putInt("PTY", mPTY);
        data.setData(mBundle);
        rdsFragment.getData(data);

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
        Message data = new Message();
        data.what = PSTXT;
        Bundle mBundle = new Bundle();
        mBundle.putString("PS", mText);
        data.setData(mBundle);
        rdsFragment.getData(data);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRdsRtChanged(RadioWrapper.EventRdsRt event) {
        Log.w("RadioListener", event.mText);
        // Logcat.d("RadioListener, pi = " + event.mPI + ", freq = " + event.mFreq + ", rt = " + event.mText);
        onRdsRtChanged(event.mPI, event.mFreq, event.mText);
    }

    private void onRdsRtChanged(int mPI, int mFreq, String mText) {
        Message data = new Message();
        data.what = RTTXT;
        Bundle mBundle = new Bundle();
        mBundle.putString("Rt", mText);
        data.setData(mBundle);
        rdsFragment.getData(data);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        Log.w("RadioAcitivity", "onDestroy");
        EventBus.getDefault().unregister(this);
        Logcat.d("mRadioManager = " + this.mRadioManager);
        mAudioManager.abandonAudioFocus(mAudioFocusChange);
        //mJancarManager.abandonKeyFocus(keyFocusListener.asBinder());
        manager = null;
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




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w("RadioAcitivity", "onNewIntent");
        onDoIntent(intent, false);
    }


}
