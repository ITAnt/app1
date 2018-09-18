package com.jancar.settings.view.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.listener.Contract.WifiSpotContractImpl;
import com.jancar.settings.manager.BaseFragment;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.WifiSpotPresenter;
import com.jancar.settings.util.wifi.WifiController;
import com.jancar.settings.util.wifi.WifiEnabler;
import com.jancar.settings.util.wifi.WifiListAdapter;
import com.jancar.settings.util.wifi.WifiSavedListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouyan on 2018/8/30.
 */

public class WifiSpotFragment extends BaseFragments<WifiSpotPresenter> implements WifiSpotContractImpl.View {



    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:" + this);
        super.onDestroy();
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public int initResid() {
        return R.xml.preference_screen_wifi_spot;
    }

    @Override
    public IPresenter initPresenter() {
        return new WifiSpotPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

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
