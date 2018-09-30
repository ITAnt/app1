package com.jancar.radio.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.util.Iterator;
import java.util.List;
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

/**
 * Created by ouyan on 2018/9/19.
 */

public class RadioActivity extends BaseActivity<RadioContract.Presenter, RadioContract.View> implements RadioContract.View, RadioManager.ConnectListener {
    @BindView(R.id.txt_channel)
    TextView channelTxt;
    Unbinder unbinder;
    @BindView(R.id.gv_1)
    RuleView gv_1;
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
    protected boolean isMobile;
    public static final int PAGE_FM = 1;
    public static final int PAGE_AM = 2;
    List<RadioStation> radioStations;
    List<TextView> list = new ArrayList<>();
    int mBandAF[] = new int[]{R.string.fm1, R.string.fm2, R.string.fm3, R.string.am1, R.string.am2, R.string.am3};
    //protected RadioNotification mRadioNotification;
    public static final int FM_FREQ_CRITICAL = 10000;
    public static final int SCAN_FREQ_LIST_DISPLAY_MAX = 15;
    private ArrayList<RadioStation> mScanResultList;
    private boolean isScanAll = false;
    public boolean isSetting = false;
    private JancarServer mJancarManager = null;
    boolean isShortSearch = false;
    protected RadioNotification mRadioNotification;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mRadioManager.setFreq(mFreq);
                    break;
            }
        }
    };
    private RadioAudioFocusChange mAudioFocusChange;
    ScheduledExecutorService scheduled = null;
    Runnable runnable = null;

    private class RadioAudioFocusChange implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logcat.d("onAudioFocusChange, focusChange = " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(mAudioFocusChange);
            }
        }
    }

    public RadioActivity() {
        // mHandler = new SafeHandler(this);
        mAudioFocusChange = new RadioAudioFocusChange();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        unbinder = ButterKnife.bind(this);
        initData();
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
        mRadioManager = new RadioManager((Context) this, this, getPresenter().getRadioListener(), getPackageName());
        SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
        Band = read.getInt("Band", 0);
        //步骤2：获取文件中的值
        //    swapBandImg.setTag();
        bandTxt.setText(mBandAF[Band]);
        VarietyBand();
        EventBus.getDefault().register(this);//订阅
        mFMFreqSeekBar.setOnSeekBarChangeListener(getPresenter());

        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(20000);//设置动画持续周期
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);//执行前的等待时间
        // originalImg.setAnimation(rotate);
        getPresenter().initText(Band, mLocation);
        initReceiver();
    }

    private void initReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        this.registerReceiver(getPresenter().getmReceiver(), intentFilter);
    }

    public void VarietyBand() {
        mLocation = RadioCacheUtil.getInstance().getLocation();
        if (Band >= 3) {
            this.mBand = 0;
            unitTxt.setText(R.string.unit_khz);
            switch (mLocation) {
                case 0:
                    gv_1.setValue(439.2f, 1693.8f, 1135.4f, 30.6f, 5, false, false);
                    //  gv_1.setValue(428.4f, 1713.6f, 1134.8f, 31.6f, 5, false);
                    break;
                // gv_1.setValue(439.2f, 1693.8f, 1135.4f, 30.6f, 5, false);
                case 1:
                    gv_1.setValue(430.2f, 1711.8f, 1104.8f, 30.6f, 5, false, true);
                    break;
                default:
                    gv_1.setValue(418f, 1812f, 1166f, 34, 5, true, false);
                    break;
            }
            // gv_1.setValue(430.2f, 1711.8f, 1166f, 30.6f, 5, false);
            // gv_1.setValue(439.2f, 1693.8f, 1135.4f, 30.6f, 5, false);
            mFMFreqSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                    layoutParams.setMargins(0, 8, 32, 0);
                    mFMFreqSeekBar.setLayoutParams(layoutParams);
                }
            });


            // gv_1.setValue(418f, 1812f, 1166f, 34, 5, true);
        } else {
            unitTxt.setText(R.string.unit_mhz);
            gv_1.setValue(84.6f, 109.8f, 98.7f, 0.6f, 5, false, false);
            switch (mLocation) {
                case 2:
                case 3:
                    mFMFreqSeekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                            layoutParams.setMargins(6, 8, 36, 0);
                            mFMFreqSeekBar.setLayoutParams(layoutParams);
                        }
                    });
                    break;
                default:
                    mFMFreqSeekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFMFreqSeekBar.getLayoutParams();
                            layoutParams.setMargins(6, 8, 33, 0);
                            mFMFreqSeekBar.setLayoutParams(layoutParams);
                        }
                    });
                    break;
            }
            this.mBand = 1;

        }
    }

    @Override
    public void initText(List<RadioStation> radioStations) {//
        this.radioStations = radioStations;
        if (radioStations.size() <= 0) {
            for (TextView mTextView : list) {
                mTextView.setText("");
                mTextView.setTextColor(Color.parseColor("#ffffff"));
            }
            SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
            mFreq = read.getInt("mFreq" + Band + mLocation, 0);
            if (mFreq == 0) {
                mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
            }
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
        channelTxt.setText(sFinalAge + "");
        if (isSetting) {
            Message mMessage = new Message();
            mMessage.what = 0;
            handler.sendMessage(mMessage);
        }
    }

    @Override
    public void init(List<RadioStation> radioStations) {
        for (TextView mTextView : list) {
            mTextView.setText("");
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
            }

        }
        if (!icFreq) {
            SharedPreferences read = getSharedPreferences("Radio", MODE_WORLD_READABLE);
            mFreq = read.getInt("mFreq" + Band + mLocation, 0);
            if (mFreq == 0) {
                mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
            }
        }
    }

    @Override
    public void initSelect(List<RadioStation> radioStations, RadioStation radioStation) {
        mRadioManager.setFreq(radioStation.getMFreq());
        init(radioStations);
    }

    @Override
    public void requestRadioFocus() {
        mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void abandonRadioFocus() {
        mAudioManager.abandonAudioFocus(mAudioFocusChange);
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    public void addRadioStation(int position, int size, RadioStation mRadioStation) {

        RadioStation mRadioStationd = null;
        for (RadioStation mRadioStations : radioStations) {
            if (mRadioStations.getPosition() == position) {
                mRadioStationd = mRadioStations;
            }
        }
        mRadioStation.setPosition(position);

        getPresenter().Replace(mRadioStation, mRadioStationd);
    }

    @OnLongClick({R.id.txt_channel_list_one, R.id.txt_channel_list_two, R.id.txt_channel_list_three,
            R.id.txt_channel_list_four, R.id.txt_channel_list_fives, R.id.txt_channel_list_six,
            R.id.btn_left, R.id.btn_right})
    public boolean onLongClick(View v) {
        if (mNeedScanStop) {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            mRadioManager.scanStop();
            mNeedScanStop = false;
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
                isScanAll = false;
                if (this.mRadioManager != null) {
                    this.mRadioManager.scanDown(this.mFreq);
                }
                break;
            case R.id.btn_right:
                isScanAll = false;
                if (this.mRadioManager != null) {
                    this.mRadioManager.scanUp(this.mFreq);
                }
                break;
        }
        return true;
    }


    @OnClick({R.id.txt_channel_list_one, R.id.txt_channel_list_two, R.id.txt_channel_list_three,
            R.id.txt_channel_list_four, R.id.txt_channel_list_fives, R.id.txt_channel_list_six,
            R.id.btn_left, R.id.btn_right, R.id.img_swap_band, R.id.img_search, R.id.img_st})
    public void OnCLick(View view) {
        if (mNeedScanStop) {
            mRadioManager.scanStop();
        }
        switch (view.getId()) {
            case R.id.img_st:
                if (stTxt.getVisibility() == View.GONE) {
                    stTxt.setVisibility(View.VISIBLE);
                } else {
                    stTxt.setVisibility(View.GONE);
                }
                break;
            case R.id.txt_channel_list_one:
                getPresenter().select(0, Band, mLocation);
                break;
            case R.id.txt_channel_list_two:
                getPresenter().select(1, Band, mLocation);
                break;
            case R.id.txt_channel_list_three:
                getPresenter().select(2, Band, mLocation);
                break;
            case R.id.txt_channel_list_four:
                getPresenter().select(3, Band, mLocation);
                break;
            case R.id.txt_channel_list_fives:
                getPresenter().select(4, Band, mLocation);
                break;
            case R.id.txt_channel_list_six:
                getPresenter().select(5, Band, mLocation);
                break;
            case R.id.btn_left:
                isMobile = false;
                if (this.mRadioManager != null) {
                    this.mRadioManager.step(-1);
                }
                break;
            case R.id.img_search:
                if (!mNeedScanStop) {
                    isScanAll = true;
                    if (this.mRadioManager != null) {
                        this.mRadioManager.scanAll();
                    }
                } else {
                    mNeedScanStop = false;
                }
           /*     if (!isShortSearch) {
                    isShortSearch = true;
                    final Handler mHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.what <= 0) {
                                mRadioManager.step(1);
                                time = 5;
                            }
                        }
                    };
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            time--;
                            Message msg = mHandler.obtainMessage(time);

                            mHandler.sendMessage(msg);
                        }
                    };
                    time = 1;
                    //初始化一个线程池大小为 1 的 ScheduledExecutorService
                    scheduled = new ScheduledThreadPoolExecutor(1);
                    scheduled.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
                } else {
                    isShortSearch = false;
                    scheduled.shutdown();
                }*/

                //scheduled.
                break;
            case R.id.btn_right:
                isMobile = false;
                if (this.mRadioManager != null) {
                    this.mRadioManager.step(1);
                }
                break;
            case R.id.img_swap_band:
                getPresenter().Change(Band, mFreq, mLocation);
                SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                editor.putInt("mFreq" + Band + mLocation, mFreq);
                editor.commit();
                Band++;
                int band = Band % 6;
                Band = band;
                VarietyBand();
                SharedPreferences.Editor editors = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
                editors.putInt("Band", band);
                editors.commit();
                bandTxt.setText(mBandAF[band]);
                getPresenter().initText(Band, mLocation);
                isSetting = true;
                break;
        }
        mNeedScanStop = false;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在异步执行
    public void onDataSynEvent(RadioPresenter.DataSynEvent event) {
        if (event.bChanged && mNeedScanStop) {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            mRadioManager.scanStop();
        }
        if (event.bChanged) {
            final int freq = RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation);
            if (mRadioManager != null) {
                mRadioManager.setFreq(freq);
            }
            isMobile = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC) //在异步执行
    public void onDataSynEvents(RadioPresenter.DataSynEvents event) {
        final int freq = RadioWrapper.getFreqStart(mBand, mLocation) + event.getCount() * RadioWrapper.getFreqStep(mBand, mLocation);
        if (mRadioManager != null) {
            mRadioManager.setFreq(freq);
        }
        isMobile = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void getDefault(final RadioWrapper.EventFreqChanged event) {
        if (!isMobile) {
            final int freqStart = RadioWrapper.getFreqStart(mBand, mLocation);
            final int freqStep = RadioWrapper.getFreqStep(mBand, mLocation);
            if (freqStep > 0) {
                mFMFreqSeekBar.setProgress((event.mFreq - freqStart) / freqStep);
            }
        }
        updateNotification(event.mFreq);
        getPresenter().Change(event.mFreq, radioStations);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFreq = event.mFreq;
                String sAgeFormat = getResources().getString(R.string.txt_channel);
                String sFinalAge = String.format(sAgeFormat, RadioWrapper.getFreqString(event.mFreq, mBand, mLocation));
                Log.d("sFinalAge", sFinalAge);
                channelTxt.setText(sFinalAge + "");
            }
        });

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
        getPresenter().Change(event.mFreq, radioStations);
        //  getPresenter().Change(Band, event.mFreq ,mLocation);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String sAgeFormat = getResources().getString(R.string.txt_channel);
                String sFinalAge = String.format(sAgeFormat, RadioWrapper.getFreqString(event.mFreq, mBand, mLocation));
                Log.d("sFinalAge", sFinalAge);
                channelTxt.setText(sFinalAge + "");
            }
        });
    }


    public void onScanResult(final int ifreq, final int isignal) {
        if (this.mNeedScanStop) {
            if (this.mRadioManager != null) {
                Logcat.d(RadioWrapper.ScanAction.getName(this.mRadioManager.getScanAction()));
            }
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
                    mRadioManager.scanStop();
                    mRadioManager.setFreq(ifreq);
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
        if (bsave) {
            if (this.mScanResultList != null && this.mScanResultList.size() > 0) {
                getPresenter().save(mScanResultList, mBand, Band, mLocation);
            } else {
                Toast.makeText(this, R.string.search_no_result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanStart(final RadioWrapper.EventScanStart event) {
        mNeedScanStop = true;
        mScanResultList.clear();
       // this.showNotification(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanEnd(final RadioWrapper.EventScanEnd event) {
        onScanEnd(event.mScanAll);
        mNeedScanStop = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //   
    public void onScanAbort(final RadioWrapper.EventScanAbort event) {//
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
        unregisterReceiver(getPresenter().getmReceiver());
        resetFreqStart();
        closeManager();
        SharedPreferences.Editor editor = getSharedPreferences("Radio", MODE_WORLD_WRITEABLE).edit();
        editor.putInt("mFreq" + Band + mLocation, mFreq);
        editor.commit();
        getPresenter().Change(Band, mFreq, mLocation);
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
                 /*           if (getCurRadioFragment().getFavStationVaild()) {
                                getCurRadioFragment().favoritePrev();
                            } else {
                                if (RadioMainActivity.this.mRadioManager != null) {
                                    RadioMainActivity.this.mRadioManager.step(-1);
                                }
                            }*/
                        }
                    }
                    break;
                case KEY_NEXT:
                    if (keyAction == KEY_ACTION_DOWN_LONG) {
                        //getCurRadioFragment().scanDown();
                    } else {
                     /*   if (keyAction == KEY_ACTION_UP) {
                            if (getCurRadioFragment().getFavStationVaild()) {
                                getCurRadioFragment().favoriteNext();
                            } else {
                                if (RadioMainActivity.this.mRadioManager != null) {
                                    RadioMainActivity.this.mRadioManager.step(1);
                                }
                            }
                        }*/
                    }
                    break;
                case KEY_AS:
                    if (keyAction == KEY_ACTION_UP) {
                        //  getCurRadioFragment().scanUp();
                    }
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

                    int mFreq = read.getInt("mFreq" + Band + mLocation, 0);
                    if (mFreq == 0) {
                        mFreq = RadioWrapper.getFreqStart(this.mBand, this.mLocation);
                    }
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
