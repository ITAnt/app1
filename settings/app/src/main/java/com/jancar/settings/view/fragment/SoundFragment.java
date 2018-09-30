package com.jancar.settings.view.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.audio.AudioEffectManager;
import com.jancar.audio.AudioEffectParam;
import com.jancar.settings.R;
import com.jancar.settings.adapter.SoundListAdapter;
import com.jancar.settings.contract.EqEntity;
import com.jancar.settings.lib.SettingManager;
import com.jancar.settings.listener.Contract.SoundContractImpl;
import com.jancar.settings.listener.Contract.TimeContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.SoundPresenter;
import com.jancar.settings.presenter.TimePresenter;
import com.jancar.settings.widget.DspBalance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_WORLD_READABLE;
import static android.content.Context.MODE_WORLD_WRITEABLE;
import static android.security.keystore.SoterKeyStoreKeyPairRSAGeneratorSpi.getApplicationContext;
import static android.view.View.inflate;
import static com.jancar.settings.util.Tool.setDialogParam;

/**
 * Created by ouyan on 2018/9/7.
 */

public class SoundFragment extends BaseFragments<SoundPresenter> implements SoundContractImpl.View, com.jancar.settings.widget.Spinner.spinnerListener, SeekBar.OnSeekBarChangeListener, AudioEffectManager.AudioListener, View.OnClickListener {
    private View view;
    List<EqEntity> stringList;
    SoundListAdapter adapter;
    DspBalance balanceDsp;
    SeekBar trebleSoundValueSeekBar;
    TextView trebleSoundValueTxt;
    SeekBar midrangeSoundValueSeekBar;
    TextView midrangeSoundValueTxt;

