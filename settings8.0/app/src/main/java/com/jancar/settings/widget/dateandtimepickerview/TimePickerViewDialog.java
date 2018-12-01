package com.jancar.settings.widget.dateandtimepickerview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.jancar.settings.R;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by Administrator on 2017/10/17.
 */

public class TimePickerViewDialog extends Dialog implements NumberPickerView.OnValueChangeListener{
    private static final String TAG = "picker";
    private TimePickerViewCallBack timePickerViewCallBack;
    private Context mContext;
    public NumberPickerView mPickerViewH;
    private NumberPickerView mPickerViewM;
    private TextView submitText;
    private TextView titleTxt;
    private String [] values;

    public TimePickerViewDialog(@NonNull Context context,String [] values) {
        super(context);
        mContext = context;
        this.values=values;
    }
    public TimePickerViewDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }
    public TimePickerViewDialog(@NonNull Context context, int themeResId) {
        super(context,themeResId);
        mContext = context;
    }
    public void setTimePickerViewCallBack(TimePickerViewCallBack timePickerViewCallBack) {
        this.timePickerViewCallBack = timePickerViewCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_pickerview_dialog);
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;
        int min=t.minute;
        mPickerViewH = (NumberPickerView)this.findViewById(R.id.picker_hour);
        mPickerViewH.setMaxValue(23);
//        mPickerViewH.setDisplayedValues(values);
        mPickerViewH.setValue(hour);
        mPickerViewM = (NumberPickerView)this.findViewById(R.id.picker_minute);
        mPickerViewM.setMaxValue(59);
        mPickerViewM.setValue(min);
        mPickerViewH.setOnValueChangedListener(this);
        mPickerViewM.setOnValueChangedListener(this);
        submitText=(TextView) findViewById(R.id.submitText);
        titleTxt=(TextView) findViewById(R.id.txt_title);
        titleTxt.setText(hour+" : "+min);
        submitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String h = mPickerViewH.getContentByCurrValue();
                String m = mPickerViewM.getContentByCurrValue();
                if (timePickerViewCallBack!=null){
                    timePickerViewCallBack.timePickerViewCallBack(h,
                            m);
                }else {
                    dismiss();
                }

            }
        });
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {

    }


    //点击确定时的回调函数，返回选择的时间
    public interface TimePickerViewCallBack{
        public void timePickerViewCallBack(String hour, String minute);
    }

}
