package com.jancar.settings.view.fragment;import android.annotation.SuppressLint;import android.app.Dialog;import android.app.Fragment;import android.content.Intent;import android.content.SharedPreferences;import android.os.Build;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.support.annotation.NonNull;import android.support.annotation.Nullable;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.AdapterView;import android.widget.ArrayAdapter;import android.widget.Button;import android.widget.ImageView;import android.widget.SeekBar;import android.widget.Spinner;import android.widget.TextView;import android.widget.Toast;import com.jancar.audio.AudioEffectManager;import com.jancar.audio.AudioEffectParam;import com.jancar.settings.R;import com.jancar.settings.adapter.SoundListAdapter;import com.jancar.settings.contract.EqEntity;import com.jancar.settings.lib.SettingManager;import com.jancar.settings.listener.Contract.SoundContractImpl;import com.jancar.settings.listener.Contract.TimeContractImpl;import com.jancar.settings.listener.IPresenter;import com.jancar.settings.manager.BaseFragments;import com.jancar.settings.presenter.SoundPresenter;import com.jancar.settings.presenter.TimePresenter;import com.jancar.settings.view.activity.MainActivity;import com.jancar.settings.widget.DspBalance;import java.math.BigDecimal;import java.util.ArrayList;import java.util.List;import static android.content.Context.MODE_PRIVATE;import static android.content.Context.MODE_WORLD_READABLE;import static android.content.Context.MODE_WORLD_WRITEABLE;import static android.security.keystore.SoterKeyStoreKeyPairRSAGeneratorSpi.getApplicationContext;import static android.view.View.inflate;import static com.jancar.settings.util.Tool.setDialogParam;/** * Created by ouyan on 2018/9/7. */public class SoundFragment extends BaseFragments<SoundPresenter> implements SoundContractImpl.View, com.jancar.settings.widget.Spinner.spinnerListener, SeekBar.OnSeekBarChangeListener, AudioEffectManager.AudioListener, View.OnClickListener {    private View view;    List<EqEntity> stringList;    SoundListAdapter adapter;    DspBalance balanceDsp;    SeekBar trebleSoundValueSeekBar;    TextView trebleSoundValueTxt;    SeekBar midrangeSoundValueSeekBar;    TextView midrangeSoundValueTxt;    private SettingManager settingManager;    SeekBar bassSoundValueSeekbar;    SeekBar waitingSoundValueSeekbar;    TextView bassSoundValueTxt;    TextView waitingSoundValueTxt;    com.jancar.settings.widget.Spinner spinnerLlinear;    private int miFad = 7;    private int miBal = 7;    private final int BALANCE_MAX = 14;    TextView resetTxt;    private AudioEffectManager mAudioEffectManager = null;    int value;    @SuppressLint("HandlerLeak")    Handler handler=new Handler(){        @Override        public void handleMessage(Message msg) {            super.handleMessage(msg);            ((MainActivity)getContext()).isDelay=false;            SharedPreferences read = getContext().getSharedPreferences("EQ", MODE_PRIVATE);                /*if (mAudioEffectManager != null) {                    mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(read.getInt("fBal", 4),  read.getInt("fFad", 3)));                }*/            int ibanlce = mAudioEffectManager.getBalanceSpeakerValue();            Log.d("ibanlce", "get" + ibanlce);            //*/步骤2：获取文件中的值            balanceDsp.updateBalance(AudioEffectParam.getBalance(ibanlce) + read.getFloat("x", 0.9633102f) , AudioEffectParam.getFade(ibanlce) + read.getFloat("y", 0.8699093f) );        //    balanceDsp.updateBalance(AudioEffectParam.getBalance(ibanlce) + read.getFloat("x", 0) , AudioEffectParam.getFade(ibanlce) + read.getFloat("y", 0) );        }    };    @Override    public void onDestroy() {        super.onDestroy();  /*      mAudioEffectManager.c*/        handler=null;    }    @Override    public void initView(@Nullable Bundle savedInstanceState) {        if (view != null) {            spinnerLlinear = (com.jancar.settings.widget.Spinner) view.findViewById(R.id.llinear_spinner);            balanceDsp = (DspBalance) view.findViewById(R.id.dsp_balance);            resetTxt = (TextView) view.findViewById(R.id.txt_reset);            trebleSoundValueSeekBar = (SeekBar) view.findViewById(R.id.seekbar_treble_sound_value);            midrangeSoundValueSeekBar = (SeekBar) view.findViewById(R.id.seekbar_midrange_sound_value);            bassSoundValueSeekbar = (SeekBar) view.findViewById(R.id.seekbar_bass_sound_value);            trebleSoundValueTxt = (TextView) view.findViewById(R.id.txt_treble_sound_value);            midrangeSoundValueTxt = (TextView) view.findViewById(R.id.txt_midrange_sound_value);            bassSoundValueTxt = (TextView) view.findViewById(R.id.txt_bass_sound_value);            waitingSoundValueTxt = (TextView) view.findViewById(R.id.txt_waiting_sound_value_p);            waitingSoundValueSeekbar = (SeekBar) view.findViewById(R.id.seekbar_waiting_sound_value);        }    }    @Override    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {        view = inflater.inflate(R.layout.fragment_sound, null);        initView(savedInstanceState);        initData(savedInstanceState);        return view;    }    @Override    public int initResid() {        return 0;    }    @Override    public IPresenter initPresenter() {        return new SoundPresenter(this);    }    @Override    public void initData(@Nullable Bundle savedInstanceState) {        settingManager = SettingManager.getSettingManager(this.getActivity());        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {            mAudioEffectManager=new AudioEffectManager(getContext(),this,getActivity().getLocalClassName());        }else {            mAudioEffectManager = settingManager.getAudioEffectManager();        }        //mAudioEffectManager = new AudioEffectManager(getContext(),this,get());        stringList = mPresenter.initList(getContext());        //waitingSoundValueTxt.setText(mAudioEffectManager.get);        adapter = new SoundListAdapter(getContext(), stringList);        spinnerLlinear.setmSpinnerListener(this);        spinnerLlinear.setAdapter(adapter);        //步骤1：创建一个SharedPreferences接口对象        SharedPreferences read = getContext().getSharedPreferences("EQ", MODE_PRIVATE);        //步骤2：获取文件中的值        value = read.getInt("Types", 0);        trebleSoundValueSeekBar.setMax(14);        midrangeSoundValueSeekBar.setMax(14);        bassSoundValueSeekbar.setMax(14);        waitingSoundValueSeekbar.setMax(15);        midrangeSoundValueSeekBar.setOnSeekBarChangeListener(this);        trebleSoundValueSeekBar.setOnSeekBarChangeListener(this);        bassSoundValueSeekbar.setOnSeekBarChangeListener(this);        waitingSoundValueSeekbar.setOnSeekBarChangeListener(this);        int sound = mAudioEffectManager.getAudioEffectLoudness();        int sounds=  sound+15;        waitingSoundValueSeekbar.setProgress(sounds);        onItemClick(null, null, -1, value);        spinnerLlinear.setSpinnerOperatingText(stringList.get(value).getName());        adapter.setID(spinnerLlinear.getSpinnerOperatingText());        waitingSoundValueTxt.setText(sound + "");        balanceDsp.setMaxVal(14);        balanceDsp.setDefVal(14);        balanceDsp.setBalenceListener(objListener);        resetTxt.setOnClickListener(this);      /* Message msg = handler.obtainMessage(10);        handler.sendMessage(msg);*/        Message msg = new Message();        handler.sendMessage(msg);    }    @Override    public void onStart() {        super.onStart();    }    private DspBalance.OnTouchListener objListener = new DspBalance.OnTouchListener() {        @Override        public void onBalance(float fFad, float fBal,boolean isFromUser) {            float x = fFad % 1;            float y = fBal % 1;            miFad = (int) fFad;            miBal = (int) fBal;            SharedPreferences.Editor editor = getContext().getSharedPreferences("EQ", MODE_PRIVATE).edit();            editor.putFloat("x", x);            editor.putFloat("y", y);            //步骤3：提交            editor.apply();            if (mAudioEffectManager != null) {                mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(miFad - 7, miBal - 7),isFromUser);            }        }        @Override        public void onBalance(int fFad, int fBal, boolean isFromUser) {        }    };    public static int floatToByte4(float f) {        int i = Float.floatToIntBits(f);        return i;    }    @Override    public void onClick(View v) {        SharedPreferences.Editor editor = getContext().getSharedPreferences("EQ", MODE_PRIVATE).edit();        editor.putFloat("x", 0.9633102f);        editor.putFloat("y", 0.8699093f);        editor.putInt("ValueTTxt", 0);        editor.putInt("ValueMTxt", 0);        editor.putInt("ValueBTxt", 0);        editor.apply();        mAudioEffectManager.setAudioEffectLoudness(0,true);        waitingSoundValueSeekbar.setProgress(15);        onItemClick(null, null, 0, 1);        spinnerLlinear.setSpinnerOperatingText(stringList.get(0).getName());        if (mAudioEffectManager != null) {            mAudioEffectManager.setBalanceSpeakerValue(AudioEffectParam.getBalanceFadeCombine(0, 0),true);        }        //步骤3：提交        int ibanlce = mAudioEffectManager.getBalanceSpeakerValue();        balanceDsp.updateBalance(AudioEffectParam.getBalance(ibanlce)+ 0.9633102f, AudioEffectParam.getFade(ibanlce)+0.8699093f);    }    @Override    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        int Treble;        int Middle;        int Bass;        if (position == 0) {            SharedPreferences read = getContext().getSharedPreferences("EQ", MODE_PRIVATE);            //步骤2：获取文件中的值            Treble = read.getInt("ValueTTxt", 0);            Middle = read.getInt("ValueMTxt", 0);            Bass = read.getInt("ValueBTxt", 0);            //  Toast.makeText(getContext(), Treble+""+Middle+""+Bass+"   1", Toast.LENGTH_SHORT).show();        } else if (position == -1) {            Treble = mAudioEffectManager.getAudioEffectTreble();            Middle = mAudioEffectManager.getAudioEffectMiddle();            Bass = mAudioEffectManager.getAudioEffectBass();        } else {            Treble = stringList.get(position).getValueTTxt();            Middle = stringList.get(position).getValueMTxt();            Bass = stringList.get(position).getValueBTxt();            // Toast.makeText(getContext(), Treble+""+Middle+""+Bass+"   2", Toast.LENGTH_SHORT).show();        }        int ValueTTxt = 0;        int ValueMTxt = 0;        int ValueBTxt = 0;        if (position!=-1){            mAudioEffectManager.setAudioEffectTreble(Treble*2,true);            mAudioEffectManager.setAudioEffectMiddle(Middle*2,true);            mAudioEffectManager.setAudioEffectBass(Bass*2,true);            SharedPreferences.Editor editor = getContext().getSharedPreferences("EQ", MODE_PRIVATE).edit();            editor.putInt("Types", position);            value = position;            //步骤3：提交            editor.commit();            ValueTTxt = Treble;            ValueMTxt = Middle;            ValueBTxt = Bass ;        }else {            ValueTTxt = Treble/2;            ValueMTxt = Middle/2;            ValueBTxt = Bass/2 ;        }        trebleSoundValueTxt.setText(ValueTTxt + "");        midrangeSoundValueTxt.setText(ValueMTxt + "");        bassSoundValueTxt.setText(ValueBTxt + "");        trebleSoundValueSeekBar.setProgress(ValueTTxt+7);        midrangeSoundValueSeekBar.setProgress(ValueMTxt+7);        bassSoundValueSeekbar.setProgress(ValueBTxt+7);    }    @Override    public void setData(@Nullable Object data) {    }    @Override    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {        int ValuePTxt = progress - 7;        switch (seekBar.getId()) {            case R.id.seekbar_treble_sound_value:                trebleSoundValueTxt.setText(ValuePTxt + "");                mAudioEffectManager.setAudioEffectTreble(ValuePTxt*2,false);                break;            case R.id.seekbar_midrange_sound_value:                mAudioEffectManager.setAudioEffectMiddle(ValuePTxt*2,false);                midrangeSoundValueTxt.setText(ValuePTxt + "");                // midrangeSoundValueNTxt.setText(ValueNTxt + "");                break;            case R.id.seekbar_bass_sound_value:                mAudioEffectManager.setAudioEffectBass(ValuePTxt*2,false);                bassSoundValueTxt.setText(ValuePTxt + "");                //bassSoundValueNTxt.setText(ValueNTxt + "");                break;            case R.id.seekbar_waiting_sound_value:                int sound=progress-15;                mAudioEffectManager.setAudioEffectLoudness(sound,false);                waitingSoundValueTxt.setText(sound + "");                //bassSoundValueNTxt.setText(ValueNTxt + "");                break;        }    }    @Override    public void onStartTrackingTouch(SeekBar seekBar) {        switch (seekBar.getId()) {            case R.id.seekbar_waiting_sound_value:                break;            default:                if (!spinnerLlinear.getSpinnerOperatingText().equals(stringList.get(0).getName())) {                    showRestoreDefaultDialog(seekBar);                }                break;        }    }    private void showRestoreDefaultDialog(final SeekBar seekBar) {        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialog);        dialog.setContentView(R.layout.display_dialog_restore_default);        dialog.setCanceledOnTouchOutside(true);        dialog.setCancelable(true);        Button connect = (Button) dialog.findViewById(R.id.btn_connect_btn);        TextView textView = (TextView) dialog.findViewById(R.id.txt_display_dialog_title);        /*textView.setText(R.string.);*/        textView.setText(getResources().getString(R.string.dialog_prompt));        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);        View.OnClickListener buttonListener = new View.OnClickListener() {            @Override            public void onClick(View view) {                switch (view.getId()) {                    case R.id.btn_connect_btn:                        // setSeekBar(seekBar);                        SharedPreferences.Editor editor = getContext().getSharedPreferences("EQ", MODE_PRIVATE).edit();                        editor.putInt("Types", 0);                        value = 0;                        //步骤3：提交                        editor.commit();                        spinnerLlinear.setSpinnerOperatingText(stringList.get(0).getName());                        dialog.dismiss();                        break;                    case R.id.btn_cancel:                        onItemClick(null, null, value, value);                        dialog.dismiss();                        break;                }            }        };        connect.setOnClickListener(buttonListener);        cancel.setOnClickListener(buttonListener);        dialog.show();        setDialogParam(dialog, 500, 316);    }    public void setSeekBar(final SeekBar seekBar) {        int progress = seekBar.getProgress();        int ValuePTxt = progress - 7;        SharedPreferences.Editor editor = getContext().getSharedPreferences("EQ", MODE_PRIVATE).edit();        switch (seekBar.getId()) {            case R.id.seekbar_treble_sound_value:                editor.putInt("ValueTTxt", ValuePTxt);                trebleSoundValueTxt.setText(ValuePTxt + "");                mAudioEffectManager.setAudioEffectTreble(ValuePTxt*2,true);                Log.w("ValueTTxt", "setSeekBar: "+ValuePTxt);                break;            case R.id.seekbar_midrange_sound_value:                mAudioEffectManager.setAudioEffectMiddle(ValuePTxt*2,true);                editor.putInt("ValueMTxt", ValuePTxt);                midrangeSoundValueTxt.setText(ValuePTxt + "");                Log.w("ValueMTxt", "setSeekBar: "+ValuePTxt);                // midrangeSoundValueNTxt.setText(ValueNTxt + "");                break;            case R.id.seekbar_bass_sound_value:                mAudioEffectManager.setAudioEffectBass(ValuePTxt*2,true);                editor.putInt("ValueBTxt", ValuePTxt);                bassSoundValueTxt.setText(ValuePTxt + "");                Log.w("ValueBTxt", "setSeekBar: "+ValuePTxt);                //bassSoundValueNTxt.setText(ValueNTxt + "");                break;        }        //步骤3：提交        editor.apply();    }    @Override    public void onStopTrackingTouch(SeekBar seekBar) {        switch (seekBar.getId()) {            case R.id.seekbar_waiting_sound_value:                int sound=seekBar.getProgress()-15;                mAudioEffectManager.setAudioEffectLoudness(sound,true);                waitingSoundValueTxt.setText(sound + "");                break;            default:                if (spinnerLlinear.getSpinnerOperatingText().equals(stringList.get(0).getName())) {                    setSeekBar(seekBar);                }                break;        }    }    @Override    public void onDetach() {        super.onDetach();    }    @Override    public void onVolumeChanged(int i, int i1) {    }    @Override    public void onMuteChanged(boolean b, int i) {    }}