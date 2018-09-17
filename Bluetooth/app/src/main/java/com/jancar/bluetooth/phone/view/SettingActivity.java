package com.jancar.bluetooth.phone.view;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.bluetooth.Listener.BTSettingListener;
import com.jancar.bluetooth.lib.BluetoothDeviceData;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.SettingAdapter;
import com.jancar.bluetooth.phone.contract.SettingContract;
import com.jancar.bluetooth.phone.presenter.SettingPresenter;
import com.jancar.bluetooth.phone.widget.SettingDialog;
import com.jancar.bluetooth.phone.widget.SwitchButton;
import com.ui.mvp.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @author Tzq
 * @date 2018-9-3 16:00:53
 */
public class SettingActivity extends BaseActivity<SettingContract.Presenter, SettingContract.View> implements SettingContract.View, BTSettingListener {
    private static final String TAG = "SettingActivity";
    Unbinder unbinder;
    @BindView(R.id.iv_setting_switch)
    SwitchButton ivOnSw;
    @BindView(R.id.iv_setting_check_switch)
    SwitchButton ivCheckSw;
    @BindView(R.id.linear_search)
    LinearLayout linearSearch;
    @BindView(R.id.iv_setting_search)
    ImageView ivSearch;
    @BindView(R.id.setting_recyclerview)
    ListView listView;                      //配对设备列表
    @BindView(R.id.tv_blutooth_close)
    TextView tvClose;
    @BindView(R.id.tv_setting_edit_name)
    TextView tvBtName;
    @BindView(R.id.tv_paired)
    TextView tvPaired;
    @BindView(R.id.tv_del_all)
    TextView tvDelAll;
    @BindView(R.id.setting_available)
    ListView listAvailable;                 //可用设备列表

    @BindView(R.id.tv_setting_search)
    TextView tvSearch;
    private SettingDialog settingDialog;
    private AnimationDrawable animationDrawable;
    SettingAdapter pairAdapter, avaAdapter;
    private List<BluetoothDeviceData> pairedDataListList;
    private List<BluetoothDeviceData> unPairedDataListList;
    private boolean isDisCovering;
    private boolean isBTon;
    private String tvBlutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBluetManger().registerBTSettingListener(this);
        tvBlutName = getPresenter().getBlutoothName();
        tvBtName.setText(tvBlutName);
        getBluetManger().getBondDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        getBluetManger().unRegisterBTSettingListener(this);
    }

    @Override
    public SettingContract.Presenter createPresenter() {
        return new SettingPresenter();
    }

    @Override
    public SettingContract.View getUiImplement() {
        return this;
    }

    private void initView() {
        isBTon = getPresenter().isBTOn();
        isDisCovering = getBluetManger().getBTIsDisCovering();
        ivSearch.setImageResource(R.drawable.loading_animation);
        tvBlutName = getPresenter().getBlutoothName();
        tvBtName.setText(tvBlutName);
        animationDrawable = (AnimationDrawable) ivSearch.getDrawable();
        //配对设备
        if (pairAdapter == null) {
            if (pairedDataListList == null) {
                pairedDataListList = new ArrayList<>();

            }
            pairAdapter = new SettingAdapter(this, pairedDataListList);
            listView.setAdapter(pairAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String remote_device_macaddr = unPairedDataListList.get(i).getRemote_device_macaddr();
                    getPresenter().connBlutoth(remote_device_macaddr);
                }
            });
        }
        //可用设备
        if (avaAdapter == null) {
            if (unPairedDataListList == null) {
                unPairedDataListList = new ArrayList<>();
            }
            avaAdapter = new SettingAdapter(this, unPairedDataListList);
            listAvailable.setAdapter(avaAdapter);
            listAvailable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String remote_device_macaddr = unPairedDataListList.get(i).getRemote_device_macaddr();
                    getPresenter().connBlutoth(remote_device_macaddr);
                }
            });
        }
        ivOnSw.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivOnSw.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivOnSw.setCheckedImmediately(isBTon);
        if (isBTon) {
            tvClose.setText("打开");
            getPresenter().searchPairedList();
        } else {
            tvClose.setText("关闭");
<<<<<<< HEAD
        }
        if (isDisCovering) {
            tvClose.setText("打开");
        } else {
            ivCheckSw.setImageResource(R.drawable.setting_switch_off);
=======

>>>>>>> 08499a21928bf56b95edebb0cc70675d5df29e59
        }
        ivCheckSw.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivCheckSw.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivCheckSw.setCheckedImmediately(isDisCovering);

    }


    @Override
    public BluetoothManager getBluetManger() {
        BluetoothManager manager = BluetoothManager.getBluetoothManagerInstance(this);
        return manager;
    }

    @OnClick({R.id.iv_setting_check_switch, R.id.iv_setting_switch, R.id.tv_del_all, R.id.tv_setting_edit_name, R.id.tv_setting_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting_check_switch:
                isDisCovering = !isDisCovering;
                getBluetManger().setBTIsDisCovering(isDisCovering);
                break;
            case R.id.iv_setting_switch:
                isBTon = !isBTon;
                if (isBTon) {
                    tvClose.setText("打开");
                    getPresenter().openBluetooth();
                    getPresenter().searchPairedList();
                } else {
                    tvClose.setText("关闭");
                    getPresenter().closeBluetooth();
                }
                break;
            case R.id.tv_del_all:
                //断开连接
                for (BluetoothDeviceData deviceData : pairedDataListList) {
                    BluetoothManager.getBluetoothManagerInstance(this).removeDevice(deviceData.getRemote_device_macaddr());
                }
                pairedDataListList.clear();
                pairAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_setting_edit_name:
                showDialog();
                break;
            case R.id.tv_setting_search:
                getPresenter().searchPairedList();
                break;

        }
    }

    private void showDialog() {
        if (settingDialog == null) {
            settingDialog = new SettingDialog(this, R.style.AlertDialogCustom);
        }
        settingDialog.setEditText(getPresenter().getBlutoothName());
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
                getPresenter().setBlutoothName(editText);
                tvBtName.setText(getPresenter().getBlutoothName());
            }
        });
        settingDialog.show();
    }


    @Override
    public void onNotifyBTSwitchStateOn() {
        getPresenter().searchPairedList();
    }

    @Override
    public void onNotifyBTSwitchStateOff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingActivity.this, "蓝牙关闭nnnnn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNotifyOnUpdateUIPairedList(final List<BluetoothDeviceData> list) {
        Log.d(TAG, "UIlist.size():" + list.size());
        if (list != null && list.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
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
        runOnUiThread(new Runnable() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                linearSearch.setVisibility(View.GONE);
                tvSearch.setVisibility(View.VISIBLE);
            }
        });
    }
}
