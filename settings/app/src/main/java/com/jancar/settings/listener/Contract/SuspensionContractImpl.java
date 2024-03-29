package com.jancar.settings.listener.Contract;

import android.app.Activity;
import android.content.Context;

import com.jancar.settings.listener.ModelImpl;
import com.jancar.settings.listener.ViewImpl;

/**
 * Created by ouyan on 2018/10/20.
 */

public interface SuspensionContractImpl {
    interface View extends ViewImpl {
        Activity getActivity();
        Context getContext();

    }
    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,如是否使用缓存
    interface Model extends ModelImpl {


    }
}
