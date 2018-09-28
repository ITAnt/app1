package com.jancar.settings.util.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ATC6110 on 2018/3/29.
 */

public class WifiController {


    private final String TAG = "WifiController";

    public static final int WIFICIPHER_NOPASS = 0;
    public static final int WIFICIPHER_WEP = 1;
    public static final int WIFICIPHER_WPA = 2;

    public final int STATE_IDLE = 0;
    public final int STATE_CONNECTING = 1;
    public final int STATE_AUTHENTICATING = 2;
    public final int STATE_OBTAINING_IPADDR = 3;
    public final int STATE_CONNECTED = 4;
    public final int STATE_DISCONNECTING = 5;
    public final int STATE_DISCONNECTED = 6;
    public final int STATE_FAILED = 7;

    public static final String NONE = "<unknown ssid>";

    public final int NOTCONFIG = -1;
    public final int MARK = -1;

    private int mConnectionState = STATE_IDLE;

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    private List<ScanResult> mWifiList = new ArrayList<ScanResult>();
    private List<WifiConfiguration> mWifiConfigurations = new ArrayList<WifiConfiguration>();

    WifiManager.WifiLock mWifiLock;

    /*************************     init      ********************/
    private static WifiController mInstance = null;

    public void setInstance() {
        Log.d(TAG, "setInstance()");
        mInstance = null;
    }

