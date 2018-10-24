package com.jancar.settings.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTSettingListener;
import com.jancar.bluetooth.lib.BluetoothDeviceData;
import com.jancar.bluetooth.lib.BluetoothSettingManager;
import com.jancar.settings.R;
import com.jancar.settings.adapter.BluetoothAdapter;
import com.jancar.settings.listener.Contract.BluetoothContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.BluetoothPresenter;
import com.jancar.settings.util.Constants;
import com.jancar.settings.util.ToastUtil;
import com.jancar.settings.widget.AVLoadingIndicatorView;
import com.jancar.settings.widget.SettingDialog;
import com.jancar.settings.widget.SwitchButton;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouyan on 2018/9/17.
 */

public class BluetoothFragment extends BaseFragments<BluetoothPresenter> implements BluetoothContractImpl.View, BTSettingListener, View.OnClickListener {

    private static final String TAG = "BluetoothFragment";
    private Activity mActivity;
    private View view;
    // Unbinder unbinder;
    SwitchButton ivOnSw;
    SwitchButton ivCheckSw;
    LinearLayout linearSearch;
    AVLoadingIndicatorView ivSearch;
    ListView listView;                      //配对设备列表
    ListView listAvailable;                 //可用设备列表

    TextView tvClose;
    TextView tvCheckSw;
    TextView tvBtName;
    TextView tvPaired;
    TextView tvDelAll;

