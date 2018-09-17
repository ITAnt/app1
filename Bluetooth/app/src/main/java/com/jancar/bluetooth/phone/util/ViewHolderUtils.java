package com.jancar.bluetooth.phone.util;

import android.util.SparseArray;
import android.view.View;

/**
 * @anthor Tzq
 * @time 2018/8/29 19:59
 * @describe 适配器ViewHolder工具类
 */

public class ViewHolderUtils {

    /**
     * 得到视图为 view 的 viewHodler
     *
     * @param view
     * @return
     */
    public ViewHolder get(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        return viewHolder;
    }

    /**
     * viewhodler 存储 view的子 view 的索引
     */
    public class ViewHolder {
        private SparseArray<View> viewHolder;
        private View view;

        public ViewHolder(View view) {
            this.view = view;
            viewHolder = new SparseArray<View>();
        }

        /**
         * 获取id 的控件
         *
         * @param id
         * @return
         */
        public <T extends View> T get(int id) {
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
