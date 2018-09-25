package com.jancar.settings.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.internal.telephony.DctConstants;
import com.jancar.settings.R;
import com.jancar.settings.listener.Contract.WifiContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.listener.Contract.WifiSpotContractImpl;
import com.jancar.settings.manager.BaseFragment;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.WifiPresenter;
import com.jancar.settings.presenter.WifiSpotPresenter;
import com.jancar.settings.util.wifi.WifiController;
import com.jancar.settings.util.wifi.WifiEnabler;
import com.jancar.settings.util.wifi.WifiListAdapter;
import com.jancar.settings.util.wifi.WifiSavedListAdapter;
import com.jancar.settings.widget.SwitchButton;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.jancar.settings.util.Tool.setDialogParam;

/**
 * Created by ouyan on 2018/8/30.
 */

public class WifiFragment extends BaseFragments<WifiPresenter> implements WifiContractImpl.View, WifiListAdapter.OnClickListener, View.OnClickListener {
    private final String TAG = "WifiSettings";
    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;
    private AnimationDrawable animationDrawable;
    private final int ERROR = -1;
    private final int LIST_HEADER = 0;

    //utils class
    private Context mContext;
    private WifiEnabler mWifiEnabler;
    private WifiController mWifiController;

    //thread
    private HandlerThread mBgThread;

    //UI
    private SwitchButton mSwitch;
    TextView scanTxt;
    private LinearLayout llayoutFragmentPrompt;
    private LinearLayout llayoutFragmentLoading;
    private LinearLayout llayoutFragmentScanning;
    private ListView mScanList;

    private TextView wifiSummary;
    private ImageView mLoading;
    private LayoutInflater mInflater;
    private AlertDialog.Builder mBuilder;

