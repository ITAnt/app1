package com.jancar.bluetooth.phone.presenter;


import com.jancar.bluetooth.phone.contract.RecordsContract;
import com.jancar.bluetooth.phone.model.RecordsModel;
import com.jancar.bluetooth.phone.model.RecordsRepository;
import com.jancar.bluetooth.phone.util.Constants;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-8-21 16:37:21
 * 通话记录P层
 */
public class RecordsPresenter extends BaseModelPresenter<RecordsContract.View, RecordsModel> implements RecordsContract.Presenter, RecordsModel.Callback {


    @Override
    public RecordsModel createModel() {
        return new RecordsRepository(this);
    }


    @Override
    public void getCallRecordList() {
        getUi().getManager().downloadContacts();
    }

    @Override
    public boolean isSynCallRecord() {
        boolean syncedCallLogs = getUi().getManager().isSyncedCallLogs();
        return syncedCallLogs;
    }

    @Override
    public boolean isDownLoading() {
        boolean isDomnLoading = true;
        int downState = getUi().getManager().getContactsDownState();
        int callhistroyState = getUi().getManager().getContCallhistroyState();
        if ((downState == Constants.PHONEBOOK_STATE_FINSH || downState == Constants.PHONEBOOK_STATE_ERR || downState == Constants.PHONEBOOK_STATE_STOP) && callhistroyState != Constants.PHONEBOOK_STATE_START) {
            isDomnLoading = false;
        }

        return isDomnLoading;
    }
}