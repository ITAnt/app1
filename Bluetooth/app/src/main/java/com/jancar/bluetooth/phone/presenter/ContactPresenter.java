package com.jancar.bluetooth.phone.presenter;

import android.util.Log;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.contract.ContactContract;
import com.jancar.bluetooth.phone.model.ContactModel;
import com.jancar.bluetooth.phone.model.ContactRepository;
import com.jancar.bluetooth.phone.util.CheckPhoneUtil;
import com.jancar.bluetooth.phone.util.Constants;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-8-21 16:36:54
 * 联系人P层
 */
public class ContactPresenter extends BaseModelPresenter<ContactContract.View, ContactModel> implements ContactContract.Presenter, ContactModel.Callback {


    @Override
    public ContactModel createModel() {
        return new ContactRepository(this);
    }


    @Override
    public void getSynContact() {
        BluetoothManager.getBluetoothManagerInstance(getUi().getUIContext()).downloadContacts();
    }

    @Override
    public void getSearchConatct(String searchString, int type) {
        if (CheckPhoneUtil.CheckPhoneNum(searchString)) {
            getUi().getManager().queryContactsByNumber(searchString, type);
        } else {
            getUi().getManager().queryContactsByName(searchString, type);
        }

    }


    @Override
    public boolean isSynContact() {
        boolean syncedPhoneBooks = getUi().getManager().isSyncedPhoneBooks();
        return syncedPhoneBooks;
    }

    /**
     * 判断是否正在下载电话本
     *
     * @return
     */

    @Override
    public boolean isDownLoading() {
        boolean isDomnLoading = true;
        int downState = getUi().getManager().getContactsDownState();
        Log.d("ContactPresenter", "downState:" + downState);
        if (downState == Constants.PHONEBOOK_STATE_FINSH || downState == Constants.PHONEBOOK_STATE_ERR || downState == Constants.PHONEBOOK_STATE_STOP) {
            isDomnLoading = false;
        }

        return isDomnLoading;
    }
}