package com.jancar.bluetooth.music.model;


/**
 * @author Tzq
 * @date 2018-10-16 11:09:29
 */
public class MusicRepository implements MusicModel {

    private Callback mCallback;

    public MusicRepository(Callback callback) {
        this.mCallback = callback;
    }

}
