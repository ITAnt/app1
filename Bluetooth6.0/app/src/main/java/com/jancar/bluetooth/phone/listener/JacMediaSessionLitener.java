package com.jancar.bluetooth.phone.listener;

/**
 * @anthor Tzq
 * @time 2018/12/6 16:46
 * @describe TODO
 */
public interface JacMediaSessionLitener {
    /**
     * 初始化
     */
    void JacMediaInit();

    /**
     * 注册
     */
    void JacMediaRegister();

    /**
     * 反注册
     */

    void JacMadiaUnRegister();

    /**
     * 播放uri，暂时传title
     *
     * @param uri
     */
    void JacNotifyPlayUri(String uri);

    /**
     * Id3信息
     *
     * @param title   表添
     * @param artist  歌手
     * @param album   专辑
     * @param artWork 专辑图片 可传空
     */
    void JacNotifyId3(String title, String artist, String album, byte[] artWork);

    /**
     * 播放状态
     *
     * @param play_status
     */
    void JacNotifyPlayState(int play_status);

    /**
     * 进度条
     *
     * @param progress 当前进度
     * @param duration 总的时长
     */
    void JacNotifyProgress(long progress, long duration);
}
