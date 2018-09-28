package com.jancar.bluetooth.phone.presenter;

import com.jancar.bluetooth.phone.contract.MusicContentContract;
import com.jancar.bluetooth.phone.model.MusicContentModel;
import com.jancar.bluetooth.phone.model.MusicContentRepository;
import com.ui.mvp.presenter.BaseModelPresenter;

/**
 * @author Tzq
 * @date 2018-9-28 16:00:02
 */
public class MusicContentPresenter extends BaseModelPresenter<MusicContentContract.View, MusicContentModel> implements MusicContentContract.Presenter, MusicContentModel.Callback {

    @Override
    public MusicContentModel createModel() {
        return new MusicContentRepository(this);
    }
}