package com.safeway.app.emmd_batch.dao.base;


import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import com.safeway.app.emmd_batch.dao.cassandra.loader.jmx.JmxBulkLoader;
import com.safeway.app.emmd_batch.util.SstableWriterUtil;

public class CassandraBaseDao {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	@Autowired
	@Qualifier("sstableWriterUtil")
	protected SstableWriterUtil sstableWriterUtil;
	
	@Value("${sstable.output.dir}")
	protected String sstableOutputDir;

	@Value("${sstable.loader.dir}")
	protected String sstableLoaderDir;
    
	@Value("${sstable.keyspace}")
	protected String sstableKeyspace;
    
	@Value("${cassandra.batch.size}")
	protected int batchSize;
	
	@Autowired
	@Qualifier("jmxBulkLoader")
	private JmxBulkLoader jmxBulkLoader;

	public void jmxBulkLoad(String table) throws Exception{
		
		long ts1 = System.currentTimeMillis();
		
			String path=sstableLoaderDir + File.separator + sstableKeyspace + File.separator + table.trim();
		    LOGGER.info("Path: "+path);
			jmxBulkLoader.connect();
			jmxBulkLoader.bulkLoad(path);
			
		long ts2 = System.currentTimeMillis();
        LOGGER.info("JMX complete! processTime_"+table+"="+(ts2 - ts1) / 1000 + "seconds");
        
	}
	
	protected long computeTTL(Date copPromoEndDate,Date offerEffectiveEndDate){

		long ttl = 1;
		Date effectiveDate = null;
		
		if (copPromoEndDate.getTime() > offerEffectiveEndDate.getTime()) {
			effectiveDate = copPromoEndDate;
		} else {
			effectiveDate = offerEffectiveEndDate;
		}
		
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, -1);
		Date today = c1.getTime();
		
		if (effectiveDate.getTime() > today.getTime() ) {
			long tmp = effectiveDate.getTime() - today.getTime();
			ttl = tmp / 1000;
		}
		return ttl;
	}
}
