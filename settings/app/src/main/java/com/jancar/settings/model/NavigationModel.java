package com.jancar.settings.model;

import com.jancar.settings.R;
import com.jancar.settings.contract.NavigationEntity;
import com.jancar.settings.listener.Contract.NavigationContractImpl;
import com.jancar.settings.listener.Contract.SoundContractImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouyan on 2018/9/10.
 */

public class NavigationModel implements NavigationContractImpl.Model {
    @Override
    public void onDestroy() {

    }

    private String[] name = new String[]{"高德地图", "凯立德地图", "搜狗地图", "谷歌地图", "百度地图"};
    int[] img = new int[]{R.mipmap.img_timg, R.mipmap.img_careland, R.mipmap.img_sogou, R.mipmap.img_goole,R.mipmap.img_baidu};

    @Override
    public List<NavigationEntity> getListData() {
        List<NavigationEntity> list = new ArrayList<>();
        for (int i=0;i<name.length;i++){
            NavigationEntity mNavigationEntity=new NavigationEntity();
            mNavigationEntity.setImg(img[i]);
            mNavigationEntity.setName(name[i]);
            list.add(mNavigationEntity);
        }
        return list;
    }
}