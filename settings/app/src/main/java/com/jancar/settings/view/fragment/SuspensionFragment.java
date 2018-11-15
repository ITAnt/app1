package com.jancar.settings.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jancar.settings.R;
import com.jancar.settings.listener.Contract.SuspensionContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragmentsd;
import com.jancar.settings.presenter.SuspensionPresenter;
import com.jancar.settings.suspension.OverlayMenuService;
import com.jancar.settings.suspension.adapter.FloatAdapter;
import com.jancar.settings.suspension.entry.FloatEntry;
import com.jancar.settings.suspension.entry.ShowAndHideEntry;
import com.jancar.settings.suspension.entry.UpdateEntry;
import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.util.SPUtil;
import com.jancar.settings.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.jancar.settings.util.Tool.isRtl;

/**
 * Created by ouyan on 2018/10/20.
 */

public class SuspensionFragment extends BaseFragmentsd<SuspensionPresenter> implements SuspensionContractImpl.View, View.OnClickListener {
    private View view;
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
    private RelativeLayout mRelativeLayout;
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
    private Activity activity;

    public static SuspensionFragment newInstance(boolean b) {
        SuspensionFragment frag = new SuspensionFragment();
        Bundle bundle = new Bundle();
        frag.setArguments(bundle);   //设置参数
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("SuspensionFragment", "onAttach===");
        this.activity = (Activity) context;
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("SuspensionFragment", "initView===");
        view = inflater.inflate(R.layout.fragment_suspension, null);
        initView(savedInstanceState);
        initData(savedInstanceState);
        changeIconBg(indexIcon);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("SuspensionFragment", "onResume===");

        if (isRtl()) {
            mRelativeLayout.setBackgroundResource(R.drawable.iv_sus_bg_right);
        } else {
            mRelativeLayout.setBackgroundResource(R.drawable.iv_sus_bg_left);
        }
        pos_title_0 = SPUtil.getString(activity, Contacts.ICON_POS_0, getResources().getString(R.string.tv_power));
        pos_title_1 = SPUtil.getString(activity, Contacts.ICON_POS_1, getResources().getString(R.string.tv_home));
        pos_title_2 = SPUtil.getString(activity, Contacts.ICON_POS_2, getResources().getString(R.string.tv_vioce_add));
        pos_title_3 = SPUtil.getString(activity, Contacts.ICON_POS_3, getResources().getString(R.string.tv_vioce_dec));
        pos_title_4 = SPUtil.getString(activity, Contacts.ICON_POS_4, getResources().getString(R.string.tv_back));
        selectPos = SPUtil.getInt(activity, Contacts.SELECT_POS, 0);
        indexIcon = SPUtil.getInt(activity, Contacts.TAB_POS, ICON_POWER);
        setImageRes(pos_title_0, ivPower);
        setImageRes(pos_title_1, ivHome);
        setImageRes(pos_title_2, ivAdd);
        setImageRes(pos_title_3, ivDec);
        setImageRes(pos_title_4, ivBack);
        changeIconBg(indexIcon);
        adapter.setSelectPostion(selectPos);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("SuspensionFragment", "onStop===");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("SuspensionFragment", "onPause===");
        keepValue();
    }

    public void keepValue() {

        SPUtil.putInt(activity, Contacts.SELECT_POS, selectPos);
        SPUtil.putInt(activity, Contacts.TAB_POS, indexIcon);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SuspensionFragment", "onDestroy===");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    private void changeIconBg(int indexIcon) {
        Log.e("SuspensionFragment", "changeIconBg===");
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

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        Log.e("SuspensionFragment", "initView===");
        if (view != null) {
            ivPower = view.findViewById(R.id.iv_power);
            mRelativeLayout = view.findViewById(R.id.RelativeLayout);
            ivHome = view.findViewById(R.id.iv_home);
            ivAdd = view.findViewById(R.id.iv_add);
            ivDec = view.findViewById(R.id.iv_dec);
            ivBack = view.findViewById(R.id.iv_back);
            listView = view.findViewById(R.id.sus_listview);
            ivOnSus = view.findViewById(R.id.iv_float_switch);
            ivPower.setOnClickListener(this);
            ivHome.setOnClickListener(this);
            ivAdd.setOnClickListener(this);
            ivDec.setOnClickListener(this);
            ivBack.setOnClickListener(this);
            ivOnSus.setOnClickListener(this);
            ivOnSus.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
            ivOnSus.setBackDrawableRes(R.drawable.switch_custom_track_selector);
//            isOpen = Hawk.get(Contacts.ISOPEN_OVERLAY, false);
            isOpen = SPUtil.getBoolean(activity, Contacts.ISOPEN_OVERLAY, false);
            ivOnSus.setCheckedImmediately(isOpen);
            if (isOpen) {
                activity.startService(new Intent(activity, OverlayMenuService.class));
            } else {
                activity.stopService(new Intent(activity, OverlayMenuService.class));
            }
            if (adapter == null) {
                adapter = new FloatAdapter(activity, entryList);
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
    }


    private void updateIcon(String title) {
        switch (indexIcon) {
            case ICON_POWER:
                setImageRes(title, ivPower);
                pos_title_0 = title;
                saveAndUpdate(Contacts.ICON_POS_0, pos_title_0);
                break;
            case ICON_HOME:
                setImageRes(title, ivHome);
                pos_title_1 = title;
                saveAndUpdate(Contacts.ICON_POS_1, pos_title_1);
                break;
            case ICON_VOICE_ADD:
                setImageRes(title, ivAdd);
                pos_title_2 = title;
                saveAndUpdate(Contacts.ICON_POS_2, pos_title_2);
                break;
            case ICON_VOICE_RED:
                setImageRes(title, ivDec);
                pos_title_3 = title;
                saveAndUpdate(Contacts.ICON_POS_3, pos_title_3);
                break;
            case ICON_BACK:
                setImageRes(title, ivBack);
                pos_title_4 = title;
                saveAndUpdate(Contacts.ICON_POS_4, pos_title_4);
                break;
        }
    }

    private void saveAndUpdate(String key, String value) {
//        Hawk.put(key, value);
        SPUtil.putString(activity, key, value);
        EventBus.getDefault().post(new UpdateEntry(true));
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
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new SuspensionPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        entryList.add(new FloatEntry(R.drawable.iv_select_power, getResources().getString(R.string.tv_power)));
        entryList.add(new FloatEntry(R.drawable.iv_select_home, getResources().getString(R.string.tv_home)));
        entryList.add(new FloatEntry(R.drawable.iv_select_add, getResources().getString(R.string.tv_vioce_add)));
        entryList.add(new FloatEntry(R.drawable.iv_select_des, getResources().getString(R.string.tv_vioce_dec)));
        entryList.add(new FloatEntry(R.drawable.iv_select_back, getResources().getString(R.string.tv_back)));
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
//                Hawk.put(Contacts.ISOPEN_OVERLAY, isOpen);
                SPUtil.putBoolean(activity, Contacts.ISOPEN_OVERLAY, isOpen);
                if (isOpen) {
//                    activity.startService(new Intent(activity, OverlayMenuService.class));
                    EventBus.getDefault().post(new ShowAndHideEntry(true));
                } else {
//                    activity.stopService(new Intent(activity, OverlayMenuService.class));
                    EventBus.getDefault().post(new ShowAndHideEntry(false));
                }
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
