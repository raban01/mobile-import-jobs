package com.safeway.app.emmd_batch.dao.cassandra;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.safeway.app.emmd_batch.dao.base.CassandraBaseDao;
import com.safeway.app.emmd_batch.model.JobHistory;

@Component("jobHistoryCassandraDao")
public class JobHistoryCassandraDaoImpl extends CassandraBaseDao implements JobHistoryCassandraDao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobHistoryCassandraDaoImpl.class);
	
	@Value("#{emmdCassandraQuery['job_history_insert']}")   
	private String insert;
	

	@Autowired
	@Qualifier("cassandraTemplate")
	private CassandraOperations cassandraOperations;
	
	private PreparedStatement INSERT_PS;
	
	@PostConstruct
	public void init() {
		INSERT_PS=cassandraOperations.getSession().prepare(insert);	
	}
	

	@Override
	public void saveJobHistory(JobHistory jobHistory) throws Exception{
		LOGGER.info("insert="+insert);
		BoundStatement bs ;
		
		LOGGER.debug("jobHistory= "+jobHistory);
		//job_name,time,job_full_name,job_mode,step,start_time,end_time,status,message
        
		bs = new BoundStatement(INSERT_PS);
		bs.bind(jobHistory.getJobName(),
				jobHistory.getTime(),
				jobHistory.getJobFullName(),
				jobHistory.getJobMode(),
				jobHistory.getStep(),
				jobHistory.getStartTime(),
				jobHistory.getEndTime(),
				jobHistory.getStatus(),
				jobHistory.getMessage(),
				jobHistory.getUpdateTime(),
				jobHistory.getProgress());
				
						
		cassandraOperations.execute(bs);
		
						    
		
		
	}


	@Override
	public void createSstable(String table) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("not support");
	}

	
}
