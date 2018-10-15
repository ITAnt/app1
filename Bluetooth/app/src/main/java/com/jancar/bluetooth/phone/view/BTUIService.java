package com.jancar.bluetooth.phone.view;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jancar.JancarServer;
import com.jancar.bluetooth.Listener.BTPhoneCallListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.lib.BluetoothPhoneClass;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.util.AnimationUtils;
import com.jancar.bluetooth.phone.util.TimeUtil;
import com.jancar.prompt.PromptController;
import com.jancar.state.JacState;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.security.keystore.SoterUtil.TAG;

/**
 * @anthor Tzq
 * @time 2018/9/30 13:40
 * @describe TODO
 */
public class BTUIService extends Service implements BTPhoneCallListener, View.OnClickListener {


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
    private final static int MSG_BLUETOOTH_DESTROY_VIEW = 4;
    private String CAll_TYPE_TAG = "call_type";                       //通话类型 int
    private String CALL_NUMBER_TAG = "call_number";                   //通话号码 string
    private String EXTRA_FULL_DISPLAY = "full_display";               //是否全屏显示 boollean
    private boolean isFull;//是否全屏
    private boolean saveIsFull = true;//记忆是否是全屏状态，切换屏幕用
    private BluetoothManager bluetoothManager;
    private JancarServer jancarServer;
    private boolean isShowPhone = false;
    private int visibility;
    private View saveView;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BTUIService", "onCreate");
        initView();
        findView();
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(this.getApplicationContext());
        JacState jacState = new JacState() {
            @Override
            public void OnCallEx(eCallState eState, String number) {
                super.OnCallEx(eState, number);
                mCallType = eState.nativeInt;
                mCallNumber = number;
                Log.e(TAG, "OnCallEx isShowPhone==" + isShowPhone + "eState==" + eState);
                if (eState != eCallState.eCall_Idle && eState != eCallState.eCall_Terminated) {
                    if (!isShowPhone) {
                        jancarServer.requestPrompt(PromptController.DisplayType.DT_PHONE, PromptController.DisplayParam.DP_SHOW);
                    }
                }
            }
        };
        jancarServer = (JancarServer) getSystemService(JancarServer.JAC_SERVICE);
        jancarServer.registerJacStateListener(jacState.asBinder());
        jancarServer.registerPrompt(promptController.asBinder());
    }

    PromptController promptController = new PromptController(PromptController.DisplayType.DT_PHONE) {
        @Override
        public void show(boolean bMaximize, HashMap<String, Object> map) {
            super.show(bMaximize, map);
            isFull = bMaximize;
            Log.e(TAG, "show==" + isFull);
            showView();
            updataView(mCallType, mCallNumber);
        }

        @Override
        public void hide(HashMap<String, Object> map) {
            super.hide(map);
            destroyView();
        }

        @Override
        public void update(boolean bMaximize, HashMap<String, Object> map) {
            super.update(bMaximize, map);
            Log.e(TAG, "update" + bMaximize);
            isFull = bMaximize;
            if (isShowPhone) {
                if (isFull) {
                    mWindowManager.removeViewImmediate(haifView);
                    mWindowManager.addView(phoneView, mLayoutParams);
                    saveView = phoneView;
                    updataScoState(mCallScoState);
                } else {
                    Log.e(TAG, "phoneLittileView===");
                    mWindowManager.removeViewImmediate(phoneView);
                    mWindowManager.addView(haifView, mLayoutParams1);
                    saveView = haifView;
                    updataHalfScoState(mCallScoState);
                }
                updataView(mCallType, mCallNumber);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
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
                    if (bluetoothManager.isMicMute()) {
//                        bluetoothManager.muteMic(false);
                        if (isFull) {
                            ivVoice.setImageResource(R.drawable.commun_voice_selector);
                        }
                    } else {
//                        bluetoothManager.muteMic(true);
                        if (isFull) {
                            ivVoice.setImageResource(R.drawable.commun_no_voice_selector);
                        }

                    }
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
                case MSG_BLUETOOTH_DESTROY_VIEW:
//                    destroyView();
                    if (isShowPhone) {
                        jancarServer.requestPrompt(PromptController.DisplayType.DT_PHONE, PromptController.DisplayParam.DP_HIDE);
                    }
                    break;

            }
        }
    };

    private void updataScoState(int scoState) {
//        if (scoState == mCallScoState) {
//            return;
//        }
        Log.e(TAG, "updataScoState:" + scoState);
        mCallScoState = scoState;
        if (BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT == mCallScoState) {
            //车机
            ivVehicle.setImageResource(R.drawable.iv_commun_c_n);
            tvVehicle.setText(R.string.calling_vehicle);
        } else {
            //手机
            ivVehicle.setImageResource(R.drawable.iv_commun_p_n);
            tvVehicle.setText(R.string.calling_phone);
        }
    }


    private void updataHalfScoState(int scoState) {
        Log.e(TAG, "updataHalfScoState:" + scoState);
//        if (scoState == mCallScoState) {
//            return;
//        }
        mCallScoState = scoState;
        if (BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT == mCallScoState) {
            //车机状态
            ivHalfCar.setImageResource(R.drawable.iv_commun_half_c);
        } else {
            ivHalfCar.setImageResource(R.drawable.iv_commun_half_p);
        }
    }

    private void showView() {
        isShowPhone = true;
        bluetoothManager.registerBTPhoneListener(this);
        bluetoothManager.muteMic(false);
        bluetoothManager.registerCallOnKeyEvent();
        if (isFull) {
            mWindowManager.addView(phoneView, mLayoutParams);
            saveView = phoneView;
            updataScoState(mCallScoState);
        } else {
            mWindowManager.addView(haifView, mLayoutParams1);
            saveView = haifView;
            updataHalfScoState(mCallScoState);
        }

    }

    private void initView() {
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = 541;
        mLayoutParams.y = 59;
        phoneView = LayoutInflater.from(this).inflate(R.layout.activity_communicate, null);

        // 小屏
        mLayoutParams1 = new WindowManager.LayoutParams();
        mLayoutParams1.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mLayoutParams1.format = PixelFormat.RGBA_8888;
        mLayoutParams1.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams1.gravity = Gravity.CENTER_HORIZONTAL;
        mLayoutParams1.width = 450;
        mLayoutParams1.height = 140;
        mLayoutParams1.y = -170;
        haifView = LayoutInflater.from(this).inflate(R.layout.activity_communicate_half, null);
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
                bluetoothManager.sendDTMFCode((byte) '+');
                return true;
            }
        });

    }


    private void destroyView() {
        stopTimer();
        if (isShowPhone) {
            if (isFull) {
                mWindowManager.removeViewImmediate(phoneView);
                saveView = null;
            } else {
                mWindowManager.removeViewImmediate(haifView);
                saveView = null;
            }
        }
        if (saveView != null) {
            mWindowManager.removeViewImmediate(saveView);
        }
        isShowPhone = false;
        mCallScoState = BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT;
        bluetoothManager.unregisterBTPhoneListener();
        bluetoothManager.unRegisterCallOnKeyEvent();
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
                try {
                    name = bluetoothManager.getContactByNumber(mCallNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                }
                startTimer();
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_TERMINATED:
                Log.d("CommunicateActivity", "nnnnnnnn");
                BluetoothPhoneBookData bluetoothPhoneBookData = new BluetoothPhoneBookData();
                bluetoothPhoneBookData.setPhoneName(mCallName);
                bluetoothPhoneBookData.setPhoneNumber(mCallNumber);
                bluetoothPhoneBookData.setPhoneBookCallType(mCallPhoneType);
                bluetoothManager.addCallLogList(bluetoothPhoneBookData);
                stopTimer();
//                destroyView();
                if (isShowPhone) {
                    jancarServer.requestPrompt(PromptController.DisplayType.DT_PHONE, PromptController.DisplayParam.DP_HIDE);
                }
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
                try {
                    name = bluetoothManager.getContactByNumber(mCallNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                }
                startTimer();
                break;
            case BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_TERMINATED:
                saveCallLog();
                stopTimer();
                mHandler.sendEmptyMessage(MSG_BLUETOOTH_DESTROY_VIEW);
                //destroyView();
        }
    }

    private void stopTimer() {
        if (mCallTimer != null) {
            mCallTimer.cancel();
            mCallTimer = null;
        }
    }

    private void startTimer() {
        Log.e(TAG, "startTimer");
        if (mCallTimer == null) {
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
        mHandler.sendEmptyMessage(MSG_BLUETOOTH_DESTROY_VIEW);

    }

    @Override
    public void onNotifyServieReady() {
        mHandler.sendEmptyMessage(MSG_BLUETOOTH_SERVICE_READY);

    }

    private void saveCallLog() {
        BluetoothPhoneBookData bluetoothPhoneBookData = new BluetoothPhoneBookData();
        bluetoothPhoneBookData.setPhoneName(mCallName);
        bluetoothPhoneBookData.setPhoneNumber(mCallNumber);
        bluetoothPhoneBookData.setPhoneBookCallType(mCallPhoneType);
        bluetoothManager.addCallLogList(bluetoothPhoneBookData);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_comm_message_hangup:
                if (mCallType == BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_INCOMING) {
                    bluetoothManager.rejectCall();
                    saveCallLog();
                } else {
                    bluetoothManager.terminateCall();
                }
                String s = tvInputNum.getText().toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(s)) {
                    tvInputNum.setText("");
                }
//                destroyView();
                if (isShowPhone) {
                    jancarServer.requestPrompt(PromptController.DisplayType.DT_PHONE, PromptController.DisplayParam.DP_HIDE);
                }
                break;
            case R.id.iv_comm_message_voice:
                if (bluetoothManager.isMicMute()) {
                    bluetoothManager.muteMic(false);
                    ivVoice.setImageResource(R.drawable.commun_no_voice_selector);
                } else {
                    bluetoothManager.muteMic(true);
                    ivVoice.setImageResource(R.drawable.commun_voice_selector);
                }
                break;
            case R.id.iv_comm_message_keypad:
                isShowKey = !isShowKey;
                if (isShowKey) {
                    AnimationUtils.showAndHiddenAnimation(linearInputKey, AnimationUtils.AnimationState.STATE_SHOW, 1000);
                } else {
                    AnimationUtils.showAndHiddenAnimation(linearInputKey, AnimationUtils.AnimationState.STATE_HIDDEN, 1000);
                }
                break;
            case R.id.iv_comm_message_vehicle:
                if (mCallScoState != BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT) {
                    bluetoothManager.switchAudioMode(false);
                    ivVehicle.setImageResource(R.drawable.iv_commun_p_n);
                    tvVehicle.setText(R.string.calling_phone);

                } else {
                    bluetoothManager.switchAudioMode(true);
                    ivVehicle.setImageResource(R.drawable.iv_commun_c_n);
                    tvVehicle.setText(R.string.calling_vehicle);
                }
                break;
            case R.id.iv_comm_message_answer:
                bluetoothManager.acceptCall();
                break;
            case R.id.iv_commun_half_answer:
                bluetoothManager.acceptCall();
                break;
            case R.id.iv_commun_half_phone:
                if (mCallScoState != BluetoothPhoneClass.BLUETOOTH_PHONE_SCO_CONNECT) {
                    bluetoothManager.switchAudioMode(false);
                    ivHalfCar.setImageResource(R.drawable.iv_commun_half_p);
                } else {
                    ivHalfCar.setImageResource(R.drawable.iv_commun_half_c);
                    bluetoothManager.switchAudioMode(true);
                }
                break;
            case R.id.iv_commun_half_hang:
                if (mCallType == BluetoothPhoneClass.BLUETOOTH_PHONE_CALL_STATE_INCOMING) {
                    bluetoothManager.rejectCall();
                    saveCallLog();
                } else {
                    bluetoothManager.terminateCall();
                }
                if (isShowPhone) {
                    jancarServer.requestPrompt(PromptController.DisplayType.DT_PHONE, PromptController.DisplayParam.DP_HIDE);
                }
                destroyView();
                break;
            case R.id.item_dial_show_1:
                getStrKeyNum("1");
                bluetoothManager.sendDTMFCode((byte) '1');
                break;
            case R.id.item_dial_show_2:
                getStrKeyNum("2");
                bluetoothManager.sendDTMFCode((byte) '2');
                break;
            case R.id.item_dial_show_3:
                getStrKeyNum("3");
                bluetoothManager.sendDTMFCode((byte) '3');
                break;
            case R.id.item_dial_show_4:
                getStrKeyNum("4");
                bluetoothManager.sendDTMFCode((byte) '4');
                break;
            case R.id.item_dial_show_5:
                getStrKeyNum("5");
                bluetoothManager.sendDTMFCode((byte) '5');
                break;
            case R.id.item_dial_show_6:
                getStrKeyNum("6");
                bluetoothManager.sendDTMFCode((byte) '6');
                break;
            case R.id.item_dial_show_7:
                getStrKeyNum("7");
                bluetoothManager.sendDTMFCode((byte) '7');
                break;
            case R.id.item_dial_show_8:
                getStrKeyNum("8");
                bluetoothManager.sendDTMFCode((byte) '8');
                break;
            case R.id.item_dial_show_9:
                getStrKeyNum("9");
                bluetoothManager.sendDTMFCode((byte) '9');
                break;
            case R.id.item_dial_show_10:
                getStrKeyNum("*");
                bluetoothManager.sendDTMFCode((byte) '*');
                break;
            case R.id.item_dial_show_11:
                getStrKeyNum("0");
                bluetoothManager.sendDTMFCode((byte) '0');
                break;
            case R.id.item_dial_show_12:
                getStrKeyNum("#");
                bluetoothManager.sendDTMFCode((byte) '#');
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
