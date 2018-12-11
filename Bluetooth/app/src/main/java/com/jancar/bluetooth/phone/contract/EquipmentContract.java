package com.jancar.bluetooth.phone.contract;

import android.content.Context;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.ui.mvp.view.Ui;
import com.ui.mvp.presenter.IPresenter;

/**
 * @author Tzq
 * @date 2018-8-21 16:37:48
 * 设备管理接口
 */
public interface EquipmentContract {

    interface View extends Ui {
        Context getUIContext();

        void runOnUIThread(Runnable runnable);

        BluetoothManager getManager();


    }

    interface Presenter extends IPresenter {
        String getConnetName();

        String getSelfName();

        void disConnectDevice();

    }
}
