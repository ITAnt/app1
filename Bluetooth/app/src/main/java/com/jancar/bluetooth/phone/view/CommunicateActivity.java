package com.jancar.bluetooth.phone.view;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTPhoneCallListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.lib.BluetoothPhoneClass;
import com.jancar.bluetooth.phone.BuildConfig;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.CommunicateContract;
import com.jancar.bluetooth.phone.presenter.CommunicatePresenter;
import com.jancar.bluetooth.phone.util.AnimationUtils;
import com.jancar.bluetooth.phone.util.TimeUtil;
import com.ui.mvp.view.BaseActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Tzq
 * @date 2018-8-23 17:57:30
 * 电话界面
 */
public class CommunicateActivity extends BaseActivity<CommunicateContract.Presenter, CommunicateContract.View> implements CommunicateContract.View, BTPhoneCallListener, View.OnClickListener {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager.LayoutParams mLayoutParams1;

    private TextView tvNumber;              //拨号号码
    private TextView tvNumberName;          //号码运营商
    private TextView tvCommunType;          //通话类型
    private ImageView ivVoice;              //静音
    private ImageView ivKeyPad;             //键盘
    private ImageView ivVehicle;            //车载
    private TextView tvVehicle;             //车载文字
    private ImageView ivHangUp;             //挂断
    private ImageView ivAnswer;             //接听
    private LinearLayout linearVoice;       //静音linear
    private LinearLayout linearKey;         //键盘linear
    private LinearLayout linearAnswer;      //接听
    private LinearLayout linearVehicle;     //车载
    private LinearLayout linearInputKey;    //键盘布局

    private TextView tvHalfName;            //小屏界面来电显示name
    private TextView tvHalfNumber;          //小屏来电号码
    private TextView tvHalfComing;          //来电
    private ImageView ivHalfAns;            //接听来电
    private ImageView ivHalfCar;            //车载手机切换
    private ImageView ivHalfHang;           //挂断

    private ImageView keyoard1;             //键盘数字
    private ImageView keyoard2;
    private ImageView keyoard3;
    private ImageView keyoard4;
    private ImageView keyoard5;
    private ImageView keyoard6;
    private ImageView keyoard7;
    private ImageView keyoard8;
    private ImageView keyoard9;
    private ImageView keyoardx;
    private ImageView keyoard0;
    private ImageView keyoardj;

    private TextView tvInputNum;
    private String msgString = null;

    private View phoneView;
    private View haifView;

    private String mCallNumber;             //拨打号码
    private String mCallName;               //号码名字
    private String mCallPhoneType;          //电话类型
    private Timer mCallTimer;
    private int mCallTime;
    private boolean isShowKey = false;      //是否显示键盘

