package com.jancar.bluetooth.phone.view.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.Listener.BTSettingListener;
import com.jancar.bluetooth.lib.BluetoothDeviceData;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.EquipmentContract;
import com.jancar.bluetooth.phone.presenter.EquipmentPresenter;
import com.jancar.bluetooth.phone.util.IntentUtil;
import com.jancar.bluetooth.phone.view.SettingActivity;
import com.ui.mvp.view.support.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.List;

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
    private static final String TAG = "EquipmentFragment";
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


    public static byte BT_CONNECT_IS_NONE = (byte) 0x01;//蓝牙未连接
    public static byte BT_CONNECT_IS_CONNECTED = (byte) 0x02;//蓝牙已连接
    public static byte BT_CONNECT_IS_CLOSE = (byte) 0x00;//蓝牙已关闭


    private Handler mHandler = new EquipmentFragment.InternalHandler(this);


    private static class InternalHandler extends Handler {
        private WeakReference<Fragment> weakRefActivity;

        public InternalHandler(Fragment fragment) {
            weakRefActivity = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment = weakRefActivity.get();
            if (fragment != null) {
            }
        }
    }

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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public EquipmentFragment() {

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BT_CONNECT_IS_NONE) {
                ivConnet.setImageResource(R.drawable.iv_equipment_disconnect);
                btnConn.setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.GONE);
                tvConnName.setText(getPresenter().getConnetName());
                Toast.makeText(mActivity, "蓝牙未连接", Toast.LENGTH_SHORT).show();

            } else if (msg.what == BT_CONNECT_IS_CONNECTED) {
                ivConnet.setImageResource(R.drawable.iv_equipment_connet);
                btnConn.setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
                tvConnName.setText(getPresenter().getConnetName());
                Toast.makeText(mActivity, "蓝牙连接：" + getPresenter().getConnetName(), Toast.LENGTH_SHORT).show();

            } else if (msg.what == BT_CONNECT_IS_CLOSE) {
                Toast.makeText(mActivity, "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
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


    @OnClick({R.id.btn_equipment_close, R.id.btn_equipment_setting, R.id.btn_equipment_conn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_equipment_close:
                //断开蓝牙连接
                getPresenter().disConnectDevice();
                break;
            case R.id.btn_equipment_setting:
                IntentUtil.gotoActivity(getActivity(), SettingActivity.class, false);
                break;
            case R.id.btn_equipment_conn:
                String historyAddress = getManager().getHistoryConnectDeviceAddress();
                getManager().connectDevice(historyAddress);
                break;
        }
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
            ConnShowView();
            tvselfName.setText(getPresenter().getSelfName());
        }
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public BluetoothManager getManager() {
        BluetoothManager manager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        return manager;
    }

    @Override
    public void onNotifyBTConnectStateChange(final byte b) {
        Log.e(TAG, "b:" + b);
        Message msg = handler.obtainMessage();
        msg.what = b;
        handler.sendMessage(msg);
    }
}
