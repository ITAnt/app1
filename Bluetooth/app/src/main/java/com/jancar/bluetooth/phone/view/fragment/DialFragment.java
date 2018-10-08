package com.jancar.bluetooth.phone.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.Listener.BTPhonebookListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.DialNumberAdapter;
import com.jancar.bluetooth.phone.contract.DialContract;
import com.jancar.bluetooth.phone.presenter.DialPresenter;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.IntentUtil;
import com.jancar.bluetooth.phone.util.NumberFormatUtil;
import com.jancar.bluetooth.phone.util.ToastUtil;
import com.jancar.bluetooth.phone.view.MusicActivity;
import com.ui.mvp.view.support.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;

import static com.jancar.bluetooth.phone.util.Constants.BT_CONNECT_IS_NONE;


/**
 * @author Tzq
 * @date 2018-8-21 16:34:02
 * 拨号键盘界面
 */
public class DialFragment extends BaseFragment<DialContract.Presenter, DialContract.View> implements DialContract.View, BTPhonebookListener, BTConnectStatusListener {
    private static final String TAG = "DialFragment";
    private Unbinder unbinder;
    private View mRootView;
    private Activity mActivity;
    @BindView(R.id.dial_tv_number)
    TextView tvInput;
    @BindView(R.id.recy_dial_list)
    ListView listView;
    @BindView(R.id.tv_dial_syn_contact)
    TextView tvSynContact;
    private BluetoothManager bluetoothManager;
    private DialNumberAdapter adapter;
    private List<BluetoothPhoneBookData> bookDataList;
    private boolean hidden = false;
    private static int DialFragmentType = 1;

    //点击的数字
    private volatile String mStrKeyNum = "";

    private Handler mHandler = new InternalHandler(this);

    private static class InternalHandler extends Handler {
        private WeakReference<Fragment> weakRefActivity;

