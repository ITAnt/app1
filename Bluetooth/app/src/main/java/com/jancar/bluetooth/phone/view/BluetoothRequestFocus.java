package com.jancar.bluetooth.phone.view;

import android.content.Context;
import android.media.AudioManager;
import android.os.SystemProperties;
import android.util.Log;

import com.jancar.bluetooth.lib.BluetoothManager;

import java.util.logging.Handler;

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
    private AudioManager audioManager;

    public boolean isNeedGainFocus() {
        return isNeedGainFocus;
    }

    private boolean isNeedGainFocus;


    private BluetoothRequestFocus(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        blueManager = BluetoothManager.getBluetoothManagerInstance(context);
        audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
    }

    public boolean isBTConnect() {
        return blueManager.isConnect();
    }

    public static BluetoothRequestFocus getBluetoothRequestFocusStance(Context context) {
        if (bluetoothRequestFocus == null) {
            bluetoothRequestFocus = new BluetoothRequestFocus(context);
        }
        return bluetoothRequestFocus;
    }

    public void requestAudioFocus() {
        isNeedGainFocus = true;
        blueManager.setPlayerState(true);
        Log.e(TAG, "requestAudioFoucse==");
        audioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        blueManager.setPlayerState(true);
        if (blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PAUSE) {
            blueManager.play();
        }
    }

    public void releaseAudioFocus() {
        Log.e(TAG, "releaseAudioFocus==");
        isNeedGainFocus = false;
        if (mAudioFocusListener != null) {
            audioManager.abandonAudioFocus(mAudioFocusListener);
        }
        Log.e(TAG, "blueManager.getBlueMusicData().getPlay_status()==" + blueManager.getBlueMusicData().getPlay_status());
        if (blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PLAY) {
            blueManager.pause();

        }
        blueManager.setPlayerState(false);
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(final int focusChange) {
            Log.e(TAG, "onAudioFocusChange" + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    blueManager.setPlayerState(true);
                    if (blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PAUSE) {
                        blueManager.play();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    blueManager.setPlayerState(false);
                    releaseAudioFocus();
                    if (blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PLAY) {
                        blueManager.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    blueManager.setPlayerState(false);
                    if (blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PLAY) {
                        blueManager.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    boolean flag = SystemProperties.getBoolean("persist.jancar.gpsmix", true);
                    if (!flag && blueManager.getBlueMusicData().getPlay_status() == BluetoothManager.MUSIC_STATE_PLAY) {
                        blueManager.pause();
                    }
                    break;
            }
        }
    };
}
