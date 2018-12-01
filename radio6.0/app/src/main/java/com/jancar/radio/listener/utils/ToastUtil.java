package com.jancar.radio.listener.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.radio.R;

import java.util.List;
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by ouyan on 2018/10/25.
 */

public class ToastUtil {
    public static void ShowToast(Context context, String string) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_toast, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast);
        tv.setText(string);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }
    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
//        boolean flag=false;
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了
//                flag = true;
                return true;
            }
        }
        return false;
    }

    public static boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
    }
}
