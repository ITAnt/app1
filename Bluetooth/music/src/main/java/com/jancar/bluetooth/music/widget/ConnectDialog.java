package com.jancar.bluetooth.music.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jancar.bluetooth.music.R;


/**
 * @anthor Tzq
 * @time 2018/8/22 15:02
 * @describe 连接蓝牙Dialog
 */
public class ConnectDialog extends Dialog {
    private Context context;
    private TextView tvYes;
    private TextView tvNo;

    public ConnectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initViews();
    }

    public ConnectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initViews();
    }

    protected ConnectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.dialog_connect);
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View view = layoutInflater.inflate(R.layout.dialog_contact_synchronous, null);
//        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tvYes = findViewById(R.id.tv_connect_dialog_yes);
        tvNo = findViewById(R.id.tv_connect_dialog_no);
    }

    public void go2SettingOnClickListener(View.OnClickListener clickListener) {
        tvYes.setOnClickListener(clickListener);

    }

    public void setCanelOnClickListener(View.OnClickListener canelOnClickListener) {
        tvNo.setOnClickListener(canelOnClickListener);

    }

}
