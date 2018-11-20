package com.jancar.settings.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.jancar.model.WheelKey;
import com.jancar.model.WheelKeyLearnController;

import com.jancar.settings.adapter.KeyAapter;

import java.util.ArrayList;

import com.jancar.settings.R;
import com.jancar.settings.contract.KeyValue;
import com.jancar.globallib.contentprovider.ContentData;
/**
 * Created by User on 2018/5/8.
 */

public class SettingsWheelFragment extends PreferenceFragment implements WheelKeyLearnController.WheelKeyListener, View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener {
    private int start_clickStatus, clear_clickStatus, end_clickStatus;
    private byte short_clickStatus;
    private ArrayList<KeyValue> keyValues = new ArrayList<>();
    private KeyAapter keyAapter;
    private GridView keyLearn;
    private ImageView start,clear,  end;
    private ImageButton shortLong;

    private TextView startName,clearName,endName,shortLongName;

    private WheelKeyLearnController wheelKeyLearnController;
    private final static int BUTTON_NORMAL_STATUS = 0;
    private final static int BUTTON_SELECT_STATUS = 2;
    private final static int BUTTON_LEARNED_STATUS = 1;
    private final static int MSG_REFRESH_ADAPTER = 0;
    private final static int MSG_SHOW_DIALOG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REFRESH_ADAPTER:
                    keyAapter.setKeyValues(keyValues);
                    break;
                case MSG_SHOW_DIALOG:
                    toastTextShow(msg.arg1);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_pref_wheel, container, false);
        findView(root);
        initDatas();
        initController();
        setButtonStatus(R.id.end, BUTTON_LEARNED_STATUS, false);
        return root;
    }

    private void findView(View root) {
        keyLearn = root.findViewById(R.id.keyLearn);
        start = (ImageView)root.findViewById(R.id.start);
        clear = (ImageButton) root.findViewById(R.id.clear);
        end = (ImageView)root.findViewById(R.id.end);
        shortLong = (ImageButton)root.findViewById(R.id.shortLong);
        startName = root.findViewById(R.id.startName);
        endName = root.findViewById(R.id.endName);
        clearName = root.findViewById(R.id.clearName);
        shortLongName = root.findViewById(R.id.shortLongName);
        startName.setText(R.string.key_name_start);
        endName.setText(R.string.key_name_end);
        clearName.setText(R.string.key_name_clear);
        shortLongName.setText(R.string.key_name_short);
        start.setOnClickListener(this);
        clear.setOnClickListener(this);

        end.setOnClickListener(this);
        shortLong.setOnClickListener(this);

        keyLearn.setOnItemClickListener(this);
    }

    private void initController() {
        wheelKeyLearnController = new WheelKeyLearnController(this.getContext());
        wheelKeyLearnController.setWheelKeyListener(this);
        wheelKeyLearnController.scanLearnStatus();
    }

    /**
     * 初始化要学习的按键
     */
    private void initDatas() {
        KeyValue keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_power_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_POWER);
        keyValue.setName(getString(R.string.key_name_power));
        keyValues.add(keyValue);//power key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_search_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_AS);
        keyValue.setName(getString(R.string.key_name_search));
        keyValues.add(keyValue);//search key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_mode_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MODE);
        keyValue.setName(getString(R.string.key_name_mode));
        keyValues.add(keyValue);//mode key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_volume_decrease_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_VOL_DECREASE);
        keyValue.setName(getString(R.string.key_name_decrease_volum));
        keyValues.add(keyValue);//volume decrease key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_volume_increase_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_VOL_INCREASE);
        keyValue.setName(getString(R.string.key_name_increase_volum));
        keyValues.add(keyValue);//volume increase key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_pre_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_PRE);
        keyValue.setName(getString(R.string.key_name_pre));
        keyValues.add(keyValue);//pre key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_next_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_NEXT);
        keyValue.setName(getString(R.string.key_name_next));
        keyValues.add(keyValue);//next key

//        keyValue = new KeyValue();
//        keyValue.setBackground(R.drawable.btn_voice_key);
//        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
//        keyValue.setKeyValue(WheelKey.KEY_LEARNING_SPEECH);
//        keyValues.add(keyValue);//speech key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_settings_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_SETTING);
        keyValue.setName(getString(R.string.key_name_settings));
        keyValues.add(keyValue);//setting key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_home_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_HOME);
        keyValue.setName(getString(R.string.key_name_home));
        keyValues.add(keyValue);//home key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_menu_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MENU);
        keyValue.setName(getString(R.string.key_name_menu));
        keyValues.add(keyValue);//menu key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_back_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_BACK);
        keyValue.setName(getString(R.string.key_name_back));
        keyValues.add(keyValue);//back key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_gps_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_GPS);
        keyValue.setName(getString(R.string.key_name_gps));
        keyValues.add(keyValue);//gps key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_music_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MP3);
        keyValue.setName(getString(R.string.key_name_music));
        keyValues.add(keyValue);//music key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_video_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MP5);
        keyValue.setName(getString(R.string.key_name_vedio));
        keyValues.add(keyValue);//video key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_hold_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_ANSWER);
        keyValue.setName(getString(R.string.key_name_hold));
        keyValues.add(keyValue);//hold key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_hangup_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_HANGUP);
        keyValue.setName(getString(R.string.key_name_hungup));
        keyValues.add(keyValue);//hangup key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_fm_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_FM);
        keyValue.setName(getString(R.string.key_name_fm));
        keyValues.add(keyValue);//fm key

