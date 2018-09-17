package com.jancar.bluetooth.phone.contract;

import android.content.Context;

import com.jancar.bluetooth.lib.BluetoothDeviceData;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.ui.mvp.view.Ui;
import com.ui.mvp.presenter.IPresenter;

import java.util.List;

/**
 * @author Tzq
 * @date 2018-9-3 15:51:11
 * 蓝牙设置界面接口
 */
public interface SettingContract {

    interface View extends Ui {

        BluetoothManager getBluetManger();

    }

    interface Presenter extends IPresenter {
        boolean isBTOn();

        void setBlutoothName(String blutoothName);

        void searchPairedList();

        void delBlutooth(String address);

        void connBlutoth(String address);

        void openBluetooth();

        void closeBluetooth();

        String getBlutoothName();


    }
}
