package com.jancar.settings.presenter;

import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.util.Log;

import com.jancar.settings.listener.Contract.WifiContractImpl;
import com.jancar.settings.listener.Contract.WifiSpotContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.WifiModel;
import com.jancar.settings.model.WifiSpotModel;

import java.util.List;

import static com.jancar.settings.util.wifi.WifiController.WIFICIPHER_NOPASS;
import static com.jancar.settings.util.wifi.WifiController.WIFICIPHER_WEP;
import static com.jancar.settings.util.wifi.WifiController.WIFICIPHER_WPA;

/**
 * Created by ouyan on 2018/9/3.
 */

public class WifiPresenter   extends BasePresenter<WifiContractImpl. Model, WifiContractImpl. View> {
    WifiContractImpl. Model model=new WifiModel();
    public WifiPresenter(WifiContractImpl. View rootView) {
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

    public  List<ScanResult> removeDuplicate(List<ScanResult> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).SSID.equals(list.get(i).SSID)) {
                    list.remove(j);
                }
            }
        }
        return list;
    }
}
