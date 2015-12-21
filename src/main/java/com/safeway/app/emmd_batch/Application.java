package com.safeway.app.emmd_batch;


import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;












import com.datastax.driver.core.utils.UUIDs;
import com.safeway.app.emmd_batch.dao.cassandra.JobHistoryCassandraDao;
import com.safeway.app.emmd_batch.model.JobHistory;
import com.safeway.app.emmd_batch.service.WeeklyAdService;
import com.safeway.app.emmd_batch.util.EmmdEnum.EmmdEnv;
import com.safeway.app.emmd_batch.util.EmmdEnum.JobMode;
import com.safeway.app.emmd_batch.util.EmmdEnum.JobStatus;
import com.safeway.app.emmd_batch.util.EmmdEnum.WeeklyAdJobMode;
import com.safeway.app.emmd_batch.util.JobWithSteps;

@ImportResource("application-context.xml")
@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	private WeeklyAdService weeklyAdService;
	
	@Autowired
	protected JobHistoryCassandraDao jobHistoryCassandraDao ;
	
	@Value("${env.name}")
	private String envName;
	
	public static void main(String args[]) throws IOException {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		Map<String, DataSource> dataSources = ctx.getBeansOfType(DataSource.class);
		Map<String, JdbcTemplate> jdbcTemplates = ctx.getBeansOfType(JdbcTemplate.class);
		LOGGER.debug(dataSources.toString());
		LOGGER.debug(jdbcTemplates.toString());
	}

	@Override
	public void run(String... args) throws IOException {
		long ts0 = System.currentTimeMillis();
		LOGGER.info("");
		LOGGER.info("");
		LOGGER.info("");
		LOGGER.info("");
		LOGGER.info("================Job Starts====================================");
		LOGGER.info("===========================================================");
		System.setProperty("java.net.preferIPv4Stack", "true");			
		try{
			if (args != null && args.length>0){
				
				for (String arg : args) {
					LOGGER.info("============arg======"+arg);
					StringTokenizer st = new StringTokenizer(arg, "+");
					String jobName=st.nextToken();
					
					if(jobName.equals("weekly_ad")){
						processWeeklyAdJob(jobName,  st);
					}/*else{
						processJob(jobName,  st);
					}*/
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			
		}
		long ts1 = System.currentTimeMillis();
		LOGGER.info("===========================================================");
		LOGGER.info("================Job END "+(ts1 - ts0) / 1000 + " seconds=======");
		
		System.exit(0);
		
	}
	
	public void processWeeklyAdJob(String jobName, StringTokenizer st) throws Exception {
		//FULL|QAC|ALL
		LOGGER.info("================================================================");
		LOGGER.info("Arg sample: weekly_ad+WeeklyAdJobMode+ENV+STORES");
		LOGGER.info("for all stores Arg sample: weekly_ad+WeeklyAdJobMode+ENV+all");
		LOGGER.info("WeeklyAdJobMode=FULL,CASSANDRA,REFRESH_EMMD,REFRESH_EMMD_LIVE");
		LOGGER.info("ENV=LOCAL,DEV,QAI,QA1,QA2,QAC,PRF,PROD");
		LOGGER.info("STORES=ALL or store id list seperated by comma");
		LOGGER.info("================================================================");
		
		String sJobMode=st.nextToken();
		WeeklyAdJobMode jobMode=WeeklyAdJobMode.FULL;
		if(sJobMode!=null && WeeklyAdJobMode.valueOf(sJobMode)!=null){
			jobMode=WeeklyAdJobMode.valueOf(sJobMode);
		}
		String env=st.nextToken();
		String emmdHost=EmmdEnv.QAI.getUrl();
		if(env!=null && EmmdEnv.valueOf(env)!=null){
			emmdHost=EmmdEnv.valueOf(env).getUrl();
		}
		
		String stores=st.nextToken();

		LOGGER.info("Running weeklyad with jobMode="+jobMode.getCode());
		weeklyAdService.startProcess(jobMode, emmdHost, stores);
		LOGGER.info("weeklyad job ended......");
						
	}
}
