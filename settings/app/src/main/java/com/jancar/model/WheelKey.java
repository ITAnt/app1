package com.jancar.model;

public class WheelKey {
    public static final byte KEY_LEARNING_POWER = (byte)0x00;//电源
    public static final byte KEY_LEARNING_DISP = (byte)0x01;//显示时钟
    public static final byte KEY_LEARNING_AS = (byte)0x02;//电台搜索
    public static final byte KEY_LEARNING_MODE = (byte)0x03;//模式
    public static final byte KEY_LEARNING_BAND = (byte)0x04;//RADIO波段切换
    public static final byte KEY_LEARNING_PLAYPAUSE = (byte)0x05;//播放/暂停
    public static final byte KEY_LEARNING_VOL_DECREASE = (byte)0x06;//音量减
    public static final byte KEY_LEARNING_VOL_INCREASE = (byte)0x07;//音量加
    public static final byte KEY_LEARNING_EJECT = (byte)0x08;//弹出光盘
    public static final byte KEY_LEARNING_PRE = (byte)0x09;//上一曲
    public static final byte KEY_LEARNING_NEXT = (byte)0x0a;//下一曲
    public static final byte KEY_LEARNING_FINE_PRE = (byte)0x0b;//微调-/快进
    public static final byte KEY_LEARNING_FINE_NEXT = (byte)0x0c;//微调+/快退
    public static final byte KEY_LEARNING_SPEECH = (byte)0x0d;//语音识别
    public static final byte KEY_LEARNING_SETTING = (byte)0x0e;//进入设置
    public static final byte KEY_LEARNING_HOME = (byte)0x0f;//主界面
    public static final byte KEY_LEARNING_MENU = (byte)0x10;//菜单键
    public static final byte KEY_LEARNING_BACK = (byte)0x11;//返回
    public static final byte KEY_LEARNING_BACKLIGHT_ONOFF = (byte)0x12;//背光开关
    public static final byte KEY_LEARNING_GPS = (byte)0x13;//快速切换到GPS
    public static final byte KEY_LEARNING_DVD = (byte)0x14;//快速切换到DVD/DISC
    public static final byte KEY_LEARNING_MP3 = (byte)0x15;//MUSIC
    public static final byte KEY_LEARNING_MP5 = (byte)0x16;//Video
    public static final byte KEY_LEARNING_TV = (byte)0x17;//TV
    public static final byte KEY_LEARNING_BLUE = (byte)0x18;//蓝牙
    public static final byte KEY_LEARNING_ANSWER = (byte)0x19;//接听
    public static final byte KEY_LEARNING_HANGUP = (byte)0x1a;//挂断
    public static final byte KEY_LEARNING_RDS_TA = (byte)0x1b;//RDS TA开关
    public static final byte KEY_LEARNING_RDS_AF = (byte)0x1c;//RDS AF开关
    public static final byte KEY_LEARNING_EQ = (byte)0x1d;//EQ设置
    public static final byte KEY_LEARNING_FM = (byte)0x1e;//FM波段切换
    public static final byte KEY_LEARNING_PHONELINK = (byte)0x1f;//手机互联
    public static final byte KEY_LEARNING_SYSINFO = (byte)0x20;//系统信息
    public static final byte KEY_LEARNING_ENTER = (byte)0x21;//确认按键
    public static final byte KEY_LEARNING_LIST = (byte)0x22;//近期任务列表
    public static final byte KEY_LEARNING_REPEAT = (byte)0x23;//重复
    public static final byte KEY_LEARNING_RDS_EON = (byte)0x24;//RDS_EON
    public static final byte KEY_LEARNING_AM = (byte)0x25;//AM波段切换
    public static final byte KEY_LEARNING_AUX = (byte)0x26;//AUX
    public static final byte KEY_LEARNING_SCAN = (byte)0x27;//电台扫描
    public static final byte KEY_LEARNING_START = (byte)0x28;//开始结束录制
    public static final byte KEY_LEARNING_APP = (byte)0x29;//APP
    public static final byte KEY_LEARNING_PHOTO = (byte)0x2a;//图片
    public static final byte KEY_LEARNING_MUTE = (byte)0x2b;//静音
    public static final byte KEY_LEARNING_BTMUSIC = (byte)0x2c;//蓝牙音乐
    public static final byte KEY_LEARNING_DVR = (byte)0x2d;//行车记录仪
    public static final byte KEY_LEARNING_M1 = (byte)0x2e;//保留
    public static final byte KEY_LEARNING_M2 = (byte)0x2f;//保留

