package com.safeway.app.emmd_batch.model;

public class WeeklyAdResult {

	private Integer storeId;
	private Integer offersCount;
	private Boolean status;

	
	public WeeklyAdResult(Integer storeId, Integer offersCount, Boolean status) {
		this.storeId = storeId;
		this.offersCount = offersCount;
		this.status = status;
	}
	
	public WeeklyAdResult() {
	}

	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public Integer getOffersCount() {
		return offersCount;
	}
	public void setOffersCount(Integer offersCount) {
		this.offersCount = offersCount;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "[storeId=" + storeId + ", offersCount="+ offersCount + ", status=" + status + "]";
	}
}
