package com.jancar.model;

import android.content.Context;
import android.util.Log;
import com.jancar.common.XUtils;
import com.jancar.mcu.McuClient;

public class WheelKeyLearnController {
    private final static String TAG = WheelKeyLearnController.class.getSimpleName();
    private McuClient mcuClient;
    private Context context;

    public WheelKeyLearnController(Context context) {
        init(context);
    }

    public void setWheelKeyListener(WheelKeyListener wheelKeyListener) {
        this.wheelKeyListener = wheelKeyListener;
    }

    private WheelKeyListener wheelKeyListener;

    public interface WheelKeyListener {
        void onEnterLearn();

        void onExitLearn();

        void onNotifyToachKey();

        void onSuccess();

        void onFail();

        void onRefreshAadpter(byte[] datas);
    }

    public void init(Context context) {
        mcuClient = new McuClient("WheelKeyLearn");
        mcuClient.Init(context);
        mcuClient.Register(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST);
        mcuClient.Register(WheelKey.CMD_LEARN_STATUS_RESPONE);
        mcuClient.Register(WheelKey.CMD_LEARN_STATUS_SCAN_RESPONE);
        mcuClient.setMcuDataListener(new McuClient.McuDataListener() {
            @Override
            public void RcvFromMcu(byte b, byte[] bytes, int b1, int b2) {
                Log.e(TAG, "RcvFromMcu==" + XUtils.Byte2Hex(b) + "bytes==" + XUtils.ByteArrToHex(bytes));
                doExcuteCommand(b, bytes);
            }
        });
        this.context = context;
    }

    private void doExcuteCommand(byte command, byte[] datas) {
        byte subcomand;
        if (datas.length > 1) {
            switch (command) {
                case WheelKey.CMD_LEARN_STATUS_RESPONE:
                    subcomand = datas[0];
                    doExcuteLearningStatusSub(subcomand, datas);
                    break;
                case WheelKey.CMD_LEARN_STATUS_SCAN_RESPONE:
                    doExcuteLearnStatusScan(datas);
                    break;
            }
        }
    }

    private void doExcuteLearningStatusSub(byte submand, byte[] datas) {
        byte simpleValue, keyValue;
        switch (submand) {
            case WheelKey.CMD_UNLEARN_STATUS_SPONSE:
                simpleValue = datas[1];
                keyValue = datas[2];
                if (simpleValue == 0 && keyValue == 0) {
                    if (wheelKeyListener != null) {
                        wheelKeyListener.onExitLearn();
                    }
                }
                break;
            case WheelKey.CMD_ENTER_LEARNING_STATUS_SPONSE:
                simpleValue = datas[1];
                keyValue = datas[2];
                if (simpleValue == 0 && keyValue == 0) {
                    if (wheelKeyListener != null) {
                        wheelKeyListener.onEnterLearn();
                    }
                }

                break;
            case WheelKey.CMD_LEARNING_SPONSE:
                simpleValue = datas[1];
                keyValue = datas[2];
                if (simpleValue == 0 && keyValue == 0) {
                    if (wheelKeyListener != null) {
                        wheelKeyListener.onNotifyToachKey();
                    }
                }
                break;
            case WheelKey.CMD_AD_FAIL_SPONSE:
            case WheelKey.CMD_AD_NEAR_SPONSE:
            case WheelKey.CMD_AD_UNSTALE_SPONSE:
            case WheelKey.CMD_TIME_OUT_SPONSE:
            case WheelKey.CMD_KEY_UNKOWN_SPONSE:
                if (wheelKeyListener != null) {
                    wheelKeyListener.onFail();
                }
                break;
            case WheelKey.CMD_LEARN_SUCCESS_SPONSE:
                simpleValue = datas[1];
                keyValue = datas[2];
                if (simpleValue == 0 && keyValue == 0) {
                    if (wheelKeyListener != null) {
                        wheelKeyListener.onSuccess();
                    }
                }
                break;
        }
    }

