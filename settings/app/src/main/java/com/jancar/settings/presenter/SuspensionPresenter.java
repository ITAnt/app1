package com.jancar.settings.presenter;

import android.content.Context;

import com.jancar.settings.contract.EqEntity;
import com.jancar.settings.listener.Contract.SoundContractImpl;
import com.jancar.settings.listener.Contract.SuspensionContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.SoundModel;
import com.jancar.settings.model.SuspensionModel;

import java.util.List;

/**
 * Created by ouyan on 2018/10/20.
 */

public class SuspensionPresenter  extends BasePresenter<SuspensionContractImpl.Model, SuspensionContractImpl.View> {
    SuspensionContractImpl.Model model = new SuspensionModel();

    public SuspensionPresenter(SuspensionContractImpl.View rootView) {
        super(rootView);
        initModel(model);
    }

}
