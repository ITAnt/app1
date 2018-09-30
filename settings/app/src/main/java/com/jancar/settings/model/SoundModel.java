package com.jancar.settings.model;

import android.content.Context;

import com.jancar.settings.R;
import com.jancar.settings.contract.EqEntity;
import com.jancar.settings.listener.Contract.SoundContractImpl;
import com.jancar.settings.listener.Contract.TimeContractImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouyan on 2018/9/7.
 */

public class SoundModel implements SoundContractImpl.Model {
    @Override
    public void onDestroy() {

    }

    @Override
    public List<EqEntity> initList(Context mContext) {
        List<EqEntity> stringList = new ArrayList<>();
        //String sAgeFormat = getResources().getString(R.string.txt_channel);
        stringList.add(new EqEntity(mContext. getResources().getString(R.string.txt_standard),7,7,7));
        stringList.add(new EqEntity(mContext. getResources().getString(R.string.txt_popular),10,10,10));
        stringList.add(new EqEntity(mContext. getResources().getString(R.string.txt_ock),4,7,13));
        stringList.add(new EqEntity(mContext. getResources().getString(R.string.txt_jazz),4,4,4));
        stringList.add(new EqEntity(mContext. getResources().getString(R.string.txt_classical),0,8,0));
        stringList.add(new EqEntity(mContext. getResources().getString(R.string.txt_customize),0,0,0));

        return stringList;
    }
}
