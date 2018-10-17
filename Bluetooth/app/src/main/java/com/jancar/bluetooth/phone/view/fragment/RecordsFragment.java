package com.jancar.bluetooth.phone.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.jancar.bluetooth.Listener.BTCallLogListener;
import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.RecordsAdapter;
import com.jancar.bluetooth.phone.contract.RecordsContract;
import com.jancar.bluetooth.phone.presenter.RecordsPresenter;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.FlyLog;
import com.jancar.bluetooth.phone.util.ThreadUtils;
import com.jancar.bluetooth.phone.widget.AVLoadingIndicatorView;
import com.ui.mvp.view.support.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tzq
 * @date 2018-8-21 16:37:20
 * 通话记录界面
 */
public class RecordsFragment extends BaseFragment<RecordsContract.Presenter, RecordsContract.View> implements RecordsContract.View, BTCallLogListener, BTConnectStatusListener, View.OnClickListener {
    protected Activity mActivity;

    View mRootView;
    ListView listView;
    LinearLayout linearSyn;
    AVLoadingIndicatorView ivSynIng;
    ImageView ivSynRecord;
    ImageView ivSynError;
    TextView tvSynRecord;

    private boolean isSynRecord;
    private List<BluetoothPhoneBookData> callDataList;
    private RecordsAdapter adapter;
    private AnimationDrawable animationDrawable;
    private int selectPos = -1;
    private boolean hidden = false;
    BluetoothManager bluetoothManager;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
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
            ViewGroup p = (ViewGroup) mRootView.getParent();
            if (p != null) p.removeAllViewsInLayout();
        } else {
            mRootView = inflater.inflate(R.layout.fragment_records, container, false);
        }
        findView(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            getManager().registerBTCallLogListener(this);
            getManager().setBTConnectStatusListener(this);
            isConneView();
            getBTCallLog();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("RecordsFragment", "onHiddenChanged");
        this.hidden = hidden;
        if (!hidden) {
            FlyLog.d("RecordsFragment", "onHidden");
            getManager().registerBTCallLogListener(this);
            getBTCallLog();
            getManager().setBTConnectStatusListener(this);
            isConneView();
            adapter.setNormalPosition();
        } else {
            getManager().unRegisterBTCallLogListener();
            getManager().setBTConnectStatusListener(null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.setNormalPosition();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getManager().unRegisterBTCallLogListener();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void findView(View mRootView) {
        listView = mRootView.findViewById(R.id.records_recycler_list);
        linearSyn = mRootView.findViewById(R.id.linear_syn_records);
        ivSynIng = mRootView.findViewById(R.id.iv_syn_records_ing);
        ivSynRecord = mRootView.findViewById(R.id.iv_syn_records);
        ivSynError = mRootView.findViewById(R.id.iv_syn_records_error);
        tvSynRecord = mRootView.findViewById(R.id.tv_syn_record);
        ivSynRecord.setOnClickListener(this);
        ivSynError.setOnClickListener(this);
    }


    @Override
    public RecordsContract.Presenter createPresenter() {
        return new RecordsPresenter();
    }

    @Override
    public RecordsContract.View getUiImplement() {
        return this;
    }

    private void init() {
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        if (adapter == null) {
            if (callDataList == null) {
                callDataList = new ArrayList<>();
            }
            adapter = new RecordsAdapter(getActivity());
            listView.setAdapter(adapter);
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String phoneNumber = callDataList.get(position).getPhoneNumber();
                    adapter.setSelectPosition(position);
                    if (position == selectPos) {
                        bluetoothManager.hfpCall(phoneNumber);
                    }
                    selectPos = position;
                }
            });
        }

    }

    private void synShow() {
//        isSynRecord = getPresenter().isSynCallRecord();
        if (isDownLoading()) {
            linearSyn.setVisibility(View.VISIBLE);
            tvSynRecord.setText(R.string.tv_syning_record);
            ivSynRecord.setVisibility(View.GONE);
            ivSynError.setVisibility(View.GONE);
            ivSynIng.show();
            listView.setVisibility(View.GONE);
        } else {
            linearSyn.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isDownLoading() {
        boolean loading = getPresenter().isDownLoading();
        return loading;
    }

    private void isConneView() {
        if (isBluConn()) {
            synShow();
        } else {
            showText();
            tvSynRecord.setText(R.string.tv_bt_connect_is_none);
            listView.setVisibility(View.GONE);
        }
    }

    private void showText() {
        linearSyn.setVisibility(View.VISIBLE);
        ivSynError.setVisibility(View.GONE);
        ivSynIng.hide();
        ivSynRecord.setVisibility(View.GONE);
        tvSynRecord.setVisibility(View.VISIBLE);
    }

    private boolean isBluConn() {
        boolean isBluConn = getManager().isConnect();
        return isBluConn;
    }

    @Override
    public Context getUIContext() {
        return getActivity();
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
//        mHandler.post(runnable);
    }

    @Override
    public BluetoothManager getManager() {
        return bluetoothManager;
    }


    private void getBTCallLog() {
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                getManager().getBTCallLogs();
            }
        });
    }


    @Override
    public void onNotifyDownloadCallLogsIndex(int i) {

    }

    @Override
    public void onNotifyDownloadCallLogsCount(final int i) {
//        Message message = new Message();
//        message.what = Constants.CONTACT_CALL_LOGS_COUNT;
//        message.obj = i;
//        handler.sendMessage(message);

    }

    @Override
    public void onNotifyDownloadCallLogsList(final List<BluetoothPhoneBookData> list) {
        Log.d("RecordsFragment", "list.size(rr):" + list.size());
        this.callDataList = list;
        handler.removeMessages(Constants.CONTACT_CALL_LOGS);
        handler.sendEmptyMessageDelayed(Constants.CONTACT_CALL_LOGS, 100);
    }

    @Override
    public void onNotifyDownloadCallLogsStart() {
        handler.sendEmptyMessage(Constants.CONTACT_CALL_LOGS_START);

    }

    @Override
    public void onNotifyDownloadCallLogsStop() {

    }

    @Override
    public void onNotifyDownloadCallLogsError() {

    }

    @Override
    public void onNotifyDownloadCallLogsFinish() {
        handler.sendEmptyMessage(Constants.CONTACT_CALL_LOGS_FINISH);
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
                        showText();
                        tvSynRecord.setText(R.string.tv_bt_connect_is_none);
                        listView.setVisibility(View.GONE);
                    } else if (obj == Constants.BT_CONNECT_IS_CONNECTED) {
                        synShow();

                    } else if (obj == Constants.BT_CONNECT_IS_CLOSE) {
                        showText();
                        tvSynRecord.setText(R.string.tv_bt_connect_is_none);
                        listView.setVisibility(View.GONE);
                    }
                    break;
                case Constants.CONTACT_CALL_LOGS:
