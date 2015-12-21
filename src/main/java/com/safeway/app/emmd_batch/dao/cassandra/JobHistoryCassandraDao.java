package com.safeway.app.emmd_batch.dao.cassandra;

import com.safeway.app.emmd_batch.dao.cassandra.loader.LoaderDao;
import com.safeway.app.emmd_batch.model.JobHistory;

public interface JobHistoryCassandraDao extends LoaderDao{
	public void saveJobHistory(JobHistory jobHistory) throws Exception;
}
