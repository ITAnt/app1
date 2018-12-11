package com.ancar.suspension.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ancar.suspension.R;
import com.ancar.suspension.entry.FloatEntry;

import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/9/29 18:26
 * @describe TODO
 */
public class FloatAdapter extends BaseAdapter {
    private Context context;
    private List<FloatEntry> floatEntryList;
    private int defaultSelection = -1;

    public FloatAdapter(Context context, List<FloatEntry> data) {
        this.context = context;
        this.floatEntryList = data;

    }

    @Override
    public int getCount() {
        return floatEntryList == null ? 0 : floatEntryList.size();
    }

    @Override
    public Object getItem(int position) {
        return floatEntryList == null ? null : floatEntryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FloatEntry floatEntry = floatEntryList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_floating_list, parent, false);
            viewHolder.imageView = convertView.findViewById(R.id.iv_list_icon);
            viewHolder.textView = convertView.findViewById(R.id.tv_list_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(floatEntry.getIcon());
        viewHolder.textView.setText(floatEntry.getTitle());
        if (position == defaultSelection) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.bgSelected));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        private ImageView imageView;
        private TextView textView;
    }

    public void setSelectPostion(int position) {
        if (!(position < 0 || position > floatEntryList.size())) {
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }
}
