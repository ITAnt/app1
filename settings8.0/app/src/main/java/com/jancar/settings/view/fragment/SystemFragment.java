package com.jancar.settings.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build;
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
import com.jancar.settings.broadcast.ResetReceiver;
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
import com.jancar.settings.util.ToastUtil;
import com.jancar.settings.view.activity.MainActivity;
import com.jancar.settings.widget.CircleProgressView;
import com.jancar.settings.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_WRITEABLE;
import static android.security.KeyStore.getApplicationContext;
import static com.jancar.settings.util.Tool.setDialogParam;
import static com.jancar.settings.view.fragment.SuspensionFragment.newInstance;


/**
 * Created by ouyan on 2018/9/11.
 */

public class SystemFragment extends BaseFragments<SystemPresenter> implements SystemContractImpl.View, View.OnClickListener, AudioEffectManager.AudioListener, SettingManager.SettingsManagerListener, DialogInterface.OnCancelListener {
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
    private RelativeLayout systemRlayouts;
    private LinearLayout suspensionLlayout;
    SuspensionFragment mSuspensionFragment;
    @SuppressLint("HandlerLeak")
    Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            ((MainActivity) getContext()).mHadler = null;
            if (systemRlayout != null) {
                suspensionLlayout.setVisibility(View.GONE);
                systemRlayout.setVisibility(View.VISIBLE);
                mSuspensionFragment.keepValue();
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
            systemRlayouts = (RelativeLayout) view.findViewById(R.id.rlayout_system);
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
        settingManager = SettingManager.getSettingManager(this.getContext());
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
        systemRlayouts.setOnClickListener(this);
        mSuspensionFragment = newInstance(false);
        getFragmentManager().beginTransaction()
                .replace(R.id.llayout_suspension, mSuspensionFragment)
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
                    ((MainActivity) getContext()).mHadler = mHadler;
                } else {
                    ((MainActivity) getContext()).mHadler = null;
                    suspensionLlayout.setVisibility(View.GONE);
                    systemRlayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rlayout_system_cleanup:
                //  systemCleanupRlayout.setEnabled(false);
                if (CleanupDialog == null) {
                    showCleanupDialog();
                }
                break;
            case R.id.rlayout_factory_setting:
                //factorySettingRlayout.setEnabled(false);
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
            case R.id.rlayout_system:
                Intent intent = new Intent();
                // intent.setAction("com.android.settings");
                intent.setClassName("com.android.settings", "com.android.settings.Settings");
                startActivity(intent);
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
                        videoSwitch.setCheckedImmediately(true);
                        touchToneSwitch.setCheckedImmediately(false);
                        Reset();
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent it1 = new Intent(getContext(),ResetReceiver.class);
                            getContext().sendBroadcast(it1);
                        } else {
                            Intent intent = new Intent();
                            intent.setAction("com.jancar.action.reset.default.settings");
                            getContext().sendBroadcast(intent);
                        }

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

    public void Reset() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("EQ", MODE_PRIVATE).edit();
        editor.putFloat("x", 0.9633102f);
        editor.putFloat("y", 0.8699093f);
        editor.putInt("ValueTTxt", 0);
        editor.putInt("ValueMTxt", 0);
        editor.putInt("ValueBTxt", 0);
        editor.putInt("Types", 0);
        editor.apply();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FirstRun", 0);
        sharedPreferences.edit().putBoolean("DisplayFragmetn", true).commit();
        sharedPreferences.edit().putBoolean("First", true).commit();
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

    private boolean mEraseSdCard;

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


                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Bundle args = getArguments();
                            boolean mEraseSdCard = args != null && args.getBoolean("erase_sd");
                            boolean mEraseEsims = args != null && args.getBoolean("erase_esim");
                            Intent intents = new Intent("android.intent.action.FACTORY_RESET");
                            intents.setPackage("android");
                            intents.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                            intents.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
                            intents.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", mEraseSdCard);
                            intents.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", mEraseEsims);
                            getActivity().sendBroadcast(intents);
                        } else {

                            Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
                            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                            intent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
                            intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", mEraseSdCard);
                            getActivity().sendBroadcast(intent);
                        }
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
                            Toast.makeText(getContext(), R.string.label_wrong_password, Toast.LENGTH_SHORT).show();
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
        factorySettingRlayout.setEnabled(true);
    }

    Dialog CleanupDialog = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("onDestroy", "onDestroy");
        settingManager.setListener(null);
        mHadler = null;
    }

    private void showCleanupDialog() {

        CleanupDialog = new Dialog(getContext(), R.style.record_voice_dialogs);
        CleanupDialog.setContentView(R.layout.display_dialog_cleanup);
        CleanupDialog.setOnCancelListener(this);
        final CircleProgressView mCircleProgressView = (CircleProgressView) CleanupDialog.findViewById(R.id.circle_progress_view);
        final TextView scheduleTxt = (TextView) CleanupDialog.findViewById(R.id.txt_schedule);
        scheduleTxt.setText("0%");
        final TextView statusTxt = (TextView) CleanupDialog.findViewById(R.id.txt_status);
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
                        CleanupDialog.dismiss();
                        ToastUtil.ShowToast(getContext(), getContext().getString(R.string.tab_clean_up));
                        CleanupDialog = null;
                        //  Toast.makeText(getContext(), R.string.tab_clean_up, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        CleanupDialog.dismiss();
                        break;
                }
            }
        };
        cleanSystemCache(handler);
        CleanupDialog.setCanceledOnTouchOutside(true);
        CleanupDialog.setCancelable(false);
        CleanupDialog.show();
        setDialogParam(CleanupDialog, 324, 324);
        //systemCleanupRlayout.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        settingManager.setListener(null);
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
        if (getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("FirstRun", 0);
            if (sharedPreferences.getBoolean("DisplayFragmetn", false)) {
                // SharedPreferences sharedPreferences = getContext().getSharedPreferences("FirstRun", 0);
                Settings.Secure.setLocationProviderEnabled(getContext().getContentResolver(), LocationManager.GPS_PROVIDER, false);
             /*   GPS gps = new GPS();
                gps.openGPSSettings(getContext(), 3);*/
                settingManager.changeSystemLanguage(settingManager.locales[settingManager.getLanguage()], settingManager.getLanguage());
                sharedPreferences.edit().putBoolean("getNaviInfoDisplayFragmetn", false).commit();
                AudioEffectManager mAudioEffectManager;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mAudioEffectManager = new AudioEffectManager(getContext(), this, getActivity().getLocalClassName());
                } else {
                    mAudioEffectManager = settingManager.getAudioEffectManager();
                }
                mAudioEffectManager.setAudioEffectTreble(0, true);
                mAudioEffectManager.setAudioEffectMiddle(0, true);
                mAudioEffectManager.setAudioEffectBass(0, true);
                mAudioEffectManager.setAudioEffectLoudness(0, true);
                mAudioEffectManager.setAudioEffectLoudness(0, true);
                mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(0, 0), true);

            }
        }

    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        CleanupDialog = null;
    }
}
