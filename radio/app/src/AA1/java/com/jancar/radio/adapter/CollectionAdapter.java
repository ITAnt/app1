package com.jancar.radio.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jancar.radio.R;
import com.jancar.radio.entity.Collection;
import com.jancar.radio.entity.RadioStation;

import java.util.List;

public class CollectionAdapter  extends BaseAdapter {
    private List<Collection> FreqList;
    private Context mContext;
    Holder holder;
    public CollectionAdapter(Context mContext, List<Collection> FreqList) {
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

    public void Refresh(List<Collection> FreqList){
        this.FreqList = FreqList;
        this.notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_freq_list, null);
            holder = new Holder();
            holder.tv_app_name = view.findViewById(R.id.tv_app_name);
            holder.tv_freq = view.findViewById(R.id.tv_freq);
            holder.tv_freqs = view.findViewById(R.id.tv_freqs);

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
        Collection radioStation= FreqList.get(position);
        if (radioStation.getSelect()){
            holder.tv_freq.setTextColor(Color.parseColor("#1336a3"));
            holder.tv_freqs.setTextColor(Color.parseColor("#1336a3"));
        }else {
            holder.tv_freq.setTextColor(Color.parseColor("#ffffffff"));
            holder.tv_freqs.setTextColor(Color.parseColor("#ffffffff"));
        }
        holder.tv_freqs.setVisibility(View.VISIBLE);
        holder.tv_freqs.setText(getFmAm(radioStation.getFrequency()));
        holder.tv_app_name.setText(number+".");
        holder.tv_freq.setText(radioStation.getRdsname());
        return view;
    }

    public String getFmAm(int Frequency){
        String FmAm="";
        switch (Frequency){
            case 0:
                FmAm="FM1";
                break;
            case 1:
                FmAm="FM2";
                break;
            case 2:
                FmAm="FM3";
                break;
            case 3:
                FmAm="AM1";
                break;
            case 4:
                FmAm="AM2";
                break;
        }
        return FmAm;
    }
    class Holder {
        TextView tv_app_name;
        TextView tv_freq;
        TextView tv_freqs;

    }
}
