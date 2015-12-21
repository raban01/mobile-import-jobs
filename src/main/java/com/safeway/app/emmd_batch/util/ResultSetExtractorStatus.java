package com.safeway.app.emmd_batch.util;

public class ResultSetExtractorStatus {

	private Integer count;
	private Boolean status;
	
	
	public ResultSetExtractorStatus() {
	}
	public ResultSetExtractorStatus(Integer count, Boolean status) {
		this.count = count;
		this.status = status;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
}
