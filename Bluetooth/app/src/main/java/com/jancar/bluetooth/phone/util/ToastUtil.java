package com.jancar.bluetooth.phone.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.bluetooth.phone.BluetoothApplication;
import com.jancar.bluetooth.phone.R;

/**
 * @anthor Tzq
 * @time 2018/9/18 16:50
 * @describe 自定义toast提示框
 */
public class ToastUtil {
    private static Context mContext = null;
    private static Toast mToast = null;

    public static void ShowToast(String string) {
        View view = LayoutInflater.from(mContext.getApplicationContext()).inflate(R.layout.activity_toast, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast);
        //避免toast长时间显示
        if (mToast != null) {
            mToast.cancel();
        }
        tv.setText(string);
        mToast = new Toast(mContext.getApplicationContext());
        mToast.setGravity(Gravity.BOTTOM, 0, 100);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(view);
        mToast.show();
    }

    /**
     * 使toast不在显示
     */
    public static void cancleMyToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
