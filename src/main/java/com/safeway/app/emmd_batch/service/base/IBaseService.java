package com.safeway.app.emmd_batch.service.base;

import com.safeway.app.emmd_batch.util.EmmdEnum.JobMode;

public interface IBaseService {
	public void startProcess(JobMode jobMode) throws Exception;
	
	public void hiveStep() throws Exception;
	public void cassandraStep() throws Exception;
	
}
