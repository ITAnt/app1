package com.jancar.settings.presenter;

import com.jancar.settings.listener.Contract.SystemContractImpl;
import com.jancar.settings.listener.Contract.TimeContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.SystemModel;
import com.jancar.settings.model.TimeModel;

/**
 * Created by ouyan on 2018/9/11.
 */

public class SystemPresenter extends BasePresenter<SystemContractImpl.Model, SystemContractImpl.View> {
    SystemContractImpl.Model model = new SystemModel();

    public SystemPresenter(SystemContractImpl.View rootView) {
        super(rootView);
        initModel(model);
    }
}
