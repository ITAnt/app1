package com.jancar.settings.presenter;

import com.jancar.settings.listener.Contract.BluetoothContractImpl;
import com.jancar.settings.listener.Contract.CacheContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.BluetoothModel;
import com.jancar.settings.model.CacheModel;

/**
 * Created by ouyan on 2018/9/17.
 */

public class BluetoothPresenter  extends BasePresenter<BluetoothContractImpl. Model, BluetoothContractImpl. View> {
    BluetoothContractImpl. Model model=new BluetoothModel();
    public BluetoothPresenter(BluetoothContractImpl. View rootView) {
        super(rootView);
        initModel(model);
    }


    public boolean isBTOn() {
        boolean btOn = mRootView.getBluetManger().isBTOn();
        return btOn;
    }


    public void setBlutoothName(String blutoothName) {
        mRootView.getBluetManger().setBTName(blutoothName);

    }


    public void searchPairedList() {
        mRootView.getBluetManger().startSearch();
    }


    public void delBlutooth(String address) {
        mRootView.getBluetManger().removeDevice(address);
    }


    public void connBlutoth(String address) {
        mRootView.getBluetManger().connectDevice(address);
    }


    public void openBluetooth() {
        mRootView.getBluetManger().openBluetooth();
    }


    public void closeBluetooth() {
        mRootView.getBluetManger().closeBluetooth();

    }


    public String getBlutoothName() {
        String btName = mRootView.getBluetManger().getBTName();
        return btName;
    }

}