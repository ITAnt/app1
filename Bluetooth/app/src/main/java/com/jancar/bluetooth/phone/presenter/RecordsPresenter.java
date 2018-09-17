package com.jancar.bluetooth.phone.presenter;


import com.jancar.bluetooth.phone.contract.RecordsContract;
import com.jancar.bluetooth.phone.model.RecordsModel;
import com.jancar.bluetooth.phone.model.RecordsRepository;
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
}