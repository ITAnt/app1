package com.jancar.radio.listener.utils;

import com.jancar.radio.contract.RadioApplication;
import com.jancar.radio.entity.RadioStation;
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

public class RadioStationDaos {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param shop
     */
    public static void insertRadioStation(RadioStation shop) {
        RadioApplication.getDaoInstant().getRadioStationDao().insertOrReplace(shop);
    }
    public static void insertRadioStationList(List<RadioStation> shop) {
        RadioApplication.getDaoInstant().getRadioStationDao().insertOrReplaceInTx(shop);
    }
    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param shop
     */
    public static void insertRadioList(List<RadioStation> shop) {
        RadioApplication.getDaoInstant().getRadioStationDao().insertOrReplaceInTx(shop);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteRadioStation(long id) {
        RadioApplication.getDaoInstant().getRadioStationDao().deleteByKey(id);
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
     * @param mBand
     */
    public static void  delete(int mBand ,int mLocation) {
        RadioApplication.getDaoInstant().getRadioStationDao().deleteInTx(RadioApplication.getDaoInstant().getRadioStationDao().queryBuilder().where(RadioStationDao.Properties.MBand.eq(mBand),RadioStationDao.Properties.Location.eq(mLocation)).list());

       /* RadioApplication.getDaoInstant().getRadioStationDao().;*/
    }
    /**
     * 删除数据
     *
     * @param mBand
     */
    public static void deletes(int mBand ,int mLocation) {
        RadioApplication.getDaoInstant().getRadioStationDao().deleteInTx(RadioApplication.getDaoInstant().getRadioStationDao().queryBuilder().where(RadioStationDao.Properties.Frequency.eq(mBand),RadioStationDao.Properties.Location.eq(mLocation)).list());

       /* RadioApplication.getDaoInstant().getRadioStationDao().;*/
    }
    /**
     * 更新数据
     */
    public static void updateRadioStation(RadioStation shop) {
        RadioApplication.getDaoInstant().getRadioStationDao().update(shop);
    }

    /**
     * 更新数据列表
     */
    public static void updateRadioStationList(List<RadioStation> shop) {
        RadioApplication.getDaoInstant().getRadioStationDao().updateInTx(shop);
    }

    /**
     * 通过频率查询所对应的频率的所有数据
     *
     * @return
     */
    public static List<RadioStation> queryFrequency(int frequency, int mLocation) {

        return RadioApplication.getDaoInstant().getRadioStationDao().queryBuilder().where(RadioStationDao.Properties.Frequency.eq(frequency), RadioStationDao.Properties.Location.eq(mLocation)).orderAsc(RadioStationDao.Properties.Position).list();

    }

    /**
     * 通过频率
     *
     * @return
     */
    public static List<RadioStation> queryFrequencyFreq(int frequency, int mLocation, int freq) {
        return RadioApplication.getDaoInstant().getRadioStationDao().queryBuilder().where(RadioStationDao.Properties.Frequency.eq(frequency), RadioStationDao.Properties.Location.eq(mLocation), RadioStationDao.Properties.MFreq.eq(freq)).list();
    }


    /**
     * 查询所有数据
     *
     * @return
     */
    public static List<RadioStation> queryAll() {
        return RadioApplication.getDaoInstant().getRadioStationDao().loadAll();
    }

}