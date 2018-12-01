package com.jancar.settings.presenter;

import com.jancar.settings.listener.Contract. DisplayContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model. DisplayModel;

import java.util.List;

/**
 * Created by ouyan on 2018/8/30.
 */

public class DisplayPresenter  extends BasePresenter< DisplayContractImpl. Model,  DisplayContractImpl. View> {
    DisplayContractImpl. Model model=new  DisplayModel();
    public  DisplayPresenter( DisplayContractImpl. View rootView) {
        super(rootView);
        initModel(model);
    }

    public List<String> getList() {
        return model.getList();
    }
}
