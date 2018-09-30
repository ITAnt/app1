package com.jancar.settings.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.settings.R;
import com.jancar.settings.listener.Contract.CacheContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragment;
import com.jancar.settings.presenter.CachePresenter;
import com.jancar.settings.util.DataCleanManagerUtil;
import com.jancar.settings.widget.BitPreference;

/**
 * Created by ouyan on 2018/8/30.
 */

public class CacheFragment extends BaseFragment<CachePresenter> implements CacheContractImpl.View, BitPreference.OnClickListener {
    BitPreference clearDataBitPfe;//清除数据
    BitPreference clearCacheBitPfe;//清除缓存

    @Override
    public int initResid() {
        return R.xml.preference_screen_cache;
    }

    @Override
    public IPresenter initPresenter() {
        return new CachePresenter(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        clearDataBitPfe = (BitPreference) findPreference("key_clear_data");
        clearCacheBitPfe = (BitPreference) findPreference("key_clear_cache");
        try {
            clearDataBitPfe.setTitle(DataCleanManagerUtil.getTotalCacheSize(getContext()));
            clearCacheBitPfe.setTitle(DataCleanManagerUtil.getTotalCacheSize(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearCacheBitPfe.setOnClickListener(this);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View var1, String key) {
        switch (key) {
            case "key_clear_cache":
                AlertDialog alertDialog1 = new AlertDialog.Builder(getContext()).setTitle("是否删除缓存")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DataCleanManagerUtil.clearAllCache(getContext());
                            }
                        }).create();
                alertDialog1.show();
                break;
        }

    }
}
