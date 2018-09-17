package com.jancar.bluetooth.phone.model;


/**
 * @author Tzq
 * @date 2018-8-21 16:37:21
 */
public class RecordsRepository implements RecordsModel {

    private Callback mCallback;

    public RecordsRepository(Callback callback) {
        this.mCallback = callback;
    }

}
