package com.jancar.bluetooth.phone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.util.CheckPhoneUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/8/30 17:51
 * @describe 拨号键盘界面适配器
 */
public class DialNumberAdapter extends BaseAdapter {
    private Context context;
    private List<BluetoothPhoneBookData> bookDataList = new ArrayList<>();
    private int defaultSelection = -1;
    private int bg_selected_color;//选中item的背景

    public void setBookDataList(List<BluetoothPhoneBookData> bookDataList) {
        this.bookDataList = bookDataList;
        notifyDataSetChanged();

    }


    public DialNumberAdapter(Context context) {
        this.context = context;
        bg_selected_color = context.getResources().getColor(R.color.bgSelected);

    }

    @Override
    public int getCount() {
        return bookDataList == null ? 0 : bookDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return bookDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        BluetoothPhoneBookData bookData = bookDataList.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_dial_number_list, viewGroup, false);
            viewHolder.tvNumber = view.findViewById(R.id.item_dial_number_content);
            viewHolder.tvOperator = view.findViewById(R.id.item_dial_number_type);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String phoneNumber = bookData.getPhoneNumber();
        viewHolder.tvNumber.setText(phoneNumber);
        viewHolder.tvOperator.setText(CheckPhoneUtil.getNumberOperator(phoneNumber));
        if (position == defaultSelection) {
            view.setBackgroundColor(bg_selected_color);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }

    static class ViewHolder {
        TextView tvOperator;
        TextView tvNumber;
    }

    /**
     * @param position 设置高亮状态的item
     */
    public void setSelectPosition(int position) {
        if (!(position < 0 || position > bookDataList.size())) {
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }

    public void setNormalPostion() {
        defaultSelection = -1;
        notifyDataSetChanged();
    }
}
