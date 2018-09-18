package com.jancar.settings.contract;

import android.support.annotation.DrawableRes;


import com.jancar.settings.R;

/**
 * Created by ouyan on 2018/9/11.
 */

public class KeyValue  {
    private int background;
    private byte keyLearningStatus;
    private byte keyValue;

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public byte getKeyLearningStatus() {
        return keyLearningStatus;
    }

    public void setKeyLearningStatus(byte keyLearningStatus) {
        this.keyLearningStatus = keyLearningStatus;
    }

    public byte getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(byte keyValue) {
        this.keyValue = keyValue;
    }
}
