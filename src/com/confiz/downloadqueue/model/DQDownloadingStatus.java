
package com.confiz.downloadqueue.model;

public enum DQDownloadingStatus {

	DOWNLOADING("Downloading", "Downloading..."), PAUSED("Paused", "Paused"), WAITING("Waiting", "Queued"), FAILED(
	        "Failed", "Failed"), PAUSED_REQUEST("Paused_request", "Pausing"), DOWNLOAD_REQUEST(
	        "Download_request", "Downloading..."), DELETE_REQUEST("Delete_request", "Deleting..."), DELETED(
	        "Deleted", "Deleted"), COMPLETED("Completed", "Saved"), MAX_TIRES_DONE("Falied",
	        "Max tries exceded. Please delete this file and try again later"), SIZE_OVERLOADED(
	        "Limit exceed", "Size limit exceeded");


	private String status = null;

	private String message = null;


	DQDownloadingStatus(String status, String message) {

		this.status = status;
		this.message = message;
	}


	public String value() {

		return status;
	}


	public String message() {

		return message;
	}


	public static DQDownloadingStatus get(int i) {

		return values()[i];
	}
}
