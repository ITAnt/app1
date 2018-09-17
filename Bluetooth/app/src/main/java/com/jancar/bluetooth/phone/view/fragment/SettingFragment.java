package com.jancar.bluetooth.phone.view.fragment;

import com.jancar.bluetooth.lib.BluetoothDeviceData;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.R;
import com.ui.mvp.view.support.BaseFragment;
import com.jancar.bluetooth.phone.contract.SettingContract;
import com.jancar.bluetooth.phone.presenter.SettingPresenter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author Tzq
 * @date 2018-9-3 15:51:11
 */
public class SettingFragment extends BaseFragment<SettingContract.Presenter, SettingContract.View> implements SettingContract.View {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    public SettingFragment() {

    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public SettingContract.Presenter createPresenter() {
        return new SettingPresenter();
    }

    @Override
    public SettingContract.View getUiImplement() {
        return this;
    }

    @Override
    public BluetoothManager getBluetManger() {
        return null;
    }


}
