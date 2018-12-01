package com.jancar.settings.contract;


public class KeyValue {
    private String name;//按键名
    private byte keyValue;//键值
    private byte keyLearningStatus;//按键学习状态

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    private int background;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(byte keyValue) {
        this.keyValue = keyValue;
    }

    public byte getKeyLearningStatus() {
        return keyLearningStatus;
    }

    public void setKeyLearningStatus(byte keyLearningStatus) {
        this.keyLearningStatus = keyLearningStatus;
    }

    @Override
    public String toString() {
        return "KeyValue{" +
                "name='" + name + '\'' +
                ", keyValue=" + keyValue +
                ", keyLearningStatus=" + keyLearningStatus +
                '}';
    }
}
