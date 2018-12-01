package com.jancar.settings.suspension.entry;

/**
 * @anthor Tzq
 * @time 2018/10/31 17:38
 * @describe TODO
 */
public class OpenedEntry {
    private boolean isOpen;

    public OpenedEntry(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
