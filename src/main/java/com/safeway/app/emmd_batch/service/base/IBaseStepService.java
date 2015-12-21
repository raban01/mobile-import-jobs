package com.safeway.app.emmd_batch.service.base;

import com.safeway.app.emmd_batch.util.JobWithSteps;

public interface IBaseStepService {
	public void startProcess(JobWithSteps job) throws Exception;
}
