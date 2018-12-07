package com.jancar.settings.util.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.settings.R;
import com.jancar.settings.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static android.security.KeyStore.getApplicationContext;

/**
 * Created by ATC6110 on 2018/3/29.
 */

public class WifiListAdapter extends BaseAdapter {

    private static final String TAG = "WifiListAdapter";
    protected Context mContext;
    protected List<ScanResult> mDatas;
    private List<ScanResult> mSavedResultsShow;
    private LayoutInflater inflater;
    private WifiController mWifiController;
    public Boolean DEBUG = true;
    private OnClickListener OnClickListener;
    private final int STATE_IDLE = 0;
    private final int STATE_CONNECTING = 1;
    private final int STATE_AUTHENTICATING = 2;
    private final int STATE_OBTAINING_IPADDR = 3;
    private final int STATE_CONNECTED = 4;
    private final int STATE_DISCONNECTING = 5;
    private final int STATE_DISCONNECTED = 6;
    private final int STATE_FAILED = 7;
    private delete mdelete;
    private final int LEVELNUM = 4;

    final int[][] WIFI_SIGNAL_STRENGTH = {
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
                    R.mipmap.wifi_encrypt_3,
            }
    };

    public WifiListAdapter(Context context, List<ScanResult> scanResults) {

        mContext = context;
        mDatas = scanResults;
        mWifiController = WifiController.getInstance(mContext);
        inflater = LayoutInflater.from(mContext);

    }

    public WifiListAdapter(Context context) {

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

    public void setOnClickListener(WifiListAdapter.OnClickListener onClickListener) {
        OnClickListener = onClickListener;
    }

    public List<ScanResult> getmSavedResultsShow() {
        return mSavedResultsShow;
    }

    public void setmSavedResultsShow(List<ScanResult> mSavedResultsShow) {
        this.mSavedResultsShow = new ArrayList<>();
        this.mSavedResultsShow.addAll(mSavedResultsShow);
    }

    public void setMdelete(delete mdelete) {
        this.mdelete = mdelete;
    }

    public interface OnClickListener {
        void onClick(View v, int type, int position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 定义ViewHolder
        final ViewHolder viewHolder;
        if (convertView == null) {
            // convertView = mContext.
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.wifi_item_layout, null);
            viewHolder.pointSSID = (TextView) convertView.findViewById(R.id.WifiName);
            viewHolder.pointSignal = (ImageView) convertView.findViewById(R.id.WifiSignal);
            viewHolder.connectStateTxt = (TextView) convertView.findViewById(R.id.txt_connect_state);
            viewHolder.operatingTxt = (TextView) convertView.findViewById(R.id.txt_operating);
            viewHolder.wifiLlayout = (LinearLayout) convertView.findViewById(R.id.llayout_wifi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.pointSSID.setText(mDatas.get(position).SSID);
        setLvSign(mDatas.get(position), viewHolder.pointSignal);
        viewHolder.operatingTxt.setText(mContext.getResources().getString(R.string.wifi_text_connect));

        viewHolder.connectStateTxt.setText(mContext.getResources().getString(R.string.wifi_text_not_enabled));
        if (mSavedResultsShow != null) {
            for (ScanResult mScanResult : mSavedResultsShow) {
                if (mScanResult.SSID.equals(mDatas.get(position).SSID)) {
                    viewHolder.connectStateTxt.setText(mContext.getResources().getString(R.string.wifi_text_wifi_saved));
                }
            }
        }


        setSavedText(mDatas, mDatas.get(position), viewHolder.connectStateTxt, viewHolder.operatingTxt);

        viewHolder.wifiLlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OnClickListener != null) {
                    int Type = 0;
                    if (mContext.getResources().getString(R.string.wifi_text_wifi_connected).equals(viewHolder.connectStateTxt.getText().toString())) {
                        Type = 1;
                    } else if (mContext.getResources().getString(R.string.wifi_text_wifi_saved).equals(viewHolder.connectStateTxt.getText().toString())) {
                        Type = 2;
                    }
                    OnClickListener.onClick(v, Type, position);
                }
            }
        });
        if (viewHolder.connectStateTxt.getText().equals(mContext.getResources().getString(R.string.wifi_text_wifi_saved))) {
            viewHolder.operatingTxt.setBackgroundResource(R.mipmap.bg_f);
        } else {
            viewHolder.operatingTxt.setBackgroundResource(R.mipmap.bg_n);
        }
        return convertView;
    }

    private void setLvSign(ScanResult scanResult, ImageView lv) {
        Log.d(TAG, "setLvSign()");
        int level = mWifiController.getSignalLevel(scanResult.level, LEVELNUM);
        String capabilities = scanResult.capabilities;

        //       Log.d(TAG, "Scanresult.ssid:" + scanResult.SSID + ", level:" + level);
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

    private void setSavedText(List<ScanResult> datas, ScanResult mScanResult, TextView text, TextView operating) {
        Log.d(TAG, "setSavedText()");

        if (0 == datas.size()) {
            return;
        }

        String SSID = mWifiController.getConnectedSSID();
        if ("<unknown ssid>" == SSID) {
            return;
        }


        if (SSID.equals("\"" + mScanResult.SSID + "\"")) {

            int state = mWifiController.getConnectionState();

            switch (state) {
                case STATE_CONNECTED:
                    text.setText(R.string.wifi_text_wifi_connected);
                    operating.setEnabled(true);
                    operating.setText(R.string.wifi_text_disconnect);
                    break;
                case STATE_CONNECTING:
                    text.setText(R.string.wifi_text_state_connecting);
                    //   operating.setText("连接中");
                    operating.setText(R.string.wifi_text_disconnect);
                    operating.setEnabled(false);
                    break;
                case STATE_OBTAINING_IPADDR:
                    text.setText(R.string.wifi_text_obtain_an_IP_address);
                    // operating.setText("连接中");
                    operating.setEnabled(false);
                    break;
                case STATE_AUTHENTICATING:
                    text.setText(R.string.wifi_text_authentication);

                    break;
                case STATE_FAILED:
                    operating.setEnabled(true);
                   /* text.setText(R.string.wifi_text_state_failed);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_wrong_password, Toast.LENGTH_SHORT);
                    // toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();*/
                    ToastUtil.ShowToast(mContext,mContext. getString(R.string.wifi_text_state_failed));
                    if (mdelete != null) {
                        mdelete.delete(mScanResult.SSID);
                    }
                 /*   final WifiConfiguration config = mWifiController.getConfig(mScanResult.SSID);
                    if (null != config) {
                        mWifiController.removeConfig(config);
                    }*/
                    break;
                case STATE_DISCONNECTING:
                    text.setText(R.string.wifi_text_state_disconnecting);
                    /*wifiPassword.setText("");*/

                    //    operating.setText("断开中");
                    operating.setEnabled(false);
                    break;
                case STATE_DISCONNECTED:
                    text.setText(R.string.wifi_text_disconnected);
                    operating.setEnabled(true);
                    break;
                default:

                    break;
            }

        }


    }

    public interface delete {
        void delete(String s);
    }

    static class ViewHolder {
        TextView pointSSID;
        TextView connectStateTxt;
        ImageView pointSignal;
        TextView operatingTxt;
        LinearLayout wifiLlayout;
    }

}
