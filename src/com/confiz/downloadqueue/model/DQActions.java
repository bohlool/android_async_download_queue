package com.confiz.downloadqueue.model;

public enum DQActions {
	
	START_DOWNLOAD,UPDATE_DQ,STOP_DQ,PAUSE_ITEM,DELETE_ITEM,REMOVE_ITEM, START_DOWNLOAD_FROM_PAUSE;
	
	public static DQActions get(int i){
		return values()[i];
	}
}
