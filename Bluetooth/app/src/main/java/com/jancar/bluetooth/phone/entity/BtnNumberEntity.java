package com.jancar.bluetooth.phone.entity;

/**
 * @anthor Tzq
 * @time 2018/11/5 16:17
 * @describe TODO
 */
public class BtnNumberEntity {
    private boolean isBtnKey;

    public BtnNumberEntity(boolean isBtnKey) {
        this.isBtnKey = isBtnKey;
    }

    public boolean isBtnKey() {
        return isBtnKey;
    }

    public void setBtnKey(boolean btnKey) {
        isBtnKey = btnKey;
    }
}
