package com.jancar.bluetooth.phone.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.MainActivity;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.util.IntentUtil;

/**
 * @anthor Tzq
 * @time 2018/8/29 14:33
 * @describe 弹出蓝牙连接提示框
 */
public class DialogActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isConnect = BluetoothManager.getBluetoothManagerInstance(this).isConnect();
        getWindow().getDecorView().setBackgroundColor(0x7F00FF00);
        if (isConnect) {
            IntentUtil.gotoActivity(DialogActivity.this, MainActivity.class, true);
        } else {
            IntentUtil.gotoActivity(DialogActivity.this, MainActivity.class, true);
            setContentView(R.layout.dialog_connect);
            findViewById(R.id.tv_connect_dialog_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentUtil.gotoActivity(DialogActivity.this, SettingActivity.class, true);
                }
            });
            findViewById(R.id.tv_connect_dialog_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(0, 0);
    }
}
