package com.jancar.bluetooth.phone.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.MainActivity;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.EquipmentContract;
import com.jancar.bluetooth.phone.presenter.EquipmentPresenter;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.ToastUtil;
import com.ui.mvp.view.support.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @author Tzq
 * @date 2018-8-21 16:37:48
 * 设备管理
 */
public class EquipmentFragment extends BaseFragment<EquipmentContract.Presenter, EquipmentContract.View> implements EquipmentContract.View, BTConnectStatusListener {
    Unbinder unbinder;
    View mRootView;
    @BindView(R.id.tv_equipment_setting_name)
    TextView tvselfName;
    @BindView(R.id.tv_equipment_setting_conne_name)
    TextView tvConnName;
    @BindView(R.id.iv_equipment_connet)
    ImageView ivConnet;
    @BindView(R.id.btn_equipment_conn)
    Button btnConn;
    @BindView(R.id.btn_equipment_close)
    Button btnClose;
    private Activity mActivity;
    private boolean isConnet;
    private boolean hidden = false;
    BluetoothManager bluetoothManager;


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
        unbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
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
            tvselfName.setText(getPresenter().getSelfName());
            tvConnName.setText(getPresenter().getConnetName());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
        unbinder.unbind();
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
                        if (MainActivity.connectDialog.isShowing()) {
                            MainActivity.connectDialog.dismiss();
                        }

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
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        ConnShowView();
        tvselfName.setText(getPresenter().getSelfName());
        tvConnName.setText(getPresenter().getConnetName());
    }

    private void ConnShowView() {
        isConnet = getManager().isConnect();
//        Toast.makeText(mActivity, "isConnet:" + isConnet, Toast.LENGTH_SHORT).show();
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


    @OnClick({R.id.btn_equipment_close, R.id.btn_equipment_setting, R.id.btn_equipment_conn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_equipment_close:
                //断开蓝牙连接
                getPresenter().disConnectDevice();
                break;
            case R.id.btn_equipment_setting:
                go2Setting();
                break;
            case R.id.btn_equipment_conn:
                getConnect();
                break;
        }
    }

    //连接蓝牙
    private void getConnect() {
        boolean btOn = getManager().isBTOn();
        if (btOn) {
            String historyAddress = getManager().getHistoryConnectDeviceAddress();
            getManager().connectDevice(historyAddress);
        } else {
            ToastUtil.ShowToast(mActivity, mActivity.getString(R.string.tv_bt_connect_is_close));
        }
    }

    //跳转到设置界面
    private void go2Setting() {
        Intent intent = new Intent();
        intent.setClassName("com.jancar.settingss", "com.jancar.settings.view.activity.MainActivity");
        intent.putExtra("position", 1);
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
        } else {
            getManager().setBTConnectStatusListener(null);
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
}
