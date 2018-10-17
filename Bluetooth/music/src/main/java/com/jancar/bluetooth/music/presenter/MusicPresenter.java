package com.jancar.bluetooth.music.presenter;

import com.jancar.bluetooth.music.contract.MusicContract;
import com.jancar.bluetooth.music.model.MusicModel;
import com.jancar.bluetooth.music.model.MusicRepository;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-10-16 11:09:28
 */
public class MusicPresenter extends BaseModelPresenter<MusicContract.View, MusicModel> implements MusicContract.Presenter, MusicModel.Callback {

    @Override
    public MusicModel createModel() {
        return new MusicRepository(this);
    }
}