package com.jancar.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.Preference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.jancar.settings.R;

/**
 * Created by ouyan on 2018/8/30.
 */

public class DropPreference extends Preference {
    private TextView TitleTxt;
    private TextView summaryTxt;
    private TextView lineTxt;
    private ImageView img_arrow;
    private RadioGroup languageRadio;
    private RadioButton chineseRbtn;
    private RadioButton englishRbtn;
    private int laguageVisibility;//语言选择布局是否显示与隐藏状态
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick(View v, String key);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DropPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DropPreference(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.drop_prefs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Drop);
        laguageVisibility = ta.getInteger(R.styleable.Drop_Visibility, View.GONE);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TitleTxt = (TextView) view.findViewById(R.id.txt_title);
        summaryTxt = (TextView) view.findViewById(R.id.txt_summary);
        languageRadio = (RadioGroup) view.findViewById(R.id.radio_language);
        chineseRbtn = (RadioButton) view.findViewById(R.id.rbtn_chinese);
        englishRbtn = (RadioButton) view.findViewById(R.id.rbtn_english);
        lineTxt = (TextView) view.findViewById(R.id.txt_line);
        img_arrow = (ImageView) view.findViewById(R.id.img_arrow);
        img_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v, getKey());
                }
            }
        });
        TitleTxt.setText(getTitle());
        languageRadio.setOnCheckedChangeListener(onCheckedChangeListener);
        String summary = getSummary() + "";
        if (summary.equals("null")) {
            summaryTxt.setVisibility(View.GONE);
        } else {
            summaryTxt.setText(getSummary());
            summaryTxt.setVisibility(View.VISIBLE);
            if (summaryTxt.getText().toString().equals(chineseRbtn.getText().toString())) {
                chineseRbtn.setChecked(true);
            } else if (summaryTxt.getText().toString().equals(englishRbtn.getText().toString())) {
                englishRbtn.setChecked(true);
            }
        }
        if (laguageVisibility == View.GONE) {
            img_arrow.setImageResource(R.drawable.balance_btn_right_state);
        } else {
            img_arrow.setImageResource(R.drawable.balance_btn_bottom_state);
        }
        setVisibility(laguageVisibility);
    }

    public void setLaguageVisibility(int visibility) {
        this.laguageVisibility = visibility;
    }

    public void setChecked(int type) {
        if (type == 0) {
            if (englishRbtn != null) {
                englishRbtn.setChecked(true);
            }
        } else if (type == 1) {
            if (chineseRbtn != null) {
                chineseRbtn.setChecked(true);
            }
        }
    }

    public void setVisibility(int visibility) {

        if (languageRadio != null && lineTxt != null) {
            laguageVisibility=visibility;
            languageRadio.setVisibility(visibility);
            lineTxt.setVisibility(visibility);
        }
    }

    public int getVisibility() {
        if (languageRadio != null) {
            return languageRadio.getVisibility();
        }
        return View.GONE;
    }

    public DropPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropPreference(Context context) {
        this(context, null);
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }
}
