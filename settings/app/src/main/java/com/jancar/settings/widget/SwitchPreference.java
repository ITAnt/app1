package com.jancar.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.jancar.settings.R;

public class SwitchPreference extends android.preference.SwitchPreference {
	private TextView titleTxt;
	private TextView summaryTxt;
	private Context mContext;
	private View layout;
	private Switch mSwitch;
	private boolean defChecked;

	public void setOnClickListener(View.OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	private View.OnClickListener onClickListener;

	public SwitchPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public SwitchPreference(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onAttachedToActivity() {
		super.onAttachedToActivity();
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		super.onCreateView(parent);
		final String key = this.getKey();
		if(layout!=null){
			return layout;
		}
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.preference_switch_item, null);
		mSwitch = (Switch) layout.findViewById(R.id.my_switch);
		mSwitch.setChecked(defChecked);
		mSwitch.setOnClickListener(onClickListener);
		titleTxt = (TextView) layout.findViewById(R.id.txt_title);
		summaryTxt = (TextView) layout.findViewById(R.id.txt_summary);
		return layout;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		titleTxt.setText(SwitchPreference.this.getTitle());
		summaryTxt.setText(SwitchPreference.this.getSummary());
	}

	public void setDefChecked(boolean defChecked) {
		if(mSwitch!=null){
			mSwitch.setChecked(defChecked);
		}else{
			this.defChecked = defChecked;
		}
	}
}