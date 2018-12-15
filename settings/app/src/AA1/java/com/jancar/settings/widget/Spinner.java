package com.jancar.settings.widget;

import android.content.Context;
import android.support.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.adapter.SoundListAdapter;
import com.jancar.settings.contract.EqEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouyan on 2018/9/8.
 */

public class Spinner extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener {
    private View mCalendar;
    private Context mContext;
    private Context context;
    private LayoutInflater layoutInflater;
    private TextView spinnerOperatingTxt;
    private String spinnerOperatingText;
    private ListView spinnerOperatingList;
    private SoundListAdapter adapter;
    private LinearLayout spinnerOperatingLlayout;
    private List<String> stringList;
    private  spinnerListener mSpinnerListener;
    public Spinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // adapter = new SoundListAdapter(mContext, stringList);
        getmCalendar();
    }

    public Spinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //  adapter = new SoundListAdapter(mContext, stringList);
        getmCalendar();
    }

    public Spinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //adapter = new SoundListAdapter(mContext, stringList);
        getmCalendar();
    }

    public Spinner(Context context) {
        super(context);
        this.mContext = context;
        // adapter = new SoundListAdapter(mContext, stringList);
        getmCalendar();
    }

    public View getmCalendar() {
        if (mCalendar == null) {
            layoutInflater = LayoutInflater.from(mContext);
            mCalendar = layoutInflater.inflate(R.layout.spinner_layout, null);
            context = mContext;
            spinnerOperatingLlayout = (LinearLayout) mCalendar.findViewById(R.id.llayout_spinner_operating);
            spinnerOperatingTxt = (TextView) mCalendar.findViewById(R.id.txt_spinner_operating);
            if (spinnerOperatingText != null) {
                spinnerOperatingTxt.setText(spinnerOperatingText);
            }
            spinnerOperatingList = (ListView) mCalendar.findViewById(R.id.spinner_operating_list);
            if (adapter != null) {
                spinnerOperatingList.setAdapter(adapter);
            }
            spinnerOperatingList.setOnItemClickListener(this);
            spinnerOperatingLlayout.setOnClickListener(this);
            addView(mCalendar);
        }

        return mCalendar;
    }

    public void initStringListAdapterData(List<String> stringList) {
        this.stringList = stringList;

    }

    public void setListVisibility() {
        if (spinnerOperatingList != null) {
            if (spinnerOperatingList.getVisibility() == GONE) {
                spinnerOperatingList.setVisibility(VISIBLE);
                adapter.setID(getSpinnerOperatingText());
                adapter.notifyDataSetInvalidated();
                spinnerOperatingLlayout.setBackgroundResource(R.mipmap.cbb_bg_h);
            } else {
                spinnerOperatingList.setVisibility(GONE);
                spinnerOperatingLlayout.setBackgroundResource(R.mipmap.cbb_bg_n);
            }
        }

    }

    public void setSpinnerOperatingText(String spinnerOperatingText) {
        this.spinnerOperatingText = spinnerOperatingText;
        if (spinnerOperatingTxt != null) {
            spinnerOperatingTxt.setText(spinnerOperatingText);
        }
    }

    public SoundListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(SoundListAdapter adapter) {
        this.adapter = adapter;
        if (spinnerOperatingList != null) {

            spinnerOperatingList.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (spinnerOperatingTxt != null && adapter != null) {

            setSpinnerOperatingText(((EqEntity)adapter.getItem(position)).getName());
            spinnerOperatingTxt.setText(getSpinnerOperatingText());
            setListVisibility();
            if (mSpinnerListener!=null){
                mSpinnerListener.onItemClick(parent,view,position,id);
            }
        }

    }

    public void setmSpinnerListener(spinnerListener mSpinnerListener) {
        this.mSpinnerListener = mSpinnerListener;
    }

    public  interface spinnerListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    public String getSpinnerOperatingText() {
        return spinnerOperatingText;
    }

    @Override
    public void onClick(View v) {
        setListVisibility();
    }
}