        public InternalHandler(Fragment fragment) {
            weakRefActivity = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment = weakRefActivity.get();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BT_CONNECT_IS_NONE) {
                tvSynContact.setVisibility(View.VISIBLE);
                tvSynContact.setText(R.string.tv_bt_connect_is_none);
                listView.setVisibility(View.GONE);

            } else if (msg.what == Constants.BT_CONNECT_IS_CONNECTED) {
                synContactView();

            } else if (msg.what == Constants.BT_CONNECT_IS_CLOSE) {
                tvSynContact.setVisibility(View.VISIBLE);
                tvSynContact.setText(R.string.tv_bt_connect_is_none);
                listView.setVisibility(View.GONE);
            } else if (msg.what == Constants.PHONEBOOK_DATA_REFRESH) {
                final String number = (String) msg.obj;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        getPresenter().getDialContactList((number), DialFragmentType);
                    }
                }.start();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            mRootView = inflater.inflate(R.layout.fragment_dial, container, false);
        }
        unbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        bluetoothManager.registerBTPhonebookListener(this);
        bluetoothManager.setBTConnectStatusListener(this);
        if (!hidden) {
            isConneView();
            if (mStrKeyNum != null && listView.getVisibility() == View.VISIBLE) {
                Message message = new Message();
                message.what = Constants.PHONEBOOK_DATA_REFRESH;
                message.obj = mStrKeyNum;
                handler.removeMessages(Constants.PHONEBOOK_DATA_REFRESH);
                handler.sendMessageDelayed(message, 100);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mStrKeyNum = null;
        tvInput.setText(mStrKeyNum);
    }

    @Override
    public void onStop() {
        super.onStop();
        mStrKeyNum = null;
        tvInput.setText(mStrKeyNum);
        adapter.setStrKeyNum("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        handler.removeCallbacksAndMessages(null);
        bluetoothManager.unRegisterBTPhonebookListener();
        bluetoothManager.setBTConnectStatusListener(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        if (!hidden) {
            bluetoothManager.registerBTPhonebookListener(this);
            bluetoothManager.setBTConnectStatusListener(this);
            isConneView();
            if (mStrKeyNum != null && listView.getVisibility() == View.VISIBLE) {
                Message message = new Message();
                message.what = Constants.PHONEBOOK_DATA_REFRESH;
                message.obj = mStrKeyNum;
                handler.removeMessages(Constants.PHONEBOOK_DATA_REFRESH);
                handler.sendMessageDelayed(message, 100);
            }
        } else {
            bluetoothManager.unRegisterBTPhonebookListener();
            handler.removeMessages(Constants.PHONEBOOK_DATA_REFRESH);
            bluetoothManager.setBTConnectStatusListener(null);
        }
    }

    public DialFragment() {
    }

    @Override
    public DialContract.Presenter createPresenter() {
        return new DialPresenter();
    }

    @Override
    public DialContract.View getUiImplement() {
        return this;
    }

    private void init() {
        if (adapter == null) {
            if (bookDataList == null) {
                bookDataList = new ArrayList<>();
            }
            adapter = new DialNumberAdapter(getActivity());
            listView.setAdapter(adapter);
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String phoneNumber = bookDataList.get(position).getPhoneNumber();
                    if (phoneNumber.equals(mStrKeyNum)) {
                        BluetoothManager.getBluetoothManagerInstance(mActivity).hfpCall(phoneNumber);
                    } else {
                        mStrKeyNum = phoneNumber;
                        tvInput.setText(NumberFormatUtil.getNumber(mStrKeyNum));
                        adapter.setStrKeyNum(mStrKeyNum);
                    }
                }
            });
        }
        tvInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isConneView();
                final String number = charSequence.toString();
                Log.d(TAG, number);
                if (listView.getVisibility() == View.VISIBLE) {
                    Message message = new Message();
                    message.what = Constants.PHONEBOOK_DATA_REFRESH;
                    message.obj = number;
                    handler.removeMessages(Constants.PHONEBOOK_DATA_REFRESH);
                    handler.sendMessageDelayed(message, 100);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                if (TextUtils.isEmpty(string)) {
                    mStrKeyNum = null;
                    bookDataList.clear();
                    adapter.setStrKeyNum("");
                    listView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void isConneView() {
        if (getManager().isConnect()) {
            synContactView();
        } else {
            tvSynContact.setVisibility(View.VISIBLE);
            tvSynContact.setText(R.string.tv_bt_connect_is_none);
            listView.setVisibility(View.GONE);
        }
    }

    private void synContactView() {
        if (isDownLoading()) {
            tvSynContact.setVisibility(View.VISIBLE);
            tvSynContact.setText(R.string.tv_dial_contact);
            listView.setVisibility(View.GONE);
        } else {
            tvSynContact.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mStrKeyNum)) {
                listView.setVisibility(View.VISIBLE);
            } else {
                listView.setVisibility(View.GONE);
            }

        }
    }

    private boolean isSynContact() {
        boolean synContact = getPresenter().isSynContact();
        return synContact;
    }

    private boolean isDownLoading() {
        boolean downLoading = getPresenter().isDownLoading();
        return downLoading;

    }

    @Override
    public Context getUIContext() {
        return mActivity;
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public BluetoothManager getManager() {
        return bluetoothManager;
    }

    @OnClick({R.id.item_dial_number_1, R.id.item_dial_number_2, R.id.item_dial_number_3, R.id.item_dial_number_4,
            R.id.item_dial_number_5, R.id.item_dial_number_6, R.id.item_dial_number_7, R.id.item_dial_number_8,
            R.id.item_dial_number_9, R.id.item_dial_number_10, R.id.item_dial_number_11, R.id.item_dial_number_12,
            R.id.item_dial_number_call, R.id.dial_iv_del_number})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_dial_number_1:
                getStrKeyNum("1");
                break;
            case R.id.item_dial_number_2:
                getStrKeyNum("2");
                break;
            case R.id.item_dial_number_3:
                getStrKeyNum("3");
                break;
            case R.id.item_dial_number_4:
                getStrKeyNum("4");
                break;
            case R.id.item_dial_number_5:
                getStrKeyNum("5");
                break;
            case R.id.item_dial_number_6:
                getStrKeyNum("6");
                break;
            case R.id.item_dial_number_7:
                getStrKeyNum("7");
                break;
            case R.id.item_dial_number_8:
                getStrKeyNum("8");
                break;
            case R.id.item_dial_number_9:
                getStrKeyNum("9");
                break;
            case R.id.item_dial_number_10:
                getStrKeyNum("*");
                break;
            case R.id.item_dial_number_11:
                getStrKeyNum("0");
                break;
            case R.id.item_dial_number_12:
                getStrKeyNum("#");
                break;
            case R.id.item_dial_number_call:
                if (getManager().isConnect()) {
                    if (!TextUtils.isEmpty(mStrKeyNum)) {
                        BluetoothManager.getBluetoothManagerInstance(getUIContext()).hfpCall(mStrKeyNum);
                    } else {
                        ToastUtil.ShowToast(mActivity, mActivity.getString(R.string.tv_call_number_empty));
                    }
                } else {
                    ToastUtil.ShowToast(mActivity, mActivity.getString(R.string.tv_bt_connect_is_none));
                }
                break;
            case R.id.dial_iv_del_number:
                if (mStrKeyNum != null) {
                    mStrKeyNum = getPresenter().delNumber(mStrKeyNum);
                }
                tvInput.setText(mStrKeyNum);
                break;
        }
    }

    /**
     * 获取点击的数值
     *
     * @param keyNum 数值
     */

    private void getStrKeyNum(String keyNum) {
        if (mStrKeyNum == null) {
            mStrKeyNum = keyNum;
        } else {
            mStrKeyNum += keyNum;
        }
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                tvInput.setText(NumberFormatUtil.getNumber(mStrKeyNum));
            }
        });

    }

    @OnLongClick(R.id.dial_iv_del_number)
    public boolean onDellongClick() {
        mStrKeyNum = getPresenter().delLongNumber(mStrKeyNum);
        tvInput.setText(mStrKeyNum);
        return true;
    }

    @OnLongClick(R.id.item_dial_number_11)
    public boolean onLongClick1() {
        getStrKeyNum("+");
        return true;
    }

    @Override
    public void onNotifyDownloadContactsIndex(int i) {

    }

    @Override
    public void onNotifyDownloadContactsCount(int i) {

    }

    @Override
    public void onNotifyDownloadContactsList(final List<BluetoothPhoneBookData> list) {

    }

    @Override
    public void onNotifySeachContactsList(final List<BluetoothPhoneBookData> list, int i) {
        Log.d("Dial", "list.size():" + list.size());
        if (i == DialFragmentType) {
            this.bookDataList = list;
            if (!mActivity.isFinishing()) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setBookDataList(bookDataList);
                    }
                });
            }
        }
    }

    @Override
    public void onNotifyDownloadContactsStart() {

    }

    @Override
    public void onNotifyDownloadContactsStop() {

    }

    @Override
    public void onNotifyDownloadContactsError() {

    }

    @Override
    public void onNotifyDownloadContactsFinish() {

    }

    @Override
    public void onNotifyBTConnectStateChange(byte b) {
        Message msg = handler.obtainMessage();
        msg.what = b;
        handler.sendMessage(msg);
    }

}
