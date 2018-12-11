package com.jancar.bluetooth.phone.presenter;

import com.jancar.bluetooth.phone.contract.MusicContract;
import com.jancar.bluetooth.phone.model.MusicModel;
import com.jancar.bluetooth.phone.model.MusicRepository;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-9-4 16:00:23
 * 音乐界面P层
 */
public class MusicPresenter extends BaseModelPresenter<MusicContract.View, MusicModel> implements MusicContract.Presenter, MusicModel.Callback {

    @Override
    public MusicModel createModel() {
        return new MusicRepository(this);
    }
}