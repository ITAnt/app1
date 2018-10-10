package com.jancar.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jancar.bluetooth.lib.BluetoothDeviceData;
import com.jancar.bluetooth.lib.BluetoothManager;

import com.jancar.bluetooth.lib.BluetoothSettingManager;
import com.jancar.settings.R;
import com.jancar.settings.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/9/3 17:13
 * @describe 蓝牙设置界面设备搜索列表适配器
 */
public class BluetoothAdapter extends BaseAdapter {
    private boolean isShow;
    private Context mContext;
    private List<BluetoothDeviceData> list;


    public BluetoothAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setBookContact(List<BluetoothDeviceData> list) {
        this.list = new ArrayList<>(list);
        notifyDataSetChanged();

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final BluetoothDeviceData deviceData = list.get(position);
        int status = deviceData.getRemote_connect_status();
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_equip_list, parent, false);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.tv_setting_equit_name);
            viewHolder.ivDel = convertView.findViewById(R.id.iv_setting_del);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(deviceData.getRemote_device_name());
        if (isShow) {
            viewHolder.ivDel.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivDel.setVisibility(View.GONE);
        }
        if (status == Constants.BLUETOOTH_DEVICE_STATE_CONNECT) {
            viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.tipColor));
        }
        viewHolder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothSettingManager.getBluetoothSettingManager(mContext).removeDevice(deviceData.getRemote_device_macaddr());
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void changetShowDelImage(boolean isShow) {
        this.isShow = isShow;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tvName;
        ImageView ivDel;

    }


}
