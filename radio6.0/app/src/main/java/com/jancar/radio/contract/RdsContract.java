package com.jancar.radio.contract;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.widget.SeekBar;

import com.jancar.radio.RadioManager;
import com.jancar.radio.entity.RadioStation;
import com.ui.mvp.presenter.IPresenter;
import com.ui.mvp.view.Ui;

import java.util.ArrayList;
import java.util.List;

public interface RdsContract {
    interface View extends Ui {

    }

    interface Presenter extends IPresenter {


    }
}
