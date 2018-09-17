package com.jancar.bluetooth.phone.contract;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.ui.mvp.view.Ui;
import com.ui.mvp.presenter.IPresenter;

import java.util.List;

/**
 * @author Tzq
 * @date 2018-8-21 16:37:20
 * 通话记录接口
 */
public interface RecordsContract {

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
        void getCallRecordList();

        boolean isSynCallRecord();
    }
}
