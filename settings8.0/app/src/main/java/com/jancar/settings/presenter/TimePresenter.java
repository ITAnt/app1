package com.jancar.settings.presenter;

import com.jancar.settings.contract.TimeZoneEntity;
import com.jancar.settings.listener.Contract.TimeContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.TimeModel;

import java.util.List;

/**
 * Created by ouyan on 2018/9/4.
 */

public class TimePresenter extends BasePresenter<TimeContractImpl.Model, TimeContractImpl.View> {
    TimeContractImpl.Model model = new TimeModel();

    public TimePresenter(TimeContractImpl.View rootView) {
        super(rootView);
        initModel(model);
    }


    public List<TimeZoneEntity> getNameListData() {

        return model.getNameListData(mRootView.getContext());
    }

    public String getTimeZone(String id, List<TimeZoneEntity> timeZoneEntityList) {
        return model.getTimeZone( id, timeZoneEntityList);
    }
}
