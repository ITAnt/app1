package com.jancar.radio.listener.utils;

import com.jancar.radio.contract.RadioApplication;
import com.jancar.radio.entity.Collection;
import com.jancar.radio.entity.RadioStation;
import com.jancar.radio.greendao.CollectionDao;
import com.jancar.radio.greendao.RadioStationDao;

import java.util.List;

/**
 * Created by wangjitao on 2017/2/13 0013.
 * E-Mail：543441727@qq.com
 * 使用GreenDao 实现简单的增删改查，下面是基本方法
 * 增加单个数据
 * getShopDao().insert(shop);
 * getShopDao().insertOrReplace(shop);
 * 增加多个数据
 * getShopDao().insertInTx(shopList);
 * getShopDao().insertOrReplaceInTx(shopList);
 * 查询全部
 * List< Shop> list = getShopDao().loadAll();
 * List< Shop> list = getShopDao().queryBuilder().list();
 * 查询附加单个条件
 * .where()
 * .whereOr()
 * 查询附加多个条件
 * .where(, , ,)
 * .whereOr(, , ,)
 * 查询附加排序
 * .orderDesc()
 * .orderAsc()
 * 查询限制当页个数
 * .limit()
 * 查询总个数
 * .count()
 * 修改单个数据
 * getShopDao().update(shop);
 * 修改多个数据
 * getShopDao().updateInTx(shopList);
 * 删除单个数据
 * getTABUserDao().delete(user);
 * 删除多个数据
 * getUserDao().deleteInTx(userList);
 * 删除数据ByKey
 * getTABUserDao().deleteByKey();
 */

public class CollectionStationDaos {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param shop
     */
    public static void insertCollectionStation(Collection shop) {
        RadioApplication.getDaoInstant().getCollectionDao().insertOrReplace(shop);
    }


    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteCollectionStation(long id) {
        RadioApplication.getDaoInstant().getCollectionDao().deleteByKey(id);
    }
    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteCollectionStation(int id) {
        RadioApplication.getDaoInstant().getCollectionDao().deleteInTx(RadioApplication.getDaoInstant().getCollectionDao().queryBuilder().where(CollectionDao.Properties.MFreq.eq(id)).list());


    }
    /**
     * 删除数据
     *
     * @param shop
     */
    public static void deleteRadioStation(List<RadioStation> shop) {
        RadioApplication.getDaoInstant().getRadioStationDao().deleteInTx(shop);
    }

    /**
     * 删除数据
     *
     *
     */
    public static void deleteAll() {
        RadioApplication.getDaoInstant().getCollectionDao().deleteAll();

       /* RadioApplication.getDaoInstant().getRadioStationDao().;*/
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public static List<Collection> queryAll() {
        return RadioApplication.getDaoInstant().getCollectionDao().loadAll();
    }
    /**
     * 查询所有数据
     *
     * @return
     */
    public static boolean queryFreq(int Freq) {
        return (RadioApplication.getDaoInstant().getCollectionDao().queryBuilder().where(CollectionDao.Properties.MFreq.eq(Freq)).count())!=0;

    }

}