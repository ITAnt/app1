package com.jancar.radio.model;


import com.jancar.radio.RadioWrapper;
import com.jancar.radio.entity.RadioStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.jancar.radio.listener.utils.RadioStationDaos.delete;
import static com.jancar.radio.listener.utils.RadioStationDaos.deleteRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.deletes;
import static com.jancar.radio.listener.utils.RadioStationDaos.insertRadioList;
import static com.jancar.radio.listener.utils.RadioStationDaos.insertRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.insertRadioStationList;
import static com.jancar.radio.listener.utils.RadioStationDaos.queryFrequency;
import static com.jancar.radio.listener.utils.RadioStationDaos.queryFrequencyFreq;
import static com.jancar.radio.listener.utils.RadioStationDaos.updateRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.updateRadioStationList;

/**
 * @author Tzq
 * @date 2018-9-4 16:00:23
 */
public class RdsRepository implements RdsModel {

    private Callback mCallback;

    public RdsRepository(Callback callback) {
        this.mCallback = callback;
    }

}
