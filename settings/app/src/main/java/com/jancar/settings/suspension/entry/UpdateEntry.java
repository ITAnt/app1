package com.jancar.settings.suspension.entry;

/**
 * @anthor Tzq
 * @time 2018/10/20 14:03
 * @describe TODO
 */
public class UpdateEntry {
    private boolean isUpdate;

    public UpdateEntry(boolean isUpdate) {
        this.isUpdate = isUpdate;

    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
