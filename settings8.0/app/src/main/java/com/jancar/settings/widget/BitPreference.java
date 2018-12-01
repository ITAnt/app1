package com.jancar.settings.widget;

import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jancar.settings.R;

/**
 * Created by ouyan on 2018/8/30.
 */

public class BitPreference extends Preference implements View.OnClickListener {
    TextView spaceTxt;
    Button clearBtn;
    private OnClickListener onClickListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BitPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BitPreference(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.bit_prefs);
    }

    public BitPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        spaceTxt = (TextView) view.findViewById(R.id.txt_space);
        clearBtn = (Button) view.findViewById(R.id.btn_clear);
        spaceTxt.setText(getTitle());
        clearBtn.setText(getSummary());
        clearBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (onClickListener != null) {
            onClickListener.onClick(view, getKey());
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(View var1, String key);
    }
}