    private void doExcuteLearnStatusScan(byte[] datas) {
        byte[] array = new byte[21];
        //第一个八位
        byte arryValue1 = datas[0];
        byte value = (byte) (arryValue1 & (byte) 0x01);//电源键
        array[0] = (value);

        value = (byte) (arryValue1 >> 2 & (byte) 0x01);//电台搜索
        array[1] = (value);

        value = (byte) (arryValue1 >> 3 & (byte) 0x01);//模式键
        array[2] = (value);

        value = (byte) (arryValue1 >> 6 & (byte) 0x01);//音量减
        array[3] = (value);

        value = (byte) (arryValue1 >> 7 & (byte) 0x01);//音量加
        array[4] = (value);
        //第二个八位
        byte arryValue2 = datas[1];
        value = (byte) (arryValue2 >> 1 & (byte) 0x01);//上一曲
        array[5] = (value);

        value = (byte) (arryValue2 >> 2 & (byte) 0x01);//下一曲
        array[6] = (value);

//        value = (byte) (arryValue2 >> 5 & (byte) 0x01);//语音识别
//        array[7] = (value);

        value = (byte) (arryValue2 >> 6 & (byte) 0x01);//设置
        array[7] = (value);

        value = (byte) (arryValue2 >> 7 & (byte) 0x01);//主界面
        array[8] = (value);
        //第三个八位
        byte arryValue3 = datas[2];
        value = (byte) (arryValue3 >> 0 & (byte) 0x01);//菜单
        array[9] = (value);

        value = (byte) (arryValue3 >> 1 & (byte) 0x01);//返回
        array[10] = (value);

        value = (byte) (arryValue3 >> 3 & (byte) 0x01);//GPS
        array[11] = (value);

        value = (byte) (arryValue3 >> 5 & (byte) 0x01);//音乐
        array[12] = (value);

        value = (byte) (arryValue3 >> 6 & (byte) 0x01);//视频
        array[13] = (value);
        //第四个八位
        byte arryValue4 = datas[3];
        value = (byte) (arryValue4 >> 1 & (byte) 0x01);//接听
        array[14] = (value);

        value = (byte) (arryValue4 >> 2 & (byte) 0x01);//挂断
        array[15] = (value);

        value = (byte) (arryValue4 >> 6 & (byte) 0x01);//FM
        array[16] = (value);
        //第5个八位
        byte arryValue5 = datas[4];
//        value = (byte) (arryValue5 >> 1 & (byte) 0x01);//确认
//        array[18] = (value);

        value = (byte) (arryValue5 >> 5 & (byte) 0x01);//AM
        array[17] = (value);

        value = (byte) (arryValue5 >> 6 & (byte) 0x01);//AUX
        array[18] = (value);
        //第六个八位
        byte arryValue6 = datas[5];
        value = (byte) (arryValue6 >> 3 & (byte) 0x01);//静音
        array[19] = (value);

        value = (byte) (arryValue6 >> 4 & (byte) 0x01);//蓝牙音乐
        array[20] = (value);

//        value = (byte) (arryValue6 >> 5 & (byte) 0x01);//行车记录仪
//        array[23] = (value);

        if (wheelKeyListener != null) {
            wheelKeyListener.onRefreshAadpter(array);
        }
    }

    public void release(Context context) {
        mcuClient.UnRegister(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST);
        mcuClient.UnRegister(WheelKey.CMD_LEARN_STATUS_RESPONE);
        mcuClient.UnRegister(WheelKey.CMD_LEARN_STATUS_SCAN_RESPONE);
        mcuClient.DeInit(context);
    }

    /**
     * 开始按键学习
     */
    public void startLearn() {
        if (mcuClient == null) {
            Log.e(TAG, "mcuClient===" + mcuClient);
            return;
        }
        byte datas[];
        datas = new byte[]{WheelKey.CMD_START_LEARN_ACTION, 0x00, 0x00};
        int len = datas.length;
        mcuClient.Send2Mcu(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST, datas, (byte) len);
        Log.e(TAG, "Send2Mcu " + XUtils.Byte2Hex(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST) + "data==" + XUtils.ByteArrToHex(datas));
    }

    /**
     * 结束按键学习
     */
    public void endLearn() {
        if (mcuClient == null) {
            Log.e(TAG, "mcuClient===" + mcuClient);
            return;
        }
        byte datas[];
        datas = new byte[]{WheelKey.CMD_EXIT_LEARN_ACTION, 0x00, 0x00};
        int len = datas.length;
        mcuClient.Send2Mcu(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST, datas, (byte) len);
        Log.e(TAG, "Send2Mcu " + XUtils.Byte2Hex(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST) + "data==" + XUtils.ByteArrToHex(datas));
    }

    /**
     * 清除按键状态
     */
    public void clearLearn() {
        if (mcuClient == null) {
            Log.e(TAG, "mcuClient===" + mcuClient);
            return;
        }
        byte datas[];
        datas = new byte[]{WheelKey.CMD_CLEAR_LEARN_ACTION, 0x00, 0x00};
        int len = datas.length;
        mcuClient.Send2Mcu(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST, datas, (byte) len);
        Log.e(TAG, "Send2Mcu " + XUtils.Byte2Hex(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST) + "data==" + XUtils.ByteArrToHex(datas));
    }

    /***
     * 发送键值和按键状态给MCU
     * @param clickStatus
     * @param keyValue
     */
    public void sendKeyValueToMcu(byte clickStatus, byte keyValue) {
        byte[] datas = new byte[3];
        int len = datas.length;
        datas[0] = WheelKey.CMD_LEARNING_ACTION;
        datas[1] = keyValue;
        datas[2] = clickStatus;
        mcuClient.Send2Mcu(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST, datas, (byte) len);
        Log.e(TAG, "Send2Mcu " + XUtils.Byte2Hex(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST) + "data==" + XUtils.ByteArrToHex(datas));
    }

    public void release(){
        mcuClient.UnRegister(WheelKey.CMD_LEARN_STATUS_CONTRL_REQUEST);
        mcuClient.UnRegister(WheelKey.CMD_LEARN_STATUS_RESPONE);
        mcuClient.UnRegister(WheelKey.CMD_LEARN_STATUS_SCAN_RESPONE);
        mcuClient.DeInit(context);
    }
}
