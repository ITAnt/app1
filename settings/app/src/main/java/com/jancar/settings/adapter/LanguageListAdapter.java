package com.jancar.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.contract.TimeZoneEntity;

import java.util.List;

/**
 * Created by ouyan on 2018/9/5.
 */

public class LanguageListAdapter extends BaseAdapter {
    private Holder holder;
    private Context mContext;
    private List<String>nameList;
    private int ID;
    public LanguageListAdapter() {
    }
    public LanguageListAdapter(Context mContext, List<String>nameList) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_language_list, null);
            holder.nameTitleTxt = (TextView) view.findViewById(R.id.txt_name_title);
            holder.timeListItemRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_time_list_item);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (ID==pos){
            holder.timeListItemRlayout.setBackgroundResource(R.color.bg_main_rlayout_h);
        } else {
            holder.timeListItemRlayout.setBackgroundResource(R.color.colorPrimary);
        }
        holder.nameTitleTxt.setText(nameList.get(pos));
        return view;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    class Holder {
        public TextView nameTitleTxt;
        public RelativeLayout timeListItemRlayout;
    }

}
