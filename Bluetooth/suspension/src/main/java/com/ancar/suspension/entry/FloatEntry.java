package com.ancar.suspension.entry;

/**
 * @anthor Tzq
 * @time 2018/9/29 18:03
 * @describe 悬浮按钮实体类
 */
public class FloatEntry {
    private int icon;
    private String title;

    public FloatEntry(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "FloatEntry{" +
                "icon=" + icon +
                ", title='" + title + '\'' +
                '}';
    }
}
