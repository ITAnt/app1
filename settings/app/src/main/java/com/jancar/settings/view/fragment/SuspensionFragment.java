package com.jancar.settings.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.settings.R;
import com.jancar.settings.listener.Contract.CacheContractImpl;
import com.jancar.settings.listener.Contract.SuspensionContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragment;
import com.jancar.settings.manager.BaseFragmentsd;
import com.jancar.settings.presenter.CachePresenter;
import com.jancar.settings.presenter.SuspensionPresenter;

/**
 * Created by ouyan on 2018/10/20.
 */

public class SuspensionFragment  extends BaseFragmentsd<SuspensionPresenter> implements SuspensionContractImpl.View{
    private View view;
    public static SuspensionFragment newInstance(boolean b) {
        SuspensionFragment frag = new SuspensionFragment();
        Bundle bundle = new Bundle();
        frag.setArguments(bundle);   //设置参数

        return frag;
    }
    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_suspension, null);
        initView(savedInstanceState);
        initData(savedInstanceState);
        return view;
    }
    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }
    @Override
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new SuspensionPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setData(@Nullable Object data) {

    }
}
