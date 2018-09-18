package com.jancar.settings.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jancar.settings.R;
import com.jancar.settings.SetDateTime;
import com.jancar.settings.adapter.TimeZoneListAdapter;
import com.jancar.settings.contract.TimeZoneEntity;
import com.jancar.settings.listener.Contract.TimeContractImpl;
import com.jancar.settings.listener.IPresenter;
import com.jancar.settings.manager.BaseFragment;
import com.jancar.settings.manager.BaseFragments;
import com.jancar.settings.presenter.TimePresenter;
import com.jancar.settings.util.wifi.WifiController;
import com.jancar.settings.util.wifi.WifiEnabler;
import com.jancar.settings.view.activity.MainActivity;
import com.jancar.settings.view.activity.TimeZoneActivity;
import com.jancar.settings.widget.CircleBarView;
import com.jancar.settings.widget.SwitchButton;
import com.jancar.settings.widget.dateandtimepickerview.DatePickerViewDialog;
import com.jancar.settings.widget.dateandtimepickerview.Time12PickerViewDialog;
import com.jancar.settings.widget.dateandtimepickerview.TimePickerViewDialog;

import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.provider.Settings.Global.AUTO_TIME;
import static com.jancar.settings.util.Tool.getTime;
import static com.jancar.settings.util.Tool.set24Hour;
import static com.jancar.settings.util.Tool.setAutoTime;
import static com.jancar.settings.util.Tool.setChinaTimeZone;
import static com.jancar.settings.util.Tool.setDialogParam;

/**
 * Created by ouyan on 2018/9/4.
 */

public class TimeFragment extends BaseFragments<TimePresenter> implements TimeContractImpl.View, View.OnClickListener, TimePickerViewDialog.TimePickerViewCallBack, DatePickerViewDialog.DatePickerViewDialogCallBack, RadioGroup.OnCheckedChangeListener, Time12PickerViewDialog.TimePickerViewCallBack, AdapterView.OnItemClickListener {
    private View view;
    private RelativeLayout rLayoutTime;
    private RelativeLayout rLayoutDate;
    private RelativeLayout rLayoutTimeSystem;
    private RelativeLayout rLayoutTimeZone;
    private RelativeLayout rLayoutTimeInterface;
    private ListView timeZoneList;

    private TextView timeTitleTxt;
    private TextView dateTitleTxt;
    private TextView dateTxt;
    private TextView timeTxt;
    private TextView timeSystemSummaryTxt;
    private TextView timeZoneTxt;
    private RadioGroup systemRadio;
    private RadioButton twentyHourSystemRbtn;
    private RadioButton tenHourSystemRbtn;
    private TextView systemLineTxt;
    private SwitchButton adjustSwitch;
    private TextView adjustSummaryTxt;
    private ImageView timeArrowImg;
    private ImageView dateArrowImg;
    private ImageView timeSystemArrowImg;
    DatePickerViewDialog dateDialog;
    TimePickerViewDialog timeDialog;
    Time12PickerViewDialog time12Dialog;
    private TimeZoneListAdapter timeZoneAdapter;
    private List<TimeZoneEntity> nameList;
    private Calendar mDummyDate;
    Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            ((MainActivity) getActivity()).mHadler = null;
            if (timeZoneList != null) {
                timeZoneList.setVisibility(View.GONE);
                rLayoutTimeInterface.setVisibility(View.VISIBLE);
            }

