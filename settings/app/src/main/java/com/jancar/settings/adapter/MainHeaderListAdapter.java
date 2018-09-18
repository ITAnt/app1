package com.jancar.settings.adapter;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.settings.R;

import java.util.List;

/**
 * Created by ouyan on 2018/9/5.
 */

public class MainHeaderListAdapter extends BaseAdapter {
    private Context mainActivity;
    private List<PreferenceActivity.Header> mCopyHeaders;
    private int position;

    public MainHeaderListAdapter(Context mainActivity, List<PreferenceActivity.Header> mCopyHeaders) {
        this.mainActivity = mainActivity;
        this.mCopyHeaders = mCopyHeaders;
    }

    @Override
    public int getCount() {
        return mCopyHeaders.size();
    }

    @Override
    public Object getItem(int i) {
        return mCopyHeaders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mainActivity).inflate(R.layout.main_header_item, viewGroup, false);
            holder = new HeaderViewHolder();
            holder.titleTxt = (TextView) view.findViewById(R.id.txt_title);
            holder.iconImg = (ImageView) view.findViewById(R.id.img_icon);
            holder.rlayoutMain = (RelativeLayout) view.findViewById(R.id.rlayout_main);

            view.setTag(holder);

        } else {

            view = convertView;

            holder = (HeaderViewHolder) view.getTag();

        }
        PreferenceActivity.Header header = (PreferenceActivity.Header) getItem(position);
        if (header.iconRes == 0) {

            holder.iconImg.setVisibility(View.GONE);

        } else {

            holder.iconImg.setVisibility(View.VISIBLE);

            holder.iconImg.setImageResource(header.iconRes);

        }
        if (this.position == position) {
            holder.rlayoutMain.setBackgroundResource(R.color.bg_main_rlayout_h);
        } else {
            holder.rlayoutMain.setBackgroundResource(R.color.bg_main_rlayout_n);
        }
        holder.titleTxt.setText(header.getTitle(mainActivity.getResources()));

        //   CharSequence summary = header.getSummary(mainActivity.getResources());


        return view;


    }

    public void setPosition(int position) {
        this.position = position;
    }

    class HeaderViewHolder {
        RelativeLayout rlayoutMain;

        TextView titleTxt;

        ImageView iconImg;
    }

}
