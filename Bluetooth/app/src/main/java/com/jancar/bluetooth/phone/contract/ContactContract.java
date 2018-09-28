package com.jancar.bluetooth.phone.contract;

import android.content.Context;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.adapter.BaseViewAdapter;
import com.ui.mvp.view.Ui;
import com.ui.mvp.presenter.IPresenter;

import java.util.List;

/**
 * @author Tzq
 * @date 2018-8-21 16:36:54
 * 联系人接口
 */
public interface ContactContract {

    interface View extends Ui {
        Context getUIContext();

        /**
         * 在UI线程中执行线程任务
         *
         * @param runnable 线程任务
         */
        void runOnUIThread(Runnable runnable);

        BluetoothManager getManager();

    }

    interface Presenter extends IPresenter {
        //同步联系人
        void getSynContact();

        //搜索联系人
        void getSearchConatct(String searchString,int type);


        //是否同步了联系人
        boolean isSynContact();

        boolean isDownLoading();

    }
}
