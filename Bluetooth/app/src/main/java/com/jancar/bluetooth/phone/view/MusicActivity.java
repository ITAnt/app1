package com.jancar.bluetooth.phone.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.MusicContract;
import com.jancar.bluetooth.phone.presenter.MusicPresenter;
import com.jancar.bluetooth.phone.util.IntentUtil;
import com.ui.mvp.view.BaseActivity;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:22
 */
public class MusicActivity extends BaseActivity<MusicContract.Presenter, MusicContract.View> implements MusicContract.View {
    @Override
    public MusicContract.Presenter createPresenter() {
        return new MusicPresenter();
    }

    @Override
    public MusicContract.View getUiImplement() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isConnect = BluetoothManager.getBluetoothManagerInstance(this).isConnect();
        if (isConnect) {
            IntentUtil.gotoActivity(MusicActivity.this, MusicContentActivity.class, true);
        } else {
            setContentView(R.layout.dialog_connect);
            findViewById(R.id.tv_connect_dialog_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClassName("com.jancar.settingss", "com.jancar.settings.view.activity.MainActivity");
                    intent.putExtra("position", 1);
                    startActivity(intent);
                    finish();
                }
            });
            findViewById(R.id.tv_connect_dialog_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(0, 0);
    }

}
