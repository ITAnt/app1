package com.jancar.bluetooth.phone.util;

/**
 * @anthor Tzq
 * @time 2018/8/31 9:54
 * @describe 常量定义
 */
public class Constants {
    //跳转设置
    public static final String PACKNAME = "com.jancar.settingss";
    public static final String CLASSNAME = "com.jancar.settings.view.activity.MainActivity";
    public static final String SETTING_POSITION = "position";
    public static final int SETTING_POSITION_NUM = 1;
    //开启电话服务
    public static final String BTUISERVICE_PACKAGENAME = "com.jancar.bluetooth.phone";
    public static final String BTUISERVICE_CLASSNAME = "com.jancar.bluetooth.phone.view.BTUIService";

    public static final String BTDIAL_KEY = "btdial";

    public final static byte BT_CONNECT_IS_NONE = (byte) 0x01;//蓝牙未连接
    public final static byte BT_CONNECT_IS_CONNECTED = (byte) 0x02;//蓝牙已连接
    public final static byte BT_CONNECT_IS_CLOSE = (byte) 0x00;//蓝牙已关闭

    public final static int BLUETOOTH_DEVICE_BONDED = 0;
    public final static int BLUETOOTH_DEVICE_NOBOND = 1;
    public final static int BLUETOOTH_DEVICE_STATE_DISCONNECT = 2;
    public final static int BLUETOOTH_DEVICE_STATE_CONNECT = 3;

    public final static int PHONEBOOK_STATE_START = 1;//同步开始
    public final static int PHONEBOOK_STATE_STOP = 2;//同步中止
    public final static int PHONEBOOK_STATE_FINSH = 4;//同步结束
    public final static int PHONEBOOK_STATE_ERR = 3;//同步错误

    public final static int PHONEBOOK_DATA_REFRESH = 10;//拨号界面搜索
    public final static int CONTACT_DATA_REFRESH = 11;//搜索联系人
    public final static int CONTACT_UPDATA_REFRESH = 12;//联系人
    public final static int CONTACT_SEARCH_REFRESH = 13;
    public final static int CONTACT_BT_CONNECT = 14;//联系人界面蓝牙状态
    public final static int CONTACT_CALL_LOGS = 15;//通话记录
    public final static int PHONEBOOK_SEACH_LIST = 16;//拨号界面list

    public final static int CONTACT_SEARCH_START = 17;//联系人界面搜索开始
    public final static int CONTACT_SEARCH_END = 18;//联系人界面搜索介绍
    public final static int CONTACT_SEARCH_CHANGE = 19;//
    public final static int CONTACT_CALL_LOGS_START = 20;//通话记录
    public final static int CONTACT_CALL_LOGS_FINISH = 21;//通话记录
    public final static int CONTACT_CALL_LOGS_COUNT = 24;//通话记录
    public final static int Dial_UPDATE_TEXT = 22;//拨号界面更新输入号码
    public final static String MAIN_TAB = "btpb";//
    public final static int GET_BTCONTACTS = 23;//联系人界面获取联系人
    public final static int CONTACT_CALL_LOGS_FIRST = 24;//通话记录第一条数据
    public final static int DIAL_CONTACT_FINISH = 25;//拨号界面下载同步联系人完成

}
