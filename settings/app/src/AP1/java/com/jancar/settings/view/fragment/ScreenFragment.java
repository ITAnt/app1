package com.jancar.settings.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.settings.R;

public class ScreenFragment extends Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("SuspensionFragment", "initView===");
        view = inflater.inflate(R.layout.fragment_screen, null);
        initView(savedInstanceState);
        initData(savedInstanceState);

        return view;
    }

    public void initView(@Nullable Bundle savedInstanceState) {

    }

    public void initData(@Nullable Bundle savedInstanceState) {

    }
}
