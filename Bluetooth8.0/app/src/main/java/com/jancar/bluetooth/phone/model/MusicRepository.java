package com.jancar.bluetooth.phone.model;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:23
 */
public class MusicRepository implements MusicModel {

    private Callback mCallback;

    public MusicRepository(Callback callback) {
        this.mCallback = callback;
    }

}
