package com.jancar.bluetooth.phone.model;


/**
 * @author Tzq
 * @date 2018-8-21 16:36:54
 */
public class ContactRepository implements ContactModel {

    private Callback mCallback;

    public ContactRepository(Callback callback) {
        this.mCallback = callback;
    }

}
