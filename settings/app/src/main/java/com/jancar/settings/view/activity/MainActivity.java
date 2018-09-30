package com.jancar.settings.view.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jancar.settings.R;
import com.jancar.settings.adapter.MainHeaderListAdapter;
import com.jancar.settings.lib.SettingManager;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.listener.Contract.MainContractImpl;
import com.jancar.settings.manager.BasePreferenceActivityImpl;
import com.jancar.settings.presenter.MainPresenter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BasePreferenceActivityImpl implements MainContractImpl.View {
    public int anInt;
    public Handler mHadler;
    MainHeaderListAdapter mainHeaderListAdapter;
    public int position;

    @Override
    public int initResid() {
        return R.xml.preference_headers_main;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);
        position = intent.getIntExtra("position", 0);
        if (getListAdapter() != null) {
            onHeaderClick((Header) getListAdapter().getItem(position), position);
            Log.w("s", "onRestart");
            if (mainHeaderListAdapter != null) {

                mainHeaderListAdapter.setPosition(position);
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //  position = getIntent().getIntExtra("position", 0);

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Log.w("", "onPostResume");
        initLocaleLanguage();
        //   SettingManager.getSettingManager(this).setAutoBrightness(true);
        initlLayout();
        initList(savedInstanceState);
        getListView().setVerticalScrollBarEnabled(false);
        getListView().setScrollbarFadingEnabled(false);
        /*linearLayout*/
        if (savedInstanceState != null) {
            anInt = savedInstanceState.getInt("anInt");
            if (anInt != 1) {
                mainHeaderListAdapter.setPosition(position);
                onHeaderClick((Header) getListAdapter().getItem(position), position);
            } else {
                anInt = 0;
            }

        }
        if (position != 0) {
            onHeaderClick((Header) getListAdapter().getItem(position), position);
        }
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit);
    }

    public void initList(Bundle savedInstanceState) {

        position = getIntent().getIntExtra("position", 0);
        //步骤2：获取文件中的值
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
            mCopyHeaders = savedInstanceState.getParcelableArrayList("mCopyHeaders");
        }
        if (mCopyHeaders != null) {
            mainHeaderListAdapter = new MainHeaderListAdapter(this, mCopyHeaders);
            mainHeaderListAdapter.setPosition(position);
            setListAdapter(mainHeaderListAdapter);
            getListView().setOnItemClickListener(mOnClickListener);
        }


    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
            MainActivity.this.position = position;
            mainHeaderListAdapter.setPosition(position);
        }
    };

    public void initlLayout() {
        ViewParent viewParent = getListView().getParent();
        LinearLayout linearLayout = (LinearLayout) viewParent.getParent();
        linearLayout.setBackgroundColor(getColor(R.color.bg_llayout));
        ViewParent viewParents = getListView().getParent();
        LinearLayout linearLayouts = (LinearLayout) viewParents;
        // linearLayouts.setBackgroundColor(getColor(R.color.bg_llayout));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Method noteStateNotSavedMethod;
    private Object fragmentMgr;
    private String[] activityClassName = {"MainActivity", "DisplayFragment"};

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putInt("position", position);
        outState.putParcelableArrayList("mCopyHeaders", (ArrayList<? extends Parcelable>) mCopyHeaders);
        outState.putInt("anInt", anInt);
    }

    @Override
    public IPresenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Context getContext() {
        return this;
    }


    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("按下了back键   onKeyDown()");
            if (mHadler != null) {
                Message message = new Message();
                mHadler.sendMessage(message);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