    private WifiController(Context context) {
        Log.d(TAG, "WifiController()");
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public static WifiController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WifiController(context);
        }
        return mInstance;
    }

    /*************************     controller      ********************/
    public boolean setWifiEnabled(boolean isChecked) {
        Log.d(TAG, "setWifiEnabled()");
        if (isChecked == mWifiManager.isWifiEnabled()) {
            return true;
        } else if (isChecked && !mWifiManager.isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(true);
        } else if (!isChecked && mWifiManager.isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(false);
        }
        return false;
    }


    public int getWifiState() {
        Log.d(TAG, "getWifiState()");
        return mWifiManager.getWifiState();
    }

    public void startScan() {
        Log.d(TAG, "startScan()");
        mWifiManager.startScan();
        mWifiList = mWifiManager.getScanResults();
    }


    /*************************     data      ********************/
    public List<ScanResult> getWifiList() {
        Log.d(TAG, "getWifiList()");
        mWifiList = mWifiManager.getScanResults();
        List<ScanResult> scanList = removeSameName(mWifiList);
        sortByLevel(scanList);
        return scanList;
    }

    /**
     * create the saved list to show;
     *
     * @return
     */
    public List<ScanResult> getSavedList() {
        Log.d(TAG, "getSavedList()");

        mWifiList = mWifiManager.getScanResults();
        if (null == mWifiList) {
            Log.d(TAG, "null == mWifiList");
            return null;
        }

        List<ScanResult> savedlist = new ArrayList<ScanResult>();
        getConfigurations();

        for (ScanResult result : mWifiList) {
            if (NOTCONFIG != isConfigured(result.SSID)) {
                savedlist.add(result);
            }
        }

        sortByConnectedStateAndLevel(savedlist);
        savedlist = removeSameNameSaved(savedlist);

        return savedlist;
    }


    private void sortByConnectedStateAndLevel(List<ScanResult> savedList) {

        Log.d(TAG, "sortByConnectedStateAndLevel()");

        if (null == savedList) {
            Log.d(TAG, "savedList is null ");
            return;
        }

        if (0 == savedList.size()) {
            Log.d(TAG, "savedList.size is 0 ");
            return;
        }

        //if there is connected，make the AP in the first；
        int mark =  MARK;
        String SSID = getConnectedSSID();
        if (NONE != SSID) {
            Log.d(TAG, "is connect:" + SSID);
            for (ScanResult result : savedList) {
                if (SSID.equals("\"" + result.SSID + "\"")) {
                    result.level *= mark;
                    break;
                }
            }

            sortByLevel(savedList);
            savedList.get(0).level *= mark;

        } else {
            sortByLevel(savedList);
        }

    }

    public String getConnectedSSID() {
        Log.d(TAG, "getSavedList()");
        mWifiInfo = mWifiManager.getConnectionInfo();
        String SSID = mWifiInfo.getSSID();
        return SSID;
    }

    /**
     * 根据wifi的强弱排序
     */
    private void sortByLevel(List<ScanResult> scanList) {

        Log.d(TAG, "sortByLevel()");

        if (null == scanList) {
            Log.d(TAG, "scanList is null ");
            return;
        }

        Collections.sort(scanList, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level - lhs.level;
            }
        });
    }


    /**
     * remove the record if there is have the same ssid
     *
     * @param scanlist
     * @return
     */
    private List<ScanResult> removeSameName(List<ScanResult> scanlist) {

        Log.d(TAG, "removeSameName()");

        if (null == scanlist) {
            Log.d(TAG, "scanlist is null");
            return null;
        }
        List<ScanResult> newList = new ArrayList<ScanResult>();

        for (ScanResult result : scanlist) {
            boolean isSaved = false;
            if (NOTCONFIG != isConfigured(result.SSID)) {
                isSaved = true;
            }
            if (!TextUtils.isEmpty(result.SSID) && !isExsitsInList(newList, result.SSID) && !isSaved) {
                newList.add(result);
            }

        }
        return newList;
    }

    /**
     * remove the record if there is have the same ssid
     *
     * @param scanlist
     * @return
     */
    private List<ScanResult> removeSameNameSaved(List<ScanResult> scanlist) {

        Log.d(TAG, "removeSameNameSaved()");

        if (null == scanlist) {
            return null;
        }

        if (0 == scanlist.size()) {
            Log.d(TAG, "savedList.size is 0 ");
            return scanlist;
        }
        List<ScanResult> newList = new ArrayList<ScanResult>();

        for (ScanResult result : scanlist) {
            if (!TextUtils.isEmpty(result.SSID) && !isExsitsInList(newList, result.SSID)) {
                newList.add(result);
            }
        }
        return newList;
    }

    /**
     * is the ssid exsits
     *
     * @param SSID
     * @return
     */
    private boolean isExsitsInList(List<ScanResult> scanlist, String SSID) {

        Log.d(TAG, "isExsitsInList() ");
        if (null == scanlist) {
            Log.d(TAG, "scanlist is null");
            return false;
        }
        for (ScanResult result : scanlist) {
            if (result.SSID.equals(SSID)) {
                return true;
            }
        }
        return false;
    }


    /*************************     base state get      ********************/
    /**
     * get the saved result
     */
    public void getConfigurations() {
        Log.d(TAG, "getConfigurations() ");
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    /**
     * isSaved?
     *
     * @param SSID
     * @return
     */
    public int isConfigured(String SSID) {

        Log.d(TAG, "isConfigured() ");
        if(null == mWifiConfigurations) {
            Log.d(TAG, "mWifiConfigurations is null");
            return NOTCONFIG;
        }
        
        for (int i = 0; i < mWifiConfigurations.size(); i++) {
            if (mWifiConfigurations.get(i).SSID.equals("\"" + SSID + "\"")) {
                return mWifiConfigurations.get(i).networkId;
            }
        }
        return NOTCONFIG;
    }

    /**
     * createWifiConfig for connect
     *
     * @param ssid
     * @param password
     * @param type
     * @return
     */
    public WifiConfiguration createWifiConfig(String ssid, String password, int type) {

        Log.d(TAG, "createWifiConfig()");
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";

        //如果之前有类似的配置
        WifiConfiguration tempConfig = getConfig(ssid);
        if (tempConfig != null) {
            //则清除旧有配置
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        config.hiddenSSID = true;
        //不需要密码的场景
        if (type == WIFICIPHER_NOPASS) {
            //这里很奇怪，不设置hiddenSSID的时候正常可以连接，但是隐藏无法连接，那当设置了hiddenSSID的时候正常连接也可以？
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (type == WIFICIPHER_WEP) {
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }


    public WifiConfiguration getConfig(String ssid) {
        Log.d(TAG, "getConfig()");
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        if(null == configs) {
            Log.d(TAG, "getConfig() configs is null");
            return null;
        }
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    public void removeConfig(WifiConfiguration config) {
        Log.d(TAG, "removeConfig()");
        if (null == config) {
            Log.d(TAG, "the config is null");
            return;
        }

        disConnectionWifi(config.networkId);
        mWifiManager.removeNetwork(config.networkId);

    }

    public  int connect(WifiConfiguration config) {
        Log.d(TAG, "connect()");

        if (null == config) {
            Log.d(TAG, "the config is null");
            return -1;
        }

        int wcgID = mWifiManager.addNetwork(config);
        mWifiManager.enableNetwork(wcgID, true);
       return wcgID;
    }


    public void disConnect() {
        Log.d(TAG, "disConnect()");
        mWifiManager.disconnect();
    }

    public void disConnectionWifi(int netId) {
        Log.d(TAG, "disConnectionWifi()");
        mWifiManager.disableNetwork(netId);
    }




    public void acquireWifiLock() {
        Log.d(TAG, "acquireWifiLock()");
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        Log.d(TAG, "releaseWifiLock()");
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void createWifiLock() {
        Log.d(TAG, "createWifiLock()");
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    public int getSignalLevel(int rssi, int numLevels) {
        Log.d(TAG, "getSignalLevel()");
        return mWifiManager.calculateSignalLevel(rssi, numLevels);
    }

    public int getStrength() {
        Log.d(TAG, "getStrength()");

        int rssi = 0;
        WifiInfo info = mWifiManager.getConnectionInfo();
        if(null != info.getBSSID()){
            rssi = info.getRssi();
        }
        return rssi;
    }

    public void setConnectionState(int state) {
        Log.d(TAG, "setConnectionState()");
        Log.d(TAG, "setConnectionState:" + state);
        mConnectionState = state;
    }

    public int getConnectionState() {
        Log.d(TAG, "getConnectionState() ");
        Log.d(TAG, "getConnectionState:" + mConnectionState);
        return mConnectionState;

    }
}