//                    if (callDataList.size() > 0 && callDataList != null) {
//                        linearSyn.setVisibility(View.GONE);
//                        listView.setVisibility(View.VISIBLE);
//                        adapter.setBTPhoneBooks(callDataList);
//                    } else {
//                        showText();
//                        tvSynRecord.setText(R.string.tv_record_log);
//                        listView.setVisibility(View.GONE);
//                    }
                    adapter.setBTPhoneBooks(callDataList);

                    break;
                case Constants.CONTACT_CALL_LOGS_START:
                    listView.setVisibility(View.GONE);
                    linearSyn.setVisibility(View.VISIBLE);
                    ivSynRecord.setVisibility(View.GONE);
                    ivSynIng.show();
                    tvSynRecord.setText(R.string.tv_record_error);
                    break;
                case Constants.CONTACT_CALL_LOGS_FINISH:
                    linearSyn.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    break;
                case Constants.CONTACT_CALL_LOGS_COUNT:
//                    int obj1 = (int) msg.obj;
//                    if (obj1 == 0) {
//                        showText();
//                        tvSynRecord.setText(R.string.tv_record_log);
//                        listView.setVisibility(View.GONE);
//                    } else {
//                        linearSyn.setVisibility(View.GONE);
//                        listView.setVisibility(View.VISIBLE);
//                    }
                    break;
            }
        }
    };

    @Override
    public void onNotifyBTConnectStateChange(byte b) {
        Message message = new Message();
        message.what = Constants.CONTACT_BT_CONNECT;
        message.obj = b;
        handler.sendMessage(message);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_syn_records:
                getCallLogs();
                break;
            case R.id.iv_syn_records_error:
                getCallLogs();
                break;
        }
    }

    private void getCallLogs() {
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                getPresenter().getCallRecordList();
            }
        });
    }
}
