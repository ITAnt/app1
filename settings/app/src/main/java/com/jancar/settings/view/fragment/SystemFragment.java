package com.jancar.settings.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.audio.AudioEffectManager;
import com.jancar.audio.AudioEffectParam;
import com.jancar.bluetooth.lib.BluetoothSettingManager;
import com.jancar.settings.R;
import com.jancar.settings.lib.SettingManager;
import com.jancar.settings.listener.Contract.OnContractImpl;
import com.jancar.settings.listener.Contract.SystemContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.OnPresenter;
import com.jancar.settings.presenter.SystemPresenter;
import com.jancar.settings.util.AppCleanEngine;
import com.jancar.settings.util.GPS;
import com.jancar.settings.util.QRCodeUtil;
import com.jancar.settings.view.activity.MainActivity;
import com.jancar.settings.widget.CircleProgressView;
import com.jancar.settings.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_WORLD_WRITEABLE;
import static com.jancar.settings.util.Tool.setDialogParam;
import static com.jancar.settings.view.fragment.SuspensionFragment.newInstance;


/**
 * Created by ouyan on 2018/9/11.
 */

public class SystemFragment extends BaseFragments<SystemPresenter> implements SystemContractImpl.View, View.OnClickListener, AudioEffectManager.AudioListener, SettingManager.SettingsManagerListener {
    private View view;
    private RelativeLayout systemCleanupRlayout;
    private RelativeLayout factorySettingRlayout;
    private RelativeLayout resetRlayout;
    private RelativeLayout restartRlayout;
    private SwitchButton videoSwitch;
    private SwitchButton touchToneSwitch;
    //  private SwitchButton suspensionSwitch;//悬浮按钮开关
    private RelativeLayout suspensionRlayout;//悬浮按钮
    private int cleanAppCount = 0;
    private int appListSize = 0;
    private SettingManager settingManager;
    private RelativeLayout restoreDefaultRlayout;
    private RelativeLayout systemRlayout;
    private LinearLayout suspensionLlayout;
    SuspensionFragment mSuspensionFragment;
    Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            ((MainActivity) getActivity()).mHadler = null;
            if (systemRlayout != null) {
                suspensionLlayout.setVisibility(View.GONE);
                systemRlayout.setVisibility(View.VISIBLE);
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        //cleanSystemCache();
        if (view != null) {
            videoSwitch = (SwitchButton) view.findViewById(R.id.switch_video);
            touchToneSwitch = (SwitchButton) view.findViewById(R.id.switch_touch_tone);
            // suspensionSwitch = (SwitchButton) view.findViewById(R.id.switch_suspension);
            systemCleanupRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_system_cleanup);
            factorySettingRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_factory_setting);
            suspensionRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_suspension);
            resetRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_reset);
            restartRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_restart);
            restoreDefaultRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_restore_default);
            systemRlayout = (RelativeLayout) view.findViewById(R.id.llayout_system);
            suspensionLlayout = (LinearLayout) view.findViewById(R.id.llayout_suspension);
        }

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_system, null);
        initView(savedInstanceState);
        initData(savedInstanceState);
        return view;
    }

    @Override
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new SystemPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        settingManager = SettingManager.getSettingManager(this.getActivity());
        touchToneSwitch.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        touchToneSwitch.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        videoSwitch.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        videoSwitch.setBackDrawableRes(R.drawable.switch_custom_track_selector);
      /*  suspensionSwitch.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        suspensionSwitch.setBackDrawableRes(R.drawable.switch_custom_track_selector);*/
        videoSwitch.setCheckedImmediately(settingManager.getDrivingStopVedio());
        touchToneSwitch.setCheckedImmediately(settingManager.getIsNeedkeySound());
        systemCleanupRlayout.setOnClickListener(this);
        factorySettingRlayout.setOnClickListener(this);
        resetRlayout.setOnClickListener(this);
        restartRlayout.setOnClickListener(this);
        touchToneSwitch.setOnClickListener(this);
        videoSwitch.setOnClickListener(this);
        restoreDefaultRlayout.setOnClickListener(this);
        settingManager.setListener(this);
        suspensionRlayout.setOnClickListener(this);
        mSuspensionFragment = newInstance(false);
        getFragmentManager().beginTransaction()
                .add(R.id.llayout_suspension, mSuspensionFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlayout_suspension:
                //showCleanupDialog();
                if (suspensionLlayout.getVisibility() == View.GONE) {
                    suspensionLlayout.setVisibility(View.VISIBLE);
                    systemRlayout.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).mHadler = mHadler;
                } else {
                    ((MainActivity) getActivity()).mHadler = null;
                    suspensionLlayout.setVisibility(View.GONE);
                    systemRlayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rlayout_system_cleanup:
                showCleanupDialog();
                break;
            case R.id.rlayout_factory_setting:
                showSettingDialog();
                break;
            case R.id.rlayout_reset:
                showResetDialog();
                break;
            case R.id.rlayout_restore_default:
                showRestoreDefaultDialog();
                break;
            case R.id.rlayout_restart:
                showRestartDialogs();

                break;
            case R.id.switch_video:
                settingManager.setDrivingStopVedio(!settingManager.getDrivingStopVedio());
                break;
            case R.id.switch_touch_tone:
                settingManager.setIsNeedKeySound(!settingManager.getIsNeedkeySound());
                break;
        }
    }

    private void showRestoreDefaultDialog() {
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.display_dialog_restore_default);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Button connect = (Button) dialog.findViewById(R.id.btn_connect_btn);
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_connect_btn:
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
                        sharedPreferences.edit().putBoolean("DisplayFragmetn", true).commit();
                        //     dropLanguage.setLaguageVisibility(View.GONE);
                       /* SettingManager.getSettingManager(getContext()).setAutoBrightness(true);
                        if (!settingManager.getTailState()) {
                            settingManager.setBrightness(100, false);
                        }
                        settingManager.setDayBrightness(100, true);
                        if (settingManager.getTailState()) {
                            settingManager.setBrightness(100, false);
                        }
                        settingManager.setNightBrightness(100, true);
*/
                        AudioEffectManager mAudioEffectManager;
                        mAudioEffectManager = new AudioEffectManager(getContext(), SystemFragment.this, getActivity().getPackageName());
                        mAudioEffectManager.setAudioEffectTreble(7, true);
                        mAudioEffectManager.setAudioEffectMiddle(7, true);
                        mAudioEffectManager.setAudioEffectBass(7, true);
                        mAudioEffectManager.setAudioEffectBass(7, true);
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("EQ", MODE_WORLD_WRITEABLE).edit();
                        editor.putFloat("x", 0.9633102f);
                        editor.putFloat("y", 0.8699093f);
                        editor.putInt("ValueTTxt", 7);
                        editor.putInt("ValueMTxt", 7);
                        editor.putInt("ValueBTxt", 7);
                        editor.putInt("Types", 0);
                        editor.apply();
                        mAudioEffectManager.setAudioEffectLoudness(0, true);
                        mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(0, 0), true);
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                        BluetoothSettingManager manager = BluetoothSettingManager.getBluetoothSettingManager(getContext());
                        manager.openBluetooth();
                        //manager.searchPairedList();
                        settingManager.resetDeafaultSettings();
                      /*  displayScrollView.setVisibility(View.VISIBLE);
                        languageList.setVisibility(View.GONE);*/
                        dialog.dismiss();
                        // settingManager.changeSystemLanguage(settingManager.locales[0], 0);
                        //((MainActivity) getActivity()).finish();
                      /*  Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("position", 2);
                        startActivity(intent);
                       */
                        break;
                    case R.id.btn_cancel:
                        dialog.dismiss();
                        break;
                }
            }
        };
        connect.setOnClickListener(buttonListener);
        cancel.setOnClickListener(buttonListener);

        dialog.show();
        setDialogParam(dialog, 500, 316);
    }

    private void showRestartDialogs() {
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.dialog_reset);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Button connect = (Button) dialog.findViewById(R.id.btn_connect_btn);
        TextView TextView = (TextView) dialog.findViewById(R.id.text_name_dialog);
        TextView.setText(getResources().getString(R.string.dialog_tab_restart));
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_connect_btn:
                        showRestartDialog();
                        dialog.dismiss();
                        break;
                    case R.id.btn_cancel:
                        dialog.dismiss();
                        break;
                }
            }
        };
        connect.setOnClickListener(buttonListener);
        cancel.setOnClickListener(buttonListener);

        dialog.show();
        setDialogParam(dialog, 500, 316);
    }

    private void reboot() {

        PowerManager manager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        manager.reboot("restart");
    }

    int time = 5;

    public void startTime1() {


    }

    private void showRestartDialog() {
        final String sAgeFormat = getResources().getString(R.string.device_restart);
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.dialog_restart);
        final TextView nameDialogTxt = dialog.findViewById(R.id.text_name_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what <= 0) {
                    reboot();
                } else {
                    @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
                    String sFinalAge = String.format(sAgeFormat, msg.what);
                    nameDialogTxt.setText(sFinalAge);
                }

            }
        };
        ScheduledExecutorService scheduled;

        time = 5;
        //初始化一个线程池大小为 1 的 ScheduledExecutorService
        scheduled = new ScheduledThreadPoolExecutor(1);
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                time--;
                Message msg = mHandler.obtainMessage(time);

                mHandler.sendMessage(msg);
            }
        }, 0, 1, TimeUnit.SECONDS);

        dialog.show();
        setDialogParam(dialog, 316, 89);
    }

    private void showResetDialog() {
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.dialog_reset);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Button connect = (Button) dialog.findViewById(R.id.btn_connect_btn);
        TextView TextView = (TextView) dialog.findViewById(R.id.text_name_dialog);
        TextView.setText(getResources().getString(R.string.dialog_restore_defaults));
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_connect_btn:
                        settingManager.resetDeafaultSettings();
                        Intent intent = new Intent();
                        intent.setAction("com.qucii.sendreset");
                        getActivity().sendBroadcast(intent);
                        dialog.dismiss();
                        break;
                    case R.id.btn_cancel:
                        dialog.dismiss();
                        break;
                }
            }
        };
        connect.setOnClickListener(buttonListener);
        cancel.setOnClickListener(buttonListener);

        dialog.show();
        setDialogParam(dialog, 500, 316);
    }

    private void showSettingDialog() {
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.display_dialog_system);
        final EditText passwordEdit = (EditText) dialog.findViewById(R.id.edit_password);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txt_dialog_determine:
                        if (passwordEdit.getText().toString().equals("1234")) {
                            Intent intent = new Intent();
                            intent.setClassName("com.jancar.ac8227l", "com.jancar.settings.SettingsPreferenceActivity");
                            startActivity(intent);
                            dialog.dismiss();
                        } else {
                            passwordEdit.setText("");
                            Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.txt_dialog_cancel:
                        dialog.dismiss();
                        break;
                    default:
                        TextView mTextView = (TextView) v;
                        passwordEdit.setText(passwordEdit.getText().toString() + mTextView.getText().toString());
                        break;
                }
            }
        };


        ((TextView) dialog.findViewById(R.id.txt_dialog_1)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_2)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_3)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_4)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_5)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_6)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_7)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_8)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_9)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_0)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_determine)).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.txt_dialog_cancel)).setOnClickListener(listener);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        setDialogParam(dialog, 393, 393);
    }

    private void showCleanupDialog() {

        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialogs);
        dialog.setContentView(R.layout.display_dialog_cleanup);
        final CircleProgressView mCircleProgressView = (CircleProgressView) dialog.findViewById(R.id.circle_progress_view);
        final TextView scheduleTxt = (TextView) dialog.findViewById(R.id.txt_schedule);
        scheduleTxt.setText("0%");
        final TextView statusTxt = (TextView) dialog.findViewById(R.id.txt_status);
        mCircleProgressView.setAnime(true);
        mCircleProgressView.setMaxProgress(100);
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        mCircleProgressView.setProgress(msg.arg1);
                        statusTxt.setText(R.string.tab_cleaning_up);
                        scheduleTxt.setText(msg.arg1 + "%");
                        break;
                    case 1:
                        statusTxt.setText(R.string.tab_clean_up);
                        mCircleProgressView.setProgress(100);
                        scheduleTxt.setText(100 + "%");

                        dialog.dismiss();
                        Toast.makeText(getContext(), R.string.tab_clean_up, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:

                        dialog.dismiss();
                        break;
                }
            }
        };
        cleanSystemCache(handler);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.show();
        setDialogParam(dialog, 324, 324);
    }

    public void cleanSystemCache(final Handler handler) {
        AppCleanEngine mAppCleanEngine = new AppCleanEngine(getContext());
        ArrayList<String> appList = mAppCleanEngine.scanAppList();
        appListSize = appList.size();
        final PackageManager packageManager = getContext().getPackageManager();
        Log.e("ygl", "appListSize:" + appListSize);
        mAppCleanEngine.setDataObserver(new IPackageDataObserver.Stub() {

            @Override
            public void onRemoveCompleted(String packageName, boolean succeeded)
                    throws RemoteException {
                String appLabel = "";
                try {
                    appLabel = String.valueOf(packageManager.getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES).loadLabel(
                            packageManager));
                } catch (PackageManager.NameNotFoundException e1) {
                    e1.printStackTrace();
                }

                cleanAppCount++;
                Log.e("ygl", "packageName:" + cleanAppCount);
                Log.e("ygl", "cleanAppCount:" + cleanAppCount);
                Message msg = new Message();
                if (cleanAppCount >= appListSize) {
                    Log.e("ygl", "cleanAppCount == appListSize:" + (cleanAppCount == appListSize));
                    appListSize = 0;
                    cleanAppCount = 0;
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    int percent = 100 * cleanAppCount / appListSize;
                    msg.what = 0;
                    msg.arg1 = percent;

                    handler.sendMessage(msg);

                }
            }
        });
        mAppCleanEngine.cleanAppCache(appList);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void onVolumeChanged(int i, int i1) {

    }

    @Override
    public void onMuteChanged(boolean b, int i) {

    }

    boolean isDefault = false;

    @Override
    public void notifyRefreshUI() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        isDefault = sharedPreferences.getBoolean("DisplayFragmetn", false);
        if (isDefault) {
            sharedPreferences.edit().putBoolean("DisplayFragmetn", false).commit();
            videoSwitch.setCheckedImmediately(settingManager.getDrivingStopVedio());
            touchToneSwitch.setCheckedImmediately(settingManager.getIsNeedkeySound());
            Settings.Secure.setLocationProviderEnabled(getContext().getContentResolver(), LocationManager.GPS_PROVIDER, false);
            GPS gps = new GPS();
            gps.openGPSSettings(getContext(), 3);
            settingManager.changeSystemLanguage(settingManager.locales[settingManager.getLanguage()], settingManager.getLanguage());
        }
        //Toast.makeText(getContext(), "123", Toast.LENGTH_SHORT).show();

    }
}
