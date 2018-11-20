package com.jancar.radio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.radio.R;

import java.util.List;

public class RtyAdapter extends BaseAdapter {
    private List<String> ptyList;
    private Context mContext;
    Holder holder;
    private  int Pty;
    public RtyAdapter(Context mContext, List<String> ptyList, int Pty) {
        this.mContext = mContext;
        this.ptyList = ptyList;
        this.Pty = Pty;
    }

    public void setPty(int pty) {
        Pty = pty;
    }

    public int getPty() {
        return Pty;
    }

    @Override
    public int getCount() {
        return ptyList.size();
    }

    @Override
    public Object getItem(int position) {
        return ptyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_language_list, null);
            holder = new Holder();
            holder.tv_app_name = view.findViewById(R.id.tv_app_name);
            holder.mLinearLayout = view.findViewById(R.id.mLinearLayout);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (Pty==position){
            holder.mLinearLayout.setBackgroundResource(R.drawable.btn_bg_d);
        }else {
            holder.mLinearLayout.setBackgroundResource(R.drawable.btn_bg_f);

        }
        holder.tv_app_name.setText(getItem(position).toString());
        return view;
    }

    class Holder {
        TextView tv_app_name;
        LinearLayout mLinearLayout;
    }

}
