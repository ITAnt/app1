package com.jancar.settings.model;import com.jancar.settings.listener.Contract.DisplayContractImpl;import com.jancar.settings.listener.Contract.MainContractImpl;import java.util.ArrayList;import java.util.List;/** * Created by ouyan on 2018/8/30. */public class DisplayModel  implements DisplayContractImpl.Model {    @Override    public void onDestroy() {    }    @Override    public List<String> getList() {        List<String> stringList=new ArrayList<>();        stringList.add("中文（简体）");        stringList.add("中文（繁体）");        stringList.add("English");        stringList.add("русский");        stringList.add("Ελληνικά");        stringList.add("Polski");        stringList.add("Türk");        stringList.add("العربية");        stringList.add("فارسی");        stringList.add("România");        stringList.add("français");        stringList.add("magyar");        stringList.add("Italia");        stringList.add("ไทย");        stringList.add("Deutsch");        stringList.add("Українська");        stringList.add("España");        stringList.add("Portugal");        return stringList;    }}