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
    private ImageView start, clear, end;
    private ImageButton shortLong;
    private LinearLayout startLlayout;
    private TextView startTxt;
    private LinearLayout clearLlayout;
    private TextView clearTxt;

    private LinearLayout shortLongLlayout;
    private TextView shortLongTxt;

    private LinearLayout endLlayout;
    private TextView endTxt;
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
        start = root.findViewById(R.id.start);
        startLlayout = root.findViewById(R.id.llayout_start);
        startTxt = root.findViewById(R.id.txt_start);
        clearLlayout = root.findViewById(R.id.llayout_clear);
        clearTxt = root.findViewById(R.id.txt_clear);
        endLlayout = root.findViewById(R.id.llayout_end);
        endTxt = root.findViewById(R.id.txt_end);
        clear = root.findViewById(R.id.clear);
        end = root.findViewById(R.id.end);
        shortLong = root.findViewById(R.id.shortLong);
        shortLongLlayout = root.findViewById(R.id.llayout_shortLong);
        shortLongTxt = root.findViewById(R.id.txt_shortLong);
        start.setOnClickListener(this);
        clear.setOnClickListener(this);
        clear.setOnTouchListener(this);
        shortLong.setOnTouchListener(this);
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
        keyValue.setBackground(R.drawable.btn_power_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_POWER);
        keyValues.add(keyValue);//power key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_search_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_AS);
        keyValues.add(keyValue);//search key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_mode_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MODE);
        keyValues.add(keyValue);//mode key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_volume_decrease_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_VOL_DECREASE);
        keyValues.add(keyValue);//volume decrease key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_volume_increase_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_VOL_INCREASE);
        keyValues.add(keyValue);//volume increase key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_pre_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_PRE);
        keyValues.add(keyValue);//pre key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_next_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_NEXT);
        keyValues.add(keyValue);//next key

//        keyValue = new KeyValue();
//        keyValue.setBackground(R.drawable.btn_voice_key);
//        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
//        keyValue.setKeyValue(WheelKey.KEY_LEARNING_SPEECH);
//        keyValues.add(keyValue);//speech key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_settings_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_SETTING);
        keyValues.add(keyValue);//setting key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_home_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_HOME);
        keyValues.add(keyValue);//home key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_menu_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MENU);
        keyValues.add(keyValue);//menu key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_back_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_BACK);
        keyValues.add(keyValue);//back key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_gps_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_GPS);
        keyValues.add(keyValue);//gps key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_music_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MP3);
        keyValues.add(keyValue);//music key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_video_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MP5);
        keyValues.add(keyValue);//video key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_hold_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_ANSWER);
        keyValues.add(keyValue);//hold key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_hangup_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_HANGUP);
        keyValues.add(keyValue);//hangup key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_fm_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_FM);
        keyValues.add(keyValue);//fm key

//        keyValue = new KeyValue();
//        keyValue.setBackground(R.drawable.btn_ok_key);
//        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
//        keyValue.setKeyValue(WheelKey.KEY_LEARNING_ENTER);
//        keyValues.add(keyValue);//ok key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_am_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_AM);
        keyValues.add(keyValue);//am key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_aux_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_AUX);
        keyValues.add(keyValue);//aux key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_mute_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_MUTE);
        keyValues.add(keyValue);//mute key

        keyValue = new KeyValue();
        keyValue.setBackground(R.drawable.btn_btmusic_key);
        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
        keyValue.setKeyValue(WheelKey.KEY_LEARNING_BTMUSIC);
        keyValues.add(keyValue);//btmusic key

//        keyValue = new KeyValue();
//        keyValue.setBackground(R.drawable.btn_dvr_key);
//        keyValue.setKeyLearningStatus(WheelKey.CMD_UNLEARNED_SPONSE);
//        keyValue.setKeyValue(WheelKey.KEY_LEARNING_DVR);
//        keyValues.add(keyValue);//dvr key

        keyAapter = new KeyAapter(keyValues, this.getContext());
        keyAapter.setKeyValues(keyValues);
        keyLearn.setAdapter(keyAapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                if (short_clickStatus == WheelKey.CMD_CLICK_KEY_ACTION) {
                    short_clickStatus = WheelKey.CMD_LONG_CLCIK_KEY_ACTION;
                    shortLong.setImageResource(R.drawable.btn_long);
                    shortLongTxt.setText(R.string.Long);
                } else {
                    short_clickStatus = WheelKey.CMD_CLICK_KEY_ACTION;
                    shortLongTxt.setText(R.string.shorts);
                    shortLong.setImageResource(R.drawable.btn_short);
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
                if (clickStatus == 0) {
                    startLlayout.setBackgroundColor(Color.parseColor("#1B1A1A"));
                    startTxt.setTextColor(Color.parseColor("#FFFFFF"));
                } else if (clickStatus == 1) {
                    startLlayout.setBackgroundColor(Color.parseColor("#414244"));
                    startTxt.setTextColor(Color.parseColor("#949494"));
                } else {
                    startLlayout.setBackgroundColor(Color.parseColor("#242529"));
                    startTxt.setTextColor(Color.parseColor("#FFFFFF"));
                }

                break;
            case R.id.clear:
                break;
            case R.id.end:
                end_clickStatus = clickStatus;
                end.setImageLevel(clickStatus);
                end.setClickable(buttonAble);
                if (clickStatus == 0) {
                    endLlayout.setBackgroundColor(Color.parseColor("#1B1A1A"));
                    endTxt.setTextColor(Color.parseColor("#FFFFFF"));
                } else if (clickStatus == 1) {
                    endLlayout.setBackgroundColor(Color.parseColor("#414244"));
                    endTxt.setTextColor(Color.parseColor("#949494"));
                } else {
                    endLlayout.setBackgroundColor(Color.parseColor("#242529"));
                    endTxt.setTextColor(Color.parseColor("#FFFFFF"));
                }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        wheelKeyLearnController.release();
        wheelKeyLearnController=null;
        mHandler=null;
        keyAapter.onDestroy();
        keyValues.clear();;
        keyValues=null;

        Log.w("settingsWheelfragment","onDestroy");
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.clear:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clearLlayout.setBackgroundResource(R.drawable.shape_gradient);
                        break;
                    case MotionEvent.ACTION_UP:
                        clearLlayout.setBackgroundColor(Color.parseColor("#1E1D1D"));
                        break;
                }
                break;
            case R.id.shortLong:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shortLongLlayout.setBackgroundResource(R.drawable.shape_gradient);
                        break;
                    case MotionEvent.ACTION_UP:
                        shortLongLlayout.setBackgroundColor(Color.parseColor("#1E1D1D"));
                        break;
                }
                break;
        }
        return false;
    }
}
