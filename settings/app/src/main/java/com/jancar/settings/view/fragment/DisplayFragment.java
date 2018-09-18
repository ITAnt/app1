package com.jancar.settings.view.fragment;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.settings.R;
import com.jancar.settings.adapter.LanguageListAdapter;
import com.jancar.settings.lib.SettingManager;
import com.jancar.settings.listener.Contract.CacheContractImpl;
import com.jancar.settings.listener.Contract.DisplayContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragment;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.CachePresenter;
import com.jancar.settings.presenter.DisplayPresenter;
import com.jancar.settings.view.activity.MainActivity;
import com.jancar.settings.widget.BitPreference;
import com.jancar.settings.widget.DropPreference;
import com.jancar.settings.widget.SeekBarPreference;
import com.jancar.settings.widget.SelfDialog;
import com.jancar.settings.widget.SwitchPreference;

import java.util.ArrayList;
import java.util.List;

import static com.jancar.settings.contract.SettingContants.PRE_AUTO_LIGHT;
import static com.jancar.settings.contract.SettingContants.PRE_LANGUAGE;
import static com.jancar.settings.contract.SettingContants.PRE_LIGHT_VALUE;
import static com.jancar.settings.util.Tool.setDialogParam;

/**
 * Created by ouyan on 2018/8/30.
 */
