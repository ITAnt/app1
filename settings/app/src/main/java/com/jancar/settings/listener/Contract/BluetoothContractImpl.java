package com.jancar.settings.listener.Contract;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.settings.listener.ModelImpl;
import com.jancar.settings.listener.ViewImpl;

/**
 * Created by ouyan on 2018/9/17.
 */

public interface BluetoothContractImpl {
    interface View extends ViewImpl {

        BluetoothManager getBluetManger();
    }
    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,如是否使用缓存
    interface Model extends ModelImpl {

    }
}
