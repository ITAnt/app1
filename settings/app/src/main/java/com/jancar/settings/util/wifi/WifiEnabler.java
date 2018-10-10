package com.jancar.settings.util.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Switch;

import com.jancar.settings.widget.SwitchButton;

import java.util.concurrent.atomic.AtomicBoolean;

public class WifiEnabler {

    private static final String TAG = "WifiEnabler";
    private Context mContext;
    private SwitchButton mSwitch;

    private AtomicBoolean mConnected = new AtomicBoolean(false);

    //   private final WifiManager mWifiManager;
    private boolean mStateMachineEvent;
    private final IntentFilter mIntentFilter;
    private WifiController mWifiController;
    public static final int MSG_START_SCAN = 0;

    /**
     * receive the broadcast to set the switch,and the ui
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
            } 
/*                  else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                        NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        mConnected.set(info.isConnected());
                    }*/
        }
    };

    private static final String EVENT_DATA_IS_WIFI_ON = "is_wifi_on";
    private static final int EVENT_UPDATE_INDEX = 0;

    public WifiEnabler(Context context, SwitchButton switchid) {

        mWifiController = WifiController.getInstance(context);
        mContext = context;
        mSwitch = switchid;

        mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
//mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, mIntentFilter);
        setupSwitchBar();
    }

    //when we into wifi，we need to get the state of it, and update the UI;
    public void setupSwitchBar() {

        final int state = mWifiController.getWifiState();
        handleWifiStateChanged(state);

    }

    public void teardownSwitchBar() {

    }

    public void resume(Context context) {
        mContext = context;
        // Wi-Fi state is sticky, so just let the receiver update UI
        mContext.registerReceiver(mReceiver, mIntentFilter);
    }

    public void pause() {
        mContext.unregisterReceiver(mReceiver);
        mScanHandler.removeCallbacksAndMessages(null);
    }

    private void handleWifiStateChanged(int state) {
        Log.d(TAG, "handleWifiStateChanged, state = " + state);
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                long end_time = System.currentTimeMillis();
                Log.d(TAG, "calculate enable time [end] [WifiEnable]: " + Long.toString(end_time));
                setSwitchBarChecked(true);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                setSwitchBarChecked(false);
                break;
            default:
                setSwitchBarChecked(false);
        }
    }


    private void setSwitchBarChecked(boolean checked) {
        Log.d(TAG, "setSwitchChecked, checked = " + checked);
        mStateMachineEvent = true;
        mSwitch.setCheckedImmediately(checked);
        mStateMachineEvent = false;
        if (checked) {
            mScanHandler.sendEmptyMessage(MSG_START_SCAN);
        }

    }

    Handler mScanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_SCAN:
                    Log.d(TAG, "MSG_START_SCAN");
                    mWifiController.startScan();
//                                  mStateMachineEvent = false;
                    break;
                default:
                    break;
            }
        }
    };

    public Handler getScanHandler() {
        return mScanHandler;
    }

    public void onSwitchChanged(SwitchButton switchView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged, isChecked = " + isChecked);
        //Do nothing if called as a result of a state machine event
        if (mStateMachineEvent) {
            Log.d(TAG, " onCheckedChanged = " + isChecked);
            return;
        }
        Log.d(TAG, "onCheckedChanged, setWifiEnabled = " + isChecked);

        long start_time = System.currentTimeMillis();
        Log.d(TAG, "calculate enable time [start] [WifiEnable]: " + Long.toString(start_time));
        if (!mWifiController.setWifiEnabled(isChecked)) {
            mSwitch.setEnabled(true);
        }
    }

}