//        keyValue = new KeyValue();
//        keyValue.setBackground(R.drawable.btn_ok_key);
//        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
//        keyValue.setKeyValue(WheelKey.KEY_LEARNING_ENTER);
//        keyValues.add(keyValue);//ok key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_am_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_AM);
        keyValue.setName(getString(R.string.key_name_am));
        keyValues.add(keyValue);//am key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_aux_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_AUX);
        keyValue.setName(getString(R.string.key_name_aux));
        keyValues.add(keyValue);//aux key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_mute_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MUTE);
        keyValue.setName(getString(R.string.key_name_mute));
        keyValues.add(keyValue);//mute key

        keyValue = new KeyValue();
        //keyValue.setBackground(R.drawable.btn_btmusic_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_BTMUSIC);
        keyValue.setName(getString(R.string.key_name_btmusic));
        keyValues.add(keyValue);//btmusic key

//        keyValue = new KeyValue();
//        keyValue.setBackground(R.drawable.btn_dvr_key);
//        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
//        keyValue.setKeyValue(WheelKey.KEY_LEARNING_DVR);
//        keyValues.add(keyValue);//dvr key

        keyAapter = new KeyAapter(keyValues, this.getActivity());
        keyAapter.setKeyValues(keyValues);
        keyLearn.setAdapter(keyAapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wheelKeyLearnController.release();
    }

    @Override
    public void onEnterLearn() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_DIALOG;
        msg.arg1 = R.string.str_settings_wheel_enter_learning;
        msg.sendToTarget();
    }


    @Override
    public void onExitLearn() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_DIALOG;
        msg.arg1 = R.string.str_settings_wheel_exit_learning;
        msg.sendToTarget();
    }

    @Override
    public void onNotifyToachKey() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_DIALOG;
        msg.arg1 = R.string.str_settings_wheel_touch_key;
        msg.sendToTarget();
    }

    @Override
    public void onSuccess() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_DIALOG;
        msg.arg1 = R.string.str_settings_wheel_learn_success;
        msg.sendToTarget();
    }

    @Override
    public void onFail() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_DIALOG;
        msg.arg1 = R.string.str_settings_wheel_learn_error;
        msg.sendToTarget();
    }

    @Override
    public void onRefreshAadpter(byte[] datas) {
        int count = 0;
        for (byte b :
                datas) {
            keyValues.get(count).setKeyLearningStatus(b);
            count++;
        }
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_REFRESH_ADAPTER;
        msg.sendToTarget();
    }

    private void toastTextShow(int id) {
        try {
            Toast.makeText(this.getContext(), getResources().getString(id), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                wheelKeyLearnController.startLearn();
                setButtonStatus(R.id.start, BUTTON_LEARNED_STATUS, false);
                setButtonStatus(R.id.end, BUTTON_NORMAL_STATUS, true);
                break;
            case R.id.end:
                setButtonStatus(R.id.end, BUTTON_LEARNED_STATUS, false);
                setButtonStatus(R.id.start, BUTTON_NORMAL_STATUS, true);
                wheelKeyLearnController.endLearn();
                break;
            case R.id.clear:

                wheelKeyLearnController.clearLearn();
                break;
            case R.id.shortLong:
                if(short_clickStatus== WheelKey.CMD_CLICK_KEY_ACTION){
                    short_clickStatus = WheelKey.CMD_LONG_CLCIK_KEY_ACTION;
                    shortLongName.setText(R.string.key_name_long);
                }else{
                    short_clickStatus = WheelKey.CMD_CLICK_KEY_ACTION;
                    shortLongName.setText(R.string.key_name_short);
                }
                break;
        }
    }

    private void setButtonStatus(int buttonId, int clickStatus, boolean buttonAble) {
        switch (buttonId) {
            case R.id.start:
                start_clickStatus = clickStatus;
                start.setImageLevel(clickStatus);
                start.setClickable(buttonAble);


                break;
            case R.id.clear:
                break;
            case R.id.end:
                end_clickStatus = clickStatus;
                end.setImageLevel(clickStatus);
                end.setClickable(buttonAble);

               /* Intent intent = new Intent();
                intent.setClass(this.getContext(), SettingsService.class);
                this.getContext().startService(intent);*/
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        clearStatus();

        if (start_clickStatus != BUTTON_LEARNED_STATUS) {
            return;
        }
        KeyValue keyValue = keyValues.get(position);
        byte kValue = keyValue.getKeyValue();
        wheelKeyLearnController.sendKeyValueToMcu(short_clickStatus, kValue);
        keyValue.setKeyLearningStatus((byte) BUTTON_SELECT_STATUS);
        keyAapter.setKeyValues(keyValues);
    }


    private void clearStatus() {
        for (KeyValue keyValue :
                keyValues) {
            if (keyValue.getKeyLearningStatus() == BUTTON_SELECT_STATUS) {
                keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
