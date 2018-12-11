package com.jancar.bluetooth.phone.model;


/**
 * @author Tzq
 * @date 2018-8-21 16:37:48
 */
public class EquipmentRepository implements EquipmentModel {

    private Callback mCallback;

    public EquipmentRepository(Callback callback) {
        this.mCallback = callback;
    }

}
