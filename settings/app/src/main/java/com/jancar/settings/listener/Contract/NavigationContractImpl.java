package com.jancar.settings.listener.Contract;

import android.app.Activity;
import android.content.Context;

import com.jancar.settings.contract.NavigationEntity;
import com.jancar.settings.listener.ModelImpl;
import com.jancar.settings.listener.ViewImpl;

import java.util.List;

/**
 * Created by ouyan on 2018/9/10.
 */

public interface NavigationContractImpl {
    interface View extends ViewImpl {
        Activity getActivity();
        Context getContext();

    }
    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,如是否使用缓存
    interface Model extends ModelImpl {

        List<NavigationEntity> getListData();
    }
}
