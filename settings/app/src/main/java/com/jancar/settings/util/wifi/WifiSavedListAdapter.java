package com.jancar.settings.util.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jancar.settings.R;

import java.util.List;

/**
 * Created by ATC6110 on 2018/3/29.
 */

public class WifiSavedListAdapter extends BaseAdapter {

    private static final String TAG = "WifiSavedListAdapter";

    public Boolean DEBUG = true;
    protected Context mContext;
    protected List<ScanResult> mDatas;
    private LayoutInflater inflater;
    private WifiController mWifiController;


    private final int STATE_IDLE = 0;
    private final int STATE_CONNECTING = 1;
    private final int STATE_AUTHENTICATING = 2;
    private final int STATE_OBTAINING_IPADDR = 3;
    private final int STATE_CONNECTED = 4;
    private final int STATE_DISCONNECTING = 5;
    private final int STATE_DISCONNECTED = 6;
    private final int STATE_FAILED = 7;

    private final int LEVELNUM = 4;

    static final int[] WIFI_STATE = {
            R.string.wifi_text_state_none,
            R.string.wifi_text_state_connecting,
            R.string.wifi_text_state_authenticating,
            R.string.wifi_text_state_obtaining_ipaddr,
            R.string.wifi_text_state_connected,
            R.string.wifi_text_state_none,
            R.string.wifi_text_state_none,
            R.string.wifi_text_state_failed,
    };

    static final int[][] WIFI_SIGNAL_STRENGTH = {
            {
                    R.mipmap.wifi_0,
                    R.mipmap.wifi_1,
                    R.mipmap.wifi_2,
                    R.mipmap.wifi_3

            },
            {
                    R.mipmap.wifi_encrypt_0,
                    R.mipmap.wifi_encrypt_1,
                    R.mipmap.wifi_encrypt_2,
                    R.mipmap.wifi_encrypt_3,
            },
            {
                    R.mipmap.wifi_encrypt_0,
                    R.mipmap.wifi_encrypt_1,
                    R.mipmap.wifi_encrypt_2,
                    R.mipmap.wifi_encrypt_3
            }
    };


    public WifiSavedListAdapter(Context context, List<ScanResult> scanResults) {

        mContext = context;
        mDatas = scanResults;
        mWifiController = WifiController.getInstance(mContext);
        inflater = LayoutInflater.from(mContext);

    }

    public WifiSavedListAdapter(Context context) {

        mContext = context;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<ScanResult> datas) {

        mDatas.clear();
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public ScanResult getItem(int position) {

        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 定义ViewHolder
        ViewHolder viewHolder;
        if (convertView == null) {
            // convertView = mContext.
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.wifi_item_layout, null);
            viewHolder.pointSSID = (TextView) convertView.findViewById(R.id.WifiName);
            viewHolder.pointSignal = (ImageView) convertView.findViewById(R.id.WifiSignal);
            viewHolder.connectState = (TextView) convertView.findViewById(R.id.txt_connect_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.pointSSID.setText(mDatas.get(position).SSID);
        setLvSign(mDatas.get(position), viewHolder.pointSignal);
        if (0 == position) {
            setSavedText(mDatas, viewHolder.connectState);
        } else {
            viewHolder.connectState.setText("");
        }

        return convertView;
    }

    private void setLvSign(ScanResult scanResult, ImageView lv) {
        Log.d(TAG, "setLvSign()");
        int level = mWifiController.getSignalLevel(scanResult.level, LEVELNUM);
        if (DEBUG)
            Log.d(TAG, "Scanresult.ssid:" + scanResult.SSID + "scanResult.RSSI:" + scanResult.level + ", level:" + level);
        String capabilities = scanResult.capabilities;

        int type = mWifiController.WIFICIPHER_WPA;
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                type = mWifiController.WIFICIPHER_WPA;
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                type = mWifiController.WIFICIPHER_WEP;
            } else {
                type = mWifiController.WIFICIPHER_NOPASS;
            }
        }
        lv.setImageDrawable(mContext.getResources().getDrawable(WIFI_SIGNAL_STRENGTH[type][level]));
    }

    private void setSavedText(List<ScanResult> datas, TextView text) {
        Log.d(TAG, "setSavedText()");
        if (0 == datas.size()) {
            return;
        }

        int state = mWifiController.getConnectionState();
        Log.d(TAG, "connectstate:" + state);
        if (STATE_FAILED == state) {
            text.setText(mContext.getString(WIFI_STATE[state]));
            return;
        }
        String SSID = mWifiController.getConnectedSSID();
        Log.d(TAG, "ssid:" + SSID);
        if ("<unknown ssid>" == SSID) {
            text.setText("");
            return;
        }
        if (SSID.equals("\"" + datas.get(0).SSID + "\"")) {
            text.setText(mContext.getString(WIFI_STATE[state]));
        }
    }

    public void updateRssi(int rssi, int position) {
        Log.d(TAG, "updateRssi()");
        if (null == mDatas) {
            if (DEBUG)
                Log.d(TAG, "updateRssi: mData is null");
            return;
        }
        if (mDatas.size() > position) {
            mDatas.get(position).level = rssi;
        }
    }

    static class ViewHolder {
        TextView pointSSID;
        TextView connectState;
        ImageView pointSignal;

    }

}
