package com.jancar.bluetooth.phone.presenter;

import com.jancar.bluetooth.phone.contract.SettingContract;
import com.jancar.bluetooth.phone.model.SettingModel;
import com.jancar.bluetooth.phone.model.SettingRepository;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-9-3 15:51:11
 * 蓝牙设置P层
 */
public class SettingPresenter extends BaseModelPresenter<SettingContract.View, SettingModel> implements SettingContract.Presenter, SettingModel.Callback {

    @Override
    public SettingModel createModel() {
        return new SettingRepository(this);
    }

    @Override
    public boolean isBTOn() {
        boolean btOn = getUi().getBluetManger().isBTOn();
        return btOn;
    }

    @Override
    public void setBlutoothName(String blutoothName) {
        getUi().getBluetManger().setBTName(blutoothName);

    }

    @Override
    public void searchPairedList() {
        getUi().getBluetManger().startSearch();
    }

    @Override
    public void delBlutooth(String address) {
        getUi().getBluetManger().removeDevice(address);
    }

    @Override
    public void connBlutoth(String address) {
        getUi().getBluetManger().connectDevice(address);
    }

    @Override
    public void openBluetooth() {
        getUi().getBluetManger().openBluetooth();
    }

    @Override
    public void closeBluetooth() {
        getUi().getBluetManger().closeBluetooth();

    }

    @Override
    public String getBlutoothName() {
        String btName = getUi().getBluetManger().getBTName();
        return btName;
    }


}