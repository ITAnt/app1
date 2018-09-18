package com.jancar.settings.presenter;

import com.jancar.settings.listener.Contract.OnContractImpl;
import com.jancar.settings.listener.Contract.SoundContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.OnModel;
import com.jancar.settings.model.SoundModel;

import java.util.List;

/**
 * Created by ouyan on 2018/9/11.
 */

public class OnPresenter extends BasePresenter<OnContractImpl.Model, OnContractImpl.View> {
    OnContractImpl.Model model = new OnModel();

    public OnPresenter(OnContractImpl.View rootView) {
        super(rootView);
        initModel(model);
    }


}
