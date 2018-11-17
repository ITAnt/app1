package com.jancar.bluetooth.phone.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTCallLogListener;
import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.Listener.BTPhonebookListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.DialNumberAdapter;
import com.jancar.bluetooth.phone.contract.DialContract;
import com.jancar.bluetooth.phone.entity.BtnNumberEntity;
import com.jancar.bluetooth.phone.entity.Event;
import com.jancar.bluetooth.phone.presenter.DialPresenter;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.NumberFormatUtil;
import com.jancar.bluetooth.phone.util.ThreadUtils;
import com.ui.mvp.view.support.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.jancar.bluetooth.phone.util.Constants.BT_CONNECT_IS_NONE;


/**
 * @author Tzq
 * @date 2018-8-21 16:34:02
 * 拨号键盘界面
 */
public class DialFragment extends BaseFragment<DialContract.Presenter, DialContract.View> implements DialContract.View, BTPhonebookListener, BTConnectStatusListener, View.OnClickListener, BTCallLogListener {
    private static final String TAG = "DialFragment";
    private View mRootView;
    private Activity mActivity;
    TextView tvInput;
    ListView listView;
    TextView tvSynContact;
    ImageView number1, number2, number3, number4, number5, number6, number7, number8, number9, number10, number11, number12, numberCall, numberDel;
    private BluetoothManager bluetoothManager;
    private DialNumberAdapter adapter;
    private List<BluetoothPhoneBookData> bookDataList, callLogsList;
    private boolean hidden = false;
    private static int DialFragmentType = 1;
    String FirstLog;


