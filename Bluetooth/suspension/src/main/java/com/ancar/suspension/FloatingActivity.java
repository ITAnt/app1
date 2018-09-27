package com.ancar.suspension;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.ancar.suspension.widget.SwitchButton;

/**
 * @anthor Tzq
 * @time 2018/9/22 15:20
 * @describe 悬浮按钮设置界面
 */
public class FloatingActivity extends FragmentActivity {
    SwitchButton ivOnSus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);
        initView();
    }

    private void initView() {
        ivOnSus = findViewById(R.id.iv_float_switch);
        ivOnSus.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivOnSus.setBackDrawableRes(R.drawable.switch_custom_track_selector);
    }
}
