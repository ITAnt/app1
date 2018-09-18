package com.jancar.settings.presenter;

import com.jancar.settings.listener.Contract.CacheContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.CacheModel;

/**
 * Created by ouyan on 2018/8/30.
 */

public class CachePresenter extends BasePresenter<CacheContractImpl. Model, CacheContractImpl. View> {
    CacheContractImpl. Model model=new CacheModel();
    public CachePresenter(CacheContractImpl. View rootView) {
        super(rootView);
        initModel(model);
    }

}
