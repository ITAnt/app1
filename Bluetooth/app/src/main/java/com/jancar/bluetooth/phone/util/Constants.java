package com.jancar.bluetooth.phone.util;

/**
 * @anthor Tzq
 * @time 2018/8/31 9:54
 * @describe 常量定义
 */
public class Constants {
    public static byte BT_CONNECT_IS_NONE = (byte) 0x01;//蓝牙未连接
    public static byte BT_CONNECT_IS_CONNECTED = (byte) 0x02;//蓝牙已连接
    public static byte BT_CONNECT_IS_CLOSE = (byte) 0x00;//蓝牙已关闭

    public static final int BLUETOOTH_DEVICE_BONDED = 0;
    public static final int BLUETOOTH_DEVICE_NOBOND = 1;
    public static final int BLUETOOTH_DEVICE_STATE_DISCONNECT = 2;
    public static final int BLUETOOTH_DEVICE_STATE_CONNECT = 3;


    public final static int PHONEBOOK_STATE_START = 1;//同步开始
    public final static int PHONEBOOK_STATE_STOP = 2;//同步中止
    public final static int PHONEBOOK_STATE_FINSH = 4;//同步结束
    public final static int PHONEBOOK_STATE_ERR = 3;//同步错误

    public final static int PHONEBOOK_DATA_REFRESH = 10;
    public final static int CONTACT_DATA_REFRESH = 11;

}
