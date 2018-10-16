package com.jancar.radio.model;


import com.jancar.radio.entity.RadioStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.jancar.radio.listener.utils.RadioStationDaos.delete;
import static com.jancar.radio.listener.utils.RadioStationDaos.deleteRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.insertRadioList;
import static com.jancar.radio.listener.utils.RadioStationDaos.insertRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.queryFrequency;
import static com.jancar.radio.listener.utils.RadioStationDaos.queryFrequencyFreq;
import static com.jancar.radio.listener.utils.RadioStationDaos.updateRadioStation;
import static com.jancar.radio.listener.utils.RadioStationDaos.updateRadioStationList;

/**
 * @author Tzq
 * @date 2018-9-4 16:00:23
 */
public class RadioRepository implements RadioModel {

    private Callback mCallback;

    public RadioRepository(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public List<RadioStation> initText(int band, int mLocation) {
        return queryFrequency(band, mLocation);
    }

    @Override
    public void Replace(RadioStation radioStations, RadioStation radioStation) {

        List<RadioStation> stationList = queryFrequency(radioStations.getFrequency(), radioStations.getLocation());
        for (RadioStation mRadioStation : stationList) {
            mRadioStation.setSelect(false);
        }
        updateRadioStationList(stationList);
        List<RadioStation> radioStationList = queryFrequencyFreq(radioStations.getFrequency(), radioStations.getLocation(), radioStations.getMFreq());
        if (radioStationList.size() > 0) {
            if (radioStation != null) {
                radioStation.setPosition(radioStationList.get(0).getPosition());
                radioStation.setSelect(false);
                updateRadioStation(radioStation);
            }
            radioStationList.get(0).setPosition(radioStations.getPosition());
            radioStationList.get(0).setSelect(true);
            updateRadioStation(radioStationList.get(0));
        } else {
            if (radioStation != null) {
                deleteRadioStation(radioStation.get_id());
            }
            insertRadioStation(radioStations);
           /* RadioStation radioStations=new RadioStation();
            radioStations.setRdsname();*/
        }
    }

    @Override
    public RadioStation select(int i, Integer tag, int mLocation) {
        RadioStation radioStation = null;
        List<RadioStation> stationList = queryFrequency(tag, mLocation);
        for (RadioStation mRadioStation : stationList) {
            if (i == mRadioStation.getPosition()) {
                mRadioStation.setSelect(true);
                radioStation = mRadioStation;
            } else {
                mRadioStation.setSelect(false);
            }
        }
        updateRadioStationList(stationList);
        return radioStation;
    }

    @Override
    public void Change(int frequency, int mFreq, int mLocation) {
        List<RadioStation> radioStationList = queryFrequencyFreq(frequency, mLocation, mFreq);
        List<RadioStation> stationList = queryFrequency(frequency, mLocation);
        if (radioStationList.size() > 0) {
            for (RadioStation mRadioStation : stationList) {
                if (radioStationList.get(0).getPosition() == mRadioStation.getPosition()) {
                    mRadioStation.setSelect(true);
                } else {
                    mRadioStation.setSelect(false);
                }
            }
        } else {
            for (RadioStation mRadioStation : stationList) {
                mRadioStation.setSelect(false);
            }
        }
        updateRadioStationList(stationList);
    }

    @Override
    public void processAndSave(ArrayList<RadioStation> mScanResultList, int mBand ,int mLocation) {
        Collections.sort(mScanResultList, new Comparator<RadioStation>() {
            @Override
            public int compare(RadioStation u1, RadioStation u2) {
                int diff = u1.getKind() - u2.getKind();
                if (diff < 0) {
                    return 1;
                } else if (diff > 0) {
                    return -1;
                }
                return 0; //相等为0
            }
        }); // 按强度排序
        for (int i = 0; i < mScanResultList.size(); i++) {
            if (mBand == 1) {
                if (i <= 5) {
                    mScanResultList.get(i).setPosition(i);
                    mScanResultList.get(i).setFrequency(0);

                } else if (i > 5 && i <= 11) {
                    mScanResultList.get(i).setPosition(i - 6);
                    mScanResultList.get(i).setFrequency(1);
                } else if (i > 11 && i <= 17) {
                    mScanResultList.get(i).setPosition(i - 12);
                    mScanResultList.get(i).setFrequency(2);
                }
            } else {
                if (i <= 5) {
                    mScanResultList.get(i).setPosition(i);
                    mScanResultList.get(i).setFrequency(3);
                } else if (i > 5 && i <= 11) {
                    mScanResultList.get(i).setPosition(i - 6);
                    mScanResultList.get(i).setFrequency(4);
                } else if (i > 11 && i <= 17) {
                    mScanResultList.get(i).setPosition(i - 12);
                    mScanResultList.get(i).setFrequency(5);
                }
            }
        }
        ArrayList<RadioStation> mScanResultLists = new ArrayList<>();
        delete(mBand ,mLocation);
        if (mScanResultList.size() > 18) {
            mScanResultLists.addAll(mScanResultList.subList(0, 18));
        } else {
            mScanResultLists.addAll(mScanResultList);
        }
        insertRadioList(mScanResultLists);

    }
}