    SeekBar bassSoundValueSeekbar;
    TextView bassSoundValueTxt;
    com.jancar.settings.widget.Spinner spinnerLlinear;
    private int miFad = 7;
    private int miBal = 7;
    private final int BALANCE_MAX = 14;
    TextView resetTxt;
    private AudioEffectManager mAudioEffectManager = null;
    int value;

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            spinnerLlinear = (com.jancar.settings.widget.Spinner) view.findViewById(R.id.llinear_spinner);
            balanceDsp = (DspBalance) view.findViewById(R.id.dsp_balance);
            resetTxt = (TextView) view.findViewById(R.id.txt_reset);
            trebleSoundValueSeekBar = (SeekBar) view.findViewById(R.id.seekbar_treble_sound_value);
            midrangeSoundValueSeekBar = (SeekBar) view.findViewById(R.id.seekbar_midrange_sound_value);
            bassSoundValueSeekbar = (SeekBar) view.findViewById(R.id.seekbar_bass_sound_value);
            trebleSoundValueTxt = (TextView) view.findViewById(R.id.txt_treble_sound_value);
            midrangeSoundValueTxt = (TextView) view.findViewById(R.id.txt_midrange_sound_value);
            bassSoundValueTxt = (TextView) view.findViewById(R.id.txt_bass_sound_value);

        }
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sound, null);
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
        return new SoundPresenter(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mAudioEffectManager = new AudioEffectManager(getContext(), this, getActivity().getPackageName());
        stringList = mPresenter.initList(getContext());
        adapter = new SoundListAdapter(getContext(), stringList);
        spinnerLlinear.setmSpinnerListener(this);
        spinnerLlinear.setAdapter(adapter);
        //步骤1：创建一个SharedPreferences接口对象
        SharedPreferences read = getActivity().getSharedPreferences("EQ", MODE_WORLD_READABLE);
        //步骤2：获取文件中的值
        value = read.getInt("Types", 0);
        if (stringList.get(value).getName().equals( getResources().getString(R.string.txt_standard))) {
            onItemClick(null, null, 0, 1);
        }
        spinnerLlinear.setSpinnerOperatingText(stringList.get(value).getName());
        adapter.setID(spinnerLlinear.getSpinnerOperatingText());
        int Treble = mAudioEffectManager.getAudioEffectTreble();
        int Middle = mAudioEffectManager.getAudioEffectMiddle();
        int Bass = mAudioEffectManager.getAudioEffectBass();
        int ValueTTxt = Treble - 7;
        int ValueMTxt = Middle - 7;
        int ValueBTxt = Bass - 7;
        trebleSoundValueTxt.setText(ValueTTxt + "");
        midrangeSoundValueTxt.setText(ValueMTxt + "");
        bassSoundValueTxt.setText(ValueBTxt + "");
        trebleSoundValueSeekBar.setMax(14);
        trebleSoundValueSeekBar.setProgress(Treble);
        trebleSoundValueSeekBar.setOnSeekBarChangeListener(this);
        midrangeSoundValueSeekBar.setMax(14);
        midrangeSoundValueSeekBar.setProgress(Middle);
        midrangeSoundValueSeekBar.setOnSeekBarChangeListener(this);
        bassSoundValueSeekbar.setMax(14);
        bassSoundValueSeekbar.setProgress(Bass);
        bassSoundValueSeekbar.setOnSeekBarChangeListener(this);
        balanceDsp.setMaxVal(6);
        balanceDsp.setDefVal(6);
        balanceDsp.setBalenceListener(objListener);
        resetTxt.setOnClickListener(this);
        balanceDsp.post(new Runnable() {
            @Override
            public void run() {
                int ibanlce = mAudioEffectManager.getBalanceSpeakerValue();
                Log.d("ibanlce", "get" + ibanlce);
                SharedPreferences read = getActivity().getSharedPreferences("EQ", MODE_WORLD_READABLE);
                //步骤2：获取文件中的值
                balanceDsp.updateBalance(AudioEffectParam.getBalance(ibanlce)+ read.getFloat("fad", 0), AudioEffectParam.getFade(ibanlce)+  read.getFloat("bal", 0));
            }
        });

        //spinnerLlinear.initStringListAdapterData(stringList);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    private DspBalance.OnTouchListener objListener = new DspBalance.OnTouchListener() {
        @Override
        public void onBalance(float fFad, float fBal) {
            float bal=fBal%1;
            float fad=fFad%1;
            miBal = (int) fFad;
            miFad = (int) fBal;
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("EQ", MODE_WORLD_WRITEABLE).edit();
            editor.putFloat("bal", bal);
            editor.putFloat("fad", fad);
            //步骤3：提交
            editor.apply();
         /*   miBal = new BigDecimal((double) fFad).setScale(0, 4).intValue();
            miFad = new BigDecimal((double) fBal).setScale(0, 4).intValue();*/
            Log.d("ibanlce", "set" + AudioEffectParam.getBalanceFadeCombine(miBal, miFad));
            if (mAudioEffectManager != null) {
                mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(miBal, miFad));
            }
        }

        @Override
        public void onBalance(int fFad, int fBal, boolean isFromUser) {
            miBal = fFad;
            miFad = fBal;
            Log.e("DspBalance2", "fBal=%d" + fBal + "fFad" + fFad);
            if (isFromUser) {
                if (mAudioEffectManager != null) {
                    mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(miBal, miFad));
                }
            }
        }
    };

    public static int floatToByte4(float f) {
        int i = Float.floatToIntBits(f);
        return i;
    }

    @Override
    public void onClick(View v) {
        onItemClick(null, null, 0, 1);
        spinnerLlinear.setSpinnerOperatingText(stringList.get(0).getName());
        if (mAudioEffectManager != null) {
            mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(4, 3));
        }
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("EQ", MODE_WORLD_WRITEABLE).edit();
        editor.putFloat("bal", 0.8872025f);
        editor.putFloat("fad", 0.1400003f);
        //步骤3：提交
        editor.apply();
        int ibanlce = mAudioEffectManager.getBalanceSpeakerValue();

        balanceDsp.updateBalance(AudioEffectParam.getBalance(ibanlce)+0.1400003f , AudioEffectParam.getFade(ibanlce)+0.8872025f);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int Treble;
        int Middle;
        int Bass;
        if (position == 5) {
            SharedPreferences read = getActivity().getSharedPreferences("EQ", MODE_WORLD_READABLE);
            //步骤2：获取文件中的值
            Treble = read.getInt("ValueTTxt", 10);
            Middle = read.getInt("ValueMTxt", 10);
            Bass = read.getInt("ValueBTxt", 10);
        } else {

            Treble = stringList.get(position).getValueTTxt();
            Middle = stringList.get(position).getValueMTxt();
            Bass = stringList.get(position).getValueBTxt();
        }
        int ValueTTxt = Treble - 7;
        int ValueMTxt = Middle - 7;
        int ValueBTxt = Bass - 7;
        trebleSoundValueTxt.setText(ValueTTxt + "");
        midrangeSoundValueTxt.setText(ValueMTxt + "");
        bassSoundValueTxt.setText(ValueBTxt + "");
        mAudioEffectManager.setAudioEffectTreble(Treble);
        mAudioEffectManager.setAudioEffectMiddle(Middle);
        mAudioEffectManager.setAudioEffectBass(Bass);
        trebleSoundValueSeekBar.setProgress(Treble);
        midrangeSoundValueSeekBar.setProgress(Middle);
        bassSoundValueSeekbar.setProgress(Bass);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("EQ", MODE_WORLD_WRITEABLE).edit();
        editor.putInt("Types", position);
        value = position;
        //步骤3：提交
        editor.commit();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int ValuePTxt = progress - 7;

        switch (seekBar.getId()) {
            case R.id.seekbar_treble_sound_value:

                trebleSoundValueTxt.setText(ValuePTxt + "");
                mAudioEffectManager.setAudioEffectTreble(progress);
                break;
            case R.id.seekbar_midrange_sound_value:
                mAudioEffectManager.setAudioEffectMiddle(progress);
                midrangeSoundValueTxt.setText(ValuePTxt + "");
                // midrangeSoundValueNTxt.setText(ValueNTxt + "");
                break;
            case R.id.seekbar_bass_sound_value:
                mAudioEffectManager.setAudioEffectBass(progress);
                bassSoundValueTxt.setText(ValuePTxt + "");
                //bassSoundValueNTxt.setText(ValueNTxt + "");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        if (!spinnerLlinear.getSpinnerOperatingText().equals(stringList.get(5).getName())) {
            showRestoreDefaultDialog(seekBar);
        }

    }

    private void showRestoreDefaultDialog(final SeekBar seekBar) {
        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);
        dialog.setContentView(R.layout.display_dialog_restore_default);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Button connect = (Button) dialog.findViewById(R.id.btn_connect_btn);
        TextView textView = (TextView) dialog.findViewById(R.id.txt_display_dialog_title);
        /*textView.setText(R.string.);*/
        textView.setText(getResources().getString(R.string.dialog_prompt));
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.btn_connect_btn:
                        setSeekBar(seekBar);
                        dialog.dismiss();
                        break;
                    case R.id.btn_cancel:

                        int ValueTTxt = stringList.get(value).getValueTTxt() - 7;
                        int ValueMTxt = stringList.get(value).getValueMTxt() - 7;
                        int ValueBTxt = stringList.get(value).getValueBTxt() - 7;
                        trebleSoundValueTxt.setText(ValueTTxt + "");
                        midrangeSoundValueTxt.setText(ValueMTxt + "");
                        bassSoundValueTxt.setText(ValueBTxt + "");
                        mAudioEffectManager.setAudioEffectTreble(stringList.get(value).getValueTTxt());
                        mAudioEffectManager.setAudioEffectMiddle(stringList.get(value).getValueMTxt());
                        mAudioEffectManager.setAudioEffectBass(stringList.get(value).getValueBTxt());
                        trebleSoundValueSeekBar.setProgress(stringList.get(value).getValueTTxt());
                        midrangeSoundValueSeekBar.setProgress(stringList.get(value).getValueMTxt());
                        bassSoundValueSeekbar.setProgress(stringList.get(value).getValueBTxt());

                        dialog.dismiss();
                        break;
                }
            }
        };
        connect.setOnClickListener(buttonListener);
        cancel.setOnClickListener(buttonListener);

        dialog.show();
        setDialogParam(dialog, 500, 316);
    }

    public void setSeekBar(final SeekBar seekBar) {
        int progress = seekBar.getProgress();
        int ValuePTxt = progress - 7;
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("EQ", MODE_WORLD_WRITEABLE).edit();
        spinnerLlinear.setSpinnerOperatingText(stringList.get(5).getName());
        switch (seekBar.getId()) {
            case R.id.seekbar_treble_sound_value:
                editor.putInt("ValueTTxt", progress);
                editor.putInt("ValueMTxt", midrangeSoundValueSeekBar.getProgress());
                editor.putInt("ValueBTxt", bassSoundValueSeekbar.getProgress());
                trebleSoundValueTxt.setText(ValuePTxt + "");
                mAudioEffectManager.setAudioEffectTreble(progress);

                break;
            case R.id.seekbar_midrange_sound_value:
                mAudioEffectManager.setAudioEffectMiddle(progress);
                editor.putInt("ValueTTxt", trebleSoundValueSeekBar.getProgress());
                editor.putInt("ValueMTxt", progress);
                editor.putInt("ValueBTxt", bassSoundValueSeekbar.getProgress());
                midrangeSoundValueTxt.setText(ValuePTxt + "");
                // midrangeSoundValueNTxt.setText(ValueNTxt + "");
                break;
            case R.id.seekbar_bass_sound_value:
                mAudioEffectManager.setAudioEffectBass(progress);
                editor.putInt("ValueTTxt", trebleSoundValueSeekBar.getProgress());
                editor.putInt("ValueMTxt", bassSoundValueSeekbar.getProgress());
                editor.putInt("ValueBTxt", progress);
                bassSoundValueTxt.setText(ValuePTxt + "");
                //bassSoundValueNTxt.setText(ValueNTxt + "");
                break;
        }

        //步骤2-2：将获取过来的值放入文件
        editor.putInt("Types", 5);
        value = 5;
        //步骤3：提交
        editor.commit();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (spinnerLlinear.getSpinnerOperatingText().equals(stringList.get(5).getName())) {
            setSeekBar(seekBar);
        }
    }

    @Override
    public void onVolumeChanged(int i, int i1) {

    }

    @Override
    public void onMuteChanged(boolean b, int i) {

    }


}
