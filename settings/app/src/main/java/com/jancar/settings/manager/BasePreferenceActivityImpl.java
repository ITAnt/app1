/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jancar.settings.manager;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.jancar.settings.listener.ActivityImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.presenter.NavigationPresenter;
import com.jancar.settings.view.fragment.BluetoothFragment;
import com.jancar.settings.view.fragment.CacheFragment;
import com.jancar.settings.view.fragment.DisplayFragment;
import com.jancar.settings.view.fragment.NavigationFragment;
import com.jancar.settings.view.fragment.OnFragment;
import com.jancar.settings.view.fragment.SoundFragment;
import com.jancar.settings.view.fragment.SystemFragment;
import com.jancar.settings.view.fragment.TimeFragment;
import com.jancar.settings.view.fragment.VehicleFragment;
import com.jancar.settings.view.fragment.WifiFragment;
import com.jancar.settings.view.fragment.WifiSpotFragment;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ================================================
 * PreferenceActivity基类
 * ================================================
 */
public abstract class BasePreferenceActivityImpl<P extends IPresenter> extends PreferenceActivity implements ActivityImpl {
    protected final String TAG = this.getClass().getSimpleName();
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null
    /**
     * 过滤非法的fragment
     */
    public List<Header> mCopyHeaders;
    private static final String[] ENTRY_FRAGMENTS = {
            DisplayFragment.class.getName(), WifiSpotFragment.class.getName(),
            WifiFragment.class.getName(), TimeFragment.class.getName(),
            CacheFragment.class.getName(), SoundFragment.class.getName(),
            NavigationFragment.class.getName(),VehicleFragment.class.getName(),
            OnFragment.class.getName(),SystemFragment.class.getName(), BluetoothFragment.class.getName()};

    @Override
    public void onBuildHeaders(List<Header> target) {
        if (mPresenter == null) {
            mPresenter = (P) initPresenter();
        }
        int layoutResID = initResid();
        //如果initView返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
        if (layoutResID != 0) {
            mCopyHeaders = target;
            loadHeadersFromResource(layoutResID, target);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initLocaleLanguage();
        super.onCreate(savedInstanceState);
        initData(savedInstanceState);
    }
    public void initLocaleLanguage() {
        Resources resources = getResources();                    // 获得res资源对象
        Configuration config = resources.getConfiguration();     // 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = Locale.getDefault();
        resources.updateConfiguration(config, dm);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        for (int i = 0; i < ENTRY_FRAGMENTS.length; i++) {
            if (ENTRY_FRAGMENTS[i].equals(fragmentName))
                return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }


    /**
     * 这个Activity是否会使用Fragment,框架会根据这个属性判断是否注册{@link android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks}
     * 如果返回false,那意味着这个Activity不需要绑定Fragment,那你再在这个Activity中绑定继承于 {@link BaseFragment} 的Fragment将不起任何作用
     *
     * @return
     */
    @Override
    public boolean useFragment() {
        return true;
    }
}
