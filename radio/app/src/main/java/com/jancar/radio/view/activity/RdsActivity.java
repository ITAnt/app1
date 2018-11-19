package com.jancar.radio.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jancar.radio.R;
import com.jancar.radio.contract.RadioContract;
import com.jancar.radio.contract.RdsContract;
import com.jancar.radio.presenter.RadioPresenter;
import com.jancar.radio.presenter.RdsPresenter;
import com.ui.mvp.view.BaseActivity;

public class RdsActivity extends BaseActivity<RdsContract.Presenter, RdsContract.View> implements RdsContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rds);
    }

    @Override
    public RdsContract.Presenter createPresenter() {
        return new RdsPresenter();
    }

    @Override
    public RdsContract.View getUiImplement() {
        return this;
    }
}
