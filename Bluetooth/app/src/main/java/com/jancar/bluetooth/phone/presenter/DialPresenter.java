package com.jancar.bluetooth.phone.presenter;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.contract.DialContract;
import com.jancar.bluetooth.phone.model.DialModel;
import com.jancar.bluetooth.phone.model.DialRepository;
import com.jancar.bluetooth.phone.util.CheckPhoneUtil;
import com.jancar.bluetooth.phone.util.NumberFormatUtil;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-8-21 16:34:03
 */
public class DialPresenter extends BaseModelPresenter<DialContract.View, DialModel> implements DialContract.Presenter, DialModel.Callback {


    @Override
    public DialModel createModel() {
        return new DialRepository(this);
    }

    @Override
    public void getDialContactList(String dialNumber) {
        String trim = dialNumber.trim();
        if (CheckPhoneUtil.CheckPhoneNum(trim)) {
            getUi().getManager().queryContactsByNumber(trim);
        } else {
            getUi().getManager().queryContactsByName(trim);
        }
    }

    /**
     * 格式化号码
     *
     * @param number
     * @return
     */

    @Override
    public String getNewNumber(String number) {

        return NumberFormatUtil.getNumber(number);
    }

    /**
     * 点击删除号码
     *
     * @param number 号码
     * @return
     */

    @Override
    public String delNumber(String number) {
        if (number != null) {
            return number.substring(0, (number.length() - 1));
        }
        return null;
    }

    /**
     * 长按删除号码
     *
     * @param number
     * @return
     */

    @Override
    public String delLongNumber(String number) {
        if (number != null) {
            return null;
        }
        return null;
    }

    /**
     * 是否同步了联系人
     *
     * @return
     */

    @Override
    public boolean isSynContact() {
        boolean syncedPhoneBooks = BluetoothManager.getBluetoothManagerInstance(getUi().getUIContext()).isSyncedPhoneBooks();
        return syncedPhoneBooks;
    }

}