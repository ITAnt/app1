package com.jancar.settings.util;

/**
 * @anthor Tzq
 * @time 2018/10/18 19:19
 * @describe 300毫秒防止重复点击
 */
public class DelayedUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 300) {
            return true;
        }
        lastClickTime = time;
        return false;


    }
}
