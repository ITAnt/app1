package com.jancar.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.settings.R;
import com.jancar.settings.contract.NavigationEntity;
import com.jancar.settings.contract.TimeZoneEntity;

import java.util.List;

/**
 * Created by ouyan on 2018/9/5.
 */

public class NavigationListAdapter extends BaseAdapter {
    private Holder holder;
    private Context mContext;
    private List<NavigationEntity>nameList;
    private String ID;
    public NavigationListAdapter() {
    }
    public NavigationListAdapter(Context mContext, List<NavigationEntity>nameList) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_navigation_list, null);
            holder.nameTitleTxt = (TextView) view.findViewById(R.id.txt_name_title);
            holder.img_icon = (ImageView) view.findViewById(R.id.img_icon);
            holder.timeListItemRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_time_list_item);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (nameList.get(pos).isUp()){
            holder.timeListItemRlayout.setBackgroundResource(R.color.bg_main_rlayout_h);
        } else {
            holder.timeListItemRlayout.setBackgroundResource(R.color.lucency);
        }
        holder.nameTitleTxt.setText(nameList.get(pos).getName());
        holder.img_icon.setBackground(nameList.get(pos).getImg());
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
        public ImageView img_icon;
        public RelativeLayout timeListItemRlayout;
    }

}
