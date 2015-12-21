package com.safeway.app.emmd_batch.dao.cassandra;


import java.util.Calendar;
import java.util.Date;

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
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.safeway.app.emmd_batch.dao.base.CassandraBaseDao;

@Component("jobImportCassandraDao")
public class JobImportCassandraDaoImpl extends CassandraBaseDao implements JobImportCassandraDao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobImportCassandraDaoImpl.class);
	
	@Value("#{emmdCassandraQuery['job_import_insert']}")   
	private String insert;
	
	@Value("#{emmdCassandraQuery['job_import_select']}")   
	private String select;
	

	@Autowired
	@Qualifier("cassandraTemplate")
	private CassandraOperations cassandraOperations;
	
	private PreparedStatement INSERT_PS;
	
	@PostConstruct
	public void init() {
		INSERT_PS=cassandraOperations.getSession().prepare(insert);	
		
	}


	@Override
	public void createSstable(String table) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("not support");
	}


	@Override
	public void saveJobImport(String jobName, Date jobDate) throws Exception {
		LOGGER.info("insert="+insert);
		BoundStatement bs ;
		
		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(jobDate);                           // set cal to date
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millis in second
		Date zeroedDate = cal.getTime(); 
		
		LOGGER.debug("jobName= "+jobName +" jobDate="+zeroedDate);
		//job_name,time,job_full_name,job_mode,step,start_time,end_time,status,message
        
		bs = new BoundStatement(INSERT_PS);
		bs.bind(jobName,zeroedDate);
				
						
		cassandraOperations.execute(bs);
		
		
	}


	@Override
	public Date getJobImportDate(String jobName) throws Exception{
		LOGGER.info("select="+select);
		
		LOGGER.debug("jobName= "+jobName);
		
		
		ResultSet results=cassandraOperations.query(select+"'"+jobName+"'");
		Row row = results.one();

		if (row != null) {
			return row.getDate("job_date");
			
		}else{
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, 2000);
			return c.getTime();
		}
		
	}

	
}
