package com.safeway.app.emmd_batch.model;

import java.util.Date;
import java.util.UUID;

public class JobHistory extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2455151942720838588L;
	
	private String jobName;
	private UUID time;
	private String jobFullName;
	private String jobMode;
	private int step;
	Date  startTime;
	Date  updateTime;
	Date endTime;
	private String status;
	private String message;
	private String progress;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public UUID getTime() {
		return time;
	}
	public void setTime(UUID time) {
		this.time = time;
	}
	public String getJobFullName() {
		return jobFullName;
	}
	public void setJobFullName(String jobFullName) {
		this.jobFullName = jobFullName;
	}
	public String getJobMode() {
		return jobMode;
	}
	public void setJobMode(String jobMode) {
		this.jobMode = jobMode;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	
	
}
