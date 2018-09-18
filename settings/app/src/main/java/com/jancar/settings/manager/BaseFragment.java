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

import android.os.Bundle;
import android.os.HandlerThread;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.settings.R;
import com.jancar.settings.listener.FragmentImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.util.wifi.WifiController;
import com.jancar.settings.util.wifi.WifiEnabler;

/**
 * ================================================
 * ================================================
 */
public abstract class BaseFragment<P extends IPresenter> extends PreferenceFragment implements FragmentImpl {
    protected final String TAG = this.getClass().getSimpleName();

    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter==null){
            mPresenter= (P) initPresenter();
        }
        int preferencesResId = initResid();
        if (preferencesResId != 0) {
            addPreferencesFromResource(preferencesResId);
        }
        initView(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }


}