    //点击的数字
    private volatile String mStrKeyNum = "";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CONTACT_BT_CONNECT:
                    //蓝牙状态
                    byte obj = (byte) msg.obj;
                    if (obj == BT_CONNECT_IS_NONE) {
                        tvSynContact.setVisibility(View.VISIBLE);
                        tvSynContact.setText(R.string.tv_bt_connect_is_none);
                        listView.setVisibility(View.GONE);

                    } else if (obj == Constants.BT_CONNECT_IS_CONNECTED) {
                        synContactView();
                        EventBus.getDefault().post(new Event(true));

                    } else if (obj == Constants.BT_CONNECT_IS_CLOSE) {
                        tvSynContact.setVisibility(View.VISIBLE);
                        tvSynContact.setText(R.string.tv_bt_connect_is_none);
                        listView.setVisibility(View.GONE);
                    }
                    break;
                case Constants.PHONEBOOK_DATA_REFRESH:
                    final String number = (String) msg.obj;
                    ThreadUtils.execute(new Runnable() {
                        @Override
                        public void run() {
                            getPresenter().getDialContactList((number), DialFragmentType);
                        }
                    });
                    break;
                case Constants.PHONEBOOK_SEACH_LIST:
                    adapter.setBookDataList(bookDataList);
                    break;
                case Constants.Dial_UPDATE_TEXT:
                    tvInput.setText(NumberFormatUtil.getNumber(mStrKeyNum));
                    break;
                case Constants.CONTACT_CALL_LOGS_FIRST:
                    for (int i = 0; i < callLogsList.size(); i++) {
                        FirstLog = callLogsList.get(0).getPhoneNumber();
                    }
                    break;
                case Constants.DIAL_CONTACT_FINISH:
                    tvSynContact.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    break;
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
        Log.e(TAG, "onCreate==");

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
        findView(mRootView);
        return mRootView;
    }

    private void findView(View mRootView) {
        tvInput = mRootView.findViewById(R.id.dial_tv_number);
        listView = mRootView.findViewById(R.id.recy_dial_list);
        tvSynContact = mRootView.findViewById(R.id.tv_dial_syn_contact);
        number1 = mRootView.findViewById(R.id.item_dial_number_1);
        number2 = mRootView.findViewById(R.id.item_dial_number_2);
        number3 = mRootView.findViewById(R.id.item_dial_number_3);
        number4 = mRootView.findViewById(R.id.item_dial_number_4);
        number5 = mRootView.findViewById(R.id.item_dial_number_5);
        number6 = mRootView.findViewById(R.id.item_dial_number_6);
        number7 = mRootView.findViewById(R.id.item_dial_number_7);
        number8 = mRootView.findViewById(R.id.item_dial_number_8);
        number9 = mRootView.findViewById(R.id.item_dial_number_9);
        number10 = mRootView.findViewById(R.id.item_dial_number_10);
        number11 = mRootView.findViewById(R.id.item_dial_number_11);
        number12 = mRootView.findViewById(R.id.item_dial_number_12);
        numberCall = mRootView.findViewById(R.id.item_dial_number_call);
        numberDel = mRootView.findViewById(R.id.dial_iv_del_number);
        number1.setOnClickListener(this);
        number2.setOnClickListener(this);
        number3.setOnClickListener(this);
        number4.setOnClickListener(this);
        number5.setOnClickListener(this);
        number6.setOnClickListener(this);
        number7.setOnClickListener(this);
        number8.setOnClickListener(this);
        number9.setOnClickListener(this);
        number10.setOnClickListener(this);
        number11.setOnClickListener(this);
        number12.setOnClickListener(this);
        numberCall.setOnClickListener(this);
        numberDel.setOnClickListener(this);
        number11.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getStrKeyNum("+");
                return true;
            }
        });

        numberDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mStrKeyNum = getPresenter().delLongNumber(mStrKeyNum);
                tvInput.setText(mStrKeyNum);
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated==");
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume==");
        bluetoothManager.registerBTPhonebookListener(this);
        bluetoothManager.setBTConnectStatusListener(this);
        bluetoothManager.registerBTCallLogListener(this);

        if (!hidden) {
            getBTCallLog();
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
        Log.e(TAG, "onPause==");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop==");
        mStrKeyNum = null;
        tvInput.setText(mStrKeyNum);
        adapter.setStrKeyNum("");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy==");
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        bluetoothManager.unRegisterBTCallLogListener();
        bluetoothManager.setBTConnectStatusListener(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            if (bluetoothManager == null) {
                bluetoothManager = BluetoothManager.getBluetoothManagerInstance(mActivity);
            }
            bluetoothManager.registerBTCallLogListener(this);
            bluetoothManager.registerBTPhonebookListener(this);
            bluetoothManager.setBTConnectStatusListener(this);
            isConneView();
            getBTCallLog();
            if (mStrKeyNum != null && listView.getVisibility() == View.VISIBLE) {
                Message message = new Message();
                message.what = Constants.PHONEBOOK_DATA_REFRESH;
                message.obj = mStrKeyNum;
                handler.removeMessages(Constants.PHONEBOOK_DATA_REFRESH);
                handler.sendMessageDelayed(message, 100);
            }
        } else {
            if (bluetoothManager != null) {
                bluetoothManager.unRegisterBTCallLogListener();
                bluetoothManager.setBTConnectStatusListener(null);
            }
            handler.removeMessages(Constants.PHONEBOOK_DATA_REFRESH);
        }
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
    public DialContract.Presenter createPresenter() {
        return new DialPresenter();
    }

    @Override
    public DialContract.View getUiImplement() {
        return this;
    }

    private void init() {
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        if (adapter == null) {
            if (bookDataList == null) {
                bookDataList = new ArrayList<>();
            }
            adapter = new DialNumberAdapter(mActivity);
            listView.setAdapter(adapter);
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (bookDataList != null && bookDataList.size() > 0) {
                        String phoneNumber = bookDataList.get(position).getPhoneNumber();
                        if (phoneNumber.equals(mStrKeyNum)) {
                            bluetoothManager.hfpCall(phoneNumber);
                        } else {
                            mStrKeyNum = phoneNumber;
                            tvInput.setText(NumberFormatUtil.getNumber(mStrKeyNum));
                            adapter.setStrKeyNum(mStrKeyNum);
                        }
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
//        mHandler.post(runnable);
    }

    @Override
    public BluetoothManager getManager() {
        return bluetoothManager;
    }

    private void cleanCallNumber() {
        mStrKeyNum = null;
        tvInput.setText(mStrKeyNum);
        adapter.setStrKeyNum("");

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
        handler.sendEmptyMessage(Constants.Dial_UPDATE_TEXT);

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
        Log.d("DialFragment", "list.size():" + list.size());
        if (i == DialFragmentType) {
            this.bookDataList = new ArrayList<>(list);
            handler.sendEmptyMessage(Constants.PHONEBOOK_SEACH_LIST);
        }
    }

    @Override
    public void onNotifyDownloadContactsStart() {

    }

    @Override
    public void onNotifyDownloadContactsStop() {
        handler.sendEmptyMessage(Constants.DIAL_CONTACT_FINISH);
    }

    @Override
    public void onNotifyDownloadContactsError() {
        handler.sendEmptyMessage(Constants.DIAL_CONTACT_FINISH);
    }

    @Override
    public void onNotifyDownloadContactsFinish() {
        Log.d(TAG, "onNotifyDownloadContactsFinish==");
        handler.sendEmptyMessage(Constants.DIAL_CONTACT_FINISH);

    }

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
                CallNumber();
                break;
            case R.id.dial_iv_del_number:
                if (mStrKeyNum != null) {
                    mStrKeyNum = getPresenter().delNumber(mStrKeyNum);
                }
                tvInput.setText(mStrKeyNum);
                break;
        }

    }

    private void CallNumber() {
        if (getManager().isConnect()) {
            if (!TextUtils.isEmpty(mStrKeyNum)) {
                bluetoothManager.hfpCall(mStrKeyNum);
                cleanCallNumber();
            } else {
                if (!isDownLoading()) {
                    mStrKeyNum = FirstLog;
                    tvInput.setText(mStrKeyNum);
                } else {
                    //正在下载
                }
            }
        } else {
        }
    }

    @Override
    public void onNotifyDownloadCallLogsIndex(int i) {

    }

    @Override
    public void onNotifyDownloadCallLogsCount(int i) {

    }

    @Override
    public void onNotifyDownloadCallLogsList(List<BluetoothPhoneBookData> list) {
        this.callLogsList = new ArrayList<>(list);
        handler.sendEmptyMessage(Constants.CONTACT_CALL_LOGS_FIRST);

    }

    @Override
    public void onNotifyDownloadCallLogsStart() {

    }

    @Override
    public void onNotifyDownloadCallLogsStop() {

    }

    @Override
    public void onNotifyDownloadCallLogsError() {

    }

    @Override
    public void onNotifyDownloadCallLogsFinish() {

    }

    /**
     * 事件响应方法
     * 接收消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBtnNum(BtnNumberEntity event) {
        if (event.isBtnKey()) {
            Log.e(TAG, "onEvent===" + mStrKeyNum);
            CallNumber();
        }
    }
}
