package com.jancar.settings.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    public int initView(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        SettingManager.getSettingManager(this).setAutoBrightness(true);
        initlLayout();
        initList( savedInstanceState);
        /*linearLayout*/
        if (savedInstanceState != null) {
            anInt = savedInstanceState.getInt("anInt");
            if (anInt != 1) {
                mainHeaderListAdapter.setPosition(position);
                onHeaderClick((Header) getListAdapter().getItem(position), position);
            }else {
                anInt=0;
            }

        }
        if (position!=0){
            onHeaderClick((Header) getListAdapter().getItem(position), position);
        }

    }

    public void initList(Bundle savedInstanceState) {
        position=getIntent().getIntExtra("position",0);
        if (savedInstanceState != null) {
            position=savedInstanceState.getInt("position");
            mCopyHeaders=savedInstanceState.getParcelableArrayList("mCopyHeaders");
        }
        if (mCopyHeaders != null) {
            mainHeaderListAdapter = new MainHeaderListAdapter(this, mCopyHeaders);
            mainHeaderListAdapter.setPosition(position);
            setListAdapter(mainHeaderListAdapter);
            getListView().setOnItemClickListener(mOnClickListener);
        }


    }
    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id)
        {
            onListItemClick((ListView)parent, v, position, id);
            MainActivity. this.position=position;
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
        invokeFragmentManagerNoteStateNotSaved();
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putInt("position", position);
        outState.putParcelableArrayList("mCopyHeaders", (ArrayList<? extends Parcelable>) mCopyHeaders);
        outState.putInt("anInt", anInt);
    }
    private void invokeFragmentManagerNoteStateNotSaved() {
        //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        try {
            if (noteStateNotSavedMethod != null && fragmentMgr != null) {
                noteStateNotSavedMethod.invoke(fragmentMgr);
                return;
            }
            Class cls = getClass();
            do {
                cls = cls.getSuperclass();
            } while (!(activityClassName[0].equals(cls.getSimpleName())
                    || activityClassName[1].equals(cls.getSimpleName())));

            Field fragmentMgrField = prepareField(cls, "mFragments");
            if (fragmentMgrField != null) {
                fragmentMgr = fragmentMgrField.get(this);
                noteStateNotSavedMethod = getDeclaredMethod(fragmentMgr, "noteStateNotSaved");
                if (noteStateNotSavedMethod != null) {
                    noteStateNotSavedMethod.invoke(fragmentMgr);
                }
            }

        } catch (Exception ex) {
        }
    }

    private Field prepareField(Class<?> c, String fieldName) throws NoSuchFieldException {
        while (c != null) {
            try {
                Field f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } finally {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    private Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
            }
        }
        return null;
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

    @Override
    public void showLoading() {

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


    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void launchActivity(@NonNull Intent intent) {

    }

    @Override
    public void killMyself() {

    }
}
