package com.jancar.settings.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jancar.audio.AudioEffectManager;
import com.jancar.audio.AudioEffectParam;
import com.jancar.bluetooth.lib.BluetoothSettingManager;
import com.jancar.settings.view.fragment.SystemFragment;

import static android.content.Context.MODE_WORLD_WRITEABLE;

/**
 * Created by ouyan on 2018/11/8.
 */

public class ResetReceiver extends BroadcastReceiver implements AudioEffectManager.AudioListener {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("com.jancar.action.reset.default.settings")){
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            Log.w("ResetReceiver","true");
            AudioEffectManager mAudioEffectManager;
            mAudioEffectManager = new AudioEffectManager(context, this, context.getPackageName());
            mAudioEffectManager.setAudioEffectTreble(0, true);
            mAudioEffectManager.setAudioEffectMiddle(0, true);
            mAudioEffectManager.setAudioEffectBass(0, true);
            mAudioEffectManager.setAudioEffectLoudness(0, true);
            mAudioEffectManager.setAudioEffectLoudness(0, true);
            mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(0, 0), true);
            BluetoothSettingManager manager = BluetoothSettingManager.getBluetoothSettingManager(context);
            manager.openBluetooth();
            mAudioEffectManager.close();
        }

    }


    @Override
    public void onVolumeChanged(int i, int i1) {

    }

    @Override
    public void onMuteChanged(boolean b, int i) {

    }
}
