package com.ancar.suspension;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ancar.suspension.adapter.FloatAdapter;
import com.ancar.suspension.entry.FloatEntry;
import com.ancar.suspension.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/9/22 15:20
 * @describe 悬浮按钮设置界面
 */
public class FloatingActivity extends FragmentActivity implements View.OnClickListener {
    SwitchButton ivOnSus;
    ImageView ivPower;
    ImageView ivHome;
    ImageView ivAdd;
    ImageView ivDec;
    ImageView ivBack;
    ListView listView;
    private boolean isOpen = false;
    private List<FloatEntry> entryList = new ArrayList<>();
    private FloatAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);
        initData();
        initView();
    }

    private void initData() {
        entryList.add(new FloatEntry(R.drawable.iv_select_power, "电  源"));
        entryList.add(new FloatEntry(R.drawable.iv_select_home, "主屏幕"));
        entryList.add(new FloatEntry(R.drawable.iv_select_add, "音量加"));
        entryList.add(new FloatEntry(R.drawable.iv_select_des, "音量键"));
        entryList.add(new FloatEntry(R.drawable.iv_select_back, "返  回"));
    }

    private void initView() {
        ivOnSus = findViewById(R.id.iv_float_switch);
        ivOnSus.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivOnSus.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivOnSus.setCheckedImmediately(isOpen);
        if (isOpen) {

        } else {

        }
        ivPower = findViewById(R.id.iv_power);
        ivHome = findViewById(R.id.iv_home);
        ivAdd = findViewById(R.id.iv_add);
        ivDec = findViewById(R.id.iv_dec);
        ivBack = findViewById(R.id.iv_back);
        listView = findViewById(R.id.sus_listview);
        ivPower.setOnClickListener(this);
        ivHome.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        ivDec.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        if (adapter == null) {
            adapter = new FloatAdapter(this, entryList);
        }
        listView.setAdapter(adapter);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(FloatingActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_power:
                break;
            case R.id.iv_home:
                break;
            case R.id.iv_add:
                break;
            case R.id.iv_dec:
                break;
            case R.id.iv_back:
                break;
            case R.id.iv_float_switch:
                isOpen = !isOpen;
                if (isOpen) {
                } else {
                }
                break;
        }
    }
}
