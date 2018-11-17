package com.jancar.bluetooth.phone.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.Listener.BTPhonebookListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.ContactAdapter;
import com.jancar.bluetooth.phone.adapter.ContactSearchAdapter;
import com.jancar.bluetooth.phone.contract.ContactContract;
import com.jancar.bluetooth.phone.entity.Event;
import com.jancar.bluetooth.phone.presenter.ContactPresenter;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.FlyLog;
import com.jancar.bluetooth.phone.util.ThreadUtils;
import com.jancar.bluetooth.phone.util.ToastUtil;
import com.jancar.bluetooth.phone.widget.AVLoadingIndicatorView;
import com.jancar.bluetooth.phone.widget.ContactDialog;
import com.jancar.bluetooth.widget.SideBar;
import com.ui.mvp.view.support.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tzq
 * @date 2018-8-21 16:36:54
 * 联系人界面
 */
public class ContactFragment extends BaseFragment<ContactContract.Presenter, ContactContract.View> implements ContactContract.View, TextWatcher, BTPhonebookListener, BTConnectStatusListener, OnClickListener {

    private static final String TAG = "ContactFragment";
    View mRootView;
    ListView listView;
    EditText editSearch;
    SideBar sideBar;
    AVLoadingIndicatorView ivSynIng;
    ImageView ivSynContact;
    LinearLayout linerSyn;
    TextView tvSynContact;
    RelativeLayout relativeLayout;
    ImageView downImage;

