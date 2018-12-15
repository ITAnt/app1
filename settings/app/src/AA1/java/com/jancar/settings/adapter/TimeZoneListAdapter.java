package com.jancar.settings.adapter;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.contract.TimeZoneEntity;
import com.jancar.settings.view.activity.TimeZoneActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ouyan on 2018/9/5.
 */

public class TimeZoneListAdapter extends BaseAdapter {
    private Holder holder;
    private Context mContext;
    private List<TimeZoneEntity>nameList;
    private String ID;
    private int anInt;
    public TimeZoneListAdapter() {
    }
    public TimeZoneListAdapter(Context mContext, List<TimeZoneEntity>nameList) {
        this.mContext=mContext;
        this.nameList=nameList;
    }
    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int arg0) {

        return nameList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup arg2) {

        holder = new Holder();
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_time_list, null);
            holder.nameTitleTxt = (TextView) view.findViewById(R.id.txt_name_title);
            holder.summaryTxt = (TextView) view.findViewById(R.id.txt_summary);
            holder.timeListItemRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_time_list_item);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (nameList.get(pos).getId().equals(ID)){
            anInt=pos;
            holder.timeListItemRlayout.setBackgroundResource(R.color.bg_main_rlayout_h);
        } else {
            holder.timeListItemRlayout.setBackgroundResource(R.color.lucency);
        }
        holder.nameTitleTxt.setText(nameList.get(pos).getName());
        holder.summaryTxt.setText(nameList.get(pos).getTime());
        return view;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getAnInt() {
        return anInt;
    }

    class Holder {
        public TextView nameTitleTxt;
        public TextView summaryTxt;
        public RelativeLayout timeListItemRlayout;
    }

}
