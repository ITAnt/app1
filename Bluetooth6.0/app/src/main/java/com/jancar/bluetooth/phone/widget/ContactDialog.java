package com.jancar.bluetooth.phone.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jancar.bluetooth.phone.R;


/**
 * @anthor Tzq
 * @time 2018/8/22 15:02
 * @describe 同步通讯录Dialog
 */
public class ContactDialog extends Dialog {
    private Context context;
    private TextView tvYes;
    private TextView tvNo;

    public ContactDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initViews();
    }

    public ContactDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initViews();
    }

    protected ContactDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.dialog_contact_synchronous);
        tvYes = findViewById(R.id.tv_contact_dialog_yes);
        tvNo = findViewById(R.id.tv_contact_dialog_no);
    }

    public void setSynchOnClickListener(View.OnClickListener clickListener) {
        tvYes.setOnClickListener(clickListener);

    }

    public void setCanelOnClickListener(View.OnClickListener canelOnClickListener) {
        tvNo.setOnClickListener(canelOnClickListener);

    }

}
