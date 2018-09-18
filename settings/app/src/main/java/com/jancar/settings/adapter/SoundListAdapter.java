package com.jancar.settings.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.contract.EqEntity;
import com.jancar.settings.contract.TimeZoneEntity;

import java.util.List;

/**
 * Created by ouyan on 2018/9/5.
 */

public class SoundListAdapter extends BaseAdapter {
    private Holder holder;
    private Context mContext;
    private List<EqEntity>nameList;
    private String ID;
    public SoundListAdapter() {
    }
    public SoundListAdapter(Context mContext, List<EqEntity>nameList) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_sound_list, null);
            holder.nameTitleTxt = (TextView) view.findViewById(R.id.txt_title);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (nameList.get(pos).getName().equals(ID)){
            holder.nameTitleTxt.setTextColor(Color.parseColor("#0068B7"));
        } else {
            holder.nameTitleTxt.setTextColor(Color.parseColor("#ffffff"));
        }
        holder.nameTitleTxt.setText(nameList.get(pos).getName());

        return view;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    class Holder {
        public TextView nameTitleTxt;
    }

}
