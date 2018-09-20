package com.jancar.settings.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.settings.R;
import com.jancar.settings.adapter.BluetoothAdapter;
import com.jancar.settings.listener.Contract.BluetoothContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.BluetoothPresenter;
import com.jancar.settings.util.Constants;
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
    ImageView ivSearch;
    ListView listView;                      //配对设备列表
    TextView tvClose;
    TextView tvCheckSw;
    TextView tvBtName;
    TextView tvPaired;
    TextView tvDelAll;
    ListView listAvailable;                 //可用设备列表
    TextView tvSearch;
    private AnimationDrawable animationDrawable;
    BluetoothAdapter pairAdapter, avaAdapter;
    SettingDialog settingDialog;
    private List<BluetoothDeviceData> pairedDataListList;
    private List<BluetoothDeviceData> unPairedDataListList;
    private boolean isDisCovering;
    private boolean isBTon;
    private String tvBlutName;
    private boolean hidden = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            getBluetManger().registerBTSettingListener(this);
            tvBlutName = mPresenter.getBlutoothName();
            tvBtName.setText(tvBlutName);
            getBluetManger().getBondDevice();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            getBluetManger().registerBTSettingListener(this);
            tvBlutName = mPresenter.getBlutoothName();
            tvBtName.setText(tvBlutName);
            getBluetManger().getBondDevice();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //    unbinder.unbind();
        getBluetManger().unRegisterBTSettingListener(this);
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
            ivSearch = (ImageView) view.findViewById(R.id.iv_setting_search);
            listView = (ListView) view.findViewById(R.id.setting_recyclerview);
            listAvailable = (ListView) view.findViewById(R.id.setting_available);
            tvClose = (TextView) view.findViewById(R.id.tv_blutooth_close);
            tvCheckSw = (TextView) view.findViewById(R.id.txt_inspection_prompt);
            tvBtName = (TextView) view.findViewById(R.id.tv_setting_edit_name);
            tvPaired = (TextView) view.findViewById(R.id.tv_paired);
            tvDelAll = (TextView) view.findViewById(R.id.tv_del_all);
            tvSearch = (TextView) view.findViewById(R.id.tv_setting_search);
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
        isBTon = mPresenter.isBTOn();
        isDisCovering = getBluetManger().getBTIsDisCovering();
        ivSearch.setImageResource(R.drawable.loading_animation);
        tvBlutName = mPresenter.getBlutoothName();
        tvBtName.setText(tvBlutName);
        animationDrawable = (AnimationDrawable) ivSearch.getDrawable();
        //配对设备
        if (pairAdapter == null) {
            if (pairedDataListList == null) {
                pairedDataListList = new ArrayList<>();

            }
            pairAdapter = new BluetoothAdapter(getContext(), pairedDataListList);
            listView.setAdapter(pairAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String remote_device_macaddr = pairedDataListList.get(i).getRemote_device_macaddr();
                    int status = pairedDataListList.get(i).getRemote_connect_status();
                    if (status == Constants.BLUETOOTH_DEVICE_BONDED) {
                        mPresenter.connBlutoth(remote_device_macaddr);
                    }

                }
            });
        }
        //可用设备
        if (avaAdapter == null) {
            if (unPairedDataListList == null) {
                unPairedDataListList = new ArrayList<>();
            }
            avaAdapter = new BluetoothAdapter(getContext(), unPairedDataListList);
            listAvailable.setAdapter(avaAdapter);
            listAvailable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String remote_device_macaddr = unPairedDataListList.get(i).getRemote_device_macaddr();
                    mPresenter.connBlutoth(remote_device_macaddr);
                }
            });
        }
        ivOnSw.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivOnSw.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivOnSw.setCheckedImmediately(isBTon);
        if (isBTon) {
            tvClose.setText("打开");
            mPresenter.searchPairedList();
        } else {
            tvClose.setText("关闭");

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
                getBluetManger().setBTIsDisCovering(isDisCovering);
                break;
            case R.id.iv_setting_switch:
                isBTon = !isBTon;
                if (isBTon) {
                    tvClose.setText("打开");
                    mPresenter.openBluetooth();
                    mPresenter.searchPairedList();
                } else {
                    tvClose.setText("关闭");
                    mPresenter.closeBluetooth();
                }
                break;
            case R.id.tv_del_all:
                //断开连接
                for (BluetoothDeviceData deviceData : pairedDataListList) {
                    BluetoothManager.getBluetoothManagerInstance(getContext()).removeDevice(deviceData.getRemote_device_macaddr());
                }
                pairedDataListList.clear();
                pairAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_setting_edit_name:
                showDialog();
                break;
            case R.id.tv_setting_search:
                mPresenter.searchPairedList();
                break;

        }
    }


    private void showDialog() {
        if (settingDialog == null) {
            settingDialog = new SettingDialog(getActivity(), R.style.AlertDialogCustom);
        }
        settingDialog.setEditText(mPresenter.getBlutoothName());
        settingDialog.setCanelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog.dismiss();
            }
        });
        settingDialog.setEditOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                String editText = settingDialog.getEditText();
                mPresenter.setBlutoothName(editText);
                tvBtName.setText(mPresenter.getBlutoothName());
            }
        });
        settingDialog.show();
    }


    @Override
    public void onNotifyBTSwitchStateOn() {
        mPresenter.searchPairedList();
    }

    @Override
    public void onNotifyBTSwitchStateOff() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "蓝牙关闭nnnnn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNotifyOnUpdateUIPairedList(final List<BluetoothDeviceData> list) {
        Log.d(TAG, "UIlist.size():" + list.size());
        if (list != null && list.size() > 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!getActivity().isFinishing()) {
                        pairedDataListList.clear();
                        for (BluetoothDeviceData deviceData : list) {
                            pairedDataListList.add(deviceData);
                        }
                        pairAdapter.changetShowDelImage(true);
                        pairAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onNotifyOnUpdateUIUnpairedList(final List<BluetoothDeviceData> list) {
        Log.d(TAG, "UNlist.size():" + list.size());
        if (list != null && list.size() > 0) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mActivity.isFinishing()) {
                        unPairedDataListList.clear();
                        for (BluetoothDeviceData deviceData : list) {
                            unPairedDataListList.add(deviceData);
                        }
                        avaAdapter.changetShowDelImage(false);
                        avaAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onNotifyScanDeviceStart() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvSearch.setVisibility(View.GONE);
                linearSearch.setVisibility(View.VISIBLE);
                animationDrawable.start();
            }
        });
    }

    @Override
    public void onNotifyScanDeviceEnd() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                linearSearch.setVisibility(View.GONE);
                tvSearch.setVisibility(View.VISIBLE);
            }
        });
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
    public BluetoothManager getBluetManger() {
        return BluetoothManager.getBluetoothManagerInstance(getContext());

    }
}
