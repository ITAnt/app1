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

import com.jancar.bluetooth.Listener.BTCallLogListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.RecordsAdapter;
import com.jancar.bluetooth.phone.contract.RecordsContract;
import com.jancar.bluetooth.phone.presenter.RecordsPresenter;
import com.ui.mvp.view.support.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
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
public class RecordsFragment extends BaseFragment<RecordsContract.Presenter, RecordsContract.View> implements RecordsContract.View, BTCallLogListener {
    protected Activity mActivity;
    View mRootView;
    Unbinder mUnbinder;
    @BindView(R.id.records_recycler_list)
    ListView listView;
    @BindView(R.id.linear_syn_records)
    LinearLayout linearSyn;
    @BindView(R.id.linear_syn_records_ing)
    LinearLayout linearSynIng;
    @BindView(R.id.linear_syn_records_error)
    LinearLayout linearSynError;
    @BindView(R.id.iv_syn_records_ing)
    ImageView ivSynIng;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
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
    public void onResume() {
        super.onResume();
        getManager().registerBTCallLogListener(this);
        if (!hidden) {
            synShow();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        getManager().unRegisterBTCallLogListener(this);
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
            adapter = new RecordsAdapter(getActivity(), callDataList);
            listView.setAdapter(adapter);
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
        synShow();
    }

    private void synShow() {
        isSynRecord = getPresenter().isSynCallRecord();
        if (isSynRecord) {
            linearSyn.setVisibility(View.GONE);
            linearSynError.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            linearSynIng.setVisibility(View.GONE);
        } else {
            linearSyn.setVisibility(View.VISIBLE);
            linearSynError.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearSynIng.setVisibility(View.GONE);
        }

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
        this.hidden = hidden;
        if (!hidden) {
            synShow();
        }
    }


    @Override
    public void onNotifyDownloadCallLogsIndex(int i) {

    }

    @Override
    public void onNotifyDownloadCallLogsCount(int i) {

    }

    @Override
    public void onNotifyDownloadCallLogsList(final List<BluetoothPhoneBookData> list) {
        Log.e("list", "list:" + list.size());
        if (!mActivity.isFinishing()) {
            if (list != null && list.size() > 0) {
                mRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        callDataList.clear();
                        Iterator<BluetoothPhoneBookData> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            callDataList.add(iterator.next());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            } else {

            }
        }

    }

    @Override
    public void onNotifyDownloadCallLogsStart() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                linearSyn.setVisibility(View.GONE);
                linearSynError.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                linearSynIng.setVisibility(View.VISIBLE);
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
                linearSyn.setVisibility(View.GONE);
                linearSynError.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                linearSynIng.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onNotifyDownloadCallLogsFinish() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                linearSyn.setVisibility(View.GONE);
                linearSynError.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                linearSynIng.setVisibility(View.GONE);
                animationDrawable.stop();
            }
        });
    }
}
