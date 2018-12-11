package com.jancar.bluetooth.phone.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.bluetooth.phone.R;

/**
 * @anthor Tzq
 * @time 2018/9/18 16:50
 * @describe 自定义toast提示框
 */
public class ToastUtil {
    private Toast mToast;
    TextView msgTv = null;

    public ToastUtil(Context context) {
        mToast = new Toast(context.getApplicationContext());
        mToast.setGravity(Gravity.BOTTOM, 0, 100);
        View view = View.inflate(context, R.layout.activity_toast, null);
        msgTv = (TextView) view.findViewById(R.id.tv_toast);
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
    }

    public void ShowTipText(Context context, String tip) {
        msgTv = (TextView) mToast.getView().findViewById(R.id.tv_toast);
        msgTv.setText(tip);
        mToast.show();
    }

    public void Cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}

