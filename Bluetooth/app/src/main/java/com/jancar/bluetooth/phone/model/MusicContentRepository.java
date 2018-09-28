package com.jancar.bluetooth.phone.model;


/**
 * @author Tzq
 * @date 2018-9-28 16:00:02
 */
public class MusicContentRepository implements MusicContentModel {

    private Callback mCallback;

    public MusicContentRepository(Callback callback) {
        this.mCallback = callback;
    }

}
