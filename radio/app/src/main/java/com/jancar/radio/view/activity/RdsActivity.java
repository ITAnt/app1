package com.jancar.radio.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.jancar.radio.R;
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
import butterknife.Unbinder;

public class RdsActivity extends BaseActivity<RdsContract.Presenter, RdsContract.View> implements RdsContract.View, AdapterView.OnItemClickListener {
    Unbinder unbinder;
    @BindView(R.id.gv_rty)
    GridView gv_rty;
    @BindView(R.id.txt_pty)
    TextView TextView;
    RtyAdapter adapter;
    private List<String> ptyList;
    String pty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rds);
        unbinder = ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        ptyList = Arrays.asList(RadioCacheUtil.ptyArr);
        adapter=new RtyAdapter(this,ptyList,RadioCacheUtil.getInstance().getPty());
        gv_rty.setAdapter(adapter);
        TextView.setText("PTY: "+ptyList.get(adapter.getPty()));
        gv_rty.setOnItemClickListener(this);
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setPty(position);
        pty=ptyList.get(position);
        adapter.notifyDataSetChanged();
        TextView.setText("PTY: "+ptyList.get(adapter.getPty()));
    }
}
