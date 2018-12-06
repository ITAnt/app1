package com.jancar.radio.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jancar.radio.R;
import com.jancar.radio.entity.RadioStation;

import java.util.List;

public class FreqAdapter extends BaseAdapter {
    private List<RadioStation> FreqList;
    private Context mContext;
    Holder holder;
    public FreqAdapter(Context mContext, List<RadioStation> FreqList) {
        this.mContext = mContext;
        this.FreqList = FreqList;
    }

    @Override
    public int getCount() {
        return FreqList.size();
    }

    @Override
    public Object getItem(int position) {
        return FreqList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_freq_list, null);
            holder = new Holder();
            holder.tv_app_name = view.findViewById(R.id.tv_app_name);
            holder.tv_freq = view.findViewById(R.id.tv_freq);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
       /* if (Pty==position){
            holder.mLinearLayout.setBackgroundResource(R.drawable.btn_bg_d);
        }else {
            holder.mLinearLayout.setBackgroundResource(R.drawable.btn_bg_f);

        }*/
       int number=position+1;
        RadioStation radioStation= (RadioStation) getItem(position);
        if (radioStation.getSelect()){
            holder.tv_freq.setTextColor(Color.parseColor("#1336a3"));
        }else {
            holder.tv_freq.setTextColor(Color.parseColor("#ffffffff"));

        }
        holder.tv_app_name.setText(number+".");
        holder.tv_freq.setText(radioStation.getRdsname());
        return view;
    }

    class Holder {
        TextView tv_app_name;
        TextView tv_freq;

    }

}