    //arm to mcu
    public static final byte CMD_LEARN_STATUS_CONTRL_REQUEST = (byte)0x11;//按键学习控制
    //mcu to arm
    public static final byte CMD_LEARN_STATUS_RESPONE = (byte)0x12;//学习状态应答
    public static final byte CMD_LEARN_STATUS_SCAN_RESPONE = (byte)0x13;//按键学习状态预览
    public static final byte CMD_LEARN_KEY_CONTRY_RESPONE = (byte)0x20;//按键控制

    /**
     * 命令按键学习控制subcommand
     */
    public static final byte CMD_LEARN_UNUSEED_ACTION = (byte)0x00;//无效
    public static final byte CMD_CLEAR_LEARN_ACTION = (byte)0x01;//清除当前采样方式的所有按键状态
    public static final byte CMD_START_LEARN_ACTION = (byte)0x02;//开始学习
    public static final byte CMD_EXIT_LEARN_ACTION = (byte)0x03;//退出学习
    public static final byte CMD_LEARNING_ACTION = (byte)0x10;//学习
    public static final byte CMD_SCAN_LEARN_ACTION = (byte)0x04;//预览学习状态
    public static final byte CMD_LEARNING_QUERY_ACTION = (byte)0x14;//查询学习状态
    /**
     * 按键状态
     */
    public static final byte CMD_CLICK_KEY_ACTION = (byte)0x00;//短按
    public static final byte CMD_LONG_CLCIK_KEY_ACTION = (byte)0x01;//长按
    /**
     * 学习状态应答 subcommand
     */
    public static final byte CMD_UNLEARN_STATUS_SPONSE = (byte)0x00;//未进入学习状态/已退出学习状态
    public static final byte CMD_ENTER_LEARNING_STATUS_SPONSE = (byte)0x01;//已进入学习状态
    public static final byte CMD_LEARNING_SPONSE = (byte)0x02;//学习中
    public static final byte CMD_AD_FAIL_SPONSE = (byte)0x03;//学习失败-AD采样值无效/未检测到按键
    public static final byte CMD_AD_NEAR_SPONSE = (byte)0x04;//学习失败-AD采样值接近
    public static final byte CMD_AD_UNSTALE_SPONSE = (byte)0x05;//学习失败-AD采样值不稳定/未检测到按键
    public static final byte CMD_TIME_OUT_SPONSE = (byte)0x06;//学习失败-采样超时
    public static final byte CMD_KEY_UNKOWN_SPONSE = (byte)0x07;//按键值无效
    public static final byte CMD_LEARN_SUCCESS_SPONSE = (byte)0x08;//学习成功
    /**
     * 按键学习状态预览 subcommand
     */
    public static final byte CMD_UNLEARNED_SPONSE = (byte)0x0;//未学习
    public static final byte CMD_LEARNED_SPONSE = (byte)0x1;//已学习
    /**
     * 按键控制 subcommand
     */
    public static final byte CMD_KEY_STATUS_UNKOWN_SPONSE = (byte)0x00;//无效
    public static final byte CMD_KEY_STATUS_TOUCHED_SPONSE = (byte)0x01;//按下
    public static final byte CMD_KEY_STATUS_LONG_SPONSE = (byte)0x02;//长按并保持有效
    public static final byte CMD_KEY_STATUS_TOUCH_SPONSE = (byte)0x03;//短按或长按释放
    public static final byte CMD_KEY_STATUS_REPEAT = (byte)0x04;//重复
}
