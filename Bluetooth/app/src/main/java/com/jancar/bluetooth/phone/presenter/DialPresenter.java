package com.jancar.bluetooth.phone.presenter;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.contract.DialContract;
import com.jancar.bluetooth.phone.model.DialModel;
import com.jancar.bluetooth.phone.model.DialRepository;
import com.jancar.bluetooth.phone.util.CheckPhoneUtil;
import com.jancar.bluetooth.phone.util.Constants;
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
    public void getDialContactList(String dialNumber, int type) {
        String trim = dialNumber.trim();
        if (CheckPhoneUtil.CheckPhoneNum(trim)) {
            getUi().getManager().queryContactsByNumber(trim, type);
        } else {
            getUi().getManager().queryContactsByName(trim,type);
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
        if (number != null && number.length() > 0) {
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

    @Override
    public boolean isDownLoading() {
        boolean isDomnLoading = true;
        int downState = getUi().getManager().getContactsDownState();
        if (downState == Constants.PHONEBOOK_STATE_FINSH || downState == Constants.PHONEBOOK_STATE_ERR) {
            isDomnLoading = false;
        }

        return isDomnLoading;
    }

}