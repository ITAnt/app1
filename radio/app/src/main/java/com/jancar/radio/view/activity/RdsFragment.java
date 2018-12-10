package com.jancar.radio.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.jancar.radio.R;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.adapter.RtyAdapter;
import com.jancar.radio.contract.RadioCacheUtil;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.contract.RdsContract;
import com.jancar.radio.notification.uite;
import com.jancar.radio.presenter.RadioPresenter;
import com.jancar.radio.presenter.RdsPresenter;
import com.ui.mvp.view.BaseActivity;
import com.ui.mvp.view.BaseFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jancar.radio.notification.uite.CONTEXT;
import static com.jancar.radio.notification.uite.FREQ;
import static com.jancar.radio.notification.uite.INITIAL;
import static com.jancar.radio.notification.uite.PSTXT;
import static com.jancar.radio.notification.uite.RTTXT;

public class RdsFragment extends BaseFragment<RdsContract.Presenter, RdsContract.View> implements RdsContract.View, AdapterView.OnItemClickListener {
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

    Context   context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.activity_rds, null);
        unbinder =ButterKnife.bind(this, view);
        initData();
        return view;
    }

    public void getData(Object data){
        String mps;
        Bundle mBundle = (Bundle) ((Message) data).getData();
        switch (((Message) data).what) {
            case CONTEXT:

                //mCustomer = mBundle.getParcelable("Customer");//得到对客户详情请求的参数 客户ID
                   context = (Context) ((Message) data).obj;//得到该界面context
             break;
            case INITIAL:
                //Bundle mBundle = (Bundle) ((Message) data).getData();
                Freq=  mBundle.getInt("Freq", 0);
                mLocation=    mBundle.getInt("mLocation", 0);
                mBand=  mBundle.getInt("mBand", 0);
                mPs=  mBundle.getString("PS");
            int     mPty=  mBundle.getInt("PTY",0);
                txt_frequency.setText( RadioWrapper.getFreqString(Freq, mBand, mLocation));
                if (mPty<ptyList.size()){

                    TextView.setText("PTY: "+ptyList.get(mPty));
                }
                 mps=mPs==null?"":mPs;
                psTxt.setText("PS: "+mps);
               // psTxt.setText("PS: "+mPs);
                break;
            case PSTXT:
                mPs=  mBundle.getString("PS");
                 mps=mPs==null?"":mPs;
                psTxt.setText("PS: "+mps);
               // psTxt.setText("PS: "+mPs);
                //mCustomer = mBundle.getParcelable("Customer");//得到对客户详情请求的参数 客户ID
               // context = (Context) ((Message) data).obj;//得到该界面context
                break;
            case RTTXT:
                mRt=  mBundle.getString("Rt");
                rtTxt.setText("RT: "+mRt);
                //mCustomer = mBundle.getParcelable("Customer");//得到对客户详情请求的参数 客户ID
                // context = (Context) ((Message) data).obj;//得到该界面context
                break;
            case FREQ:
                Freq=  mBundle.getInt("Freq", 0);
                txt_frequency.setText( RadioWrapper.getFreqString(Freq, mBand, mLocation));
                break;
        }
        }
    private void initData() {
        ptyList = Arrays.asList(RadioCacheUtil.ptyArr);
        adapter=new RtyAdapter(getContext(),ptyList,RadioCacheUtil.getInstance(getContext().getApplicationContext()).getPty());
        mPty=RadioCacheUtil.getInstance(getContext().getApplicationContext()).getPty();
        adapter.setPty(mPty);
        gv_rty.setAdapter(adapter);
        TA=RadioCacheUtil.getInstance(getContext().getApplicationContext()).getTA();
        AF=RadioCacheUtil.getInstance(getContext().getApplicationContext()).getAF();

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
        gv_rty.setOnItemClickListener(this);
        txt_frequency.setText( RadioWrapper.getFreqString(Freq, mBand, mLocation));
        if(mPty<ptyList.size()){

            TextView.setText("PTY: "+ptyList.get(mPty));
        }
        String mps=mPs==null?"":mPs;
        psTxt.setText("PS: "+mps);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setPty(position);
        pty=ptyList.get(position);
        RadioCacheUtil.getInstance(getContext().getApplicationContext()).setPty(position);

        ((RadioActivity)context).selectRdsPty(adapter.getPty());
        adapter.notifyDataSetChanged();

    }
    @OnClick({R.id.txt_ta,R.id.txt_af,R.id.txt_rds})
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
                ((RadioActivity)context).selectRdsTa(Ta);
                RadioCacheUtil.getInstance(getContext().getApplicationContext()).setTA(TA);

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
                ((RadioActivity)context).selectRdsAf(Af);
                RadioCacheUtil.getInstance(getContext().getApplicationContext()).setAF(AF);
              //  sendBroadcast(intent);
              //  RadioCacheUtil.getInstance(getApplicationContext()).setAF(AF);
                break;
            case R.id.txt_rds:

                        ((RadioActivity)context).hideRds(0);
                break;
        }
    }

}
