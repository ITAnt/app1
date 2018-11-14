package com.jancar.radio.listener.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.radio.R;

import java.util.Locale;

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
    public static boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
    }
}
