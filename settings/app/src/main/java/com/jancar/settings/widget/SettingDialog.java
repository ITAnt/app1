package com.jancar.settings.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jancar.settings.R;


/**
 * @anthor Tzq
 * @time 2018/8/22 15:02
 * @describe 蓝牙设置Dialog
 */
public class SettingDialog extends Dialog {
    private Context context;
    private TextView tvYes;
    private TextView tvNo;
    private EditText editText;


    public SettingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initViews();
    }


    private void initViews() {
        setContentView(R.layout.dialog_setting_name);
        tvYes = findViewById(R.id.tv_setting_dialog_yes);
        tvNo = findViewById(R.id.tv_setting_dialog_no);
        editText = findViewById(R.id.edit_set_name);
    }

    public void setEditOnClickListener(View.OnClickListener clickListener) {
        tvYes.setOnClickListener(clickListener);

    }

    public void setCanelOnClickListener(View.OnClickListener canelOnClickListener) {
        tvNo.setOnClickListener(canelOnClickListener);
    }

    public String getEditText() {
        return editText.getText().toString();
    }

    public void setEditText(String textName) {
        editText.setText(textName);
        editText.setSelection(textName.length());
    }

}
