package com.jancar.radio.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.jancar.radio.R;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.adapter.RtyAdapter;
import com.jancar.radio.contract.RadioCacheUtil;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.contract.RdsContract;
import com.jancar.radio.presenter.RadioPresenter;
import com.jancar.radio.presenter.RdsPresenter;
import com.ui.mvp.view.BaseActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RdsActivity extends BaseActivity<RdsContract.Presenter, RdsContract.View> implements RdsContract.View, AdapterView.OnItemClickListener {
    public static final String BROADCAST_RDS = "RadioActivity.RdsActivity";
    Unbinder unbinder;
    @BindView(R.id.gv_rty)
    GridView gv_rty;
    @BindView(R.id.txt_pty)
    TextView TextView;
    @BindView(R.id.txt_ta)
    TextView ta_Txt;
    @BindView(R.id.txt_ps)
    TextView psTxt;
    @BindView(R.id.ta)
    TextView ta;
    @BindView(R.id.tp)
    TextView tp;
    @BindView(R.id.af)
    TextView af;
    @BindView(R.id.txt_rt)
    TextView rtTxt;
    @BindView(R.id.txt_frequency)
    TextView txt_frequency;
    RtyAdapter adapter;
    private List<String> ptyList;
    String pty;
    int TA=0;
    int AF=0;
    int mPty;
    int mtp;
    int ifreq;
    String mPs;
     String mRt;
    int Freq;
    int mLocation;
    int mBand;
    RdsReceiver mRdsReceiver;
    public class RdsReceiver extends BroadcastReceiver {
        public static final String TAG = "Rds";

        @Override
        public void onReceive(Context context, Intent intent) {
            int Type = intent.getIntExtra("Type", 0);
            switch (Type) {
                case 0:
                    Freq =intent.getIntExtra("ifreq", 0);
                    if (txt_frequency!=null){

                        txt_frequency.setText( RadioWrapper.getFreqString(Freq, mBand, mLocation));
                    }
                    break;
                case 1:
                    mPty =intent.getIntExtra("mPty", 0);
                    mtp =intent.getIntExtra("mTP", 0);
                    TA =intent.getIntExtra("mTA", 0);
                  /*  intent.putExtra("mTP", mTP);
                    intent.putExtra("mTA", mTA);*/
                    if (TextView!=null){
                        if (TA==1){
                            ta.setTextColor(Color.parseColor("#1336a3"));
                        }else {
                            ta.setTextColor(Color.parseColor("#ffffff"));
                        }
                        if (mtp==1){
                            tp.setTextColor(Color.parseColor("#1336a3"));
                        }else {
                            tp.setTextColor(Color.parseColor("#ffffff"));
                        }
                    TextView.setText("PTY: "+ptyList.get(mPty));
                    }
                    break;
                case 2:
                    mPs =intent.getStringExtra("ps");
                    if (psTxt!=null){
                        psTxt.setText("PS: "+mPs);
                    }
                    break;
                case 3:
                    mRt =intent.getStringExtra("Rt");
                    if (rtTxt!=null){
                        rtTxt.setText("RT: "+mRt);
                    }
                    break;
                case 4:
                    mLocation=    intent.getIntExtra("mLocation", 0);
                    mBand=   intent.getIntExtra("mBand", 0);
                    Freq =intent.getIntExtra("ifreq", 0);
                    if (txt_frequency!=null){

                        txt_frequency.setText( RadioWrapper.getFreqString(Freq, mBand, mLocation));
                    }
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rds);
        unbinder = ButterKnife.bind(this);
        initData();
    }

    private void initData() {

        Intent intent  =getIntent();
         Freq=  intent.getIntExtra("Freq", 0);
         mLocation=    intent.getIntExtra("mLocation", 0);
         mBand=   intent.getIntExtra("mBand", 0);
         mPs=  intent.getStringExtra("PS");
        mPty=  intent.getIntExtra("PTY",0);
        mRdsReceiver=new RdsReceiver();
        txt_frequency.setText( RadioWrapper.getFreqString(Freq, mBand, mLocation));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_RDS);
        registerReceiver(mRdsReceiver, intentFilter);
        ptyList = Arrays.asList(RadioCacheUtil.ptyArr);
        adapter=new RtyAdapter(this,ptyList,RadioCacheUtil.getInstance(getApplicationContext()).getPty());
        gv_rty.setAdapter(adapter);
       TextView.setText("PTY: "+ptyList.get(mPty));
        psTxt.setText("PS: "+mPs);
        gv_rty.setOnItemClickListener(this);
        TA=RadioCacheUtil.getInstance(getApplicationContext()).getTA();
        AF=RadioCacheUtil.getInstance(getApplicationContext()).getAF();
        if (TA==0){
            ta.setTextColor(Color.parseColor("#ffffff"));
        }else {
            ta.setTextColor(Color.parseColor("#1336a3"));
        }
        if (AF==0){
            af.setTextColor(Color.parseColor("#ffffff"));
        }else {
            af.setTextColor(Color.parseColor("#1336a3"));
        }
    }

    @Override
    public RdsContract.Presenter createPresenter() {
        return new RdsPresenter();
    }

    @Override
    public RdsContract.View getUiImplement() {
        return this;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        unregisterReceiver(mRdsReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setPty(position);
        pty=ptyList.get(position);
        adapter.notifyDataSetChanged();
        RadioCacheUtil.getInstance(getApplicationContext()).setPty(position);
        //TextView.setText("PTY: "+ptyList.get(adapter.getPty()));
        Intent intent = new Intent();
        intent.setAction(RadioActivity.BROADCAST_ACTION);
        intent.putExtra("Type", 1);
        intent.putExtra("Pty",adapter.getPty());
        sendBroadcast(intent);
    }
    @OnClick({R.id.txt_ta,R.id.txt_af})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.txt_ta:
                boolean Ta=false;
                if (TA!=0){
                    TA=0;
                    ta.setTextColor(Color.parseColor("#ffffff"));
                }else {
                    TA=1;
                    Ta=true;
                    ta.setTextColor(Color.parseColor("#1336a3"));
                }
                Intent intents = new Intent();
                intents.setAction(RadioActivity.BROADCAST_ACTION);
                intents.putExtra("Type", 2);
                intents.putExtra("RdsTa",Ta);
                sendBroadcast(intents);
                RadioCacheUtil.getInstance(getApplicationContext()).setTA(TA);
                break;
            case R.id.txt_af:
                boolean Af=false;
                if (AF!=0){
                    AF=0;
                    af.setTextColor(Color.parseColor("#ffffff"));
                }else {
                    AF=1;
                    Af=true;
                    af.setTextColor(Color.parseColor("#1336a3"));
                }
                Intent intent = new Intent();
                intent.setAction(RadioActivity.BROADCAST_ACTION);
                intent.putExtra("Type", 3);
                intent.putExtra("RdsAf",Af);
                sendBroadcast(intent);
                RadioCacheUtil.getInstance(getApplicationContext()).setAF(AF);
                break;
        }
    }

}
