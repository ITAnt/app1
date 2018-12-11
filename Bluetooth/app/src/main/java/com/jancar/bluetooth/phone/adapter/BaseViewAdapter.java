package com.jancar.bluetooth.phone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.entity.SortModel;
import com.jancar.bluetooth.phone.listener.ILettersSection;
import com.jancar.bluetooth.phone.util.PinyinUtil;
import com.jancar.bluetooth.phone.util.ViewHolderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 侧边索引页绑定lsitview 的基础adapter类
 *
 * @param <T> 数据类型
 * @author cy
 */
public abstract class BaseViewAdapter<T> extends BaseAdapter implements ILettersSection {

    public Context mCtx;
    public LayoutInflater mInflater;
    public List<SortModel<T>> mListDatas;
    private ViewHolderUtils mViewHolderUtils;
    private int mLayoutID;
    private OnClickListener onClick;
    private boolean isVisible = true;

    public BaseViewAdapter(Context context, List<T> listDatas, int layoutID) {
        this.mCtx = context;
        // 转换后排好序的数据源
        this.mListDatas = fillAndSortData(mCtx, listDatas);
        // 实例化视图工具操作类
        this.mViewHolderUtils = new ViewHolderUtils();
        this.mInflater = LayoutInflater.from(this.mCtx);
        // layout id
        this.mLayoutID = layoutID;
    }

    public void setPhoneBooks(List<T> listDatas) {
        this.mListDatas = fillAndSortData(mCtx, listDatas);
    }


    @Override
    public int getCount() {
        if (mListDatas == null) {
            return 0;
        }
        return mListDatas.size();
    }

    @Override
    public Object getItem(int position) {
        if (mListDatas == null) {
            return null;
        }
        return mListDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sidebar_head_item, parent, false);
            LinearLayout mListitemheader = convertView.findViewById(R.id.mListitemheader);

            mListitemheader.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onClick != null) {
                        onClick.onClick(v);
                    }
                }
            });
            // 实例化内容view
            View contentView = mInflater.inflate(mLayoutID, null);
            // 将内容view 嵌入父view中
            ((FrameLayout) mViewHolderUtils.get(convertView).get(R.id.content_framelayout)).addView(contentView);
        }

        SortModel<T> sortModel = mListDatas.get(position);

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);


        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            // 显示title bar
//			mViewHolderUtils.get(convertView).get(R.id.catalog).setVisibility(View.VISIBLE);
            if (!isVisible) {
                mViewHolderUtils.get(convertView).get(R.id.catalog).setVisibility(View.GONE);
            } else {
                mViewHolderUtils.get(convertView).get(R.id.catalog).setVisibility(View.VISIBLE);
            }
            // 设置bar 显示的字符内容
            ((TextView) mViewHolderUtils.get(convertView).get(R.id.catalog)).setText(sortModel.getSortLetters());
        } else {
            // 隐藏title bar
            mViewHolderUtils.get(convertView).get(R.id.catalog).setVisibility(View.GONE);
        }
        // 内容layout 填充,由继承类实现
        getView(position, convertView, mViewHolderUtils.get(convertView), sortModel.getObject());

        return convertView;
    }

    /**
     * 转拼音，填充 封装成sortmodel 格式数据 排序
     */
    private List<SortModel<T>> fillAndSortData(Context context, List<T> listDatas) {
        //实例化数组集合
        List<SortModel<T>> lists = new ArrayList<SortModel<T>>();
        // 循环转换拼音，添加sortmodel
        for (int i = 0; i < listDatas.size(); i++) {
            //获取要根据的排序字符串
            String sortFileld = getSortFileld(listDatas.get(i), i);
            // 转换拼音，添加到集合中
            if (!TextUtils.isEmpty(sortFileld)) {
                lists.add(new SortModel<T>(listDatas.get(i), PinyinUtil.getFirstLetter(sortFileld)));
            } else {
                lists.add(new SortModel<T>(listDatas.get(i), PinyinUtil.getFirstLetter("#")));
            }
        }
        /**
         *  排序
         */
        Collections.sort(lists);
        return lists;
    }

    public void setIsVisible(boolean b) {
        isVisible = b;
//		this.notifyDataSetChanged();
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mListDatas.get(position).getSortLetters().charAt(0);
    }


    @Override
    public int getPositionForSection(int section) {
        // 循环获取数据判断位置
        for (int i = 0; i < getCount(); i++) {
            // 取出字母
            String sortStr = mListDatas.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            // 判断字母是否相等，是则返回位置
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 更新数据
     *
     * @param listDatas
     */
    public void notifyDataSetChanged(List<T> listDatas) {
        // 转换后排好序的数据源
        this.mListDatas = fillAndSortData(mCtx, listDatas);
        // 通知更新数据
        notifyDataSetChanged();
    }

    /**
     * 内容填充view
     *
     * @param position
     * @param v
     * @param obj
     */
    public abstract void getView(int position, View v, ViewHolderUtils.ViewHolder viewHolder, T obj);

    /**
     * 排序字段
     *
     * @param position 数组下标
     * @return
     */
    public abstract String getSortFileld(T obj, int position);

}
