package com.safeway.app.emmd_batch.model;

public class WeeklyAd {
	private Integer storeId;
	private String offerId;
	private String response;
	
	public WeeklyAd() {
	}
	public WeeklyAd(Integer storeId, String response) {
		this.storeId = storeId;
		this.response = response;
	}
	
	public WeeklyAd(String offerId, String response) {
		this.offerId = offerId;
		this.response = response;
	}

	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public String getOfferId() {
		return offerId;
	}
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
