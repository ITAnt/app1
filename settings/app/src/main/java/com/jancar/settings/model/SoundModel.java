package com.jancar.settings.model;

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
    public List<EqEntity> initList() {
        List<EqEntity> stringList = new ArrayList<>();

        stringList.add(new EqEntity("标准",7,7,7));
        stringList.add(new EqEntity("流行",10,10,10));
        stringList.add(new EqEntity("摇滚",4,7,13));
        stringList.add(new EqEntity("爵士",4,4,4));
        stringList.add(new EqEntity("古典",0,8,0));
        stringList.add(new EqEntity("自定义",0,0,0));

        return stringList;
    }
}
