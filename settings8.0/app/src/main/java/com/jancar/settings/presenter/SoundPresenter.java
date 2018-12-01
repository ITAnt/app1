package com.jancar.settings.presenter;

import android.content.Context;

import com.jancar.settings.contract.EqEntity;
import com.jancar.settings.listener.Contract.SoundContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.SoundModel;

import java.util.List;

/**
 * Created by ouyan on 2018/9/7.
 */

public class SoundPresenter extends BasePresenter<SoundContractImpl.Model, SoundContractImpl.View> {
    SoundContractImpl.Model model = new SoundModel();

    public SoundPresenter(SoundContractImpl.View rootView) {
        super(rootView);
        initModel(model);
    }

    public List<EqEntity> initList(Context mContext) {
        return  model.initList( mContext);
    }
}