    private WifiListAdapter mScanAdapter;
    //    private WifiListAdapter mSavedAdapter;
    private WifiSavedListAdapter mSavedAdapter;
    private  WifiReceiver mReceiver;
    List<ScanResult> mScanResults = new ArrayList<ScanResult>();
    List<ScanResult> mSavedResultsShow = new ArrayList<ScanResult>();
    private View view;
    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            Log.d(TAG, "onReceive:" + action);
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "onReceive: WIFI_STATE_CHANGED_ACTION");
                mSwitch.setEnabled(false);
                scanTxt.setTextColor(Color.parseColor("#484949"));
                handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));

            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                // wifi已成功扫描到可用wifi。
                Log.d(TAG, "onReceive: SCAN_RESULTS_AVAILABLE_ACTION");
                scanTxt.setEnabled(true);
                scanTxt.setTextColor(Color.parseColor("#ffffff"));
                loadingStopRefresh();
                showListView();
                handleNetWorkStateChanged();
                handleWifiScanResult();
            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "qyp onReceive: RSSI_CHANGED_ACTION");
                int updateRSSI = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -200);
                if (null != mSavedResultsShow && null != mScanAdapter) {
                    if (mWifiController.STATE_CONNECTED == mWifiController.getConnectionState()) {
                        if (mSavedResultsShow.size() > 0) {
                            //  mSavedResultsShow.


                            mScanResults.addAll(0, mSavedResultsShow);
                            mScanResults = removeDuplicate(mScanResults);
                            mScanAdapter.setmSavedResultsShow(mSavedResultsShow);
                            Log.d(TAG, "update Rssi: " + updateRSSI);
                            mScanAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                //检测到wifi连接状态的变化
                Log.d(TAG, "qyp onReceive: NETWORK_STATE_CHANGED_ACTION");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                Log.d(TAG, "qyp onReceive: NETWORK_STATE_CHANGED_ACTION NetworkInfo:" + info.getState());

                if (NetworkInfo.State.DISCONNECTED.equals(info.getState())) {
                    Log.d(TAG, "qyp onReceive: NETWORK_STATE_CHANGED_ACTION" + info.getState());
                    mWifiController.setConnectionState(mWifiController.STATE_DISCONNECTED);
                 /*   if (mScanResults.size() != mSavedResultsShow.size()) {
                        loadingStopRefresh();
                    }*/

                } else if (NetworkInfo.State.DISCONNECTING.equals(info.getState())) {
                    mWifiController.setConnectionState(mWifiController.STATE_DISCONNECTING);
                } else if (NetworkInfo.State.CONNECTED.equals(info.getState())) {
                    mWifiController.setConnectionState(mWifiController.STATE_CONNECTED);


                } else {
                    Log.d(TAG, "qyp onReceive: NETWORK_STATE_CHANGED_ACTION" + info.getDetailedState());
                    NetworkInfo.DetailedState state = info.getDetailedState();
                    if (state == state.CONNECTING) {
                        Log.d(TAG, "qyp onReceive: NETWORK_STATE_CHANGED_ACTION" + info.getDetailedState());
                        mWifiController.setConnectionState(mWifiController.STATE_CONNECTING);
                    } else if (state == state.AUTHENTICATING) {
                        mWifiController.setConnectionState(mWifiController.STATE_AUTHENTICATING);
                    } else if (state == state.OBTAINING_IPADDR) {
                        mWifiController.setConnectionState(mWifiController.STATE_OBTAINING_IPADDR);
                    } else if (state == state.FAILED) {
                        mWifiController.setConnectionState(mWifiController.STATE_FAILED);
                    }
                }
                handleNetWorkStateChanged();
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "onReceive: SUPPLICANT_STATE_CHANGED_ACTION");
                SupplicantState wifiSupplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                Log.d(TAG, "onReceive: SUPPLICANT_STATE_CHANGED_ACTION:" + wifiSupplicantState);
                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                Log.d(TAG, "onReceive: SUPPLICANT_STATE_CHANGED_ACTION:" + error);
                if (WifiManager.ERROR_AUTHENTICATING == error) {
                    mWifiController.setConnectionState(mWifiController.STATE_FAILED);
                    mSavedAdapter.notifyDataSetChanged();
                }
            }

        }
    }
   /* private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

    };*/

    private void handleNetWorkStateChanged() {
        Log.d(TAG, "handleNetWorkStateChanged");
        if (WifiManager.WIFI_STATE_ENABLED != mWifiController.getWifiState()) {
            Log.d(TAG, "wifi is off  return");
            return;
        }
        mSavedResultsShow = mWifiController.getSavedList();
        if (null != mSavedResultsShow && null != mScanAdapter) {
            Log.d(TAG, "handleWifiScanResult: " + mSavedResultsShow);
            mScanAdapter.setmSavedResultsShow(mSavedResultsShow);
            mScanResults.addAll(0, mSavedResultsShow);
            mScanResults = removeDuplicate(mScanResults);
            mScanAdapter.notifyDataSetChanged();
        }
    }

    public static List removeDuplicate(List<ScanResult> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).SSID.equals(list.get(i).SSID)) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    private void handleWifiScanResult() {
        Log.d(TAG, "handleWifiScanResult");
        if (WifiManager.WIFI_STATE_ENABLED != mWifiController.getWifiState()) {
            Log.d(TAG, "wifi is off  return");
            return;
        }

        mScanResults = mWifiController.getWifiList();

        mScanAdapter.setmSavedResultsShow(mSavedResultsShow);
        mScanResults.addAll(0, mSavedResultsShow);
        mScanResults = removeDuplicate(mScanResults);
        Log.d(TAG, "mScanResults:" + mScanResults + ", size:" + mScanResults.size());
        if (null != mScanResults && null != mScanAdapter) {
            mScanAdapter.setData(mScanResults);
            mScanAdapter.notifyDataSetChanged();
        }
        if (null != mSavedResultsShow && null != mSavedAdapter) {
            Log.d(TAG, "handleWifiScanResult: " + mSavedResultsShow);
            mSavedAdapter.setData(mSavedResultsShow);
            mSavedAdapter.notifyDataSetChanged();

        }
    }

    private void handleWifiStateChanged(int state) {
        Log.d(TAG, "handleWifiStateChanged, state = " + state);
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                scanTxt.setEnabled(false);
                mSwitch.setEnabled(false);
                scanTxt.setTextColor(Color.parseColor("#484949"));
                showListView();
                loadingStartRefresh();
                break;
            case WifiManager.WIFI_STATE_ENABLED://已打开
                mSwitch.setEnabled(true);
                long end_time = System.currentTimeMillis();
                Log.d(TAG, "calculate enable time [end] [WifiEnable]: " + Long.toString(end_time));
                //just a test
                if (null == mScanAdapter) {
                    initListView();
                } else {
                    showListView();
                    loadingStartRefresh();
                    scanTxt.setEnabled(false);
                    scanTxt.setTextColor(Color.parseColor("#484949"));
                    //   loadingStartRefresh();
                }

                wifiSummary.setText(R.string.label_adjust_open);

                break;
            case WifiManager.WIFI_STATE_DISABLING:

                mSwitch.setEnabled(false);
                scanTxt.setEnabled(false);
                scanTxt.setTextColor(Color.parseColor("#484949"));
                break;
            case WifiManager.WIFI_STATE_DISABLED://已关闭

                hideListView();
                wifiSummary.setText(R.string.label_adjust_off);
                mSwitch.setEnabled(true);
                scanTxt.setEnabled(true);
                scanTxt.setTextColor(Color.parseColor("#ffffff"));
                break;
            default:
                mSwitch.setEnabled(true);
                scanTxt.setEnabled(true);
                scanTxt.setTextColor(Color.parseColor("#ffffff"));
        }
    }


    /**
     * when we receive the wifi is enabled, we should call this method
     */
    private void initListView() {
        Log.d(TAG, "initListView()");

        showListView();
        //scan_result
        mLoading = (ImageView) view.findViewById(R.id.wifi_refresh_image);
        mScanAdapter = new WifiListAdapter(this.getContext(), mScanResults);
        mScanAdapter.setOnClickListener(this);
        mScanList.setAdapter(mScanAdapter);
        //  mScanList.setOnItemClickListener(mScanListener);

        //saved_result
        mSavedAdapter = new WifiSavedListAdapter(this.getContext(), mSavedResultsShow);


        //show refresh loading
        loadingStartRefresh();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:" + this);
        super.onDestroy();
        mBgThread.quit();
        mWifiController.setInstance();

        getActivity().unregisterReceiver(mReceiver);
    }

    private void showScanResultNopassDialog(String ssid, int type) {
        Log.d(TAG, "showScanResultDialog()");
        final int finalType = type;
        final String finalSSID = ssid;
        mWifiController.disConnect();
        WifiConfiguration config = mWifiController.createWifiConfig(finalSSID, null, finalType);
        mWifiController.connect(config);
        //when we change the click the item, we need update state
        mWifiController.setConnectionState(mWifiController.STATE_CONNECTING);
        handleWifiScanResult();
    }

    private void showScanResultDialog(String ssid, int type) {
        Log.d(TAG, "showScanResultDialog()");
        final int finalType = type;
        final String finalSSID = ssid;
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.wifi_dialog_scan_result);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
      /*  View v = mInflater.inflate(R.layout.wifi_dialog_scan_result, null);
        mBuilder.setView(v);*/

        // final AlertDialog dialog = mBuilder.create();

        TextView wifiName = (TextView) dialog.findViewById(R.id.wifi_name_dialog);
        wifiName.setText(finalSSID);
        final EditText wifiPassword = (EditText) dialog.findViewById(R.id.wifi_password);
        wifiPassword.setTypeface(Typeface.DEFAULT);
        Button connect = (Button) dialog.findViewById(R.id.wifi_connect_btn_scan);
        Button cancel = (Button) dialog.findViewById(R.id.wifi_cancel_btn_scan);
        CheckBox isShowPassword = (CheckBox) dialog.findViewById(R.id.show_password);

 /*       isShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    wifiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    wifiPassword.setTypeface(Typeface.DEFAULT);
                } else {
                    wifiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    wifiPassword.setTypeface(Typeface.DEFAULT);
                }
            }
        });*/
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.wifi_connect_btn_scan:
                        mWifiController.disConnect();
                        String password = wifiPassword.getText().toString();
                        WifiConfiguration config = mWifiController.createWifiConfig(finalSSID, password, finalType);
                        mWifiController.connect(config);
                        //when we change the click the item, we need update state
                        mWifiController.setConnectionState(mWifiController.STATE_CONNECTING);
                        handleWifiScanResult();
                        dialog.dismiss();

                        break;
                    case R.id.wifi_cancel_btn_scan:
                        dialog.dismiss();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        };
        connect.setOnClickListener(buttonListener);
        cancel.setOnClickListener(buttonListener);

        dialog.show();
        setDialogParam(dialog, 500, 316);

    }

    /**
     * to show the list when wifi is on
     */
    private void showListView() {

        Log.d(TAG, "showListView()");
        llayoutFragmentPrompt.setVisibility(View.GONE);
        //  llayoutFragmentScanning.setVisibility(View.VISIBLE);
//        loadingStartRefresh();
    }

    private void hideListView() {
        Log.d(TAG, "hideListView()");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llayoutFragmentScanning.setVisibility(View.GONE);
                llayoutFragmentPrompt.setVisibility(View.VISIBLE);
                llayoutFragmentLoading.setVisibility(View.GONE);
                mLoading.setVisibility(View.GONE);
                animationDrawable.stop();
            }
        });

    }

    /**
     * 开始刷新动画
     */
    public void loadingStartRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llayoutFragmentLoading.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.VISIBLE);
                animationDrawable.start();
            }
        });

    }

    /**
     * 停止刷新动画
     */
    public void loadingStopRefresh() {
        llayoutFragmentLoading.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        llayoutFragmentScanning.setVisibility(View.VISIBLE);

    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            mSwitch = (SwitchButton) view.findViewById(R.id.switchB);
            scanTxt = (TextView) view.findViewById(R.id.txt_scan);
            wifiSummary = (TextView) view.findViewById(R.id.txt_wifi_summary);
            mLoading = (ImageView) view.findViewById(R.id.wifi_refresh_image);
            mScanList = (ListView) view.findViewById(R.id.scan_list);
            llayoutFragmentLoading = (LinearLayout) view.findViewById(R.id.fragment_loading);
            llayoutFragmentPrompt = (LinearLayout) view.findViewById(R.id.fragment_prompt);
            llayoutFragmentScanning = (LinearLayout) view.findViewById(R.id.fragment_scanning);
            mInflater = LayoutInflater.from(this.getContext());
            mBuilder = new AlertDialog.Builder(this.getContext());
            scanTxt.setOnClickListener(this);
        }
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wifi_fragment, null);
        initView(savedInstanceState);
        initData(savedInstanceState);

        return view;
    }

    private void registerWifiReceiver() {
        Log.d(TAG, "registerWifiReceiver()");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mReceiver = new WifiReceiver();
        getActivity().registerReceiver(mReceiver, filter);
    }

    private Switch.OnCheckedChangeListener mSwitchListener = new Switch.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            Log.d(TAG, "onCheckedChanged()");
            mWifiEnabler.onSwitchChanged(mSwitch, isChecked);
        }
    };

    @Override
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new WifiPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mLoading.setImageResource(R.drawable.loading_animation);
        animationDrawable = (AnimationDrawable) mLoading.getDrawable();
        //handleWifiStateChangeds(getActivity().getIntent().getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
        registerWifiReceiver();
        mWifiController = WifiController.getInstance(this.getContext());
        mWifiEnabler = new WifiEnabler(this.getContext(), mSwitch);
        mSwitch.setOnCheckedChangeListener(mSwitchListener);
        mSwitch.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        mSwitch.setBackDrawableRes(R.drawable.switch_custom_track_selector);
   /*     mSwitch.setCheckedImmediately(b);*/
        mBgThread = new HandlerThread(TAG);
        mBgThread.start();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void launchActivity(@NonNull Intent intent) {

    }

    @Override
    public void killMyself() {

    }

    @Override
    public void onClick(View v, int Type, int position) {
        final ScanResult scanResult = mScanAdapter.getItem(position);
        int type = mPresenter.getWifiType(scanResult);
        String configSSID = mWifiController.getConnectedSSID();
        switch (Type) {
            case 0:
                if (WIFICIPHER_NOPASS != type) {//需要密码，则dialog获取密码；
                    showScanResultDialog(scanResult.SSID, type);
                } else {//不需要密码则直接连接
                    showScanResultNopassDialog(scanResult.SSID, type);
                }
                break;
            case 1:
                showConnectedResultDialog(scanResult.SSID, type);
                break;
            case 2:
                showSavedResultDialog(scanResult.SSID, type);
                break;
        }
    }

    private void showSavedResultDialog(String ssid, int type) {

        Log.d(TAG, "showSavedResultDialog()");
        final int finalType = type;
        final String finalSSID = ssid;
        final WifiConfiguration config = mWifiController.getConfig(finalSSID);
        mWifiController.disConnect();
        mWifiController.connect(config);
        //when we change the click the item, we need update state
        mWifiController.setConnectionState(mWifiController.STATE_CONNECTING);
        Log.d(TAG, "config" + config);
        handleNetWorkStateChanged();
        loadingStartRefresh();
    }

    private void showConnectedResultDialog(String ssid, int type) {

        Log.d(TAG, "showConnectedResultDialog");
        final int finalType = type;
        final String finalSSID = ssid;
        final WifiConfiguration config = mWifiController.getConfig(finalSSID);
        mWifiController.disConnectionWifi(config.networkId);
        //when we change the click the item, we need update state
        mWifiController.setConnectionState(mWifiController.STATE_CONNECTING);
        handleNetWorkStateChanged();
        loadingStartRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_scan:
                mScanResults.clear();
                mScanAdapter.notifyDataSetInvalidated();
                Log.d(TAG, "click scan");
                scanTxt.setEnabled(false);
                scanTxt.setTextColor(Color.parseColor("#484949"));
                mWifiEnabler.getScanHandler().sendEmptyMessage(WifiEnabler.MSG_START_SCAN);
                loadingStartRefresh();
                break;
            default:
                break;
        }
    }
}
