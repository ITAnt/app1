package com.jancar.settings.widget.dateandtimepickerview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;


import com.jancar.settings.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/10/17.
 */

public class DatePickerViewDialog extends Dialog implements NumberPickerView.OnValueChangeListener {
    private Context mContext;
    public NumberPickerView mPickerViewY;
    private NumberPickerView mPickerViewM;
    private NumberPickerView mPickerViewD;
    private TextView submitText;
    private TextView titleTxt;
    private String [] yearValues,monthValues,dayValues;
    private int maxDays;

    private int resultYear,resultMonth;

    private DatePickerViewDialogCallBack datePickerViewDialogCallBack;


    public DatePickerViewDialog(@NonNull Context context) {
        super(context,R.style.record_voice_dialog);
        this.mContext=context;
    }


    public void setDatePickerViewDialogCallBack(DatePickerViewDialogCallBack datePickerViewDialogCallBack) {
        this.datePickerViewDialogCallBack = datePickerViewDialogCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_pickerview_dialog);
        yearValues=new String[200];
        for (int i=0;i<yearValues.length;i++){
            yearValues[i]=1901+i+"";
        }

        monthValues=new String[12];
        for (int i=0;i<monthValues.length;i++){
            monthValues[i]=i+1+"";
        }



        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;
        int min=t.minute;
        resultYear=year;
        resultMonth=month+1;
        maxDays=getDaysByYearMonth(resultYear,resultMonth);

        dayValues=new String[maxDays];
        for (int i=0;i<dayValues.length;i++){
            dayValues[i]=i+1+"";
        }

        mPickerViewY=(NumberPickerView)findViewById(R.id.picker_year);
        mPickerViewM=(NumberPickerView)findViewById(R.id.picker_month);
        mPickerViewD=(NumberPickerView)findViewById(R.id.picker_day);

        mPickerViewY.setDisplayedValues(yearValues);
        mPickerViewY.setMaxValue(yearValues.length-1);
        mPickerViewY.setValue(year-1901);

        mPickerViewM.setDisplayedValues(monthValues);
        mPickerViewM.setMaxValue(monthValues.length-1);
        mPickerViewM.setValue(month);

        mPickerViewD.setDisplayedValues(dayValues);
        mPickerViewD.setMaxValue(dayValues.length-1);
        mPickerViewD.setValue(date-1);

        mPickerViewY.setOnValueChangedListener(this);
        mPickerViewM.setOnValueChangedListener(this);
        submitText=(TextView) findViewById(R.id.submitText);
        titleTxt=(TextView) findViewById(R.id.txt_title);
        SimpleDateFormat formatter= new SimpleDateFormat(getContext().getResources().getString(R.string.date));
        Date curDate = new Date(System.currentTimeMillis());
        titleTxt.setText(formatter.format(curDate));
        submitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String y=mPickerViewY.getContentByCurrValue();
                String m=mPickerViewM.getContentByCurrValue();
                String d=mPickerViewD.getContentByCurrValue();

                if (datePickerViewDialogCallBack!=null){
                    datePickerViewDialogCallBack.datePickerViewDialogCallBack(y,m,d);
                }else {
                    dismiss();
                }


            }
        });
    }



    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        if (picker.getId()==R.id.picker_year){
            resultYear=Integer.parseInt(yearValues[picker.getValue()]);

            maxDays=getDaysByYearMonth(resultYear,resultMonth);
            mPickerViewD.setDisplayedValues(dayValues);
            mPickerViewD.setMaxValue(dayValues.length-1);
        }else if (picker.getId()==R.id.picker_month){
            resultMonth=picker.getValue()+1;
            maxDays=getDaysByYearMonth(resultYear,resultMonth);
            dayValues=new String[maxDays];
            for (int i=0;i<dayValues.length;i++){
                dayValues[i]=i+1+"";
            }
            mPickerViewD.setDisplayedValues(dayValues,true);
            mPickerViewD.setMaxValue(dayValues.length-1);
        }
    }

    public interface DatePickerViewDialogCallBack{
        public void datePickerViewDialogCallBack(String year, String month, String day);
    }

}
