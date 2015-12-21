package com.safeway.app.emmd_batch.service;

import com.safeway.app.emmd_batch.util.EmmdEnum.WeeklyAdJobMode;


public interface WeeklyAdService {
	public void startProcess(WeeklyAdJobMode jobMode, String emmdHost, String stores) throws Exception;
}
