package com.jancar.settings.suspension;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.jancar.settings.R;
import com.jancar.settings.suspension.adapter.FloatAdapter;
import com.jancar.settings.suspension.entry.FloatEntry;
import com.jancar.settings.suspension.entry.UpdateEntry;
import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.widget.SwitchButton;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

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
    private boolean isOpen;
    private List<FloatEntry> entryList = new ArrayList<>();
    private FloatAdapter adapter;

    public static final int ICON_POWER = 0;
    public static final int ICON_HOME = 1;
    public static final int ICON_VOICE_ADD = 2;
    public static final int ICON_VOICE_RED = 3;
    public static final int ICON_BACK = 4;
    public static int indexIcon = ICON_POWER;
    private String pos_title_0;
    private String pos_title_1;
    private String pos_title_2;
    private String pos_title_3;
    private String pos_title_4;
    private int selectPos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);
        initData();
        initView();
        changeIconBg(indexIcon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pos_title_0 = Hawk.get(Contacts.ICON_POS_0, getResources().getString(R.string.tv_power));
        pos_title_1 = Hawk.get(Contacts.ICON_POS_1, getResources().getString(R.string.tv_home));
        pos_title_2 = Hawk.get(Contacts.ICON_POS_2, getResources().getString(R.string.tv_vioce_add));
        pos_title_3 = Hawk.get(Contacts.ICON_POS_3, getResources().getString(R.string.tv_vioce_dec));
        pos_title_4 = Hawk.get(Contacts.ICON_POS_4, getResources().getString(R.string.tv_back));
        selectPos = Hawk.get(Contacts.SELECT_POS, 0);
        indexIcon = Hawk.get(Contacts.TAB_POS, ICON_POWER);
        setImageRes(pos_title_0, ivPower);
        setImageRes(pos_title_1, ivHome);
        setImageRes(pos_title_2, ivAdd);
        setImageRes(pos_title_3, ivDec);
        setImageRes(pos_title_4, ivBack);
        changeIconBg(indexIcon);
        adapter.setSelectPostion(selectPos);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Hawk.put(Contacts.ICON_POS_0, pos_title_0);
        Hawk.put(Contacts.ICON_POS_1, pos_title_1);
        Hawk.put(Contacts.ICON_POS_2, pos_title_2);
        Hawk.put(Contacts.ICON_POS_3, pos_title_3);
        Hawk.put(Contacts.ICON_POS_4, pos_title_4);
        Hawk.put(Contacts.SELECT_POS, selectPos);
        Hawk.put(Contacts.TAB_POS, indexIcon);
        EventBus.getDefault().post(new UpdateEntry(true));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initData() {
        entryList.add(new FloatEntry(R.drawable.iv_select_power, getResources().getString(R.string.tv_power)));
        entryList.add(new FloatEntry(R.drawable.iv_select_home, getResources().getString(R.string.tv_home)));
        entryList.add(new FloatEntry(R.drawable.iv_select_add, getResources().getString(R.string.tv_vioce_add)));
        entryList.add(new FloatEntry(R.drawable.iv_select_des, getResources().getString(R.string.tv_vioce_dec)));
        entryList.add(new FloatEntry(R.drawable.iv_select_back, getResources().getString(R.string.tv_back)));
    }

    private void initView() {
        isOpen = Hawk.get(Contacts.ISOPEN_OVERLAY, false);
        ivOnSus = findViewById(R.id.iv_float_switch);
        ivOnSus.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        ivOnSus.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        ivOnSus.setCheckedImmediately(isOpen);
        if (isOpen) {
            startService(new Intent(FloatingActivity.this, OverlayMenuService.class));

        } else {
            stopService(new Intent(FloatingActivity.this, OverlayMenuService.class));
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
        ivOnSus.setOnClickListener(this);
        if (adapter == null) {
            adapter = new FloatAdapter(this, entryList);
        }
        listView.setAdapter(adapter);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (entryList.size() > 0 && entryList != null) {
                    adapter.setSelectPostion(position);
                    selectPos = position;
                    String title = entryList.get(position).getTitle();
                    updateIcon(title);
                }
            }
        });
    }

    private void updateIcon(String title) {
        switch (indexIcon) {
            case ICON_POWER:
                setImageRes(title, ivPower);
                pos_title_0 = title;
                break;
            case ICON_HOME:
                setImageRes(title, ivHome);
                pos_title_1 = title;
                break;
            case ICON_VOICE_ADD:
                setImageRes(title, ivAdd);
                pos_title_2 = title;
                break;
            case ICON_VOICE_RED:
                setImageRes(title, ivDec);
                pos_title_3 = title;
                break;
            case ICON_BACK:
                setImageRes(title, ivBack);
                pos_title_4 = title;
                break;
        }
    }

    private void setImageRes(String title, ImageView view) {
        if (title.equals(getResources().getString(R.string.tv_power))) {
            view.setImageResource(R.drawable.setting_power_selector);
        }
        if (title.equals(getResources().getString(R.string.tv_home))) {
            view.setImageResource(R.drawable.setting_home_selector);
        }
        if (title.equals(getResources().getString(R.string.tv_vioce_add))) {
            view.setImageResource(R.drawable.setting_voice_add_selector_left);
        }
        if (title.equals(getResources().getString(R.string.tv_vioce_dec))) {
            view.setImageResource(R.drawable.setting_voice_red_selector_left);
        }
        if (title.equals(getResources().getString(R.string.tv_back))) {
            view.setImageResource(R.drawable.setting_back_selector_left);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_power:
                if (indexIcon != ICON_POWER) {
                    changeIconBg(ICON_POWER);
                }
                updataListPos(pos_title_0);
                indexIcon = ICON_POWER;
                break;
            case R.id.iv_home:
                if (indexIcon != ICON_HOME) {
                    changeIconBg(ICON_HOME);
                }
                updataListPos(pos_title_1);
                indexIcon = ICON_HOME;
                break;
            case R.id.iv_add:
                if (indexIcon != ICON_VOICE_ADD) {
                    changeIconBg(ICON_VOICE_ADD);
                }
                updataListPos(pos_title_2);
                indexIcon = ICON_VOICE_ADD;
                break;
            case R.id.iv_dec:
                if (indexIcon != ICON_VOICE_RED) {
                    changeIconBg(ICON_VOICE_RED);
                }
                updataListPos(pos_title_3);
                indexIcon = ICON_VOICE_RED;
                break;
            case R.id.iv_back:
                if (indexIcon != ICON_BACK) {
                    changeIconBg(ICON_BACK);
                }
                updataListPos(pos_title_4);
                indexIcon = ICON_BACK;
                break;
            case R.id.iv_float_switch:
                isOpen = !isOpen;
                Hawk.put(Contacts.ISOPEN_OVERLAY, isOpen);
                if (isOpen) {
                    startService(new Intent(FloatingActivity.this, OverlayMenuService.class));

                } else {
                    stopService(new Intent(FloatingActivity.this, OverlayMenuService.class));
                }
                break;
        }
    }

    private void changeIconBg(int indexIcon) {
        ivPower.setSelected(false);
        ivHome.setSelected(false);
        ivAdd.setSelected(false);
        ivDec.setSelected(false);
        ivBack.setSelected(false);
        switch (indexIcon) {
            case ICON_POWER:
                ivPower.setSelected(true);
                break;
            case ICON_HOME:
                ivHome.setSelected(true);
                break;
            case ICON_VOICE_ADD:
                ivAdd.setSelected(true);
                break;
            case ICON_VOICE_RED:
                ivDec.setSelected(true);
                break;
            case ICON_BACK:
                ivBack.setSelected(true);
                break;
        }
    }

    private void updataListPos(String title) {
        for (int i = 0; i < entryList.size(); i++) {
            if (title.equals(entryList.get(i).getTitle())) {
                selectPos = i;
                adapter.setSelectPostion(selectPos);
            }
        }
    }
}
