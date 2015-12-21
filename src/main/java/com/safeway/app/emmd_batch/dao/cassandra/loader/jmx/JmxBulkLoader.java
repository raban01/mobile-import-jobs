package com.safeway.app.emmd_batch.dao.cassandra.loader.jmx;

import java.io.IOException;

import javax.management.MalformedObjectNameException;

public interface JmxBulkLoader {
	public void connect() throws IOException,MalformedObjectNameException;
	public void close() throws IOException;
	public void bulkLoad(String path); 
}
