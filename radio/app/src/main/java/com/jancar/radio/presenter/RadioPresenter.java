package com.jancar.radio.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.SeekBar;


import com.jancar.radio.RadioManager;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.entity.RadioStation;
import com.jancar.radio.model.RadioModel;
import com.jancar.radio.model.RadioRepository;
import com.jancar.utils.Logcat;
import com.ui.mvp.presenter.BaseModelPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class RadioPresenter extends BaseModelPresenter<RadioContract.View, RadioModel> implements RadioContract.Presenter, RadioModel.Callback {

    public static class DataSynEvent {
        private int count;
        public boolean bChanged;

        public DataSynEvent(boolean b, int count) {
            this.bChanged = b;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class DataSynEvents {
        private int count;
        public boolean bChanged;

        public DataSynEvents(boolean b, int count) {
            this.bChanged = b;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    @Override
    public RadioModel createModel() {
        return new RadioRepository(this);
    }

    @Override
    public int getBandPage(int iband) {
        int ipage = 0;
        switch (iband) {
            case 1:
                ipage = PAGE_FM;
                break;
            case 0:
                ipage = PAGE_AM;
                break;
        }
        return ipage;

    }

    @Override
    public RadioManager.RadioListener getRadioListener() {
        return mRadioListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        EventBus.getDefault().post(new RadioPresenter.DataSynEvent(fromUser, progress));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
             getUi().onStartTrackingTouch();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        EventBus.getDefault().post(new RadioPresenter.DataSynEvents(seekBar.callOnClick(), seekBar.getProgress()));

    }

    @Override
    public void initText(int Band, int mLocation, boolean first_run) {
        getUi().initText(getModel().initText(Band, mLocation, first_run));
    }

    @Override
    public void Replace(RadioStation radioStations, RadioStation radioStation, boolean first_run) {
        getModel().Replace(radioStations, radioStation);
        getUi().initText(getModel().initText(radioStations.getFrequency(), radioStations.getLocation(), first_run));
        //initText();
    }

    @Override
    public void select(int i, Integer tag, int mLocation, boolean first_run) {
        RadioStation radioStation=   getModel().select(i, tag, mLocation);
        getUi().initSelect(getModel().initText(tag, mLocation, first_run),radioStation);
    }
    @Override
    public void select(Integer tag, int mLocation, boolean first_run) {
           getModel().select( tag, mLocation);
           getUi().initSelect(getModel().initText(tag, mLocation, first_run));
    }

    @Override
    public void Change(int frequency, int mFreq, int mLocation) {
        getModel().Change(frequency, mFreq, mLocation);
        // getUi().init(getModel().initText(frequency, mLocation));
    }

    @Override
    public void Change(List<RadioStation> radioStations) {
        getModel().Change(radioStations);
    }

    @Override
    public void Change(int mFreq, List<RadioStation> radioStations) {
        for (RadioStation mRadioStation : radioStations) {
            if (mRadioStation.getMFreq() == mFreq) {
                mRadioStation.setSelect(true);
            } else {
                mRadioStation.setSelect(false);
            }
        }
        getModel().Change(radioStations);
        getUi().init(radioStations);
    }

    @Override
    public void Change(int mFreq, List<RadioStation> radioStations,int mSignalStrength) {
        List<RadioStation> radioStation=new ArrayList<>();
       List<Integer> a=new ArrayList<>();
       int i=0;
        Iterator<RadioStation> it = radioStations.iterator();
        while(it.hasNext()){
            RadioStation x = it.next();
            if (x.getMFreq() == mFreq) {
                if (mSignalStrength>0){
                    x.setSelect(true);
                }else {
                    radioStation.add(x);
                    it.remove();
                }
            } else {
                x.setSelect(false);
            }
        }
        deleteRadioStation(radioStation);
        getUi().init(radioStations);
    }


    public RadioManager.RadioListener mRadioListener = new RadioManager.RadioListener() {
        @Override
        public void next() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.NEXT));
        }

        @Override
        public void onFreqChanged(final int freq) {

            EventBus.getDefault().post(new RadioWrapper.EventFreqChanged(freq));
        }

        @Override
        public void onRdsMaskChanged(final int ipi, final int ifreq, final int ipty, final int itp, final int ita) {
            EventBus.getDefault().post(new RadioWrapper.EventRdsMask(ipi, ifreq, ipty, itp, ita));
        }

        @Override
        public void onRdsPsChanged(final int ipi, final int ifreq, final String strPs) {
            EventBus.getDefault().post(new RadioWrapper.EventRdsPs(ipi, ifreq, strPs));
        }

        @Override
        public void onRdsRtChanged(final int ipi, final int ifreq, final String strRt) {
            EventBus.getDefault().post(new RadioWrapper.EventRdsRt(ipi, ifreq, strRt));
        }

        @Override
        public void onScanAbort(final boolean babort) {
            EventBus.getDefault().post(new RadioWrapper.EventScanAbort(babort));
        }

        @Override
        public void onScanEnd(final boolean bend) {
            EventBus.getDefault().post(new RadioWrapper.EventScanEnd(bend));
        }

        @Override
        public void onScanResult(final int arg1, final int arg2) {
            EventBus.getDefault().post(new RadioWrapper.EventScanResult(arg1, arg2));
        }

        @Override
        public void onScanStart(final boolean bstart) {
            EventBus.getDefault().post(new RadioWrapper.EventScanStart(bstart));
        }

        @Override
        public void onSignalUpdate(final int n, final int n2) {
        }

        @Override
        public void pause() {
            Logcat.d("RadioListener, pause");
        }

        @Override
        public void play() {
            Logcat.d("RadioListener, play");
        }

        @Override
        public void playPause() {
            Logcat.d("RadioListener, playPause");
        }

        @Override
        public void prev() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.PREV));
        }

        @Override
        public void quitApp() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.QUIT_APP));
        }

        @Override
        public void resume() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.RESUME));
        }

        @Override
        public void scanAll() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.SCAN_ALL));
        }

        @Override
        public void scanDown() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.SCAN_DOWN));
        }

        @Override
        public void scanUp() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.SCAN_UP));
        }

        @Override
        public void select(final int index) {
            Logcat.d("RadioListener, index = " + index);
        }

        @Override
        public void setFavour(final boolean bFavour) {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.SET_FAVOUR, bFavour ? 1 : 0));
        }

        @Override
        public void stop() {
            EventBus.getDefault().post(new RadioWrapper.EventControl(
                    RadioWrapper.EventControl.Action.STOP));
        }

        @Override
        public void suspend() {
            Logcat.d("RadioListener, suspend");
        }

        @Override
        public void requestRadioFocus() {
            getUi().requestRadioFocus();
            //    RadioMainActivity.this.mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        @Override
        public void abandonRadioFocus() {
            getUi().abandonRadioFocus();
            // RadioMainActivity.this.mAudioManager.abandonAudioFocus(mAudioFocusChange);
        }

        @Override
        public void onStereo(int i, boolean b) {
            getUi().onStereo(i, b);
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(final Context context, final Intent intent) {
            Logcat.d("onReceive  intent.getAction(): " + intent.getAction());

            if (TextUtils.equals((CharSequence) intent.getAction(), (CharSequence) "android.intent.action.LOCALE_CHANGED")) {

                if (getUi().getRadioManager() == null ||
                        getUi().getRadioManager().getRdIsOpenDev()) {

                 /*   if (RadioMainActivity.this.getCurRadioFragment().getRegionDialog() != null &&
                            RadioMainActivity.this.getCurRadioFragment().getRegionDialog().isShowing()) {
                        RadioMainActivity.this.getCurRadioFragment().getRegionDialog().cancel();
                    }*/


                /*    if (RadioMainActivity.this.mAllFragments != null) {
                        final Iterator<ViewBaseFragment> iterator = RadioMainActivity.this.mAllFragments.iterator();
                        while (iterator.hasNext()) {
                            beginTransaction.remove(iterator.next());
                        }
                    }*/


                    return;
                }

                getUi().getActivity().finish();
            }
        }
    };

    @Override
    public BroadcastReceiver getmReceiver() {
        return mReceiver;
    }

    @Override
    public void save(ArrayList<RadioStation> mScanResultList, int mBand, int Band, int mLocation, boolean first_run) {
        getModel().processAndSave(mScanResultList, mBand, mLocation);
        getUi().initText(getModel().initText(Band, mLocation, first_run));
    }

    @Override
    public List<RadioStation> queryFrequency(int band, int mLocation) {
        return   getModel().initText(band, mLocation, false);
    }
}