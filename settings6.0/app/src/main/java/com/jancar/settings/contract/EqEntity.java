package com.jancar.settings.contract;

/**
 * Created by ouyan on 2018/9/14.
 */

public class EqEntity {
    private String Name;
    private int ValueTTxt;
    private int ValueMTxt;
    private int ValueBTxt;
    public EqEntity(){}
    public EqEntity(String Name,
                    int ValueTTxt,
                    int ValueMTxt,
                    int ValueBTxt) {
        this.Name = Name;
        this.ValueTTxt=ValueTTxt;
        this.ValueMTxt=ValueMTxt;
        this.ValueBTxt=ValueBTxt;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getValueTTxt() {
        return ValueTTxt;
    }

    public void setValueTTxt(int valueTTxt) {
        ValueTTxt = valueTTxt;
    }

    public int getValueMTxt() {
        return ValueMTxt;
    }

    public void setValueMTxt(int valueMTxt) {
        ValueMTxt = valueMTxt;
    }

    public int getValueBTxt() {
        return ValueBTxt;
    }

    public void setValueBTxt(int valueBTxt) {
        ValueBTxt = valueBTxt;
    }
}
