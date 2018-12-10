package com.jancar.radio.model;

import com.jancar.radio.entity.Collection;
import com.jancar.radio.entity.RadioStation;
import com.ui.mvp.model.Model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:23
 */
public interface RadioModel extends Model {

    List<RadioStation> initText(int band, int mLocation,boolean first_run);

    void Replace(RadioStation radioStations, RadioStation radioStation);

    RadioStation select(int i, Integer tag ,int mLocation);
    void select(Integer tag, int mLocation);
    void Change(int frequency, int mFreq, int mLocation);

    void processAndSave(ArrayList<RadioStation> mScanResultList, int mBand ,int mLocation);

    void Change(List<RadioStation> radioStations);

    boolean isFrequency(int mFreq);

    List<Collection> addCollection(Collection mRadioStation);

    List<Collection> deleteCollection(int mFreq);

    List<Collection> setCollection();

    List<Collection> deleteAll();

    interface Callback {

    }

}
