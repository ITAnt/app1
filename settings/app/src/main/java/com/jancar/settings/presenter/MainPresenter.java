package com.jancar.settings.presenter;import com.jancar.settings.listener.Contract.MainContractImpl;import com.jancar.settings.manager.BasePresenter;import com.jancar.settings.model.MainModel;/** * Created by ouyan on 2018/8/30. */public class MainPresenter extends BasePresenter<MainContractImpl. Model, MainContractImpl. View> {    MainContractImpl. Model model=new MainModel();    public MainPresenter(MainContractImpl. View rootView) {        super(rootView);        initModel(model);    }}