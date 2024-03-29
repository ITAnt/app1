package com.jancar.bluetooth.phone.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.jancar.bluetooth.lib.BluetoothManager;

/**
 * @anthor Tzq
 * @time 2018/9/27 15:21
 * @describe 系统媒体按键
 */
public class RegisterMediaSession {
    private static final String TAG = "RegisterMediaSession";
    private MediaSession mMediaSession;
    private Context context;
    private BluetoothManager bluetoothManager;

    public RegisterMediaSession(Context context, BluetoothManager bluetoothManager) {
        this.context = context;
        this.bluetoothManager = bluetoothManager;
        setupMediaSession();
    }

    /**
     * 初始化并激活 MediaSession
     */
    @SuppressLint("WrongConstant")
    private void setupMediaSession() {
//        第二个参数 tag: 这个是用于调试用的,随便填写即可
        mMediaSession = new MediaSession(context, context.getPackageName());
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        //指明支持的按键信息类型
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );
        Log.e(TAG, "setupMediaSession");
        mMediaSession.setCallback(new MediaSession.Callback() {

            @SuppressLint("Override")
            public boolean onMediaButtonEvent(Intent intent) {
                // TODO Auto-generated method stub
                KeyEvent keyEvent;
                Log.e(TAG, "onMediaButtonEvent: " + intent + "this:" + this);
                if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                    keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        handleMediaButton(keyEvent);
                    }
                }
                return super.onMediaButtonEvent(intent);
            }
        });
    }

    /**
     * @Title: requestMediaButton
     * @Description: 请求绑定系统媒体按键
     * @return: void
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void requestMediaButton() {
        try {
            Log.e(TAG, "mMediaSession.isActive()===" + mMediaSession.isActive());
            if (!mMediaSession.isActive()) {
                mMediaSession.setActive(true);
            }
            Log.e(TAG, "requestMediaButton:" + context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title: releaseMediaButton
     * @Description: 释放系统媒体按键
     * @return: void
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void releaseMediaButton() {
        try {
            mMediaSession.release();
            Log.e(TAG, "releaseMediaButton:" + context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMediaButton(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (KeyEvent.KEYCODE_MEDIA_NEXT == keyCode) {
            Log.e(TAG, "KEYCODE_MEDIA_NEXT===");
            bluetoothManager.next();
        } else if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == keyCode) {
        } else if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) {
        } else if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode) {
            Log.e(TAG, "KEYCODE_MEDIA_PLAY_PAUSE===");
            bluetoothManager.prev();
        } else if (KeyEvent.KEYCODE_MEDIA_STOP == keyCode) {
            Log.e(TAG, "KEYCODE_MEDIA_STOP===");
            bluetoothManager.pause();
        } else if (KeyEvent.KEYCODE_MEDIA_PAUSE == keyCode) {
            Log.e(TAG, "KEYCODE_MEDIA_PAUSE===");
            bluetoothManager.pause();
        } else if (KeyEvent.KEYCODE_MEDIA_PLAY == keyCode) {
            Log.e(TAG, "KEYCODE_MEDIA_PLAY===");
            bluetoothManager.play();
        }
    }
}
