package com.jancar.bluetooth.phone.entity;

public class SortModel<T> implements Comparable<SortModel<T>> {

	private T object;

	private String sortLetters;
	
	public SortModel(T obj , String s){
		this.object = obj ;
		this.sortLetters = s ;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	@Override
	public int compareTo(SortModel<T> o2) {
		if (getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
