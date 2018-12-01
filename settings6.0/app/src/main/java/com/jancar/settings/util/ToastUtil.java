package com.jancar.settings.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.settings.R;


/**
 * @anthor Tzq
 * @time 2018/9/18 16:50
 * @describe 自定义toast提示框
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
}
