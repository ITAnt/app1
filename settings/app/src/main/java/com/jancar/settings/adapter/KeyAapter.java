package com.jancar.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.jancar.model.WheelKey;
import com.jancar.settings.R;
import com.jancar.settings.contract.KeyValue;

import java.util.ArrayList;

/**
 * @className: KeyAapter
 * @describe_: 测试选项列表
 * @author___: ygl
 * @date_____: 2018/5/25 17:10
 * @version__: v1.0
 */

public class KeyAapter extends BaseAdapter {
    private ArrayList<KeyValue> keyValues;
    private Context mContext;

    public void setEnable(boolean enable) {
        isEnable = enable;
        notifyDataSetChanged();
    }

    private boolean isEnable = true;

    public KeyAapter(ArrayList<KeyValue> testValues, Context mContext) {
        this.keyValues = testValues;
        this.mContext = mContext;
    }

    public void setKeyValues(ArrayList<KeyValue> keyValues) {
        this.keyValues = keyValues;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return keyValues.size();
    }

    @Override
    public Object getItem(int i) {
        return keyValues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        KeyValue keyValue = keyValues.get(i);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_wheel_key_name, null);
            viewHolder.keyImage = view.findViewById(R.id.keyImage);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        byte keyLearnStatus = keyValue.getKeyLearningStatus();
        viewHolder.keyImage.setImageResource(keyValue.getBackground());
        //viewHolder.keyImage.setClickable(true);
        if(keyLearnStatus== WheelKey.CMD_UNLEARNED_SPONSE){
            viewHolder.keyImage.setImageLevel(0);
            // viewHolder.keyImage.setClickable(true);
        }else if(keyLearnStatus==WheelKey.CMD_LEARNED_SPONSE){
            viewHolder.keyImage.setImageLevel(1);
            // viewHolder.keyImage.setClickable(false);
        }else{
            viewHolder.keyImage.setImageLevel(2);
        }
        return view;
    }

    class ViewHolder {
        ImageView keyImage;
    }
}
