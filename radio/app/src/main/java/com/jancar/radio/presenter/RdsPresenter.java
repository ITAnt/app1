package com.jancar.radio.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.SeekBar;

import com.jancar.radio.RadioManager;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.contract.RdsContract;
import com.jancar.radio.entity.RadioStation;
import com.jancar.radio.model.RadioModel;
import com.jancar.radio.model.RadioRepository;
import com.jancar.radio.model.RdsModel;
import com.jancar.radio.model.RdsRepository;
import com.jancar.utils.Logcat;
import com.ui.mvp.presenter.BaseModelPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jancar.radio.listener.utils.RadioStationDaos.deleteRadioStation;
import static com.jancar.radio.view.activity.RadioActivity.PAGE_AM;
import static com.jancar.radio.view.activity.RadioActivity.PAGE_FM;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:23
 * 界面P层
 */
public class RdsPresenter extends BaseModelPresenter<RdsContract.View, RdsModel> implements RdsContract.Presenter, RdsModel.Callback {


    @Override
    public RdsModel createModel() {
        return new RdsRepository(this);
    }
}