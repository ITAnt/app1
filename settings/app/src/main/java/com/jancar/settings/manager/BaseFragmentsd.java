package com.jancar.settings.manager;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.settings.listener.FragmentImpl;
import com.jancar.settings.listener.IPresenter;

/**
 * Created by ouyan on 2018/9/3.
 */

public abstract class BaseFragmentsd <P extends IPresenter> extends Fragment implements FragmentImpl {
    protected final String TAG = this.getClass().getSimpleName();

    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter==null){
            mPresenter= (P) initPresenter();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return initView(inflater, container, savedInstanceState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }


}

