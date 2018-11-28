package com.jancar.bluetooth.phone.presenter;


import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.contract.CommunicateContract;
import com.jancar.bluetooth.phone.model.CommunicateModel;
import com.jancar.bluetooth.phone.model.CommunicateRepository;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-8-23 17:57:30
 * 通话界面p层
 */
public class CommunicatePresenter extends BaseModelPresenter<CommunicateContract.View, CommunicateModel> implements CommunicateContract.Presenter, CommunicateModel.Callback {

    @Override
    public CommunicateModel createModel() {
        return new CommunicateRepository(this);
    }

    @Override
    public void terminateCall() {
        getUi().getManager().terminateCall();

    }

    @Override
    public void rejectCall() {
        getUi().getManager().rejectCall();

    }

    @Override
    public void acceptCall() {
        getUi().getManager().acceptCall();
    }

    @Override
    public boolean isMicMute() {
        boolean micMute = getUi().getManager().isMicMute();
        return micMute;
    }

    @Override
    public String getContactByNumber(String number) {
        try {
            String contactByNumber = getUi().getManager().getContactByNumber(number);
            return contactByNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addCallLog(BluetoothPhoneBookData bluetoothPhoneBookData) {
        getUi().getManager().addCallLogList(bluetoothPhoneBookData);
    }
}