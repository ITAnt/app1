package com.jancar.bluetooth.phone.view.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.jancar.bluetooth.Listener.BTCallLogListener;
import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.RecordsAdapter;
import com.jancar.bluetooth.phone.contract.RecordsContract;
import com.jancar.bluetooth.phone.presenter.RecordsPresenter;
import com.jancar.bluetooth.phone.util.Constants;
import com.ui.mvp.view.support.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @author Tzq
 * @date 2018-8-21 16:37:20
 * 通话记录界面
 */
public class RecordsFragment extends BaseFragment<RecordsContract.Presenter, RecordsContract.View> implements RecordsContract.View, BTCallLogListener, BTConnectStatusListener {
    protected Activity mActivity;
    View mRootView;
    Unbinder mUnbinder;
    @BindView(R.id.records_recycler_list)
    ListView listView;
    @BindView(R.id.linear_syn_records)
    LinearLayout linearSyn;
    @BindView(R.id.iv_syn_records_ing)
    ImageView ivSynIng;
    @BindView(R.id.iv_syn_records)
    ImageView ivSynRecord;
    @BindView(R.id.iv_syn_records_error)
    ImageView ivSynError;
    @BindView(R.id.tv_syn_record)
    TextView tvSynRecord;

    private boolean isSynRecord;
    private List<BluetoothPhoneBookData> callDataList;
    private RecordsAdapter adapter;
    private AnimationDrawable animationDrawable;
    private int selectPos = -1;
    private boolean hidden = false;

    private Handler mHandler = new RecordsFragment.InternalHandler(this);


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
        mUnbinder = ButterKnife.bind(this, mRootView);

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
            getManager().getBTCallLogs();
            getManager().setBTConnectStatusListener(this);
            isConneView();
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
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getManager().unRegisterBTCallLogListener();
    }

    public RecordsFragment() {

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
                        BluetoothManager.getBluetoothManagerInstance(mActivity).hfpCall(phoneNumber);
                    }
                    selectPos = position;
                }
            });
        }
        ivSynIng.setImageResource(R.drawable.loading_animation_big);
        animationDrawable = (AnimationDrawable) ivSynIng.getDrawable();
    }

    private void synShow() {
//        isSynRecord = getPresenter().isSynCallRecord();
        if (isDownLoading()) {
            linearSyn.setVisibility(View.VISIBLE);
            tvSynRecord.setText(R.string.tv_syning_record);
            ivSynRecord.setVisibility(View.GONE);
            ivSynError.setVisibility(View.GONE);
            ivSynIng.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            animationDrawable.start();
        } else {
            linearSyn.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            animationDrawable.stop();
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
        ivSynIng.setVisibility(View.GONE);
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
        mHandler.post(runnable);
    }

    @Override
    public BluetoothManager getManager() {
        BluetoothManager bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        return bluetoothManager;
    }


    @OnClick({R.id.iv_syn_records, R.id.iv_syn_records_error})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_syn_records:
                getPresenter().getCallRecordList();
                break;
            case R.id.iv_syn_records_error:
                getPresenter().getCallRecordList();
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("RecordsFragment", "onHiddenChanged");
        this.hidden = hidden;
        if (!hidden) {
            getManager().registerBTCallLogListener(this);
            getManager().getBTCallLogs();
            getManager().setBTConnectStatusListener(this);
            isConneView();
            adapter.setNormalPosition();
        } else {
            getManager().unRegisterBTCallLogListener();
            getManager().setBTConnectStatusListener(null);
        }
    }


    @Override
    public void onNotifyDownloadCallLogsIndex(int i) {

    }

    @Override
    public void onNotifyDownloadCallLogsCount(final int i) {
        Log.d("RecordsFragment", "i:" + i);
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    showText();
                    listView.setVisibility(View.GONE);
                    tvSynRecord.setText(R.string.tv_record_log);
                } else {
                    adapter.setBTPhoneBooks(callDataList);
                }
            }
        });

    }

    @Override
    public void onNotifyDownloadCallLogsList(final List<BluetoothPhoneBookData> list) {
        Log.d("RecordsFragment", "list.size(rr):" + list.size());
        if (!mActivity.isFinishing()) {
            this.callDataList = list;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isBluConn()) {
                        adapter.setBTPhoneBooks(callDataList);
                    } else {
                        showText();
                        listView.setVisibility(View.GONE);
                        tvSynRecord.setText(R.string.tv_bt_connect_is_none);
                    }

                }
            }, 100);
        }
    }

    @Override
    public void onNotifyDownloadCallLogsStart() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                linearSyn.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                ivSynError.setVisibility(View.GONE);
                ivSynRecord.setVisibility(View.GONE);
                tvSynRecord.setText(R.string.tv_syning_record);
                animationDrawable.start();
            }
        });

    }

    @Override
    public void onNotifyDownloadCallLogsStop() {

    }

    @Override
    public void onNotifyDownloadCallLogsError() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                listView.setVisibility(View.GONE);
                linearSyn.setVisibility(View.VISIBLE);
                ivSynRecord.setVisibility(View.GONE);
                ivSynIng.setVisibility(View.GONE);
                tvSynRecord.setText(R.string.tv_record_error);
                animationDrawable.stop();
            }
        });

    }

    @Override
    public void onNotifyDownloadCallLogsFinish() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                linearSyn.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                animationDrawable.stop();
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.BT_CONNECT_IS_NONE) {
//                Toast.makeText(mActivity, "Record:蓝牙未连接", Toast.LENGTH_SHORT).show();
                showText();
                tvSynRecord.setText(R.string.tv_bt_connect_is_none);
                listView.setVisibility(View.GONE);

            } else if (msg.what == Constants.BT_CONNECT_IS_CONNECTED) {
//                Toast.makeText(mActivity, "Record:蓝牙连接", Toast.LENGTH_SHORT).show();
                synShow();

            } else if (msg.what == Constants.BT_CONNECT_IS_CLOSE) {
                showText();
                tvSynRecord.setText(R.string.tv_bt_connect_is_none);
                listView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onNotifyBTConnectStateChange(byte b) {
        Message msg = handler.obtainMessage();
        msg.what = b;
        handler.sendMessage(msg);
    }
}