    private int mCallType = BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_NONE;
    private int mCallScoState = BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT;
    private final static String CALLHISTROY_TYPE_INCOMING = "incoming";
    private final static String CALLHISTROY_TYPE_OUTGOING = "outgoing";
    private final static String CALLHISTROY_TYPE_MISSED = "missed";
    private final static int MSG_UPDATA_PHONE_UI = 0;               //电话界面UI
    private final static int MSG_BLUETOOTH_UPDATA_SCO_STATE = 1;
    private final static int MSG_UPDATA_CALL_TIME = 2;              //通话时间
    private final static int MSG_BLUETOOTH_SERVICE_READY = 3;
    private String CAll_TYPE_TAG = "call_type";                       //通话类型 int
    private String CALL_NUMBER_TAG = "call_number";                   //通话号码 string
    private String EXTRA_FULL_DISPLAY = "full_display";               //是否全屏显示 boollean
    private boolean isFull;//是否全屏
    private boolean saveIsFull = true;//记忆是否是全屏状态，切换屏幕用
    private BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        initView();
        findView();
        handIntent(intent);
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(this.getApplicationContext());
        bluetoothManager.registerCallOnKeyEvent();
        bluetoothManager.registerBTPhoneListener(this);
        saveIsFull = isFull;
        showView();
        updataView(mCallType, mCallNumber);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyView();
        bluetoothManager.unregisterBTPhoneListener();
        bluetoothManager.unRegisterCallOnKeyEvent();
    }

    @Override
    public CommunicateContract.Presenter createPresenter() {
        return new CommunicatePresenter();
    }

    @Override
    public CommunicateContract.View getUiImplement() {
        return this;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATA_PHONE_UI:
                    BluetoothPhoneBookData phoneBookData = (BluetoothPhoneBookData) msg.obj;
                    int callState = phoneBookData.getPhoneType();
                    String phoneNumber = phoneBookData.getPhoneNumber();
                    updataView(callState, phoneNumber);
                    break;
                case MSG_UPDATA_CALL_TIME:
                    if (isFull) {
                        tvCommunType.setText(TimeUtil.updataCallTime(mCallTime));
                    } else {
                        tvHalfComing.setText(TimeUtil.updataCallTime(mCallTime));
                    }
                    break;
                case MSG_BLUETOOTH_SERVICE_READY:
                    updataView(mCallType, mCallNumber);
                    break;

                case MSG_BLUETOOTH_UPDATA_SCO_STATE:
                    int scoState = msg.arg1;
                    if (isFull) {
                        updataScoState(scoState);
                    } else {
                        updataHalfScoState(scoState);
                    }

                    break;

            }
        }
    };

    private void updataScoState(int scoState) {
        if (scoState == mCallScoState) {
            return;
        }
        mCallScoState = scoState;
        if (BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT == mCallScoState) {
            //车机
            ivVehicle.setImageResource(R.drawable.iv_commun_car_n);
            tvVehicle.setText(R.string.calling_vehicle);
        } else {
            //手机
            ivVehicle.setImageResource(R.drawable.iv_commun_phone_n);
            tvVehicle.setText(R.string.calling_phone);
        }
    }


    private void updataHalfScoState(int scoState) {
        if (scoState == mCallScoState) {
            return;
        }
        mCallScoState = scoState;
        if (BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT == mCallScoState) {
            //车机
            ivHalfCar.setImageResource(R.drawable.iv_commun_half_car);
        } else {

            ivHalfCar.setImageResource(R.drawable.iv_commun_half_phone);
        }
    }


    private void handIntent(Intent intent) {
        mCallType = intent.getIntExtra(CAll_TYPE_TAG, BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_NONE);
        mCallNumber = intent.getStringExtra(CALL_NUMBER_TAG);
        isFull = intent.getBooleanExtra(EXTRA_FULL_DISPLAY, false);

    }

    private void showView() {
        if (isFull) {
            mWindowManager.addView(phoneView, mLayoutParams);
        } else {
            setSystemUIVisible(false);
            mWindowManager.addView(haifView, mLayoutParams1);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handIntent(intent);
        if (isFull != saveIsFull) {
            if (isFull == false) {
                mWindowManager.removeViewImmediate(phoneView);
                mWindowManager.addView(haifView, mLayoutParams1);
            } else {
                mWindowManager.removeViewImmediate(haifView);
                mWindowManager.addView(phoneView, mLayoutParams);
            }
        }
        saveIsFull = isFull;
        updataView(mCallType, mCallNumber);

    }

    private String getTopActivityInfo() {
        String className = "";
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(2);
        if (runningTaskInfos != null && runningTaskInfos.size() > 1) {
            ComponentName componentName = runningTaskInfos.get(1).topActivity;
            className = componentName.getClassName();
        }
//        Log.e(TAG, "getTopActivityInfo"+className);
        return className;
    }

    private void initView() {
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = 541;
        mLayoutParams.y = 59;
        phoneView = LayoutInflater.from(this).inflate(R.layout.activity_communicate, null);

        // 小屏
        mLayoutParams1 = new WindowManager.LayoutParams();
        mLayoutParams1.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams1.format = PixelFormat.RGBA_8888;
        mLayoutParams1.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams1.gravity = Gravity.CENTER_HORIZONTAL;
        mLayoutParams1.width = 400;
        mLayoutParams1.height = 200;
//        mLayoutParams1.y = -170;
        haifView = LayoutInflater.from(this).inflate(R.layout.activity_communicate_half, null);
    }

    private void setSystemUIVisible(boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    private void findView() {
        findkeyoardView();
        tvNumber = phoneView.findViewById(R.id.tv_comm_message_number);
        tvNumberName = phoneView.findViewById(R.id.tv_comm_message_type);
        tvCommunType = phoneView.findViewById(R.id.tv_comm_message_direction);
        ivHangUp = phoneView.findViewById(R.id.iv_comm_message_hangup);
        ivVoice = phoneView.findViewById(R.id.iv_comm_message_voice);
        ivKeyPad = phoneView.findViewById(R.id.iv_comm_message_keypad);
        ivVehicle = phoneView.findViewById(R.id.iv_comm_message_vehicle);
        tvVehicle = phoneView.findViewById(R.id.tv_comm_vehicle);
        ivAnswer = phoneView.findViewById(R.id.iv_comm_message_answer);
        tvInputNum = phoneView.findViewById(R.id.dial_tv_number);
        linearVoice = phoneView.findViewById(R.id.line_voice);
        linearKey = phoneView.findViewById(R.id.line_key);
        linearAnswer = phoneView.findViewById(R.id.linear_answer);
        linearVehicle = phoneView.findViewById(R.id.liner_vehicle);
        linearInputKey = phoneView.findViewById(R.id.liner_comm_key);

        ivVoice.setEnabled(false);
        ivKeyPad.setEnabled(false);
        ivVehicle.setEnabled(false);

        ivHangUp.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        ivKeyPad.setOnClickListener(this);
        ivVehicle.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        ivAnswer.setOnClickListener(this);
        if (isShowKey) {
            linearInputKey.setVisibility(View.VISIBLE);
        } else {
            linearInputKey.setVisibility(View.GONE);
        }

        tvHalfName = haifView.findViewById(R.id.tv_commun_half_name);            //小屏界面来电显示name
        tvHalfNumber = haifView.findViewById(R.id.tv_commun_half_number);       //小屏来电号码
        tvHalfComing = haifView.findViewById(R.id.tv_commun_half_type);         //来电
        ivHalfAns = haifView.findViewById(R.id.iv_commun_half_answer);          //接听来电
        ivHalfCar = haifView.findViewById(R.id.iv_commun_half_phone);           //车载手机切换
        ivHalfHang = haifView.findViewById(R.id.iv_commun_half_hang);           //挂断
        ivHalfAns.setOnClickListener(this);
        ivHalfCar.setOnClickListener(this);
        ivHalfHang.setOnClickListener(this);

    }

    private void findkeyoardView() {
        keyoard1 = phoneView.findViewById(R.id.item_dial_show_1);
        keyoard2 = phoneView.findViewById(R.id.item_dial_show_2);
        keyoard3 = phoneView.findViewById(R.id.item_dial_show_3);
        keyoard4 = phoneView.findViewById(R.id.item_dial_show_4);
        keyoard5 = phoneView.findViewById(R.id.item_dial_show_5);
        keyoard6 = phoneView.findViewById(R.id.item_dial_show_6);
        keyoard7 = phoneView.findViewById(R.id.item_dial_show_7);
        keyoard8 = phoneView.findViewById(R.id.item_dial_show_8);
        keyoard9 = phoneView.findViewById(R.id.item_dial_show_9);
        keyoardx = phoneView.findViewById(R.id.item_dial_show_10);
        keyoard0 = phoneView.findViewById(R.id.item_dial_show_11);
        keyoardj = phoneView.findViewById(R.id.item_dial_show_12);
        keyoard1.setOnClickListener(this);
        keyoard2.setOnClickListener(this);
        keyoard3.setOnClickListener(this);
        keyoard4.setOnClickListener(this);
        keyoard5.setOnClickListener(this);
        keyoard6.setOnClickListener(this);
        keyoard7.setOnClickListener(this);
        keyoard8.setOnClickListener(this);
        keyoard9.setOnClickListener(this);
        keyoardx.setOnClickListener(this);
        keyoard0.setOnClickListener(this);
        keyoardj.setOnClickListener(this);
        keyoard0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getStrKeyNum("+");
                getManager().sendDTMFCode((byte) '+');
                return true;
            }
        });

    }


    private void destroyView() {
        if (isFull) {
            mWindowManager.removeViewImmediate(phoneView);
            Log.d("CommunicateActivity", "mm");
        } else {
            mWindowManager.removeViewImmediate(haifView);
        }
    }


    private void updataView(int calltype, String callNum) {
        if (!isFull) {
            upadateLittileView(calltype, callNum);
            return;
        }

        String name = null;
        if (callNum == null) {
            tvNumber.setText(R.string.str_phone_unkonow);
            tvNumberName.setText(R.string.str_phone_unkonow);
        } else {
            if ((mCallNumber != null) && (!callNum.equals(mCallNumber))) {
                mCallNumber = callNum;
                tvNumber.setText(mCallNumber);

            } else if (mCallNumber == null) {
                mCallNumber = callNum;
                tvNumber.setText(mCallNumber);
            } else {
                tvNumber.setText(mCallNumber);
            }
        }
        if (mCallNumber != null) {
            if (mCallNumber.equals("10086") || mCallNumber.equals("1008611")) {
                name = getResources().getString(R.string.str_phone_mobile);
            } else if (mCallNumber.equals("10010")) {
                name = getResources().getString(R.string.str_phone_unicom);
            } else if (mCallNumber.equals("10000")) {
                name = getResources().getString(R.string.str_phone_telecom);
            } else {
                name = getPresenter().getContactByNumber(mCallNumber);
            }
        }
        if (name == null || name.equals("")) {
            mCallName = getResources().getString(R.string.str_phone_unkonow);
            tvNumberName.setText(mCallName);
        } else {
            mCallName = name;
            tvNumberName.setText(mCallName);

        }
        switch (calltype) {
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_DIALING:
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_ALERTING:
                //正在呼叫中
                mCallPhoneType = CALLHISTROY_TYPE_OUTGOING;
                tvCommunType.setText(R.string.str_phone_outgoing);
                ivVoice.setEnabled(false);
                ivKeyPad.setEnabled(false);
                ivVehicle.setEnabled(false);
                if (calltype != mCallType) {
                    mCallType = calltype;
                } else {
                    mCallType = calltype;
                }
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_INCOMING:
                mCallPhoneType = CALLHISTROY_TYPE_MISSED;
                tvCommunType.setText(R.string.str_phone_missed);
                linearKey.setVisibility(View.INVISIBLE);
                linearVoice.setVisibility(View.INVISIBLE);
                linearVehicle.setVisibility(View.GONE);
                linearAnswer.setVisibility(View.VISIBLE);
                if (calltype != mCallType) {
                    mCallType = calltype;
                }
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_ACTIVE:

                if (mCallPhoneType == null) {
                    mCallPhoneType = CALLHISTROY_TYPE_INCOMING;
                } else {
                    if (mCallPhoneType.equals(CALLHISTROY_TYPE_MISSED)) {
                        mCallPhoneType = CALLHISTROY_TYPE_INCOMING;
                    }
                }
                linearKey.setVisibility(View.VISIBLE);
                linearVoice.setVisibility(View.VISIBLE);
                linearVehicle.setVisibility(View.VISIBLE);
                linearAnswer.setVisibility(View.GONE);
                ivHalfAns.setVisibility(View.GONE);
                ivVoice.setEnabled(true);
                ivKeyPad.setEnabled(true);
                ivVehicle.setEnabled(true);
                if (calltype != mCallType) {
                    mCallType = calltype;
                    startTimer();
                }
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_TERMINATED:
                Log.d("CommunicateActivity", "nnnnnnnn");
                BluetoothPhoneBookData bluetoothPhoneBookData = new BluetoothPhoneBookData();
                bluetoothPhoneBookData.setPhoneName(mCallName);
                bluetoothPhoneBookData.setPhoneNumber(mCallNumber);
                bluetoothPhoneBookData.setPhoneBookCallType(mCallPhoneType);
                getPresenter().addCallLog(bluetoothPhoneBookData);
                stopTimer();
                this.finish();
        }
    }

    private void upadateLittileView(int calltype, String callNum) {

        String name = null;
        if (callNum == null) {
            tvHalfNumber.setText(R.string.str_phone_unkonow);
            tvHalfName.setText(R.string.str_phone_unkonow);
        } else {
            if ((mCallNumber != null) && (!callNum.equals(mCallNumber))) {
                mCallNumber = callNum;
                tvHalfNumber.setText(mCallNumber);

            } else if (mCallNumber == null) {
                mCallNumber = callNum;
                tvHalfNumber.setText(mCallNumber);
            } else {
                tvHalfNumber.setText(mCallNumber);
            }
        }
        if (mCallNumber != null) {
            if (mCallNumber.equals("10086") || mCallNumber.equals("1008611")) {
                name = getResources().getString(R.string.str_phone_mobile);
            } else if (mCallNumber.equals("10010")) {
                name = getResources().getString(R.string.str_phone_unicom);
            } else if (mCallNumber.equals("10000")) {
                name = getResources().getString(R.string.str_phone_telecom);
            } else {
                name = getPresenter().getContactByNumber(mCallNumber);
            }
        }
        if (name == null || name.equals("")) {
            mCallName = getResources().getString(R.string.str_phone_unkonow);
            tvHalfName.setText(mCallName);
        } else {
            mCallName = name;
            tvHalfName.setText(mCallName);
        }

        switch (calltype) {
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_DIALING:
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_ALERTING:
                //正在呼叫中
                mCallPhoneType = CALLHISTROY_TYPE_OUTGOING;
                tvHalfComing.setText(R.string.str_phone_outgoing);
                ivHalfAns.setVisibility(View.GONE);
                if (calltype != mCallType) {
                    mCallType = calltype;
                } else {
                    mCallType = calltype;
                }
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_INCOMING:
                mCallPhoneType = CALLHISTROY_TYPE_MISSED;
                tvHalfComing.setText(R.string.str_phone_missed);
                ivHalfAns.setVisibility(View.VISIBLE);
                if (calltype != mCallType) {
                    mCallType = calltype;
                }
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_ACTIVE:
                ivHalfAns.setVisibility(View.GONE);
                if (mCallPhoneType == null) {
                    mCallPhoneType = CALLHISTROY_TYPE_INCOMING;
                } else {
                    if (mCallPhoneType.equals(CALLHISTROY_TYPE_MISSED)) {
                        mCallPhoneType = CALLHISTROY_TYPE_INCOMING;
                    }
                }

                if (calltype != mCallType) {
                    mCallType = calltype;
                    startTimer();
                }
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_TERMINATED:
                BluetoothPhoneBookData bluetoothPhoneBookData = new BluetoothPhoneBookData();
                bluetoothPhoneBookData.setPhoneName(mCallName);
                bluetoothPhoneBookData.setPhoneNumber(mCallNumber);
                bluetoothPhoneBookData.setPhoneBookCallType(mCallPhoneType);
                getPresenter().addCallLog(bluetoothPhoneBookData);
                stopTimer();
                this.finish();
        }

    }

    private void stopTimer() {
        if (mCallTimer != null) {
            mCallTimer.cancel();
            mCallTimer = null;
        }
    }

    private void startTimer() {
        mCallTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mCallTime += 1;
                mHandler.sendEmptyMessage(MSG_UPDATA_CALL_TIME);
            }
        };
        mCallTime = -1;
        mCallTimer.schedule(task, 0, 1000);
    }


    @Override
    public void onNotifyPhoneCallUI(BluetoothPhoneBookData bluetoothPhoneBookData) {
        Message message = mHandler.obtainMessage();
        message.what = MSG_UPDATA_PHONE_UI;
        message.obj = bluetoothPhoneBookData;
        message.sendToTarget();
    }

    @Override
    public void onNotifySocState(Message message) {
        mHandler.sendMessage(message);

    }

    @Override
    public void onNotifyPhoneCallTime() {
        mHandler.sendEmptyMessage(MSG_UPDATA_CALL_TIME);

    }

    @Override
    public void onNotifyServiceDisconnect() {
        this.finish();

    }

    @Override
    public void onNotifyServieReady() {
        mHandler.sendEmptyMessage(MSG_BLUETOOTH_SERVICE_READY);

    }


    @Override
    public BluetoothManager getManager() {

        return bluetoothManager;
    }

    @Override
    public Context getUIContext() {
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_comm_message_hangup:
                if (mCallType == BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_INCOMING) {
                    getPresenter().rejectCall();
                } else {
                    getPresenter().terminateCall();
                }
                this.finish();
                break;
            case R.id.iv_comm_message_voice:
                if (getPresenter().isMicMute()) {
                    getManager().muteMic(false);
                    ivVoice.setImageResource(R.drawable.commun_voice_selector);
                } else {
                    getManager().muteMic(true);
                    ivVoice.setImageResource(R.drawable.commun_no_voice_selector);
                }
                break;
            case R.id.iv_comm_message_keypad:
                isShowKey = !isShowKey;
                if (isShowKey) {
//                    linearInputKey.setVisibility(View.VISIBLE);
                    AnimationUtils.showAndHiddenAnimation(linearInputKey, AnimationUtils.AnimationState.STATE_SHOW, 1000);
                } else {
//                    linearInputKey.setVisibility(View.GONE);
                    AnimationUtils.showAndHiddenAnimation(linearInputKey, AnimationUtils.AnimationState.STATE_HIDDEN, 1000);
                }
                break;
            case R.id.iv_comm_message_vehicle:
                if (mCallScoState != BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT) {
                    getManager().switchAudioMode(false);
                    ivVehicle.setImageResource(R.drawable.iv_commun_phone_n);
                    tvVehicle.setText(R.string.calling_phone);

                } else {
                    getManager().switchAudioMode(true);
                    ivVehicle.setImageResource(R.drawable.iv_commun_car_n);
                    tvVehicle.setText(R.string.calling_vehicle);
                }
                break;
            case R.id.iv_comm_message_answer:
                getPresenter().acceptCall();
                break;
            case R.id.iv_commun_half_answer:
                getPresenter().acceptCall();
                break;
            case R.id.iv_commun_half_phone:
                if (mCallScoState != BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT) {
                    getManager().switchAudioMode(false);
                    ivHalfCar.setImageResource(R.drawable.iv_commun_half_phone);

                } else {
                    getManager().switchAudioMode(true);
                    ivHalfCar.setImageResource(R.drawable.iv_commun_half_car);
                }
                break;
            case R.id.iv_commun_half_hang:
                if (mCallType == BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_INCOMING) {
                    getPresenter().rejectCall();
                } else {
                    getPresenter().terminateCall();
                }
                break;
            case R.id.item_dial_show_1:
                getStrKeyNum("1");
                getManager().sendDTMFCode((byte) '1');
                break;
            case R.id.item_dial_show_2:
                getStrKeyNum("2");
                getManager().sendDTMFCode((byte) '2');
                break;
            case R.id.item_dial_show_3:
                getStrKeyNum("3");
                getManager().sendDTMFCode((byte) '3');
                break;
            case R.id.item_dial_show_4:
                getStrKeyNum("4");
                getManager().sendDTMFCode((byte) '4');
                break;
            case R.id.item_dial_show_5:
                getStrKeyNum("5");
                getManager().sendDTMFCode((byte) '5');
                break;
            case R.id.item_dial_show_6:
                getStrKeyNum("6");
                getManager().sendDTMFCode((byte) '6');
                break;
            case R.id.item_dial_show_7:
                getStrKeyNum("7");
                getManager().sendDTMFCode((byte) '7');
                break;
            case R.id.item_dial_show_8:
                getStrKeyNum("8");
                getManager().sendDTMFCode((byte) '8');
                break;
            case R.id.item_dial_show_9:
                getStrKeyNum("9");
                getManager().sendDTMFCode((byte) '9');
                break;
            case R.id.item_dial_show_10:
                getStrKeyNum("*");
                getManager().sendDTMFCode((byte) '*');
                break;
            case R.id.item_dial_show_11:
                getStrKeyNum("0");
                getManager().sendDTMFCode((byte) '0');
                break;
            case R.id.item_dial_show_12:
                getStrKeyNum("#");
                getManager().sendDTMFCode((byte) '#');
                break;
        }
    }

    /**
     * 获取点击的数值
     *
     * @param keyNum 数值
     */
    private void getStrKeyNum(String keyNum) {
        if (msgString == null) {
            msgString = keyNum;
        } else {
            msgString += keyNum;
        }
        tvInputNum.setText(msgString);
    }
}
