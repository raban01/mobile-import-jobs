package com.safeway.app.emmd_batch.dao.cassandra;

import java.util.Date;

import com.safeway.app.emmd_batch.dao.cassandra.loader.LoaderDao;

public interface JobImportCassandraDao extends LoaderDao{
	public void saveJobImport(String jobName, Date jobDate) throws Exception;
	public Date getJobImportDate(String jobName) throws Exception;
}
