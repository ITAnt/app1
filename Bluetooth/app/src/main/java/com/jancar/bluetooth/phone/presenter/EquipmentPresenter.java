package com.jancar.bluetooth.phone.presenter;

import android.text.TextUtils;

import com.jancar.bluetooth.phone.contract.EquipmentContract;
import com.jancar.bluetooth.phone.model.EquipmentModel;
import com.jancar.bluetooth.phone.model.EquipmentRepository;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-8-21 16:37:48
 * 设备管理P层
 */
public class EquipmentPresenter extends BaseModelPresenter<EquipmentContract.View, EquipmentModel> implements EquipmentContract.Presenter, EquipmentModel.Callback {

    @Override
    public EquipmentModel createModel() {
        return new EquipmentRepository(this);
    }

    @Override
    public String getConnetName() {
        String connectDeviceName = getUi().getManager().getConnectDeviceName();
        return connectDeviceName;
    }

    @Override
    public String getSelfName() {
        String btName = getUi().getManager().getBTName();
        return btName;
    }

    @Override
    public void disConnectDevice() {
        String remoteAddr = getUi().getManager().getRemoteAddr();
        if (!TextUtils.isEmpty(remoteAddr)) {
            getUi().getManager().disConnectDevice(remoteAddr);
        }
    }
}