package com.jancar.bluetooth.phone.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTPhonebookListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.adapter.ContactAdapter;
import com.jancar.bluetooth.phone.adapter.ContactSearchAdapter;
import com.jancar.bluetooth.phone.contract.ContactContract;
import com.jancar.bluetooth.phone.presenter.ContactPresenter;

import com.jancar.bluetooth.phone.widget.ContactDialog;
import com.jancar.bluetooth.widget.SideBar;
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
 * @date 2018-8-21 16:36:54
 * 联系人界面
 */
public class ContactFragment extends BaseFragment<ContactContract.Presenter, ContactContract.View> implements ContactContract.View, TextWatcher, BTPhonebookListener {

    private static final String TAG = "ContactFragment";
    Unbinder mUnbinder;
    View mRootView;
    @BindView(R.id.contact_list)
    ListView listView;
    @BindView(R.id.contact_search)
    EditText editSearch;
    @BindView(R.id.item_sidebar)
    SideBar sideBar;
    @BindView(R.id.iv_syn_contact_ing)
    ImageView ivSynIng;
    @BindView(R.id.linear_syn_contact)
    LinearLayout linerSyn;
    @BindView(R.id.linear_syn_contact_error)
    LinearLayout linerSynError;
    @BindView(R.id.linear_syn_contact_ing)
    LinearLayout linerSynIng;
    @BindView(R.id.rela_listView)
    RelativeLayout relativeLayout;
    @BindView(R.id.tv_contact_empty)
    TextView tvEmpty;
    protected Activity mActivity;
    private ContactDialog contactDialog;
    private ContactAdapter adapter;
    private ContactSearchAdapter searchAdapter;
    private List<BluetoothPhoneBookData> bookDataList;
    private List<BluetoothPhoneBookData> bookSearchList;
    private AnimationDrawable animationDrawable;
    private String editInputString;
    private int selectPos = -1;
    private boolean hidden = false;
    private boolean isSynContact;

    private Handler mHandler = new ContactFragment.InternalHandler(this);

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
        mUnbinder = ButterKnife.bind(this, mRootView);

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
        getManager().registerBTPhonebookListener(this);
        if (!hidden) {
            showView();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getManager().unRegisterBTPhonebookListener();
    }

    public ContactFragment() {
    }


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
    public ContactContract.Presenter createPresenter() {
        return new ContactPresenter();
    }

    @Override
    public ContactContract.View getUiImplement() {
        return this;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            showView();
        }
    }

    private void initView() {
        editSearch.setCursorVisible(false);
        editInputString = editSearch.getText().toString().trim();
        ivSynIng.setImageResource(R.drawable.loading_animation_big);
        animationDrawable = (AnimationDrawable) ivSynIng.getDrawable();
        showView();
        if (bookDataList == null) {
            bookDataList = new ArrayList<>();
        }
        if (bookSearchList == null) {
            bookSearchList = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new ContactAdapter(getActivity(), bookDataList, R.layout.item_contact_list);
        }
        if (searchAdapter == null) {
            searchAdapter = new ContactSearchAdapter(getActivity());
        }
        if (TextUtils.isEmpty(editInputString)) {
            listView.setAdapter(adapter);
            sideBar.setVisibility(View.VISIBLE);
        } else {
            listView.setAdapter(searchAdapter);
            sideBar.setVisibility(View.INVISIBLE);
        }

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
                String phoneNumber = bookDataList.get(position).getPhoneNumber();
                searchAdapter.setSelectPosition(position);
                if (selectPos == position) {
                    getManager().hfpCall(phoneNumber);
                }
                selectPos = position;
            }
        });
        editSearch.addTextChangedListener(this);
    }

    private void showView() {
        isSynContact = getPresenter().isSynContact();
        if (isSynContact) {
            linerSynIng.setVisibility(View.GONE);
            linerSyn.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            linerSynError.setVisibility(View.GONE);
        } else {
            linerSynIng.setVisibility(View.GONE);
            linerSyn.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            linerSynError.setVisibility(View.GONE);
        }

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
        BluetoothManager bluetoothManager = BluetoothManager.getBluetoothManagerInstance(getUIContext());
        return bluetoothManager;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String number = charSequence.toString();
        getPresenter().getSearchConatct(number);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String string = editable.toString();
        showListContact(string);
    }

    private void showListContact(String string) {
        if (TextUtils.isEmpty(string)) {
            listView.setAdapter(adapter);
            sideBar.setVisibility(View.VISIBLE);
            editSearch.setCursorVisible(false);
        } else {
            listView.setAdapter(searchAdapter);
            sideBar.setVisibility(View.INVISIBLE);
            editSearch.setCursorVisible(true);
        }

    }

    @OnClick({R.id.iv_contanct_synchronous, R.id.iv_syn_contact, R.id.iv_syn_contact_error, R.id.contact_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_contanct_synchronous:
                showDialog();
                break;
            case R.id.iv_syn_contact:
                SynContactAll();
                break;
            case R.id.iv_syn_contact_error:
                SynContactAll();
                break;
            case R.id.contact_search:
                editSearch.setCursorVisible(true);
                break;
        }
    }

    private void showDialog() {
        if (contactDialog == null) {
            contactDialog = new ContactDialog(getActivity(), R.style.AlertDialogCustom);
        }
        contactDialog.setCanelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactDialog.dismiss();
            }
        });
        contactDialog.setSynchOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDialog.dismiss();
                SynContactAll();

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
        Log.d(TAG, "list.size():" + list.size());
        if (!mActivity.isFinishing()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (list != null && list.size() > 0) {
                        relativeLayout.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                        bookSearchList = list;
                        searchAdapter.setBookContact(list);
                        //======= 联系人列表
                        bookDataList = list;
                        adapter.setPhoneBooks(bookDataList);
                        adapter.notifyDataSetChanged();
                    } else {
                        relativeLayout.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }, 100);
        }

    }

    @Override
    public void onNotifyDownloadContactsStart() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                linerSynIng.setVisibility(View.VISIBLE);
                linerSyn.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                linerSynError.setVisibility(View.GONE);
                animationDrawable.start();
            }
        });

    }

    @Override
    public void onNotifyDownloadContactsStop() {

    }

    @Override
    public void onNotifyDownloadContactsError() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                linerSynIng.setVisibility(View.GONE);
                linerSyn.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                linerSynError.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onNotifyDownloadContactsFinish() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                linerSynIng.setVisibility(View.GONE);
                linerSyn.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                linerSynError.setVisibility(View.GONE);
                animationDrawable.stop();
            }
        });

    }

    private void SynContactAll() {
        if (!hidden) {
            getPresenter().getSynContact();
        }
    }
}
