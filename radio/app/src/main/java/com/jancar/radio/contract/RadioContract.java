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

/**
 * Created by ouyan on 2018/9/19.
 */

public interface RadioContract {
    interface View extends Ui {
        RadioManager getRadioManager();

        void initText(List<RadioStation> radioStations);

        void init(List<RadioStation> radioStations);

        void initSelect(List<RadioStation> radioStations, RadioStation mRadioStation);

        void requestRadioFocus();

        void abandonRadioFocus();

        Activity getActivity();
    }

    interface Presenter extends IPresenter, SeekBar.OnSeekBarChangeListener {

        int getBandPage(int mBand);

        RadioManager.RadioListener getRadioListener();

        @Override
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        @Override
        void onStartTrackingTouch(SeekBar seekBar);

        @Override
        void onStopTrackingTouch(SeekBar seekBar);

        void initText(int Band, int mLocation);

        void Replace(RadioStation radioStations, RadioStation radioStation);

        void select(int i, Integer tag, int mLocation);

        void Change(int frequency, int mFreq, int mLocation);
        void Change( int mFreq,List<RadioStation> radioStations);
        BroadcastReceiver getmReceiver();

        void save(ArrayList<RadioStation> mScanResultList, int mBand, int Band, int mLocation);
    }
}
