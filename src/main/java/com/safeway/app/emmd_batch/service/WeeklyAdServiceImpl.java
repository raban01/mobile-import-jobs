package com.safeway.app.emmd_batch.service;

import static com.safeway.app.emmd_batch.util.EmmdEnum.EmmdAlertCode.EMMDALERT002;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.safeway.app.emmd_batch.dao.cassandra.JobHistoryCassandraDao;
import com.safeway.app.emmd_batch.dao.cassandra.WeeklyAdCassandraDao;
import com.safeway.app.emmd_batch.model.JobHistory;
import com.safeway.app.emmd_batch.model.WeeklyAd;
import com.safeway.app.emmd_batch.model.WeeklyAdResult;
import com.safeway.app.emmd_batch.service.email.EmailService;
import com.safeway.app.emmd_batch.util.EmmdEnum.JobStatus;
import com.safeway.app.emmd_batch.util.EmmdEnum.WeeklyAdJobMode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.joda.time.DateTime;
import org.joda.time.Days;

@Service("weeklyAdService")
public class WeeklyAdServiceImpl implements WeeklyAdService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyAdServiceImpl.class);

	@Autowired
	private WeeklyAdCassandraDao weeklyAdCassandraDao;
	
	@Autowired
	protected EmailService emailService;
	
	@Value("${weekly.ad.3rdparty.url}")
	private String host;
	
	@Value("${weekly.ad.3rdparty.uri}")
	private String uri;
	
	@Value("${weekly.ad.emmd.uri}")
	private String emmdUri;
	
	@Value("${weekly.ad.emmd.live.uri}")
	private String emmdLiveUri;
	
	
	@Value("${weekly.ad.retry.count}")
	private int retryCount;
	
	private int HTTP_CONNECTIONREQUEST_TIMEOUT=2000;
	private int HTTP_CONNECTION_TIMEOUT=2000;
	private int HTTP_SOCKET_TIMEOUT=5000;
	

	private static String EMMD_APIKEY="peJkLZiK1Iw0m1aLmzxx6Wf9baKERh5K";
	
	@Autowired
	protected JobHistoryCassandraDao jobHistoryCassandraDao ;
	
	@Override
	public void startProcess(WeeklyAdJobMode jobMode, String emmdHost, String stores) throws Exception{
		String jobName = "WeeklyAdImportJob";
		
		LOGGER.info(jobName + " start.");
		LOGGER.info("host="+host);
		
		JobHistory jobHistory=new JobHistory();
		jobHistory.setJobName(jobName);
		jobHistory.setTime(UUIDs.timeBased());
		
		jobName=emmdHost+"-"+jobName+"-"+jobMode.getCode();
		jobHistory.setJobFullName(jobName);
		jobHistory.setJobMode(jobMode.getCode());
		jobHistory.setStep(0);
		jobHistory.setStartTime(new Date());
		jobHistory.setStatus(JobStatus.START.getCode());
		jobHistory.setProgress("Starting");
		jobHistory.setUpdateTime(new Date());
		
		Date startDate = new Date();
		LOGGER.info("Start Time-------------------" + startDate);
		long requestStartTime = startDate.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyyy HH:mm a");
		
		long ts1 = System.currentTimeMillis();
		long ts2 = System.currentTimeMillis();
		StringBuilder emailbody = new StringBuilder();
		 
        emailbody.append("The Process will perform the following steps\n\n\t"
				 + "Step 01 - Send notification that job has started\n\t"
				 + "Step 02 - Extract records from weeklyad third party\n\t"
				 + "Step 03 - Save in cassandra (FULL or CASSANDRA) \n\t"
				 + "Step 04 - refresh EMMD cache (REFRESH or REFRESHLIVE) \n\t"
				 + "Step 07 - Send final Notification");
        
		// Send notification at the beginning of the import job process
        // Disable email notification
        // emailService.sendMail(jobName + " Started", jobName, emailbody.toString().trim(), sdf.format(new Date()));

		// Clear emailbody for next use
        emailbody.setLength(0);

		try {
			jobHistoryCassandraDao.saveJobHistory(jobHistory);
    		
			
			emailbody.append("stores="+stores+"\n\n");
			
			Set<Integer> storeIds = new HashSet<Integer>();
			List<WeeklyAdResult> result = new ArrayList<WeeklyAdResult>();
			
			List<WeeklyAdResult> result2 = new ArrayList<WeeklyAdResult>();
			
			if ("all".equalsIgnoreCase(stores)){
				storeIds = weeklyAdCassandraDao.getOpenStores();
			}
			else {
				String[] ids = stores.split(",");
				for (String s : ids) {
					storeIds.add(new Integer(s));
				}
			}
			
			if(jobMode.equals(WeeklyAdJobMode.FULL)
					|| jobMode.equals(WeeklyAdJobMode.CASSANDRA)){
				int failureCount=0;
				LOGGER.info(jobName + " start cassandra refresh.");
				
				failureCount = saveWeeklyAdDataLocally(storeIds,result);
				
				LOGGER.info(jobName + " saveWeeklyAdDataLocally done.");
				
				Date endDate = new Date();
				long requestEndTime = endDate.getTime();
				
				emailbody.append("\n\nTask of weekly_add CASSANDRA finished @ "+TimeUnit.MILLISECONDS.toSeconds((requestEndTime - requestStartTime))+" second(s).\n\n");
				emailbody.append("JobName:\tweekly_ad CASSANDRA\n\n");
				emailbody.append("failure count:"+failureCount+"\n\n");
				emailbody.append("StoreId\t\tOffer_Count\t\tStatus\n");
				
				Collections.sort(result, new Comparator<WeeklyAdResult>() {
			        
			        public int compare(final WeeklyAdResult o1, final WeeklyAdResult o2) {
			            int i= o1.getStatus().compareTo(o2.getStatus());
			            if (i != 0) return i;
			            return o1.getStoreId().compareTo(o2.getStoreId());
			        }
			    });

				for (WeeklyAdResult r : result) {
					emailbody.append(r.getStoreId()+"\t\t"+r.getOffersCount()+"\t\t\t"+(r.getStatus().booleanValue()?"Success":"Fail")+"\n");
				}
				LOGGER.debug("Sending mail......");
				// Disable email notification
				// emailService.sendMail(jobName + " cassandra status", emailbody.toString().trim());

				jobHistory.setUpdateTime(new Date());
				jobHistory.setStatus(JobStatus.INPROGRESS.getCode());
				String progress=jobHistory.getProgress()+"|"+"Finished Cassandra import";
				jobHistory.setProgress(progress);
				jobHistoryCassandraDao.saveJobHistory(jobHistory);
			}
			// Clear emailbody for next use
	        emailbody.setLength(0);

	        /* 
			if(jobMode.equals(WeeklyAdJobMode.FULL)
					|| jobMode.equals(WeeklyAdJobMode.REFRESH_EMMD)
					|| jobMode.equals(WeeklyAdJobMode.REFRESH_EMMD_LIVE)){
				Date startDate2 = new Date();
				LOGGER.info("Start Time-------------------" + startDate);
				long requestStartTime2 = startDate2.getTime();
				
				int failureCount=0;
				if(jobMode.equals(WeeklyAdJobMode.FULL)
						|| jobMode.equals(WeeklyAdJobMode.REFRESH_EMMD)){
					failureCount=refreshEmmd(storeIds, emmdHost, result2, false);
	
				}else if(jobMode.equals(WeeklyAdJobMode.REFRESH_EMMD_LIVE)){
					failureCount=refreshEmmd(storeIds, emmdHost, result2, true);
				}
				Date endDate = new Date();
				long requestEndTime = endDate.getTime();
				
				Collections.sort(result2, new Comparator<WeeklyAdResult>() {
			        
			        public int compare(final WeeklyAdResult o1, final WeeklyAdResult o2) {
			        	int i= o1.getStatus().compareTo(o2.getStatus());
			            if (i != 0) return i;
			            return o1.getStoreId().compareTo(o2.getStoreId());
			        }
			    });
				
				emailbody.append("\n\nTask of weekly_add EMMD refresh finished @ "+TimeUnit.MILLISECONDS.toSeconds((requestEndTime - requestStartTime2))+" second(s).\n\n");
				emailbody.append("JobName:\tweekly_ad EMMD refresh\n\n");
				//if(failureCount>0){
				emailbody.append("failure count:"+failureCount+"\n\n");
				//}
				emailbody.append("StoreId\t\t\t\tStatus\n");
				
				for (WeeklyAdResult r : result2) {
					emailbody.append(r.getStoreId()+"\t\t\t\t"+(r.getStatus().booleanValue()?"Success":"Fail")+"\n");
				}
				
				LOGGER.debug("Sending mail......");
				//LOGGER.debug(mailText.toString());
				// Disable email notification
				// emailService.sendMail(jobName + " refresh status", emailbody.toString().trim());

				jobHistory.setUpdateTime(new Date());
				jobHistory.setStatus(JobStatus.INPROGRESS.getCode());
				String progress=jobHistory.getProgress()+"|"+"Finished EMMD Refresh";
				jobHistory.setProgress(progress);
				jobHistoryCassandraDao.saveJobHistory(jobHistory);
			} */
			
			
			Date endDate = new Date();
			long requestEndTime = endDate.getTime();
			
			LOGGER.debug("Total Execution time in second(s): " + TimeUnit.MILLISECONDS.toSeconds((requestEndTime - requestStartTime)) + " sec");
			LOGGER.debug("End Time-------------------" + endDate);
			jobHistory.setEndTime(new Date());
			jobHistory.setStatus(JobStatus.SUCCESS.getCode());
			jobHistory.setUpdateTime(new Date());
			String progress=jobHistory.getProgress()+"|"+"End Process";
			jobHistory.setProgress(progress);
			jobHistoryCassandraDao.saveJobHistory(jobHistory);
			
			
			//emailService.sendMail(jobName + " Completed", jobName,"finished at " + new Date(), sdf.format(new Date()));
			ts2 = System.currentTimeMillis();
			LOGGER.debug("Mail Sent......");
		} catch (Exception e) {
			LOGGER.error(jobName + " exit with error");
			LOGGER.error(e.getMessage(), e);
			LOGGER.error("ERROR CODE=" + EMMDALERT002.getCode() + " MESSAGE=" + EMMDALERT002.getMsg() + ": " + jobName + " : " + e.getMessage());
			
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
			// Disable email notification
			// emailService.sendAlertEmail(EMMDALERT002, jobName, "Exit with error " + new Date() + " " + e.getMessage(), sdf.format(new Date()));
		
		}
		LOGGER.info("Time taken for processing the weekly ad data : " + (ts2 - ts1) / 1000 + " seconds");

		LOGGER.info(jobName + " stop.");
	}
	
	private int saveWeeklyAdDataLocally(Set<Integer> storeIds, List<WeeklyAdResult> result) {
		
		int failureCount=0;
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		
		//The default size of the pool of concurrent connections that can be open 
		//by the manager is 2 for each route or target host, and 20 for total open connections.
		
		// Increase max total connection to 200
		poolingConnManager.setMaxTotal(100);
				
		// Increase default max connection per route to 20
		poolingConnManager.setDefaultMaxPerRoute(100);
		
		HttpHost httpHost = new HttpHost(host,80,"http");
		HttpRoute route = new HttpRoute(httpHost);

		// Increase max connections for this host to 50
		poolingConnManager.setMaxPerRoute(route, 100);

		// Set the timeout
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(HTTP_CONNECTIONREQUEST_TIMEOUT)
		        .setConnectTimeout(HTTP_CONNECTION_TIMEOUT)
		        .setSocketTimeout(HTTP_SOCKET_TIMEOUT)
		        .setExpectContinueEnabled(true)
		        .setStaleConnectionCheckEnabled(true).build();
		
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).setConnectionManager(poolingConnManager).build();
		
		int noOfStoreWrote=1;
		
		LOGGER.debug("Getting the list of $5 Friday search strings...");
		List<String> lstFiveDollarFridayText = weeklyAdCassandraDao.getFiveDollarFridayText();
		
		for (Integer storeId: storeIds){
			
			// specify the get request
			HttpGet getRequest = new HttpGet(uri.replace("<STORE_ID>", String.valueOf(storeId)));
			
			LOGGER.debug("storeId="+storeId);
			CloseableHttpResponse httpResponse = null;
			HttpEntity entity = null;
			
			
			int offersSize=0;
			try {
				
				httpResponse = httpclient.execute(httpHost, getRequest);
				
				entity = httpResponse.getEntity();
				
				if ( httpResponse.getStatusLine().getStatusCode() == 200){
					
					String orig_response = EntityUtils.toString(entity);
					/*BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
					String orig_response = "";
					String line;
					while ((line = br.readLine())!= null) {
						orig_response = orig_response + line;
					}*/
					
					String response = changeFridaySpecialsDates(orig_response, lstFiveDollarFridayText);
					
					List<WeeklyAd> offersAdList = extractOffers(response);
					offersSize = offersAdList.size();
					
					weeklyAdCassandraDao.saveStoreWeeklyAdData(new WeeklyAd(storeId, response),offersAdList);
					
					LOGGER.debug("No of store wrote: "+noOfStoreWrote++);
					
					result.add(new WeeklyAdResult(storeId, offersSize, new Boolean(true)));
					
				}
				
			} catch (Exception e) {
				LOGGER.error(e.toString(),e);
				result.add(new WeeklyAdResult(storeId,  offersSize, new Boolean(false)));
				failureCount++;
			}
			finally {
				try {
					EntityUtils.consume(entity);
				} catch (IOException ioe) {
					LOGGER.error(ioe.toString(),ioe);
				}
			}
		}
		
		try {
			httpclient.close();
		} catch (IOException e) {
			LOGGER.error(e.toString(),e);
		}
		
		poolingConnManager.close();
		poolingConnManager.shutdown();
		return failureCount;
	}
	
	
	private List<WeeklyAd> extractOffers(String response) throws Exception {
		List<WeeklyAd> offersAdList = new ArrayList<WeeklyAd>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		
		Iterator<JsonNode> itr = rootNode.path("value").iterator();
		while (itr.hasNext()){
			JsonNode n=itr.next();
			offersAdList.add(new WeeklyAd(n.path("OfferId").asText(), n.toString()));
			//LOGGER.debug("offerId: "+ n.path("OfferId").asText() + "===="+n );
		}
		
		
		return offersAdList;
	}
	
	private int refreshEmmd(Set<Integer> storeIds, String emmdHost, List<WeeklyAdResult> result, boolean live) {
		
		
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		
		//The default size of the pool of concurrent connections that can be open 
		//by the manager is 2 for each route or target host, and 20 for total open connections.
		
		// Increase max total connection to 200
		poolingConnManager.setMaxTotal(100);
				
		// Increase default max connection per route to 20
		poolingConnManager.setDefaultMaxPerRoute(100);
		
		String uri=emmdUri;
		if(live){
			uri=emmdLiveUri;
		}
		
		LOGGER.info("EMMD host="+emmdHost+", uri="+uri+", retryCount="+retryCount);
		
		HttpHost httpHost = new HttpHost(emmdHost,80,"http");
		HttpRoute route = new HttpRoute(httpHost);

		// Increase max connections for this host to 50
		poolingConnManager.setMaxPerRoute(route, 100);

		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(poolingConnManager).build();
		
		Map<Integer,Boolean> sStatusMap=new HashMap<Integer,Boolean>();
		Map<Integer,Boolean> fStatusMap=new HashMap<Integer,Boolean>();
		int processCycleCount=0;
		while(processCycleCount<retryCount && storeIds.size()>0){
			
			processCycleCount++;
			LOGGER.debug("processCycleCount="+processCycleCount+" storeIds.size="+storeIds.size());
			
			fStatusMap.clear();
			
			for (Integer storeId: storeIds){
				LOGGER.debug("storeId="+storeId);
				// specify the get request
				HttpPost postRequest = new HttpPost(uri+storeId);
				
				
				CloseableHttpResponse httpResponse = null;
				
				try {
					String reqTimeUtcMillis = String.valueOf(System.currentTimeMillis());
					String signatureUrl="http://"+emmdHost+uri+storeId + reqTimeUtcMillis+ EMMD_APIKEY;
					//LOGGER.debug("signatureUrl="+signatureUrl);
					String swySignature = getDigest(signatureUrl);
					
					postRequest.addHeader("REQUEST_TIME", reqTimeUtcMillis);
					postRequest.addHeader("SWY_SIGNATURE", swySignature);
					
					httpResponse = httpclient.execute(httpHost, postRequest);
	
					if ( httpResponse.getStatusLine().getStatusCode() == 200){	
						String responseString=readAsString(new InputStreamReader(httpResponse.getEntity().getContent()));
						LOGGER.debug("responseString="+responseString);
						
						if(responseString.startsWith("{\"ack\":\"0\"")){
							sStatusMap.put(storeId, new Boolean(true));
							
							LOGGER.debug("storeId="+storeId+" loaded, successfulCount="+sStatusMap.size()+" falseCount="+fStatusMap.size());
						}else if(responseString.endsWith("\"detail\":\"Bad Request\"}]}")){
							sStatusMap.put(storeId, new Boolean(true));
							
							LOGGER.debug("storeId="+storeId+" store is not available, successfulCount="+sStatusMap.size()+" falseCount="+fStatusMap.size());
						}else {
							throw new Exception("status code is "+httpResponse.getStatusLine().getStatusCode()+", responseString="+responseString);
						}
					}else{
						throw new Exception("status code is "+httpResponse.getStatusLine().getStatusCode());
						
					}
					
				} catch (Exception e) {
					//LOGGER.error("store_id="+storeId +" "+e.toString(),e);
					fStatusMap.put(storeId, new Boolean(false));
					LOGGER.debug("storeId="+storeId+" failed, falseCount="+fStatusMap.size()+" processCycleCount="+processCycleCount);
				} finally {
					if (httpResponse!=null){
						try {
							httpResponse.close();
						} catch (IOException e) {
							LOGGER.error(e.toString(),e);
						}
					}
				}
			}
			storeIds=new HashSet<Integer>(fStatusMap.keySet());
		}
		
		for(Integer storeId:sStatusMap.keySet()){
			result.add(new WeeklyAdResult(storeId, 0, new Boolean(true)));
		}
		for(Integer storeId:fStatusMap.keySet()){
			result.add(new WeeklyAdResult(storeId, 0, new Boolean(false)));
		}
		
		try {
			httpclient.close();
		} catch (IOException e) {
			LOGGER.error(e.toString(),e);
		}
		
		poolingConnManager.close();
		poolingConnManager.shutdown();
		
		return fStatusMap.keySet().size();
		
	}
	
	private String getDigest(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        return new Base64().encodeToString(result);
	}

	private String readAsString(Reader reader) throws IOException {

		if( reader == null ) return null;

		BufferedReader br = new BufferedReader(reader);
		String read = br.readLine();

		if (read==null) return null;

		StringBuilder sb = new StringBuilder(read);
		while (read != null) {
			read = br.readLine();
			if (read != null)
				sb.append(read);
		}
		return sb.toString();
	}

	private static String changeFridaySpecialsDates(String response, List<String> lstFiveDollarFridayText) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		boolean isDatesUpdated = false;
		
		if(0 != lstFiveDollarFridayText.size()) 
		{
			Iterator<JsonNode> itr = rootNode.path("value").iterator();
			while (itr.hasNext()){
				JsonNode offerNode=itr.next();
				Iterator<JsonNode> customPropsIter = offerNode.path("CustomProperties").iterator();
				while (customPropsIter.hasNext()) {
					JsonNode customProperty=customPropsIter.next();
					if("event".equalsIgnoreCase(customProperty.path("PropertyName").asText())){
						String eventName = customProperty.path("PropertyValue").asText().toLowerCase();
							
						if(isFiveDollarOffer(eventName, lstFiveDollarFridayText))
						{
							String startDate = offerNode.path("StartDate").asText();
							String endDate = offerNode.path("EndDate").asText();
							if(dateDiff(startDate,endDate)>1){
								LOGGER.debug("Start Date is :" + startDate + " End Date is :"+endDate);
								String fridayDate = fridayDate(startDate);
								startDate = fridayDate + "T00:00:00";
								endDate   = fridayDate + "T23:59:59";
								LOGGER.debug("(Changed) Start Date is :" + startDate + " End Date is :"+endDate);
								//write the modified dates back
								((ObjectNode)offerNode).put("StartDate",startDate);
								((ObjectNode)offerNode).put("EndDate",endDate);
								isDatesUpdated=true;
							} else
								LOGGER.debug("diff < 1");
						}
					}
				}
			}
			
		}
		
		if(!isDatesUpdated) {
			return response;
		}
		
		return String.valueOf(rootNode);
	}
	
	private static boolean isFiveDollarOffer(String eventName, List<String> lstFiveDollarFridayText) {
		boolean status = false;
		for(String currText : lstFiveDollarFridayText)
		{
			if(currText.trim().equalsIgnoreCase(eventName)){
				status = true;
				break;
			}
		}
		return status;
	}
	
	private static int dateDiff(String start, String end) throws ParseException{
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = dateFormat.parse(start);
		Date endDate = dateFormat.parse(end);
		
		DateTime dateTime1 = new DateTime(startDate);
		DateTime dateTime2 = new DateTime(endDate);
		int result= Days.daysBetween(dateTime1, dateTime2).getDays();
		//System.out.printf("Days b/w %s & %s dates is %d\n", start,end,result);
		return result;
	}
	
	private static String fridayDate(String date) throws ParseException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = dateFormat.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int offset = Calendar.FRIDAY - cal.get(Calendar.DAY_OF_WEEK);
		if(offset==-1)
			offset+=Calendar.SATURDAY;
		cal.add(Calendar.DATE, offset);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format( cal.getTime() );
	}
}
