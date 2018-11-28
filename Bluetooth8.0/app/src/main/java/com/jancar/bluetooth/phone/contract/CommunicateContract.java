package com.jancar.bluetooth.phone.contract;

import android.content.Context;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.ui.mvp.view.Ui;
import com.ui.mvp.presenter.IPresenter;

/**
 * @author Tzq
 * @date 2018-8-23 17:57:30
 * 电话接口
 */
public interface CommunicateContract {

    interface View extends Ui {
        BluetoothManager getManager();

        Context getUIContext();

    }

    interface Presenter extends IPresenter {
        void terminateCall();//挂断电话

        void rejectCall();//拒绝电话

        void acceptCall();//接听电话

        boolean isMicMute();//是否静音

        String getContactByNumber(String number);//根据号码查询通讯录名字

        void addCallLog(BluetoothPhoneBookData bluetoothPhoneBookData);


    }
}
