package com.jancar.bluetooth.phone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/8/31 11:50
 * @describe 联系人搜索列表
 */
public class ContactSearchAdapter extends BaseAdapter {
    private int defaultSelection = -1;
    private Context mContext;
    private List<BluetoothPhoneBookData> list = new ArrayList<>();

    public void setBookContact(List<BluetoothPhoneBookData> list) {
        this.list = list;

    }

    public ContactSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final BluetoothPhoneBookData bookData = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_contact_search_list, parent, false);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.tv_contact_search_name);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tv_contact_search_number);
            viewHolder.imageView = convertView.findViewById(R.id.iv_contact_search_call);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvPhone.setText(bookData.getPhoneNumber());
        viewHolder.tvName.setText(bookData.getPhoneName());
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothManager.getBluetoothManagerInstance(mContext).hfpCall(bookData.getPhoneNumber());
            }
        });

        if (position == defaultSelection) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.bgSelected));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvPhone;
        TextView tvName;
        ImageView imageView;
    }

    /**
     * @param position 设置高亮状态的item
     */
    public void setSelectPosition(int position) {
        if (!(position < 0 || position > list.size())) {
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }

    /**
     * @param
     */
    public void setNormalPosition() {

        defaultSelection = -1;
        notifyDataSetChanged();

    }
}
