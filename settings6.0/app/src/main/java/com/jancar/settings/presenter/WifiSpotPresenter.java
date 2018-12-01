package com.jancar.settings.presenter;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.listener.Contract.WifiSpotContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.WifiSpotModel;

import static com.jancar.settings.util.wifi.WifiController.WIFICIPHER_NOPASS;
import static com.jancar.settings.util.wifi.WifiController.WIFICIPHER_WEP;
import static com.jancar.settings.util.wifi.WifiController.WIFICIPHER_WPA;

/**
 * Created by ouyan on 2018/8/30.
 */

public class WifiSpotPresenter  extends BasePresenter<WifiSpotContractImpl. Model, WifiSpotContractImpl. View> {
    WifiSpotContractImpl. Model model=new WifiSpotModel();
    public WifiSpotPresenter(WifiSpotContractImpl. View rootView) {
        super(rootView);
        initModel(model);
    }

    public int getWifiType(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        Log.d(TAG, "capabilities:" + capabilities);
        int type = WIFICIPHER_WPA;
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                type = WIFICIPHER_WPA;
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                type = WIFICIPHER_WEP;
            } else {
                type = WIFICIPHER_NOPASS;
            }
        }
        return type;
    }

}