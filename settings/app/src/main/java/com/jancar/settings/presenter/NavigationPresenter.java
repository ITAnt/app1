package com.jancar.settings.presenter;

import com.jancar.settings.contract.NavigationEntity;
import com.jancar.settings.listener.Contract.NavigationContractImpl;
import com.jancar.settings.listener.Contract.SoundContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.NavigationModel;
import com.jancar.settings.model.SoundModel;

import java.util.List;

/**
 * Created by ouyan on 2018/9/10.
 */

public class NavigationPresenter extends BasePresenter<NavigationContractImpl.Model, NavigationContractImpl.View> {
    NavigationContractImpl.Model model = new NavigationModel();

    public NavigationPresenter(NavigationContractImpl.View rootView) {
        super(rootView);
        initModel(model);
    }

    public List<NavigationEntity> getListData() {
        return model.getListData();
    }
}