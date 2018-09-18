package com.jancar.settings.view.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.adapter.NavigationListAdapter;
import com.jancar.settings.contract.NavigationEntity;
import com.jancar.settings.lib.SettingManager;
import com.jancar.settings.listener.Contract.NavigationContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.NavigationPresenter;
import com.jancar.settings.view.activity.MainActivity;
import com.jancar.settings.widget.SwitchButton;

import java.util.List;

/**
 * Created by ouyan on 2018/9/10.
 */

public class NavigationFragment extends BaseFragments<NavigationPresenter> implements NavigationContractImpl.View, View.OnClickListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {
    private View view;
    private RelativeLayout softwareNavigationRlayout;
    private ScrollView rlayoutScrollview;
    private RelativeLayout pMixingNavigationRlayout;
    private RelativeLayout soundMixingRlayout;
    private SwitchButton mixingSwitch;
    private TextView soundMixingLineTxt;
    private TextView dsoundMixingValueTxt;
    private SeekBar soundMixingValueSeekbar;
    private ImageView pMixingNavigationArrowImg;
    private ListView navigationList;
    private List<NavigationEntity> navigationEntityList;
    private NavigationListAdapter listAdapter;
    private SettingManager settingManager;
    Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            ((MainActivity) getActivity()).mHadler = null;
            if (rlayoutScrollview != null) {
                navigationList.setVisibility(View.GONE);
                rlayoutScrollview.setVisibility(View.VISIBLE);
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            rlayoutScrollview = (ScrollView) view.findViewById(R.id.rlayout_scrollview);
            softwareNavigationRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_software_navigation);
            pMixingNavigationRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_p_mixing_navigation);
            soundMixingRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_sound_mixing);
            mixingSwitch = (SwitchButton) view.findViewById(R.id.switch_mixing);
            soundMixingLineTxt = (TextView) view.findViewById(R.id.txt_sound_mixing_line);
            dsoundMixingValueTxt = (TextView) view.findViewById(R.id.txt_dsound_mixing_value);
            soundMixingValueSeekbar = (SeekBar) view.findViewById(R.id.seekbar_sound_mixing_value);
            pMixingNavigationArrowImg = (ImageView) view.findViewById(R.id.img_p_mixing_navigation_arrow);
            navigationList = (ListView) view.findViewById(R.id.list_navigation);
        }
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation, null);
        initView(savedInstanceState);
        initData(savedInstanceState);
        return view;
    }

    @Override
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new NavigationPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        settingManager = SettingManager.getSettingManager(this.getActivity());
        softwareNavigationRlayout.setOnClickListener(this);
        pMixingNavigationRlayout.setOnClickListener(this);
        mixingSwitch.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        mixingSwitch.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        mixingSwitch.setBackRadius(60);
        mixingSwitch.setOnClickListener(this);
        //  mixingSwitch.
        soundMixingValueSeekbar.setMax(99);
        soundMixingValueSeekbar.setProgress(settingManager.getNaviMixValue());
        dsoundMixingValueTxt.setText(settingManager.getNaviMixValue() + "%");
        soundMixingValueSeekbar.setOnSeekBarChangeListener(this);
        navigationEntityList = mPresenter.getListData();
        listAdapter = new NavigationListAdapter(getContext(), navigationEntityList);
        navigationList.setAdapter(listAdapter);
        listAdapter.setID(navigationEntityList.get(0).getName());
        navigationList.setOnItemClickListener(this);
        mixingSwitch.setCheckedImmediately(settingManager.getNaviMixState());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlayout_software_navigation:
                navigationList.setVisibility(View.VISIBLE);
                rlayoutScrollview.setVisibility(View.GONE);
                ((MainActivity) getActivity()).mHadler = mHadler;

                break;
            case R.id.rlayout_p_mixing_navigation:
                setVisibility();
                break;
            case R.id.switch_mixing:
              /*  settingManager.setIsNeedKeySound();
                setDrivingStopVedio*/
                //   mixingSwitch.setCheckedImmediately(settingManager.getNaviMixState());
              /*  settingManager.getNaviMixState();
                settingManager.setNaviMixValue();*/
                settingManager.setNaviMixState(!settingManager.getNaviMixState());
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int progres = progress + 1;
        dsoundMixingValueTxt.setText(progres + "%");
        settingManager.setNaviMixValue(progres, false);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (!settingManager.getNaviMixState()) {
            mixingSwitch.setCheckedImmediately(true);
            settingManager.setNaviMixState(true);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progres = seekBar.getProgress() + 1;
        settingManager.setNaviMixValue(progres, true);
    }

    public void setVisibility() {
        if (soundMixingRlayout.getVisibility() == View.GONE) {
            soundMixingRlayout.setVisibility(View.VISIBLE);
            soundMixingLineTxt.setVisibility(View.VISIBLE);
            pMixingNavigationArrowImg.setImageResource(R.drawable.balance_btn_bottom_state);
        } else {
            pMixingNavigationArrowImg.setImageResource(R.drawable.balance_btn_right_state);
            soundMixingLineTxt.setVisibility(View.GONE);
            soundMixingRlayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void launchActivity(@NonNull Intent intent) {

    }

    @Override
    public void killMyself() {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listAdapter.setID(navigationEntityList.get(position).getName());
        listAdapter.notifyDataSetInvalidated();
    }
}
