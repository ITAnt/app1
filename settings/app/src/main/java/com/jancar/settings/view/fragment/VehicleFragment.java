package com.jancar.settings.view.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.jancar.settings.R;
import com.jancar.settings.listener.Contract.NavigationContractImpl;
import com.jancar.settings.listener.Contract.VehicleContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.NavigationPresenter;
import com.jancar.settings.presenter.VehiclePresenter;
import com.jancar.settings.view.activity.MainActivity;

import static com.jancar.settings.util.Tool.setDialogParam;

/**
 * Created by ouyan on 2018/9/10.
 */

public class VehicleFragment extends BaseFragments<VehiclePresenter> implements VehicleContractImpl.View, View.OnClickListener {
    private View view;
    private RelativeLayout reversingMirrorRlayout;
    private RelativeLayout mirrorRlayout;
    private RelativeLayout reverseDisplayRlayout;
    private RelativeLayout keyLearningRlayout;
    private ImageView reversingMirrorArrowImg;
    SettingsWheelFragment settingsWheelFragment;
    private LinearLayout navigationLlayout;
    private ScrollView rlayoutScrollview;
    Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            ((MainActivity) getActivity()).mHadler = null;
            if (navigationLlayout != null) {
                navigationLlayout.setVisibility(View.GONE);
                rlayoutScrollview.setVisibility(View.VISIBLE);
            }

            super.handleMessage(msg);
        }
    };
    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            reversingMirrorRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_reversing_mirror);
            mirrorRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_mirror);
            reverseDisplayRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_reverse_display);
            keyLearningRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_key_learning);
            navigationLlayout = (LinearLayout) view.findViewById(R.id.llayout_vehicle);
            rlayoutScrollview = (ScrollView) view.findViewById(R.id.rlayout_scrollview);
            reversingMirrorArrowImg = (ImageView) view.findViewById(R.id.img_reversing_mirror_arrow);
            settingsWheelFragment = new SettingsWheelFragment();
        }
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vehicle, null);
        initView(savedInstanceState);
        initData(savedInstanceState);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlayout_reversing_mirror:
                if (mirrorRlayout.getVisibility() == View.GONE) {
                    mirrorRlayout.setVisibility(View.VISIBLE);
                    reversingMirrorArrowImg.setImageResource(R.drawable.balance_btn_bottom_state);

                } else {
                    reversingMirrorArrowImg.setImageResource(R.drawable.balance_btn_right_state);

                    mirrorRlayout.setVisibility(View.GONE);
                }
                break;
            case R.id.rlayout_reverse_display:
                showDialog();
                break;
            case R.id.rlayout_key_learning:
                ((MainActivity) getActivity()).mHadler = mHadler;
                navigationLlayout.setVisibility(View.VISIBLE);
                rlayoutScrollview.setVisibility(View.GONE);
                FragmentManager fragmentManager = ((MainActivity) getContext()).getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.llayout_vehicle, settingsWheelFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.display_dialog_vehicle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        setDialogParam(dialog, 500, 316);

    }

    @Override
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new VehiclePresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        reversingMirrorRlayout.setOnClickListener(this);
        reverseDisplayRlayout.setOnClickListener(this);
        keyLearningRlayout.setOnClickListener(this);
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


}
