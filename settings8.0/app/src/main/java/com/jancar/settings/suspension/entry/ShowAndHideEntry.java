package com.jancar.settings.suspension.entry;

/**
 * @anthor Tzq
 * @time 2018/10/20 14:03
 * @describe TODO
 */
public class ShowAndHideEntry {
    private boolean isShow;

    public ShowAndHideEntry(boolean isShow) {
        this.isShow = isShow;

    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