            super.handleMessage(msg);
        }
    };
    private IntentFilter intentFilter;

    private TimeChangeReceiver timeChangeReceiver;

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.time_fragment, null);
        initView(savedInstanceState);
        initData(savedInstanceState);

        return view;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            rLayoutTime = (RelativeLayout) view.findViewById(R.id.fragment_time);
            rLayoutDate = (RelativeLayout) view.findViewById(R.id.fragment_date);
            rLayoutTimeSystem = (RelativeLayout) view.findViewById(R.id.fragment_time_system);
            rLayoutTimeZone = (RelativeLayout) view.findViewById(R.id.fragment_time_zone);
            rLayoutTimeInterface = (RelativeLayout) view.findViewById(R.id.fragment_time_interface);
            timeZoneList = (ListView) view.findViewById(R.id.list_time_zone);
            timeTxt = (TextView) view.findViewById(R.id.txt_time);
            timeTitleTxt = (TextView) view.findViewById(R.id.txt_time_title);
            dateTitleTxt = (TextView) view.findViewById(R.id.txt_date_title);
            dateTxt = (TextView) view.findViewById(R.id.txt_date);
            timeSystemSummaryTxt = (TextView) view.findViewById(R.id.txt_time_system_summary);
            systemRadio = (RadioGroup) view.findViewById(R.id.radio_system);
            twentyHourSystemRbtn = (RadioButton) view.findViewById(R.id.rbtn_24_hour_system);
            tenHourSystemRbtn = (RadioButton) view.findViewById(R.id.rbtn_12_hour_system);
            systemLineTxt = (TextView) view.findViewById(R.id.txt_system_line);
            adjustSwitch = (SwitchButton) view.findViewById(R.id.switch_adjust);
            adjustSummaryTxt = (TextView) view.findViewById(R.id.txt_adjust_summary);
            timeSystemArrowImg = (ImageView) view.findViewById(R.id.img_time_system_arrow);
            timeArrowImg = (ImageView) view.findViewById(R.id.img_time_arrow);
            dateArrowImg = (ImageView) view.findViewById(R.id.img_date_arrow);
            timeZoneTxt = (TextView) view.findViewById(R.id.txt_time_zone);
            rLayoutTimeSystem.setOnClickListener(this);
            rLayoutTime.setOnClickListener(this);
            rLayoutDate.setOnClickListener(this);
            rLayoutTimeZone.setOnClickListener(this);
            systemRadio.setOnCheckedChangeListener(this);
            adjustSwitch.setOnClickListener(this);


        }
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        adjustSwitch.setThumbDrawableRes(R.drawable.switch_custom_thumb_selector);
        adjustSwitch.setBackDrawableRes(R.drawable.switch_custom_track_selector);
        dateTxt.setText(getTime(1));
        mDummyDate = Calendar.getInstance();
        String strTimeFormat = android.provider.Settings.System.getString(getContext().getContentResolver(), android.provider.Settings.System.TIME_12_24);
        if ("24".equals(strTimeFormat)) {
            timeTxt.setText(getTime(0));
            twentyHourSystemRbtn.setChecked(true);
            timeSystemSummaryTxt.setText(R.string.label_24_hour_system);
        } else {
            long time = System.currentTimeMillis();
            final Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(time);
            int apm = mCalendar.get(Calendar.AM_PM);
            if (apm == 0) {
                timeTxt.setText(getTime(0));
            } else {
                timeTxt.setText(getTime(0));
            }
            /*apm=0 表示上午，apm=1表示下午。*/
            tenHourSystemRbtn.setChecked(true);
            timeSystemSummaryTxt.setText(R.string.label_12_hour_system);
        }
        nameList = mPresenter.getNameListData();
        timeZoneAdapter = new TimeZoneListAdapter(getContext(), nameList);
        timeZoneAdapter.setID(TimeZone.getDefault().getID());
        timeZoneList.setAdapter(timeZoneAdapter);
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        intentFilter.addAction(AUTO_TIME);
        timeChangeReceiver = new TimeChangeReceiver();
        getActivity().registerReceiver(timeChangeReceiver, intentFilter);
        timeZoneTxt.setText(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT) + " " + mPresenter.getTimeZone(TimeZone.getDefault().getID(), nameList));
        timeZoneList.setOnItemClickListener(this);

        twentyHourSystemRbtn.setOnClickListener(this);
        tenHourSystemRbtn.setOnClickListener(this);
        initHourSystem();
        //    getTime(xrp.getAttributeValue(0));

        //timeSystemSummaryTxt.setText());
    }

    public void initHourSystem() {
        int autoTime = 0;
        try {
            autoTime = Settings.Global.getInt(getActivity().getContentResolver(), AUTO_TIME);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (autoTime == 1) {
            adjustSwitch.setCheckedImmediately(true);
            adjustSummaryTxt.setText(R.string.label_adjust_open);
            timeTitleTxt.setTextColor(Color.parseColor("#484949"));
            dateTitleTxt.setTextColor(Color.parseColor("#484949"));
            dateTxt.setTextColor(Color.parseColor("#484949"));
            timeTxt.setTextColor(Color.parseColor("#484949"));
            timeArrowImg.setImageResource(R.mipmap.balance_right_s);
            dateArrowImg.setImageResource(R.mipmap.balance_right_s);
        } else {
            adjustSummaryTxt.setText(R.string.label_adjust_off);
            adjustSwitch.setCheckedImmediately(false);
            timeTitleTxt.setTextColor(Color.parseColor("#ffffff"));
            dateTitleTxt.setTextColor(Color.parseColor("#ffffff"));
            dateTxt.setTextColor(Color.parseColor("#ffffff"));
            timeTxt.setTextColor(Color.parseColor("#ffffff"));
            timeArrowImg.setImageResource(R.drawable.balance_btn_right_state);
            dateArrowImg.setImageResource(R.drawable.balance_btn_right_state);
        }
    }

    @Override
    public void onClick(View v) {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        switch (v.getId()) {
            case R.id.switch_adjust:

                setAutoTime(getContext(), adjustSwitch.isChecked());
                initHourSystem();
                break;
            case R.id.rbtn_24_hour_system:
                set24Hour(getContext(), "24");
                timeChanged.putExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", true);
                getActivity().sendBroadcast(timeChanged);
                updateTimeAndDateDisplay(getContext());
                timeSystemSummaryTxt.setText(R.string.label_24_hour_system);
                break;
            case R.id.rbtn_12_hour_system:
                set24Hour(getContext(), "12");
                timeChanged.putExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", true);
                getActivity().sendBroadcast(timeChanged);
                updateTimeAndDateDisplay(getContext());
                timeSystemSummaryTxt.setText(R.string.label_12_hour_system);
                //
                break;
            case R.id.fragment_time_system:
                if (systemRadio.getVisibility() == View.GONE) {
                    systemRadio.setVisibility(View.VISIBLE);
                    systemLineTxt.setVisibility(View.VISIBLE);


                    timeSystemArrowImg.setImageResource(R.drawable.balance_btn_bottom_state);

                } else {
                    systemRadio.setVisibility(View.GONE);
                    systemLineTxt.setVisibility(View.GONE);
                    timeSystemArrowImg.setImageResource(R.drawable.balance_btn_right_state);
                }
           /*     android.provider.Settings.System.get*/
                //loadStringSetting(stmt, Settings.System.TIME_12_24, R.string.time_12_24);

                break;
            case R.id.fragment_time:
              //  String strTimeFormat = android.provider.Settings.System.getString(getContext().getContentResolver(), android.provider.Settings.System.TIME_12_24);
                if (timeSystemSummaryTxt.getText().toString().equals("24小时制")) {
                    if (adjustSummaryTxt.getText().toString().equals("关闭")) {
                        timeDialog = new TimePickerViewDialog(getContext(), R.style.record_voice_dialog);
                        timeDialog.setTimePickerViewCallBack(this);
                        timeDialog.show();
                        setDialogParam(timeDialog, 552, 338);
                    }

                } else {
                    if (adjustSummaryTxt.getText().toString().equals("关闭")) {
                        time12Dialog = new Time12PickerViewDialog(getContext(), R.style.record_voice_dialog);
                        time12Dialog.setTimePickerViewCallBack(this);
                        time12Dialog.show();
                        setDialogParam(time12Dialog, 552, 338);

                    }
                }

                break;
            case R.id.fragment_date:
                if (adjustSummaryTxt.getText().toString().equals("关闭")) {
                    dateDialog = new DatePickerViewDialog(getContext());
                    dateDialog.setDatePickerViewDialogCallBack(this);
                    dateDialog.show();
                    setDialogParam(dateDialog, 552, 338);

                }

                break;
            case R.id.fragment_time_zone:
                if (rLayoutTimeInterface.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).mHadler = mHadler;

                    timeZoneList.setVisibility(View.VISIBLE);
                    rLayoutTimeInterface.setVisibility(View.GONE);

                    timeZoneList.post(new Runnable() {
                        @Override
                        public void run() {
                            int i=0;
                            for (TimeZoneEntity mTimeZoneEntity:nameList){
                                if (mTimeZoneEntity.getId().equals(timeZoneAdapter.getID())){
                                    break;
                                }
                                i++;
                            }
                            timeZoneList.smoothScrollToPosition(i);
                        }
                    });

                } else {
                    ((MainActivity) getActivity()).mHadler = null;
                    timeZoneList.setVisibility(View.GONE);
                    rLayoutTimeInterface.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void timePickerViewCallBack(String hour, String minute, String a) {
        android.text.format.Time t = new android.text.format.Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hours = Integer.parseInt(hour);
        int minutes = Integer.parseInt(minute);
        if (a.equals("下午")) {
            String s = year + "-" + month + "-" + date + " " + hours + ":" + minutes;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            java.util.Date curDate = null;
            try {
                curDate = formatter.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long ds = curDate.getTime() + (12L * 60 * 1000 * 60);
            if (curDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(ds);
                curDate = cal.getTime();
                SimpleDateFormat h = new SimpleDateFormat("HH");
                SimpleDateFormat m = new SimpleDateFormat("HH");
                hours = Integer.parseInt(h.format(curDate));
                minutes = Integer.parseInt(m.format(curDate));
            }

        }
        //setDateTime.setTimeToSystem(System.currentTimeMillis()+24*3600*1000);
        setDateTime(year, month, date, hours, minutes);
        timeTxt.setText(getTime(0));
        dateTxt.setText(getTime(1));
        time12Dialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setChinaTimeZone(getActivity(), nameList.get(position).getId());

    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    timeTxt.setText(getTime(0));
                    dateTxt.setText(getTime(1));
                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    timeTxt.setText(getTime(0));
                    dateTxt.setText(getTime(1));
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    timeZoneAdapter.setID(TimeZone.getDefault().getID());
                    timeZoneTxt.setText(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT) + " " + mPresenter.getTimeZone(TimeZone.getDefault().getID(), nameList));
                    timeZoneAdapter.notifyDataSetInvalidated();
                    timeTxt.setText(getTime(0));
                    dateTxt.setText(getTime(1));
                    //设置了系统时区的action
                    //   Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) {
            outState = new Bundle();
        }
        ((MainActivity) getActivity()).anInt = 1;

        //   outState.putString("DisplayFragment", "否");

    }

    public void updateTimeAndDateDisplay(Context context) {
        final Calendar now = Calendar.getInstance();
        mDummyDate.setTimeZone(now.getTimeZone());
        // We use December 31st because it's unambiguous when demonstrating the date format.
        // We use 13:00 so we can demonstrate the 12/24 hour options.
        mDummyDate.set(now.get(Calendar.YEAR), 11, 31, 13, 0, 0);
        java.util.Date dummyDate = mDummyDate.getTime();
/*        mDatePref.setSummary(DateFormat.getLongDateFormat(context).format(now.getTime()));
        mTimePref.setSummary(DateFormat.getTimeFormat(getActivity()).format(now.getTime()));
        mTimeZone.setSummary(ZoneGetter.getTimeZoneOffsetAndName(now.getTimeZone(), now.getTime()));
        mTime24Pref.setSummary(DateFormat.getTimeFormat(getActivity()).format(dummyDate));*/
    }

    @Override
    public void timePickerViewCallBack(String hour, String minute) {
        android.text.format.Time t = new android.text.format.Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        //setDateTime.setTimeToSystem(System.currentTimeMillis()+24*3600*1000);
        setDateTime(year, month, date, Integer.parseInt(hour), Integer.parseInt(minute));
        timeTxt.setText(getTime(0));
        dateTxt.setText(getTime(1));
        timeDialog.dismiss();
    }

    @Override
    public void datePickerViewDialogCallBack(String year, String month, String day) {
        android.text.format.Time t = new android.text.format.Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int hour = t.hour;
        int min = t.minute;
        //setDateTime.setTimeToSystem(System.currentTimeMillis()+24*3600*1000);
        setDateTime(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), hour, min);

        dateDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).mHadler = null;
        timeZoneList.setVisibility(View.GONE);
        rLayoutTimeInterface.setVisibility(View.VISIBLE);
        // getActivity().unregisterReceiver(timeChangeReceiver);

    }

    public void setDateTime(int year, int month, int day, int hour, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }

    }

    @Override
    public int initResid() {
        return 0;
    }

    @Override
    public IPresenter initPresenter() {
        return new TimePresenter(this);
    }


    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void launchActivity(@NonNull Intent intent) {

    }

    @Override
    public void killMyself() {

    }

    public String getTime(int i) {
        SimpleDateFormat formatter = null;
        switch (i) {
            case 0:
                String strTimeFormat = android.provider.Settings.System.getString(getContext().getContentResolver(), android.provider.Settings.System.TIME_12_24);
                if ("24".equals(strTimeFormat)) {
                    formatter = new SimpleDateFormat("HH:mm");
                } else {
                    long time = System.currentTimeMillis();
                    final Calendar mCalendar = Calendar.getInstance();
                    mCalendar.setTimeInMillis(time);
                    int apm = mCalendar.get(Calendar.AM_PM);
                    if (apm == 0) {
                        formatter = new SimpleDateFormat("hh:mm");
                        Date curDate = new Date(System.currentTimeMillis());
                        return "上午 " + formatter.format(curDate);
                    } else {
                        formatter = new SimpleDateFormat("hh:mm");
                        Date curDate = new Date(System.currentTimeMillis());
                        return "下午 " + formatter.format(curDate);
                    }
                }


                break;
            case 1:
                formatter = new SimpleDateFormat("yyyy年MM月dd日");
                break;
        }
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }


}
