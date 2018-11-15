package com.jancar.settings.presenter;

import com.jancar.settings.contract.NavigationEntity;
import com.jancar.settings.listener.Contract.NavigationContractImpl;
import com.jancar.settings.listener.Contract.SoundContractImpl;
import com.jancar.settings.manager.BasePresenter;
import com.jancar.settings.model.NavigationModel;
import com.jancar.settings.model.SoundModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ouyan on 2018/9/10.
 */

public class NavigationPresenter extends BasePresenter<NavigationContractImpl.Model, NavigationContractImpl.View> {
    NavigationContractImpl.Model model = new NavigationModel();

    public NavigationPresenter(NavigationContractImpl.View rootView) {
        super(rootView);
        initModel(model);
    }

    public List<NavigationEntity> getListData() {
        return model.getListData();
    }

    public List<String> readXml(String fileName) {
        File f = new File(fileName);
        List<String>   keyWords =new ArrayList<>();
        //创建一个DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //创建一个DocumentBuilder的对象
        try {
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
            Document document = db.parse(f);
            //获取所有book节点的集合
            NodeList GISName = document.getElementsByTagName("GISName");
            //通过nodelist的getLength()方法可以获取bookList的长度
            //遍历每一个book节点
            for (int i = 0; i < GISName.getLength(); i++) {
                Node mNode = GISName.item(i);
                String attrs = mNode.getNodeName();
                Node s=  mNode.getFirstChild();
                String attsrs = s.getNodeValue();
                keyWords.add(attsrs);
                System.out.println("=================" + attrs + "=================");
                System.out.println("=================" +attsrs+ "=================");
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return keyWords;
    }
}