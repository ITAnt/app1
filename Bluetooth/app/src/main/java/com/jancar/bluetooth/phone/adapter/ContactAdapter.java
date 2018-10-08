package com.jancar.bluetooth.phone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothPhoneBookData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.util.ViewHolderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/8/30 10:12
 * @describe 联系人列表适配器
 */
public class ContactAdapter extends BaseViewAdapter<BluetoothPhoneBookData> {
    private Context mContext;
    private int defaultSelection = -1;
    private List<BluetoothPhoneBookData> listDatas;

    @Override
    public void setPhoneBooks(List<BluetoothPhoneBookData> listDatas) {
        super.setPhoneBooks(listDatas);
        notifyDataSetChanged();
    }

    public ContactAdapter(Context context, List<BluetoothPhoneBookData> listDatas, int layoutID) {
        super(context, listDatas, layoutID);
        this.mContext = context;
        this.listDatas = new ArrayList<>(listDatas);
    }

    @Override
    public void getView(final int position, final View v, ViewHolderUtils.ViewHolder viewHolder, final BluetoothPhoneBookData obj) {
        TextView tvName = viewHolder.get(R.id.tv_contact_name);
        TextView tvNumber = viewHolder.get(R.id.tv_contact_number);
        View view = viewHolder.get(R.id.content_framelayout);
        tvName.setText(obj.getPhoneName());
        tvNumber.setText(obj.getPhoneNumber());
        if (position == defaultSelection) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.bgSelected));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        viewHolder.get(R.id.iv_contact_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothManager.getBluetoothManagerInstance(mContext).hfpCall(obj.getPhoneNumber());

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (defaultSelection == position) {
                    BluetoothManager.getBluetoothManagerInstance(mContext).hfpCall(obj.getPhoneNumber());
                } else {
                    defaultSelection = position;
                    notifyDataSetChanged();
                }
            }
        });

    }

    /**
     * @param position 设置高亮状态的item
     */
    public void setSelectPosition(int position) {
        if (!(position < 0 || position > listDatas.size())) {
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

    @Override
    public String getSortFileld(BluetoothPhoneBookData obj, int position) {
        return obj.getPhoneName();
    }

}
