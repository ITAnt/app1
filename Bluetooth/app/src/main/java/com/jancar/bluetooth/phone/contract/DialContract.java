package com.jancar.bluetooth.phone.contract;

import android.content.Context;
import android.widget.BaseAdapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.adapter.DialNumberAdapter;
import com.ui.mvp.view.Ui;
import com.ui.mvp.presenter.IPresenter;

import java.util.List;

/**
 * @author Tzq
 * @date 2018-8-21 16:34:03
 * 拨号界面接口
 */
public interface DialContract {

    interface View extends Ui {
        /**
         * 获取UI上下文
         *
         * @return 上下文
         */
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
        /**
         * 根据输入的号码，搜索到匹配的联系人列表
         *
         * @param dialNumber
         */
        void getDialContactList(String dialNumber,int type);

        /**
         * 根据输入的number的length，显示不同的格式
         *
         * @param number
         * @return
         */

        String getNewNumber(String number);

        /**
         * 删除电话号码
         *
         * @param number
         * @return
         */

        String delNumber(String number);

        /**
         * 长按删除
         *
         * @param number
         * @return
         */

        String delLongNumber(String number);


        boolean isSynContact();
        boolean isDownLoading();


    }
}