    TextView tvSearch;
    LinearLayout linearBlue;
    BluetoothAdapter pairAdapter, avaAdapter;
    SettingDialog settingDialog;
    private List<BluetoothDeviceData> pairedDataListList;
    private List<BluetoothDeviceData> unPairedDataListList;
    private boolean isDisCovering;
    private boolean isBTon;
    private String tvBlutName;
    BluetoothSettingManager bluetoothManager;
    private final static int BT_SETTING_PAIR = 1;
    private final static int BT_SETTING_UNPAIR = 2;
    private final static int BT_SWITH_STATE_ON = 3;
    private final static int BT_SWITH_STATE_OFF = 4;
    private final static int BT_DEVICE_START = 5;
    private final static int BT_DEVICE_END = 6;
    private final static int BT_SWITCH = 7;
    private boolean isClick = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.w("BluetoothFragment", "onAttach");
        this.mActivity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume===");
        tvBlutName = bluetoothManager.getBTName().trim();
        Log.e(TAG, "tvBlutName====" + tvBlutName);
        String s = null;
        try {
            s = URLDecoder.decode(tvBlutName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "tvssss====" + s);
        tvBtName.setText(s);
        avaAdapter.notifyDataSetChanged();
        pairAdapter.notifyDataSetChanged();
        bluetoothManager.getBondDevice();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w(TAG, "onPause==");
        bluetoothManager.unRegisterBTSettingListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("BluetoothFragment", "onDestroy");
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, null);
        initView(savedInstanceState);
        initData(savedInstanceState);
        return view;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            ivOnSw = (SwitchButton) view.findViewById(R.id.iv_setting_switch);
            ivCheckSw = (SwitchButton) view.findViewById(R.id.iv_setting_check_switch);
            linearSearch = (LinearLayout) view.findViewById(R.id.linear_search);//搜索动画
            ivSearch = (AVLoadingIndicatorView) view.findViewById(R.id.iv_setting_search);//动画view
            listView = (ListView) view.findViewById(R.id.setting_recyclerview);
            listAvailable = (ListView) view.findViewById(R.id.setting_available);
            tvClose = (TextView) view.findViewById(R.id.tv_blutooth_close);//蓝牙状态
            tvCheckSw = (TextView) view.findViewById(R.id.txt_inspection_prompt);//配对设备检测
            tvBtName = (TextView) view.findViewById(R.id.tv_setting_edit_name);//蓝牙名称
            tvPaired = (TextView) view.findViewById(R.id.tv_paired);
            tvDelAll = (TextView) view.findViewById(R.id.tv_del_all);
            tvSearch = (TextView) view.findViewById(R.id.tv_setting_search);//开始搜索提示
            linearBlue = view.findViewById(R.id.liner_open_blue);//蓝牙数据view
            ivOnSw.setOnClickListener(this);
            ivCheckSw.setOnClickListener(this);
            tvDelAll.setOnClickListener(this);
            tvBtName.setOnClickListener(this);
            tvSearch.setOnClickListener(this);
        }
    }

    @Override
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new BluetoothPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Log.w("BluetoothFragment", "initData");
        bluetoothManager = BluetoothSettingManager.getBluetoothSettingManager(mActivity);
        bluetoothManager.registerBTSettingListener(this);
        isBTon = bluetoothManager.isBTOn();
        isDisCovering = bluetoothManager.getBTIsDisCovering();
        //配对设备
        if (pairAdapter == null) {
            if (pairedDataListList == null) {
                pairedDataListList = new ArrayList<>();

            }
            pairAdapter = new BluetoothAdapter(getContext());
            listView.setAdapter(pairAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (pairedDataListList.size() > 0 && pairedDataListList != null) {
                        Log.w(TAG, "listViewonItem===");
                        String remote_device_macaddr = pairedDataListList.get(i).getRemote_device_macaddr();
                        int status = pairedDataListList.get(i).getRemote_connect_status();
                        if (status == Constants.BLUETOOTH_DEVICE_BONDED) {
                            mPresenter.connBlutoth(remote_device_macaddr);
                        } else {
                            bluetoothManager.disConnectDevice(remote_device_macaddr);
                        }
                        pairAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        //可用设备
        if (avaAdapter == null) {
            if (unPairedDataListList == null) {
                unPairedDataListList = new ArrayList<>();
            }
            avaAdapter = new BluetoothAdapter(getContext());
            listAvailable.setAdapter(avaAdapter);
            listAvailable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.w(TAG, "listAvailableonItem");
                    if (unPairedDataListList.size() > 0 && unPairedDataListList != null) {
                        if (i < unPairedDataListList.size()) {
                            String remote_device_macaddr = unPairedDataListList.get(i).getRemote_device_macaddr();
                            mPresenter.connBlutoth(remote_device_macaddr);
                        }
                        avaAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        ivOnSw.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivOnSw.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivOnSw.setCheckedImmediately(isBTon);
        if (isBTon) {
            BTonView();
        } else {
            BToffView();
        }
        if (isDisCovering) {
            tvCheckSw.setText(R.string.tv_setting_check_);
        } else {
            tvCheckSw.setText(R.string.tv_setting_check_des);

        }
        ivCheckSw.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivCheckSw.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivCheckSw.setCheckedImmediately(isDisCovering);
    }

    private void BTonView() {
        tvClose.setText(R.string.tv_open);
        linearBlue.setVisibility(View.VISIBLE);
        mPresenter.searchPairedList();
    }

    private void BToffView() {
        tvClose.setText(R.string.tv_closed);
        linearBlue.setVisibility(View.GONE);
        linearSearch.setVisibility(View.GONE);
        tvSearch.setVisibility(View.VISIBLE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting_check_switch:
                isDisCovering = !isDisCovering;
                if (isDisCovering) {
                    tvCheckSw.setText(R.string.tv_setting_check_);
                } else {
                    tvCheckSw.setText(R.string.tv_setting_check_des);
                }
                bluetoothManager.setBTIsDisCovering(isDisCovering);
                break;
            case R.id.iv_setting_switch:
                if (isClick) {
                    isBTon = !isBTon;
                    if (isBTon) {
                        mPresenter.openBluetooth();
                    } else {
                        mPresenter.closeBluetooth();
                    }
                    isClick = false;
                    ivOnSw.setEnabled(false);
                    handler.sendEmptyMessageDelayed(BT_SWITCH, 3000);
                }

                break;
            case R.id.tv_del_all:
                //断开连接
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pairedDataListList.size() > 0 && pairedDataListList != null) {
                            for (BluetoothDeviceData deviceData : pairedDataListList) {
                                BluetoothSettingManager.getBluetoothSettingManager(getContext()).removeDevice(deviceData.getRemote_device_macaddr());
                            }
                        }
                    }
                }, 100);

                break;
            case R.id.tv_setting_edit_name:
                showDialog();
                break;
            case R.id.tv_setting_search:
                if (mPresenter.isBTOn()) {
                    mPresenter.searchPairedList();
                }
                break;

        }
    }


    private void showDialog() {
        if (settingDialog == null) {
            settingDialog = new SettingDialog(getActivity(), R.style.AlertDialogCustom);
        }
        String s = bluetoothManager.getBTName().trim();
        settingDialog.setEditText(s);
        settingDialog.setCanelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog.dismiss();
            }
        });
        settingDialog.setEditOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editText = settingDialog.getEditText().trim();
                if (TextUtils.isEmpty(editText)) {
                    ToastUtil.ShowToast(mActivity, getString(R.string.tv_setting_update_name));
                } else {
                    settingDialog.dismiss();
                    try {
                        String decode = URLDecoder.decode(editText, "UTF-8");
                        bluetoothManager.setBTName(decode);
                        Log.e(TAG, "decode===" + decode);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    tvBtName.setText(editText);
                }
            }
        });
        settingDialog.show();
    }

    @Override
    public void onNotifyBTSwitchStateOn() {
        handler.sendEmptyMessage(BT_SWITH_STATE_ON);
    }

    @Override
    public void onNotifyBTSwitchStateOff() {
        handler.sendEmptyMessage(BT_SWITH_STATE_OFF);

    }

    @Override
    public void onNotifyOnUpdateUIPairedList(final List<BluetoothDeviceData> list) {
        Log.w(TAG, "UIlist.size():" + list.size());
        this.pairedDataListList = new ArrayList<>(list);
        handler.sendEmptyMessage(BT_SETTING_PAIR);
    }

    @Override
    public void onNotifyOnUpdateUIUnpairedList(final List<BluetoothDeviceData> list) {
        Log.w(TAG, "UNlist.size():" + list.size());
        this.unPairedDataListList = new ArrayList<>(list);
        handler.sendEmptyMessage(BT_SETTING_UNPAIR);
    }


    @Override
    public void onNotifyScanDeviceStart() {
        handler.sendEmptyMessage(BT_DEVICE_START);

    }

    @Override
    public void onNotifyScanDeviceEnd() {
        handler.sendEmptyMessage(BT_DEVICE_END);

    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public BluetoothSettingManager getBluetManger() {
        return bluetoothManager;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BT_SETTING_PAIR://配对设备
                    pairAdapter.setBookContact(pairedDataListList);
                    pairAdapter.changetShowDelImage(true);
                    break;
                case BT_SETTING_UNPAIR://可用设备
                    avaAdapter.setBookContact(unPairedDataListList);
                    avaAdapter.changetShowDelImage(false);
                    break;
                case BT_SWITH_STATE_ON://蓝牙开启
                    Log.w(TAG, "BT_SWITH_STATE_ON");
                    ivOnSw.setCheckedImmediately(isBTon);
                    tvClose.setText(R.string.tv_open);
                    tvSearch.setVisibility(View.GONE);
                    linearBlue.setVisibility(View.VISIBLE);
                    ivSearch.show();
                    mPresenter.searchPairedList();
                    break;
                case BT_SWITH_STATE_OFF://蓝牙关闭
                    ivOnSw.setCheckedImmediately(isBTon);
                    Log.w(TAG, "BT_SWITH_STATE_OFF");
                    tvClose.setText(R.string.tv_closed);
                    linearBlue.setVisibility(View.GONE);
                    ivSearch.hide();
                    tvSearch.setVisibility(View.VISIBLE);
                    break;
                case BT_DEVICE_START://开始搜索
                    Log.w(TAG, "BT_DEVICE_START");
                    tvSearch.setVisibility(View.GONE);
                    linearSearch.setVisibility(View.VISIBLE);
                    ivSearch.show();
                    break;
                case BT_DEVICE_END://结束搜索
                    Log.w(TAG, "BT_DEVICE_END");
                    linearSearch.setVisibility(View.GONE);
                    tvSearch.setVisibility(View.VISIBLE);
                    ivSearch.hide();
                    break;
                case BT_SWITCH:
                    isClick = true;
                    ivOnSw.setEnabled(true);
                    break;
            }
        }
    };
}
