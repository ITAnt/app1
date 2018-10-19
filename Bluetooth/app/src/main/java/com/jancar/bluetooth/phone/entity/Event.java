package com.jancar.bluetooth.phone.entity;

/**
 * @anthor Tzq
 * @time 2018/10/18 13:51
 * @describe TODO
 */
public class Event {
    private boolean isConnect;

    public Event(boolean isConnect) {
        this.isConnect = isConnect;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }
}
