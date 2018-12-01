package com.jancar.settings.presenter;

import com.jancar.settings.listener.Contract.VehicleContractImpl;
import com.jancar.settings.listener.Contract.WifiContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.VehicleModel;
import com.jancar.settings.model.WifiModel;

/**
 * Created by ouyan on 2018/9/10.
 */

public class VehiclePresenter  extends BasePresenter<VehicleContractImpl. Model, VehicleContractImpl. View> {
    VehicleContractImpl. Model model=new VehicleModel();
    public VehiclePresenter(VehicleContractImpl. View rootView) {
        super(rootView);
        initModel(model);
    }
}
