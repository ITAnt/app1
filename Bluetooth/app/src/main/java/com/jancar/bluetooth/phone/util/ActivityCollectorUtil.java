package com.jancar.bluetooth.phone.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.LinkedList;

/**
 * @anthor Tzq
 * @time 2018/9/3 14:23
 * @describe activity的操作类
 */
public class ActivityCollectorUtil {
    public static LinkedList<Activity> activities = new LinkedList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void AppExit(Context context) {
        try {
            finishAll();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception ignored) {
        }
    }
}