//显示设置
public class DisplayFragment extends BaseFragments<DisplayPresenter> implements DisplayContractImpl.View, View.OnClickListener, SettingManager.SettingsManagerListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {
    private TextView dimmingDayValueTxt;
    private ScrollView displayScrollView;
    private ListView languageList;
    private SeekBar seekbar_dimming_day_value;
    private TextView dimmingNightDayValueTxt;
    private SeekBar dimmingNightValueSeekbar;
    private RelativeLayout languageRlayout;
    private TextView languageSummaryText;
    private ImageView languageArrowImg;
    private TextView languageSystemLineTxt;
    private RelativeLayout restoreDefaultRlayout;
    private SettingManager settingManager;
    SelfDialog selfDialog;
    List<String> stringList;
    LanguageListAdapter adapter;
    private View view;
    Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            ((MainActivity) getActivity()).mHadler = null;
            if (languageList != null) {
                languageList.setVisibility(View.GONE);
                displayScrollView.setVisibility(View.VISIBLE);
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public int initResid() {
        //return R.xml.preference_screen_display;
        return 0;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            displayScrollView = (ScrollView) view.findViewById(R.id.scrollview_display);
            languageList = (ListView) view.findViewById(R.id.list_language);
            dimmingDayValueTxt = (TextView) view.findViewById(R.id.txt_dimming_day_value);
            dimmingNightDayValueTxt = (TextView) view.findViewById(R.id.txt_dimming_night_value);
            seekbar_dimming_day_value = (SeekBar) view.findViewById(R.id.seekbar_dimming_day_value);
            dimmingNightValueSeekbar = (SeekBar) view.findViewById(R.id.seekbar_dimming_night_value);
            languageRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_language);
            languageSummaryText = (TextView) view.findViewById(R.id.txt_language_summary);
            languageArrowImg = (ImageView) view.findViewById(R.id.img_language_arrow);
            restoreDefaultRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_restore_default);
            languageRlayout.setOnClickListener(this);
            seekbar_dimming_day_value.setOnSeekBarChangeListener(this);
            dimmingNightValueSeekbar.setOnSeekBarChangeListener(this);
            restoreDefaultRlayout.setOnClickListener(this);

        }
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display, null);
        initView(savedInstanceState);
        initData(savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        settingManager.setListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
         settingManager.setListener(null);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        stringList = new ArrayList<>();
        stringList.add("中文（简体）");
        stringList.add("中文（繁体）");
        stringList.add("English");
        adapter = new LanguageListAdapter(getContext(), stringList);
        languageList.setAdapter(adapter);
        languageList.setOnItemClickListener(this);
        settingManager = SettingManager.getSettingManager(this.getActivity());
        languageSummaryText.setText(stringList.get(settingManager.getLanguage()));
        adapter.setID(settingManager.getLanguage());
        seekbar_dimming_day_value.setMax(settingManager.getBrightnessMax());
        dimmingNightValueSeekbar.setMax(settingManager.getBrightnessMax());
        seekbar_dimming_day_value.setProgress(settingManager.getDayBrightness());
        dimmingNightValueSeekbar.setProgress(settingManager.getNightBrightness());

    }

    @Override
    public IPresenter initPresenter() {
        return new DisplayPresenter(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //boolean checked = settingManager.getSwitchButton(PRE_AUTO_LIGHT);
            case R.id.rlayout_language:
                ((MainActivity) getActivity()).mHadler = mHadler;
                displayScrollView.setVisibility(View.GONE);
                languageList.setVisibility(View.VISIBLE);
                // setLanguageRadioVisibility(languageRadio.getVisibility());
                break;
            case R.id.rlayout_restore_default:
                showRestoreDefaultDialog();
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_dimming_day_value:
                if (!settingManager.getTailState()) {
                    settingManager.setBrightness(progress, false);
                }
                dimmingDayValueTxt.setText(seekBar.getProgress() + "");
                settingManager.setDayBrightness(progress, false);
                break;
            case R.id.seekbar_dimming_night_value:
                if (settingManager.getTailState()) {
                    settingManager.setBrightness(progress, false);
                }
                dimmingNightDayValueTxt.setText(seekBar.getProgress() + "");
                settingManager.setNightBrightness(progress, false);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        switch (seekBar.getId()) {
            case R.id.seekbar_dimming_day_value:
                if (!settingManager.getTailState()) {
                    settingManager.setBrightness(progress, false);
                }
                settingManager.setDayBrightness(progress, true);
                dimmingDayValueTxt.setText(progress + "");
                if (dimmingNightValueSeekbar.getProgress() > progress) {
                    settingManager.setNightBrightness(progress, true);
                    dimmingNightDayValueTxt.setText(progress + "");
                    dimmingNightValueSeekbar.setProgress(progress);
                }
                break;
            case R.id.seekbar_dimming_night_value:
                if (settingManager.getTailState()) {
                    settingManager.setBrightness(progress, false);
                }

                if (progress > seekbar_dimming_day_value.getProgress()) {
                    Toast.makeText(getContext(), "夜间亮度不能超过白天亮度", Toast.LENGTH_SHORT).show();
                    settingManager.setNightBrightness(seekbar_dimming_day_value.getProgress(), true);
                    dimmingNightDayValueTxt.setText(seekbar_dimming_day_value.getProgress() + "");
                    dimmingNightValueSeekbar.setProgress(seekbar_dimming_day_value.getProgress());
                } else {
                    settingManager.setNightBrightness(progress, true);
                    dimmingNightDayValueTxt.setText(progress + "");
                }
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
*/                      settingManager.resetDeafaultSettings();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        displayScrollView.setVisibility(View.VISIBLE);
        languageList.setVisibility(View.GONE);
        settingManager.changeSystemLanguage(settingManager.locales[position],position);
    }
    @Override
    public void onStart() {
        super.onStart();
        registerOberver();
    //    MLog.info(TAG, "onStart");
    }
    @Override
    public void onStop() {
        super.onStop();
        unregisterOberver();
      //  MLog.info(TAG, "onStop");
    }

    @Override
    public void notifyRefreshUI() {
        initData(null);
    }
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");//Settings.System.SCREEN_AUTO_BRIGHTNESS_ADJ);
    private boolean isRegistered = false;

    private void registerOberver() {
        try {
            if (BrightnessObserver != null) {
                if (!isRegistered) {
                    //MLog.debug(TAG, "[ouyangyj] register BrightnessObserver");
                    final ContentResolver cr = getActivity().getContentResolver();
                    cr.unregisterContentObserver(BrightnessObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, BrightnessObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, BrightnessObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, BrightnessObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
           // MLog.debug(TAG, "[ouyangyj] register BrightnessObserver error! " + throwable);
        }
    }

    private void unregisterOberver() {
        try {
            if (BrightnessObserver != null) {
                if (isRegistered) {
                   // MLog.debug(TAG, "[ouyangyj] unregister BrightnessObserver");
                  getActivity().  getContentResolver().unregisterContentObserver(BrightnessObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable throwable) {
           // MLog.debug(TAG, "[ouyangyj] unregister BrightnessObserver error! " + throwable);
        }
    }

    private ContentObserver BrightnessObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);
            if (selfChange) return;
         //   MLog.debug(TAG, "[ouyangyj] BrightnessObserver onChange");
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
              //  Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
            //    MLog.debug(this, "[ouyangyj] 亮度模式改变");
//                updateBrightnessInfo();
            } else if (BRIGHTNESS_URI.equals(uri)) {
                //settingManager.setDayBrightness(settingManager.getBrightness(), false);
              //  initData(null);
             //   Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
            //    MLog.debug(this, "[ouyangyj] 亮度模式为手动模式 值改变");
//                updateBrightnessInfo();
            }else if (BRIGHTNESS_ADJ_URI.equals(uri)) {
               // MLog.debug(this, "[ouyangyj] 亮度模式为自动模式 值改变");
//                updateBrightnessInfo();
               // Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
            } else {
              //  Toast.makeText(getContext(), "4", Toast.LENGTH_SHORT).show();
             //   MLog.debug(this, "[ouyangyj] 亮度调整 其他");
//                updateBrightnessInfo();
            }
        }
    };
    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void launchActivity(@NonNull Intent intent) {

    }


    @Override
    public void killMyself() {

    }
}
