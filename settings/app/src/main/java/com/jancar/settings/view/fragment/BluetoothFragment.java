package com.jancar.settings.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jancar.settings.util.DelayedUtils;
import com.jancar.settings.util.ToastUtil;
import com.jancar.settings.widget.AVLoadingIndicatorView;
import com.jancar.settings.widget.SettingDialog;
import com.jancar.settings.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

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
    TextView tvClose;
    TextView tvCheckSw;
    TextView tvBtName;
    TextView tvPaired;
    TextView tvDelAll;
    ListView listAvailable;                 //可用设备列表
    TextView tvSearch;
    LinearLayout linearBlue;
    //  private AnimationDrawable animationDrawable;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.w("BluetoothFragment", "onAttach");
        this.mActivity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w("BluetoothFragment", "onResume");
        tvBlutName = bluetoothManager.getBTName().trim();
        tvBtName.setText(tvBlutName);
        Log.w(TAG, "onResumeName:" + tvBlutName);
        bluetoothManager.getBondDevice();

    }

    @Override
    public void onPause() {
        super.onPause();
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
            linearSearch = (LinearLayout) view.findViewById(R.id.linear_search);
            ivSearch = (AVLoadingIndicatorView) view.findViewById(R.id.iv_setting_search);
            listView = (ListView) view.findViewById(R.id.setting_recyclerview);
            listAvailable = (ListView) view.findViewById(R.id.setting_available);
            tvClose = (TextView) view.findViewById(R.id.tv_blutooth_close);
            tvCheckSw = (TextView) view.findViewById(R.id.txt_inspection_prompt);
            tvBtName = (TextView) view.findViewById(R.id.tv_setting_edit_name);
            tvPaired = (TextView) view.findViewById(R.id.tv_paired);
            tvDelAll = (TextView) view.findViewById(R.id.tv_del_all);
            tvSearch = (TextView) view.findViewById(R.id.tv_setting_search);
            linearBlue = view.findViewById(R.id.liner_open_blue);
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
        //  bluetoothManager.
        isDisCovering = bluetoothManager.getBTIsDisCovering();

     /*   SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("Bluetooth", true);
        if (first_run) {
            isBTon=true;
            sharedPreferences.edit().putBoolean("Bluetooth", false).commit();
            linearBlue.setVisibility(View.VISIBLE);
            //linearSearch.setVisibility(View.VISIBLE);
            tvClose.setText(R.string.tv_open);
            mPresenter.openBluetooth();
            mPresenter.searchPairedList();
            //   Toast.makeText(this, "第一次", Toast.LENGTH_LONG).show();
        }*/
        //animationDrawable = (AnimationDrawable) ivSearch.getDrawable();
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
                        Log.w(TAG, "listViewonItem");
                        String remote_device_macaddr = pairedDataListList.get(i).getRemote_device_macaddr();
                        int status = pairedDataListList.get(i).getRemote_connect_status();
                        if (status == Constants.BLUETOOTH_DEVICE_BONDED) {
                            mPresenter.connBlutoth(remote_device_macaddr);
                        }
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
                    }
                }
            });
        }
        ivOnSw.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivOnSw.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivOnSw.setCheckedImmediately(isBTon);
        if (isBTon) {
            tvClose.setText(R.string.tv_open);
            mPresenter.searchPairedList();
            linearBlue.setVisibility(View.VISIBLE);
        } else {
            tvClose.setText(R.string.tv_closed);
            linearBlue.setVisibility(View.GONE);

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
                if (!DelayedUtils.isFastDoubleClick()) {
                    Log.w(TAG, "iv_setting_switch");
                    isBTon = !isBTon;
                    if (isBTon) {
                        linearBlue.setVisibility(View.VISIBLE);
                        tvClose.setText(R.string.tv_open);
                        mPresenter.openBluetooth();
                        mPresenter.searchPairedList();
                    } else {
                        linearBlue.setVisibility(View.GONE);
                        linearSearch.setVisibility(View.GONE);
                        tvClose.setText(R.string.tv_closed);
                        mPresenter.closeBluetooth();
                    }
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
                mPresenter.searchPairedList();
               /* Handler     handler = new Handler(new Handler.Callback(){
                    @Override
                    public boolean handleMessage(Message msg) {

                        if(msg.what == 1 ){
                        }
                        return true;
                    }
                });

                //五秒后发送
                handler.sendEmptyMessageDelayed(1,2000);*/
                break;

        }
    }


    private void showDialog() {
        if (settingDialog == null) {
            settingDialog = new SettingDialog(getActivity(), R.style.AlertDialogCustom);
        }
        String s = bluetoothManager.getBTName().trim();
        Log.w(TAG, "s:" + s);
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
                Log.w(TAG, "set1111:" + editText);
                if (TextUtils.isEmpty(editText)) {
                    ToastUtil.ShowToast(mActivity, getString(R.string.tv_setting_update_name));
                } else {
                    settingDialog.dismiss();
                    bluetoothManager.setBTName(editText);
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
//        handler.removeMessages(BT_SETTING_PAIR);
//        handler.sendEmptyMessageDelayed(BT_SETTING_PAIR, 100);
    }

    @Override
    public void onNotifyOnUpdateUIUnpairedList(final List<BluetoothDeviceData> list) {
        Log.w(TAG, "UNlist.size():" + list.size());
        this.unPairedDataListList = new ArrayList<>(list);
        handler.sendEmptyMessage(BT_SETTING_UNPAIR);
//        handler.removeMessages(BT_SETTING_UNPAIR);
//        handler.sendEmptyMessageDelayed(BT_SETTING_UNPAIR, 100);
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
                case BT_SETTING_PAIR:
                    pairAdapter.setBookContact(pairedDataListList);
                    pairAdapter.changetShowDelImage(true);
                    break;
                case BT_SETTING_UNPAIR:
                    avaAdapter.setBookContact(unPairedDataListList);
                    avaAdapter.changetShowDelImage(false);
                    break;
                case BT_SWITH_STATE_ON:
//                    isBTon = true;
                    linearBlue.setVisibility(View.VISIBLE);
                    mPresenter.searchPairedList();
                    break;
                case BT_SWITH_STATE_OFF:
//                    isBTon = false;
                    pairedDataListList.clear();
                    unPairedDataListList.clear();
                    pairAdapter.notifyDataSetChanged();
                    avaAdapter.notifyDataSetChanged();
                    linearBlue.setVisibility(View.GONE);
                    break;
                case BT_DEVICE_START:
                    tvSearch.setVisibility(View.GONE);
                    linearSearch.setVisibility(View.VISIBLE);
                    //animationDrawable.start();
                    ivSearch.show();
                    break;
                case BT_DEVICE_END:
                    //animationDrawable.stop();
                    ivSearch.hide();
                    linearSearch.setVisibility(View.GONE);
                    tvSearch.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
}
