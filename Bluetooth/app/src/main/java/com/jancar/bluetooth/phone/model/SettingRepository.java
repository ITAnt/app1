package com.jancar.bluetooth.phone.model;


/**
 * @author Tzq
 * @date 2018-9-3 15:51:11
 */
public class SettingRepository implements SettingModel {

    private Callback mCallback;

    public SettingRepository(Callback callback) {
        this.mCallback = callback;
    }

}
