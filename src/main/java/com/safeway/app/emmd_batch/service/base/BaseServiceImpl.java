package com.safeway.app.emmd_batch.service.base;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.safeway.app.emmd_batch.util.EmmdEnum.EmmdAlertCode.EMMDALERT002;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.utils.UUIDs;
import com.safeway.app.emmd_batch.dao.base.HiveBaseDao;
import com.safeway.app.emmd_batch.dao.cassandra.JobHistoryCassandraDao;
import com.safeway.app.emmd_batch.dao.cassandra.JobImportCassandraDao;
import com.safeway.app.emmd_batch.model.JobHistory;
import com.safeway.app.emmd_batch.service.email.EmailService;
import com.safeway.app.emmd_batch.util.EmmdEnum.JobMode;
import com.safeway.app.emmd_batch.util.EmmdEnum.JobStatus;

@Service("baseService")
public abstract class BaseServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);

	@Autowired
	protected EmailService emailService;
	
	@Autowired
	protected HiveBaseDao hiveBaseDao ;
	
	@Autowired
	protected JobHistoryCassandraDao jobHistoryCassandraDao ;
	
	@Autowired
    protected JobImportCassandraDao jobImportCassandraDao;
 
	
	@Value("${env.name}")
	private String envName;
	
	public abstract void hiveStep() throws Exception;
	public abstract void cassandraStep() throws Exception;
	
	
	
	protected void startProcess(JobMode jobMode, String jobName, StringBuilder emailBody) throws Exception{
		LOGGER.info(jobName+ ": start.");
		JobHistory jobHistory=new JobHistory();
		jobHistory.setJobName(jobName);
		jobHistory.setTime(UUIDs.timeBased());
		jobName=envName+"-"+jobName+"-"+jobMode.getCode();
		jobHistory.setJobFullName(jobName);
		jobHistory.setJobMode(jobMode.getCode());
		jobHistory.setStep(0);
		jobHistory.setStartTime(new Date());
		jobHistory.setStatus(JobStatus.START.getCode());
		jobHistory.setProgress("Starting");
		jobHistory.setUpdateTime(new Date());
		
		
		long ts0 = System.currentTimeMillis();
		long ts1 = System.currentTimeMillis();
		long ts2 = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyyy HH:mm a");
		
		
		// Send notification at the beginning of the import job process
        emailService.sendMail(jobName + " Started", jobName, emailBody.toString().trim(), sdf.format(new Date()));

		// Clear emailbody for next use
        emailBody.setLength(0);
        
        try {
        	jobHistoryCassandraDao.saveJobHistory(jobHistory);
    		
			
			if(jobMode.equals(JobMode.FULL)
					|| jobMode.equals(JobMode.HIVE)){
			
				LOGGER.info(jobName + " start hive.");
				ts1 = System.currentTimeMillis();
				hiveStep();	
				ts2 = System.currentTimeMillis();
	
				emailBody.append("\t"+jobName + " done hive. " + "Time taken to complete: " + (ts2 - ts1) / 1000 + " seconds\n");
				//emailService.sendMail(jobName + " done - HIVE", jobName, emailBody.toString().trim(), sdf.format(new Date()));
				//emailBody.setLength(0);
				LOGGER.info(jobName + ": done hive.");
				jobHistory.setUpdateTime(new Date());
				jobHistory.setStatus(JobStatus.INPROGRESS.getCode());
				String progress=jobHistory.getProgress()+"|"+"Finished HIVE Import";
				jobHistory.setProgress(progress);
				jobHistoryCassandraDao.saveJobHistory(jobHistory);
			}
			
			if(jobMode.equals(JobMode.FULL)
					|| jobMode.equals(JobMode.CASSANDRA)){
				ts1 = System.currentTimeMillis();
				cassandraStep();
				ts2 = System.currentTimeMillis();
				emailBody.append("\t"+jobName + " done cassandra. " + "Time taken to complete: " + (ts2 - ts1) / 1000 + " seconds\n");
				//emailService.sendMail(jobName + " done - CASSANDRA", jobName, emailBody.toString().trim(), sdf.format(new Date()));
				//emailBody.setLength(0);
				LOGGER.info(jobName + ": done cassandra.Time taken for processing: " + (ts2 - ts1) / 1000 + " seconds");
				jobHistory.setUpdateTime(new Date());
				jobHistory.setStatus(JobStatus.INPROGRESS.getCode());
				String progress=jobHistory.getProgress()+"|"+"Finished CASSANDRA Import";
				jobHistory.setProgress(progress);
				jobHistoryCassandraDao.saveJobHistory(jobHistory);
			}
			ts2 = System.currentTimeMillis();
			emailBody.append("\t"+jobName + " Completed. " + "Time taken for processing: " + (ts2 - ts0) / 1000 + " seconds");	
			
			emailService.sendMail(jobName + " Completed", jobName,emailBody.toString().trim(), sdf.format(new Date()));
			
			jobHistory.setEndTime(new Date());
			jobHistory.setStatus(JobStatus.SUCCESS.getCode());
			jobHistory.setUpdateTime(new Date());
			String progress=jobHistory.getProgress()+"|"+"End";
			jobHistory.setProgress(progress);
			jobHistoryCassandraDao.saveJobHistory(jobHistory);
    		
		} catch (Exception e) {
			LOGGER.error(jobName + ": exit with error");
			LOGGER.error(e.getMessage(), e);
			LOGGER.error("ERROR CODE=" + EMMDALERT002.getCode() + " MESSAGE=" + EMMDALERT002.getMsg() + ": " + jobName + " : " + e.getMessage());
			emailBody.append("\t"+jobName + " failed, Exit with error " + new Date() + " " + e.getMessage());
			
			jobHistory.setEndTime(new Date());
			jobHistory.setStatus(JobStatus.FAIL.getCode());
			jobHistory.setMessage(e.getMessage());
			jobHistory.setUpdateTime(new Date());
			
			try {
				jobHistoryCassandraDao.saveJobHistory(jobHistory);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			emailService.sendAlertEmail(EMMDALERT002, jobName, emailBody.toString().trim() , sdf.format(new Date()));
			throw e;
		}
		LOGGER.info(jobName + ": Time taken for processing: " + (ts2 - ts0) / 1000 + " seconds");
		LOGGER.info(jobName + ": stop.");
	}

}
