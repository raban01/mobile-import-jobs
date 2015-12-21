package com.safeway.app.emmd_batch.dao.cassandra.loader;

public interface LoaderDao {
	public void jmxBulkLoad(String table) throws Exception;
	public void createSstable(String table) throws Exception;
}
