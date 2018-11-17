package com.jancar.bluetooth.phone.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.SystemProperties;
import android.util.Log;

import com.jancar.JancarManager;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.state.JacState;

/**
 * @anthor Tzq
 * @time 2018/9/27 15:24
 * @describe TODO
 */
public class BluetoothRequestFocus {
    private String TAG = BluetoothRequestFocus.class.getSimpleName();
    private static BluetoothRequestFocus bluetoothRequestFocus;
    private Context context;
    private BluetoothManager blueManager;
    public static boolean HandPaused = false;
    private AudioManager audioManager;
    private static boolean BT_PLAY_STATUS = false;
    public final static int BT_INIT = 0; //蓝牙音乐的初始化状态
    public final static int BT_FOCUSE_GAIN = 1;//蓝牙音乐获取焦点状态
    public final static int BT_FOCUSE_LOSS = 2;//蓝牙音乐失去焦点状态
    public final static int BT_FOCUSE_LOSS_TRANSIENT = 3;//蓝牙音乐临时失去焦点状态
    public final static int BT_FOCUSE_TRANSIENT_CAN_DUCK = 4;//类似蓝牙音乐临时失去焦点状态
    public final static int BT_NONE = 5;//蓝牙音乐暂时没有状态
    public final static int BT_IDL = 6;//蓝牙音乐每个状态处理后进入该状态
    public final static int CallStateIdle = 0;//电话空闲状态
    public final static int CallStateTer = 1;//电话挂断
    public final static int CallStateAct = 2;//通话中
    public static int CallState = 0;
    public static boolean CarState = false;//是否处于倒车状态
    private JancarManager jancarManager;
    private BackCarListener backCarListener;

    public void setBackCarListener(BackCarListener backCarListener) {
        this.backCarListener = backCarListener;
    }

    public interface BackCarListener {
        void onNotifyBackCarStop();

        void onNotifyBackCarStart();

        void onNotifyActivityFinish();
    }

    public int getCurrentBTStatus() {
        Log.e(TAG, "getCurrentBTStatus==" + currentBTStatus);
        return currentBTStatus;
    }

    public void setCurrentBTStatus(int currentBTStatus) {
        Log.e(TAG, "setCurrentBTStatus==" + currentBTStatus);
        this.currentBTStatus = currentBTStatus;
    }

    private int currentBTStatus = BT_NONE;

    public boolean isNeedGainFocus() {
        return isNeedGainFocus;
    }

    private boolean isNeedGainFocus;


    private BluetoothRequestFocus(Context context) {
        this.context = context;
        init();
    }

    @SuppressLint("WrongConstant")
    private void init() {
        blueManager = BluetoothManager.getBluetoothManagerInstance(context);
        audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        jancarManager = (JancarManager) context.getSystemService(JancarManager.JAC_SERVICE);
        JacState jacState = new JacState() {
            @Override
            public void OnCallEx(eCallState eState, String number) {
                super.OnCallEx(eState, number);
                Log.e("BluetoothRequestFocus", "eState====:" + eState);
                if (eState == eCallState.eCall_Idle) {
                    CallState = CallStateIdle;
                } else if (eState == eCallState.eCall_Terminated) {
//                    CallState = CallStateTer;
                    CallState = CallStateIdle;
                } else {
                    CallState = CallStateAct;
                }
                Log.e("BluetoothRequestFocus", "CallState=====" + CallState);
            }

            @Override
            public void OnBackCar(boolean bState) {
                super.OnBackCar(bState);
                Log.e("BluetoothRequestFocus", "bState===:" + bState);
                CarState = bState;
                if (!bState) {
                    if (backCarListener != null) {
                        backCarListener.onNotifyBackCarStop();
                    }
                } else {
                    if (backCarListener != null) {
                        backCarListener.onNotifyBackCarStart();
                    }
                }
            }
        };
        jancarManager.registerJacStateListener(jacState.asBinder());
    }

    public boolean isBTConnect() {
        return blueManager.isConnect();
    }

    public static BluetoothRequestFocus getBluetoothRequestFocusStance(Context context) {
        if (bluetoothRequestFocus == null) {
            bluetoothRequestFocus = new BluetoothRequestFocus(context.getApplicationContext());
        }
        return bluetoothRequestFocus;
    }


    public void btMusicPlay() {
        Log.e(TAG, "btMusicPlay==");
        blueManager.play();
    }

    public void btMusicPause() {
        Log.e(TAG, "btMusicPause==");
        blueManager.pause();
    }

    public void btMusicNext() {
        Log.e(TAG, "btMusicNext==");
        blueManager.next();
    }

    public void btMusicPre() {
        Log.e(TAG, "btMusicPre==");
        blueManager.prev();
    }

    public void setBTPlayStatus(boolean b) {
        Log.e(TAG, "btSetPlayStatus==" + b);
        blueManager.setPlayerState(b);
        BT_PLAY_STATUS = b;
    }

    public boolean getPlayStatus() {
        Log.e(TAG, "getPlayStatus==" + BT_PLAY_STATUS);
        return BT_PLAY_STATUS;
    }

    public void requestAudioFocus() {
        isNeedGainFocus = true;
        Log.e(TAG, "requestAudioFoucse==");
        audioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        setCurrentBTStatus(BT_INIT);
    }

    public void releaseAudioFocus() {
        Log.e(TAG, "releaseAudioFocus==");
        isNeedGainFocus = false;
        if (mAudioFocusListener != null) {
            audioManager.abandonAudioFocus(mAudioFocusListener);
        }
        setBTPlayStatus(false);
        btMusicPause();
        setCurrentBTStatus(BT_NONE);
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(final int focusChange) {
            Log.e(TAG, "onAudioFocusChange:===" + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.e(TAG, "GAIN:" + focusChange);
                    Log.d("BluetoothRequestFocus", "HandPaused:===" + HandPaused);
                    if (!HandPaused) {
                        setCurrentBTStatus(BT_FOCUSE_GAIN);
                    }

                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    setCurrentBTStatus(BT_FOCUSE_LOSS);
                    Log.e(TAG, "LOSS===:" + focusChange);
                    HandPaused = false;
                    if (blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PLAY) {
                        btMusicPause();
                    }
                    if (backCarListener != null) {
                        Log.e("BluetoothRequestFocus", "onNotifyActivityFinish===");
                        backCarListener.onNotifyActivityFinish();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    setCurrentBTStatus(BT_FOCUSE_LOSS_TRANSIENT);
                    Log.e(TAG, "TRANSIENT===:" + focusChange);
                    if (blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PLAY) {
                        btMusicPause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    setCurrentBTStatus(BT_FOCUSE_TRANSIENT_CAN_DUCK);
                    Log.e(TAG, "CAN_DUCK===:" + focusChange);
                    int flag = SystemProperties.getInt("persist.jancar.gpsmix", 100);
                    Log.e("BluetoothRequestFocus", "flag:" + flag);
                    if (flag == 0 && blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PLAY) {
                        btMusicPause();
                    }
                    break;
            }
        }
    };
}
