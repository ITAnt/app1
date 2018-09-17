package com.jancar.bluetooth.phone.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @anthor Tzq
 * @time 2018/8/27 15:33
 * @describe 类跳转工具类
 */
public class IntentUtil {
    public IntentUtil() {
    }

    public static void gotoActivity(Activity activity, Class<?> gotoClass, boolean finishThis) {
        activity.startActivity(new Intent(activity, gotoClass));
        if (finishThis) {
            activity.finish();
        }

    }

    public static void gotoActivity(Activity activity, Class<?> gotoClass, Bundle bundle, boolean finishThis) {
        Intent intent = new Intent(activity, gotoClass);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        if (finishThis) {
            activity.finish();
        }

    }
}
