package com.jancar.bluetooth.phone.listener;

public interface ILettersSection {
	
	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
    int getPositionForSection(int section) ;
}
