package com.jancar.bluetooth.phone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
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
public class RecordsAdapter extends BaseAdapter {
    private int defaultSelection = -1;
    private Context mContext;
    private List<BluetoothPhoneBookData> list;
    public static final String INCOMG = "incoming";
    public static final String OUTGOING = "outgoing";
    public static final String MISSED = "missed";

    public RecordsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setBTPhoneBooks(List<BluetoothPhoneBookData> btPhoneBooks) {
        this.list = new ArrayList<>(btPhoneBooks);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final BluetoothPhoneBookData bookData = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_records_list, parent, false);
            viewHolder.ivcallType = convertView.findViewById(R.id.iv_records_call_type);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.iv_records_call_name);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.iv_records_call_number);
            viewHolder.ivCall = convertView.findViewById(R.id.iv_records_call);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvPhone.setText(bookData.getPhoneNumber());
        String phoneName = bookData.getPhoneName();
        if (TextUtils.isEmpty(phoneName)) {
            viewHolder.tvName.setText(R.string.str_phone_unkonow);
        } else {
            viewHolder.tvName.setText(phoneName);
        }
        String phoneBookCallType = bookData.getPhoneBookCallType();
        if (!TextUtils.isEmpty(phoneBookCallType)) {
            switch (phoneBookCallType) {
                case INCOMG:
                    viewHolder.ivcallType.setImageResource(R.drawable.iv_records_call_in);
                    break;
                case OUTGOING:
                    viewHolder.ivcallType.setImageResource(R.drawable.iv_records_call_out);

                    break;
                case MISSED:
                    viewHolder.ivcallType.setImageResource(R.drawable.iv_records_call_missed);
                    break;
            }
        }

        viewHolder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = bookData.getPhoneNumber();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    BluetoothManager.getBluetoothManagerInstance(mContext).hfpCall(bookData.getPhoneNumber());
                }
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
        ImageView ivCall;
        ImageView ivcallType;
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
