package com.jancar.bluetooth.phone.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.EquipmentContract;
import com.jancar.bluetooth.phone.entity.Event;
import com.jancar.bluetooth.phone.presenter.EquipmentPresenter;
import com.jancar.bluetooth.phone.util.ApplicationUtil;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.ToastUtil;
import com.ui.mvp.view.support.BaseFragment;

import org.greenrobot.eventbus.EventBus;


/**
 * @author Tzq
 * @date 2018-8-21 16:37:48
 * 设备管理
 */
public class EquipmentFragment extends BaseFragment<EquipmentContract.Presenter, EquipmentContract.View> implements EquipmentContract.View, BTConnectStatusListener, View.OnClickListener {
    private static final String TAG = "EquipmentFragment";
    View mRootView;
    TextView tvselfName;
    TextView tvConnName;
    ImageView ivConnet;
    Button btnConn;
    Button btnClose;
    Button btnSetting;
    private Activity mActivity;
    private boolean isConnet;
    private boolean hidden = false;
    BluetoothManager bluetoothManager;
    private ToastUtil mToast;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeAllViewsInLayout();
            }
        } else {
            mRootView = inflater.inflate(R.layout.fragment_equipment, container, false);
        }
        findView(mRootView);
        return mRootView;
    }

    private void findView(View mRootView) {
        tvselfName = mRootView.findViewById(R.id.tv_equipment_setting_name);
        tvConnName = mRootView.findViewById(R.id.tv_equipment_setting_conne_name);
        ivConnet = mRootView.findViewById(R.id.iv_equipment_connet);
        btnConn = mRootView.findViewById(R.id.btn_equipment_conn);
        btnClose = mRootView.findViewById(R.id.btn_equipment_close);
        btnSetting = mRootView.findViewById(R.id.btn_equipment_setting);
        btnClose.setOnClickListener(this);
        btnConn.setOnClickListener(this);
        btnSetting.setOnClickListener(this);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    @Override
    public void onResume() {
        super.onResume();
        getManager().setBTConnectStatusListener(this);
        if (!hidden) {
            ConnShowView();
            String selfName = getPresenter().getSelfName();
            String ConnName = getPresenter().getConnetName();
            tvselfName.setText(selfName);
            tvConnName.setText(ConnName);
            Log.e(TAG, "selfName==onResume=" + selfName + "===" + "ConnName===" + ConnName);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onDestroyView() {
        if (mToast != null) {
            mToast.Cancel();
        }
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothManager.setBTConnectStatusListener(null);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CONTACT_BT_CONNECT:
                    //蓝牙状态
                    byte obj = (byte) msg.obj;
                    if (obj == Constants.BT_CONNECT_IS_NONE) {
                        ivConnet.setImageResource(R.drawable.iv_equipment_disconnect);
                        btnConn.setVisibility(View.VISIBLE);
                        btnClose.setVisibility(View.GONE);
                        tvConnName.setText(getPresenter().getConnetName());

                    } else if (obj == Constants.BT_CONNECT_IS_CONNECTED) {
                        ivConnet.setImageResource(R.drawable.iv_equipment_connet);
                        btnConn.setVisibility(View.GONE);
                        btnClose.setVisibility(View.VISIBLE);
                        tvConnName.setText(getPresenter().getConnetName());
                        EventBus.getDefault().post(new Event(true));

                    } else if (obj == Constants.BT_CONNECT_IS_CLOSE) {
                        ivConnet.setImageResource(R.drawable.iv_equipment_disconnect);
                        btnConn.setVisibility(View.VISIBLE);
                        btnClose.setVisibility(View.GONE);
                        tvConnName.setText(getPresenter().getConnetName());
                    }
                    break;
            }
        }
    };

    @Override
    public EquipmentContract.Presenter createPresenter() {
        return new EquipmentPresenter();
    }

    @Override
    public EquipmentContract.View getUiImplement() {
        return this;
    }

    private void initView() {
        mToast = new ToastUtil(mActivity);
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        ConnShowView();
        tvselfName.setText(getPresenter().getSelfName());
        tvConnName.setText(getPresenter().getConnetName());
    }

    private void ConnShowView() {
        isConnet = getManager().isConnect();
        if (isConnet) {
            ivConnet.setImageResource(R.drawable.iv_equipment_connet);
            btnConn.setVisibility(View.GONE);
            btnClose.setVisibility(View.VISIBLE);
        } else {
            ivConnet.setImageResource(R.drawable.iv_equipment_disconnect);
            btnConn.setVisibility(View.VISIBLE);
            btnClose.setVisibility(View.GONE);
        }
    }


    //连接蓝牙
    private void getConnect() {
        boolean btOn = getManager().isBTOn();
        if (btOn) {
            try {
                String historyAddress = getManager().getHistoryConnectDeviceAddress();
                Log.e(TAG, "historyAddress===" + historyAddress);
                if (!TextUtils.isEmpty(historyAddress)) {
                    getManager().connectDevice(historyAddress);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception===" + e.getMessage());
                mToast.ShowTipText(mActivity, mActivity.getString(R.string.tv_equiment_history_adress));
            }

        } else {
            mToast.ShowTipText(mActivity, mActivity.getString(R.string.tv_bt_connect_is_close));
        }
    }

    //跳转到设置界面
    private void go2Setting() {
        Intent intent = new Intent();
        intent.setClassName(Constants.PACKNAME, Constants.CLASSNAME);
        intent.putExtra(Constants.SETTING_POSITION, Constants.SETTING_POSITION_NUM);
        startActivity(intent);
    }


    @Override
    public Context getUIContext() {
        return mActivity;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            getManager().setBTConnectStatusListener(this);
            ConnShowView();
            tvselfName.setText(getPresenter().getSelfName());
            tvConnName.setText(getPresenter().getConnetName());
            Log.e(TAG, "selfName==onHiddenChanged=" + getPresenter().getSelfName() + "===" + "ConnName===" + getPresenter().getConnetName());
        } else {
            getManager().setBTConnectStatusListener(null);
            if (mToast != null) {
                mToast.Cancel();
            }
        }
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
//        mHandler.post(runnable);
    }

    @Override
    public BluetoothManager getManager() {
        return bluetoothManager;
    }

    @Override
    public void onNotifyBTConnectStateChange(final byte b) {
        Message message = new Message();
        message.what = Constants.CONTACT_BT_CONNECT;
        message.obj = b;
        handler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_equipment_close:
                //断开蓝牙连接
                getPresenter().disConnectDevice();
                break;
            case R.id.btn_equipment_setting:
                if (ApplicationUtil.hasApplication(mActivity, Constants.PACKNAME)) {
                    go2Setting();
                } else {
//                    mToast.ShowTipText(mActivity, "应用不存在");
                }

                break;
            case R.id.btn_equipment_conn:
                getConnect();
                break;
        }

    }
}