    protected Activity mActivity;
    private ContactDialog contactDialog;
    private ContactAdapter adapter;
    private ContactSearchAdapter searchAdapter;
    private List<BluetoothPhoneBookData> bookDataList;
    private List<BluetoothPhoneBookData> bookSearchList;
    private String editInputString;
    private int selectPos = -1;
    private boolean hidden = false;
    private static int ContactFragmentType = 2;
    BluetoothManager bluetoothManager;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CONTACT_BT_CONNECT:
                    //蓝牙状态
                    byte obj = (byte) msg.obj;
                    if (obj == Constants.BT_CONNECT_IS_NONE) {
                        ShowSynText();
                        tvSynContact.setText(R.string.tv_bt_connect_is_none);
                        relativeLayout.setVisibility(View.GONE);

                    } else if (obj == Constants.BT_CONNECT_IS_CONNECTED) {
                        SynContactView();
                        getBTContacts();
                        EventBus.getDefault().post(new Event(true));

                    } else if (obj == Constants.BT_CONNECT_IS_CLOSE) {
                        ShowSynText();
                        tvSynContact.setText(R.string.tv_bt_connect_is_none);
                        relativeLayout.setVisibility(View.GONE);
                    }
                    break;
                case Constants.CONTACT_DATA_REFRESH:
                    //搜索联系人
                    final String number = (String) msg.obj;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            getPresenter().getSearchConatct(number, ContactFragmentType);
                        }
                    }.start();
                    break;
                case Constants.CONTACT_UPDATA_REFRESH:
                    //联系人
                    adapter.setPhoneBooks(bookDataList);
                    adapter.notifyDataSetChanged();
                    break;
                case Constants.CONTACT_SEARCH_REFRESH:
                    //更新搜索联系人
                    searchAdapter.setBookContact(bookSearchList);
                    break;
                case Constants.CONTACT_SEARCH_START:
                    //开始搜索
                    linerSyn.setVisibility(View.VISIBLE);
                    tvSynContact.setText(R.string.tv_syning_contact);
                    ivSynContact.setVisibility(View.GONE);
                    ivSynIng.show();
                    relativeLayout.setVisibility(View.GONE);
                    break;
                case Constants.CONTACT_SEARCH_END:
                    //结束搜索
                    linerSyn.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    break;
                case Constants.CONTACT_SEARCH_CHANGE:
                    //搜索text改变
                    String s = (String) msg.obj;
                    if (TextUtils.isEmpty(s)) {
                        listView.setAdapter(adapter);
                        sideBar.setVisibility(View.VISIBLE);
                        editSearch.setCursorVisible(false);
                    } else {
                        listView.setAdapter(searchAdapter);
                        sideBar.setVisibility(View.INVISIBLE);
                        editSearch.setCursorVisible(true);
                    }
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
            mRootView = inflater.inflate(R.layout.fragment_contact, container, false);
        }
        findView(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            getBTContacts();
            isConneView();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            getBTContacts();
            isConneView();
            adapter.setNormalPosition();
            searchAdapter.setNormalPosition();
        } else {
            if (bluetoothManager != null) {
                bluetoothManager.setBTConnectStatusListener(null);
//                bluetoothManager.unRegisterBTPhonebookListener();
            }

        }
    }

    private void getBTContacts() {
        if (bluetoothManager == null) {
            bluetoothManager = BluetoothManager.getBluetoothManagerInstance(mActivity);
        }
        bluetoothManager.registerBTPhonebookListener(this);
        bluetoothManager.setBTConnectStatusListener(this);
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                bluetoothManager.getBTContacts();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.setNormalPosition();
        searchAdapter.setNormalPosition();
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
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
//        bluetoothManager.unRegisterBTPhonebookListener();
    }


    @Override
    public ContactContract.Presenter createPresenter() {
        return new ContactPresenter();
    }

    @Override
    public ContactContract.View getUiImplement() {
        return this;
    }

    private void initView() {
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        editSearch.setCursorVisible(false);
        editInputString = editSearch.getText().toString().trim();
        if (bookDataList == null) {
            bookDataList = new ArrayList<>();
        }
        if (bookSearchList == null) {
            bookSearchList = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new ContactAdapter(mActivity, bookDataList, R.layout.item_contact_list);
        }
        if (searchAdapter == null) {
            searchAdapter = new ContactSearchAdapter(mActivity);
        }
        if (TextUtils.isEmpty(editInputString)) {
            listView.setAdapter(adapter);
            sideBar.setVisibility(View.VISIBLE);
        } else {
            listView.setAdapter(searchAdapter);
            sideBar.setVisibility(View.INVISIBLE);
        }
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 获取手指按下的字母所在的块索引
                int section = s.toCharArray()[0];
                // 根据块索引获取该字母首次在ListView中出现的位置
                int pos = adapter.getPositionForSection(section);
                // // 定位ListView到按下字母首次出现的位置
                listView.setSelection(pos);
            }
        });
        //联系人列表
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (bookSearchList != null && bookSearchList.size() > 0) {
                    String phoneNumber = bookSearchList.get(position).getPhoneNumber();
                    searchAdapter.setSelectPosition(position);
                    if (selectPos == position) {
                        bluetoothManager.hfpCall(phoneNumber);
                    }
                    selectPos = position;
                }
            }
        });
        editSearch.addTextChangedListener(this);
    }

    private void findView(View mRootView) {
        listView = mRootView.findViewById(R.id.contact_list);
        editSearch = mRootView.findViewById(R.id.contact_search);
        sideBar = mRootView.findViewById(R.id.item_sidebar);
        ivSynIng = mRootView.findViewById(R.id.iv_syn_contact_ing);
        ivSynContact = mRootView.findViewById(R.id.iv_syn_contact);
        linerSyn = mRootView.findViewById(R.id.linear_syn_contact);
        tvSynContact = mRootView.findViewById(R.id.tv_contact_syn);
        relativeLayout = mRootView.findViewById(R.id.rela_listView);
        downImage = mRootView.findViewById(R.id.iv_contanct_synchronous);
        downImage.setOnClickListener(this);
        ivSynContact.setOnClickListener(this);

    }

    private void isConneView() {
        FlyLog.d(TAG, "isConneView===");
        if (isBluConn()) {
            SynContactView();
        } else {
            linerSyn.setVisibility(View.VISIBLE);
            tvSynContact.setText(R.string.tv_bt_connect_is_none);
            ivSynContact.setVisibility(View.GONE);
            ivSynIng.hide();
            relativeLayout.setVisibility(View.GONE);
        }
    }

    private void SynContactView() {
        if (isDownLoding()) {
            linerSyn.setVisibility(View.VISIBLE);
            tvSynContact.setText(R.string.tv_syning_contact);
            ivSynContact.setVisibility(View.GONE);
            ivSynIng.show();
            relativeLayout.setVisibility(View.GONE);
        } else {
            linerSyn.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    private boolean isBluConn() {
        boolean isBluConn = bluetoothManager.isConnect();
        return isBluConn;
    }

    private boolean isDownLoding() {
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


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String number = charSequence.toString();
        Message message = new Message();
        message.what = Constants.CONTACT_DATA_REFRESH;
        message.obj = number;
        handler.removeMessages(Constants.CONTACT_DATA_REFRESH);
        handler.sendMessageDelayed(message, 100);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String string = editable.toString();
        Message message = new Message();
        message.what = Constants.CONTACT_SEARCH_CHANGE;
        message.obj = string;
        handler.sendMessage(message);
    }

    private void showDialog() {
        if (contactDialog == null) {
            contactDialog = new ContactDialog(mActivity, R.style.AlertDialogCustom);
        }
        contactDialog.setCanelOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contactDialog.dismiss();
            }
        });
        contactDialog.setSynchOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDialog.dismiss();
                getPresenter().getSynContact();
            }
        });
        contactDialog.show();
    }

    @Override
    public void onNotifyDownloadContactsIndex(int i) {

    }

    @Override
    public void onNotifyDownloadContactsCount(int i) {


    }

    @Override
    public void onNotifyDownloadContactsList(final List<BluetoothPhoneBookData> list) {
        Log.e(TAG, "DownloadContactsList===" + list.size());
        this.bookDataList = new ArrayList<>(list);
        handler.removeMessages(Constants.CONTACT_UPDATA_REFRESH);
        handler.sendEmptyMessageDelayed(Constants.CONTACT_UPDATA_REFRESH, 50);

    }

    @Override
    public void onNotifySeachContactsList(List<BluetoothPhoneBookData> list, final int i) {
        Log.e(TAG, "SeachContactsList==" + list);
        if (i == ContactFragmentType) {
            this.bookSearchList = new ArrayList<>(list);
            handler.removeMessages(Constants.CONTACT_SEARCH_REFRESH);
            handler.sendEmptyMessageDelayed(Constants.CONTACT_SEARCH_REFRESH, 100);
        }
    }

    private void ShowSynText() {
        FlyLog.e(TAG, "ShowSynText===");
        linerSyn.setVisibility(View.VISIBLE);
        ivSynContact.setVisibility(View.GONE);
        ivSynIng.hide();
    }

    @Override
    public void onNotifyDownloadContactsStart() {
        Log.e(TAG, "onNotifyDownloadContactsStart==");
        handler.sendEmptyMessage(Constants.CONTACT_SEARCH_START);

    }

    @Override
    public void onNotifyDownloadContactsStop() {

    }

    @Override
    public void onNotifyDownloadContactsError() {
//        handler.sendEmptyMessage(Constants.CONTACT_SEARCH_END);
        handler.removeMessages(Constants.CONTACT_SEARCH_END);
        handler.sendEmptyMessageDelayed(Constants.CONTACT_SEARCH_END, 100);
    }

    @Override
    public void onNotifyDownloadContactsFinish() {
        Log.e(TAG, "onNotifyDownloadContactsFinish===");
//        handler.sendEmptyMessage(Constants.CONTACT_SEARCH_END);
        handler.removeMessages(Constants.CONTACT_SEARCH_END);
        handler.sendEmptyMessageDelayed(Constants.CONTACT_SEARCH_END, 100);
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
            case R.id.iv_contanct_synchronous:
                if (isBluConn()) {
                    if (!isDownLoding()) {
                        showDialog();
                    } else {
                        ToastUtil.ShowTipText(mActivity, mActivity.getString(R.string.tv_tip_down));
                    }

                } else {
                    ToastUtil.ShowTipText(mActivity, mActivity.getString(R.string.tv_bt_connect_is_none));
                }
                break;
            case R.id.iv_syn_contact:
                Log.e(TAG, "onClick==contact==");
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        getPresenter().getSynContact();
                    }
                });

                break;

            case R.id.contact_search:
                editSearch.setCursorVisible(true);
                break;
        }

    }
}
