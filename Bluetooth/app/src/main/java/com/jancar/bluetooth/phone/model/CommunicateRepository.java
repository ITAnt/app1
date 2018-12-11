package com.jancar.bluetooth.phone.model;


/**
 * @author Tzq
 * @date 2018-8-23 17:57:30
 */
public class CommunicateRepository implements CommunicateModel {

    private Callback mCallback;

    public CommunicateRepository(Callback callback) {
        this.mCallback = callback;
    }

}
