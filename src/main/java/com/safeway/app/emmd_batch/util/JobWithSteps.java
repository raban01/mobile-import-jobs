package com.safeway.app.emmd_batch.util;

import com.safeway.app.emmd_batch.util.EmmdEnum.JobMode;

public class JobWithSteps {
	
	private JobMode jobMode;
	private int step;
	public JobMode getJobMode() {
		return jobMode;
	}
	public void setJobMode(JobMode jobMode) {
		this.jobMode = jobMode;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public JobWithSteps(JobMode jobMode, int step) {
		super();
		this.jobMode = jobMode;
		this.step = step;
	}
	
}
